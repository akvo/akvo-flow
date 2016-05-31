/*
 *  Copyright (C) 2010-2015 Stichting Akvo (Akvo Foundation)
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

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.waterforpeople.mapping.app.web.dto.TaskRequest;

import com.gallatinsystems.device.dao.DeviceDAO;
import com.gallatinsystems.device.dao.DeviceFileJobQueueDAO;
import com.gallatinsystems.device.domain.Device;
import com.gallatinsystems.device.domain.DeviceFileJobQueue;
import com.gallatinsystems.messaging.dao.MessageDao;
import com.gallatinsystems.messaging.domain.Message;
import com.gallatinsystems.survey.dao.CascadeResourceDao;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.domain.CascadeResource;
import com.gallatinsystems.survey.domain.CascadeResource.Status;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

/**
 * Servlet used by app to trigger processing of new survey data TODO: move parameter name strings
 * into constants
 */
public class ProcessorServlet extends HttpServlet {

    private static final long serialVersionUID = -7062679258542909086L;
    private static final Logger log = Logger.getLogger(ProcessorServlet.class
            .getName());

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        if (req.getParameter("action") == null) {
            log.info("No action specified for processor");
            return;
        }

        String action = StringUtils.trim(req.getParameter("action"));
        String fileName = StringUtils.trim(req.getParameter("fileName"));
        String phoneNumber = StringUtils.trim(req.getParameter("phoneNumber"));
        String androidId = StringUtils.trim(req.getParameter("androidId"));
        String imei = StringUtils.trim(req.getParameter("imei"));
        String checksum = StringUtils.trim(req.getParameter("checksum"));

        log.info("	ProcessorServlet->action->" + action);

        if (action.equals("submit")) {
            if (fileName == null) {
                log.info("Request for processing without filename: phoneNumber=(" + phoneNumber
                        + "); IMEI=(" + imei + ")");
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            
            // If we get a form ID, ensure the form is still present in the datastore
            Long formID = parseFormID(req);
            if (formID != null) {
                SurveyDAO surveyDAO = new SurveyDAO();
                if (surveyDAO.getById(formID) == null) {
                    log.warning("Form " + formID + " doesn't exist in the datastore");
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
            }

            log.info("  ProcessorServlet->filename->" + fileName);
            log.info("about to submit task for fileName: " + fileName);
            // Submit the fileName for processing
            Queue queue = QueueFactory.getDefaultQueue();
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

        } else if (action.equals("image")) {
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
            List<DeviceFileJobQueue> missing = dfDao.listByDeviceAndFile(d
                    .getKey().getId(), fileName);
            log.info(String.format(
                    "Deleting %s entities matching the fileName %s",
                    missing.size(), fileName));
            dfDao.delete(missing);
        } else if (action.equals("cascade")) {
            Long crId = null;
            final String status = req.getParameter("status");
            final CascadeResourceDao crDao = new CascadeResourceDao();

            try {
                crId = Long.valueOf(req.getParameter("cascadeResourceId"));
            } catch (NumberFormatException e) {
            }

            if (crId == null || status == null) {
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
                m.setShortMessage("Failed to publish cascade resource " + cr.getName());
            }

            crDao.save(cr);
            mDao.save(m);

        }

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
    
}
