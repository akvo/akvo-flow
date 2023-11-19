/*
 *  Copyright (C) 2010,2012-2016,2018-2019 Stichting Akvo (Akvo Foundation)
 *
 *  This file is part of Akvo FLOW.
 *
 *  Akvo FLOW is free software: you can redistribute it and modify it under the terms of
 *  the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 *  either version 3 of the License or any later version.
 *
 *  Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Affero General Public License included below for more details.
 *
 *  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */

package org.waterforpeople.mapping.app.web;

import com.gallatinsystems.common.util.MailUtil;
import com.gallatinsystems.common.util.PropertyUtil;
import com.gallatinsystems.device.dao.DeviceDAO;
import com.gallatinsystems.device.dao.DeviceFileJobQueueDAO;
import com.gallatinsystems.device.domain.Device;
import com.gallatinsystems.device.domain.DeviceFileJobQueue;
import com.gallatinsystems.survey.dao.CascadeResourceDao;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.domain.CascadeResource;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.survey.domain.CascadeResource.Status;
import com.gallatinsystems.user.dao.UserDao;
import com.gallatinsystems.user.domain.User;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

import org.akvo.flow.dao.MessageDao;
import org.akvo.flow.domain.FormSubmissionsLimit;
import org.akvo.flow.domain.Message;
import org.akvo.flow.domain.UserFormSubmissionsCounter;
import org.apache.commons.lang.StringUtils;
import org.waterforpeople.mapping.app.web.dto.TaskRequest;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Logger;

import static org.waterforpeople.mapping.app.web.EnvServlet.FORM_SUBMISSIONS_LIMIT;
import static org.waterforpeople.mapping.app.web.EnvServlet.FORM_SUBMISSIONS_SOFT_LIMIT_PERCENTAGE;

/**
 * Servlet used by app to trigger processing of new survey data
 */
public class ProcessorServlet extends HttpServlet {

    private static final long serialVersionUID = -7062679258542909086L;
    private static final Logger log = Logger.getLogger(ProcessorServlet.class.getName());

    private static final String ACTION_PARAM = "action";
    private static final String SUBMIT_ACTION = "submit";
    private static final String IMAGE_ACTION = "image";
    private static final String CASCADE_ACTION = "cascade";
    private static final String CHECKSUM_PARAM = "checksum";
    private static final String IMEI_PARAM = "imei";
    private static final String ANDROID_ID_PARAM = "androidId";
    private static final String PHONE_NUMBER_PARAM = "phoneNumber";
    private static final String FILE_NAME_PARAM = "fileName";

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String action = StringUtils.trim(req.getParameter(ACTION_PARAM));
        if (action == null) {
            log.info("No action specified for processor");
            return;
        }
        log.info("  ProcessorServlet->action->" + action);

        String fileName = StringUtils.trim(req.getParameter(FILE_NAME_PARAM));
        String phoneNumber = StringUtils.trim(req.getParameter(PHONE_NUMBER_PARAM));
        String androidId = StringUtils.trim(req.getParameter(ANDROID_ID_PARAM));
        String imei = StringUtils.trim(req.getParameter(IMEI_PARAM));
        String checksum = StringUtils.trim(req.getParameter(CHECKSUM_PARAM));
        Survey form = null;
        Integer submissionCount = 0;

        if (SUBMIT_ACTION.equals(action)) {
            if (fileName == null) {
                log.warning("Request for processing without filename: phoneNumber=(" + phoneNumber
                        + "); IMEI=(" + imei + ")");
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            // If we get a form ID, ensure the form is still present in the datastore
            Long formID = parseFormID(req);
            if (formID != null) {
                SurveyDAO surveyDAO = new SurveyDAO();
                form = surveyDAO.getById(formID);
                if (form == null) {
                    log.warning("Form " + formID + " doesn't exist in the datastore");
                    prepareJsonResponse(
                            resp,
                            HttpServletResponse.SC_NOT_FOUND,
                            String.format(
                                    "{\"error\": \"Form %d does not exist\", \"message\": \"It has probably been deleted\"}",
                                    formID));
                    return;
                }
            }

            // Form submission restriction for the Basic instance
            FormSubmissionsLimit limiter = new FormSubmissionsLimit(getFormSubmissionsLimit(),
                    getFormSubmissionSoftLimitPercentage());
            if (limiter.isEnabled()) {
                UserFormSubmissionsCounter counter = new UserFormSubmissionsCounter(
                        DatastoreServiceFactory.getDatastoreService());
                User user = new UserDao().getByKey(form.getCreateUserId());
                // increment submissions count by 1 because this check occurs before the data is saved
                submissionCount = counter.countFor(user) + 1;

                if (submissionCount >= limiter.getSoftLimit()) {
                    log.info("Send submission restriction mail, submissionCount: " + submissionCount);
                    sendFormSubmissionRestrictionEmail(user, limiter, submissionCount);
                }
                if (submissionCount >= limiter.getSoftLimit() && submissionCount <= limiter.getHardLimit()) {
                    log.info("Return hard limit error response");
                    prepareJsonResponse(
                            resp,
                            HttpServletResponse.SC_OK,
                            String.format(
                                    "{\"warning\": \"Warning, reaching the limit soon\", \"message\": \"You have reached %d%% of the form submission limit\"}",
                                    limiter.getPercentage(submissionCount)));
                }
                if (submissionCount > limiter.getHardLimit()) {
                    log.info("Return hard limit error response");
                    prepareJsonResponse(
                            resp,
                            HttpServletResponse.SC_FORBIDDEN,
                            "{\"error\": \"Error, data not submitted!\", \"message\": \"You have reached the form submission limit\"}");
                    return;
                }
            }

            log.info("  ProcessorServlet->filename->" + fileName);
            // Submit the fileName for processing
            // on a single-instance queue to prevent race conditions
            Queue queue = QueueFactory.getQueue("processDeviceFile");
            TaskOptions options = TaskOptions.Builder.withUrl("/app_worker/task")
                    .param(TaskRequest.ACTION_PARAM, TaskRequest.PROCESS_FILE_ACTION)
                    .param(TaskRequest.FILE_NAME_PARAM, fileName);

            if (androidId != null) {
                options.param(TaskRequest.ANDROID_ID, androidId);
            }
            if (phoneNumber != null) {
                options.param(TaskRequest.PHONE_NUM_PARAM, phoneNumber);
            }
            if (imei != null) {
                options.param(TaskRequest.IMEI_PARAM, imei);
            }
            if (checksum != null) {
                options.param(TaskRequest.CHECKSUM_PARAM, checksum);
            }

            queue.add(options);

            log.info("submiting task for fileName: " + fileName);

        } else if (IMAGE_ACTION.equals(action)) {
            Device d = null;
            DeviceDAO dao = new DeviceDAO();

            d = dao.getDevice(androidId, imei, phoneNumber);
            if (d == null) {
                log.severe(String.format(
                        "No device found with imei %s or phoneNumber %s",
                        imei, phoneNumber));
                return;
            }

            DeviceFileJobQueueDAO dfDao = new DeviceFileJobQueueDAO();
            List<DeviceFileJobQueue> missing = dfDao.listByDeviceAndFile(
                    d.getKey().getId(), fileName);
            log.info(String.format(
                    "Deleting %s entities matching the fileName %s",
                    missing.size(), fileName));
            dfDao.delete(missing);
        } else if (CASCADE_ACTION.equals(action)) {
            Long crId = null;
            final String status = StringUtils.trim(req.getParameter("status"));
            final String message = StringUtils.trim(req.getParameter("message"));
            final CascadeResourceDao crDao = new CascadeResourceDao();

            try {
                crId = Long.valueOf(StringUtils.trim(req.getParameter("cascadeResourceId")));
            } catch (NumberFormatException e) {
            }

            if (crId == null || status == null) {
                log.warning(String.format("Invalid processor request - [resourceId: %s , status: %s]", crId, status));
                return;
            }

            CascadeResource cr = crDao.getByKey(crId);

            if (cr == null) {
                return;
            }

            final MessageDao mDao = new MessageDao();
            final Message m = new Message();
            m.setActionAbout("cascadePublish");

            if ("published".equals(status)) {
                cr.setStatus(Status.PUBLISHED);
                cr.setVersion(cr.getVersion() + 1);
                m.setShortMessage("Cascade resource " + cr.getName() + " successfully published");
            } else {
                cr.setStatus(Status.NOT_PUBLISHED);
                String errorMessage = "Failed to publish cascade resource " + cr.getName();
                if (StringUtils.isNotBlank(message)) {
                    errorMessage = errorMessage + " - Error: " + message;
                }
                m.setShortMessage(errorMessage);
            }

            crDao.save(cr);
            mDao.save(m);

        }
        resp.addHeader("Access-Control-Allow-Origin", "*");
        resp.addHeader("Access-Control-Allow-Headers", "*");
        resp.addHeader("Access-Control-Allow-Methods", "OPTIONS, GET");
    }

    private Long parseFormID(HttpServletRequest req) {
        String formID = StringUtils.trim(req.getParameter("formID"));
        if (StringUtils.isNotBlank(formID)) {
            try {
                return Long.valueOf(formID);
            } catch (NumberFormatException e) {
                log.warning("Form ID is not a valid number: " + formID);
            }
        }

        return null;
    }

    private void sendFormSubmissionRestrictionEmail(User user, FormSubmissionsLimit limiter, Integer count) {
        Long percentage = limiter.getPercentage(count);
        String subject = String.format("%d%% limit reached", (percentage > 100) ? 100 : percentage);
        String body_1 = String.format(
                "Dear %s,\n\nYou have reached %d%% (%d / %d forms) of the form submission limit of your FLOW Basic account. Form submissions will be blocked once the limit is reached.\n\nContact support@akvo.org to upgrade to a paid plan.\n\nRegards,\n\nThe FLOW Team",
                user.getUserName(), percentage, count, limiter.getHardLimit());
        String body_2 = String.format(
                "Dear %s,\n\nYou have reached the form submission limit (%d forms). Form submissions are now blocked. You can still download your data. Please contact support@akvo.org to upgrade to a paid plan.\n\nRegards,\n\nThe FLOW Team",
                user.getUserName(), limiter.getHardLimit());

        TreeMap<String, String> recip = new TreeMap<>();
        recip.put("support@akvo.org", "support@akvo.org");
        recip.put(user.getEmailAddress(), user.getEmailAddress());
        MailUtil.sendMail("noreply@akvo.org", null, recip, subject,
                (count >= limiter.getHardLimit()) ? body_2 : body_1);
    }

    private Integer getFormSubmissionsLimit() {
        try {
            return Integer.valueOf(PropertyUtil.getProperty(FORM_SUBMISSIONS_LIMIT));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private Integer getFormSubmissionSoftLimitPercentage() {
        try {
            return Integer.valueOf(PropertyUtil.getProperty(FORM_SUBMISSIONS_SOFT_LIMIT_PERCENTAGE));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private HttpServletResponse prepareJsonResponse(HttpServletResponse response, int status, String jsonString) {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter out = response.getWriter();
            out.print(jsonString);
            out.flush();
        } catch (IOException e) {
        }
        return response;
    }
}
