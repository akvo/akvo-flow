/*
 *  Copyright (C) 2010-2017, 2019 Stichting Akvo (Akvo Foundation)
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

import com.gallatinsystems.common.domain.UploadStatusContainer;
import com.gallatinsystems.common.util.S3Util;
import com.gallatinsystems.common.util.ZipUtil;
import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;
import com.gallatinsystems.messaging.dao.MessageDao;
import com.gallatinsystems.messaging.domain.Message;
import com.gallatinsystems.operations.dao.ProcessingStatusDao;
import com.gallatinsystems.operations.domain.ProcessingStatus;
import com.gallatinsystems.survey.dao.*;
import com.gallatinsystems.survey.domain.*;
import com.gallatinsystems.survey.domain.Survey;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import org.akvo.flow.xml.PublishedForm;
import org.akvo.flow.xml.XmlForm;
import org.apache.log4j.Logger;
import org.waterforpeople.mapping.app.web.dto.SurveyAssemblyRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Random;

public class SurveyAssemblyServlet extends AbstractRestApiServlet {
    private static final Logger log = Logger
            .getLogger(SurveyAssemblyServlet.class.getName());

    private static final long serialVersionUID = -6044156962558183224L;
    public static final String FREE_QUESTION_TYPE = "free";
    public static final String OPTION_QUESTION_TYPE = "option";
    public static final String GEO_QUESTION_TYPE = "geo";
    public static final String VIDEO_QUESTION_TYPE = "video";
    public static final String PHOTO_QUESTION_TYPE = "photo";
    public static final String SCAN_QUESTION_TYPE = "scan";
    public static final String STRENGTH_QUESTION_TYPE = "strength";
    public static final String DATE_QUESTION_TYPE = "date";
    public static final String CASCADE_QUESTION_TYPE = "cascade";
    public static final String GEOSHAPE_QUESTION_TYPE = "geoshape";
    public static final String SIGNATURE_QUESTION_TYPE = "signature";
    public static final String CADDISFLY_QUESTION_TYPE = "caddisfly";

    private static final String SURVEY_UPLOAD_URL = "surveyuploadurl";
    private static final String SURVEY_UPLOAD_DIR = "surveyuploaddir";
    private static final String FORM_PUB_STATUS_KEY = "formPublication";


    private Random randomNumber = new Random();

    @Override
    protected RestRequest convertRequest() throws Exception {
        HttpServletRequest req = getRequest();
        RestRequest restRequest = new SurveyAssemblyRequest();
        restRequest.populateFromHttpRequest(req);
        return restRequest;
    }


    @Override
    protected RestResponse handleRequest(RestRequest req) throws Exception {
        RestResponse response = new RestResponse();
        SurveyAssemblyRequest importReq = (SurveyAssemblyRequest) req;
        if (SurveyAssemblyRequest.ASSEMBLE_SURVEY.equalsIgnoreCase(importReq.getAction())) {
            Date start = new Date();
            Long id = importReq.getSurveyId();
            //Instrumentation
            ProcessingStatusDao statusDao = new ProcessingStatusDao();
            ProcessingStatus status = statusDao.getStatusByCode(
                    FORM_PUB_STATUS_KEY + (id != null ? ":" + id : ""));
            if (status == null) {
                status = new ProcessingStatus();
                status.setCode(FORM_PUB_STATUS_KEY + (id != null ? ":" + id : ""));
                status.setMaxDurationMs(0L);
            }
            status.setLastEventDate(start);
            Long maxDuration = status.getMaxDurationMs();
            if (maxDuration == null) {
                maxDuration = 0L;
            }
            status.setInError(true); //In case it never saves an end sts
            status.setValue("inProgress");
            statusDao.save(status);

            //Need to keep this shorter than task queue limit (600 seconds)
            boolean ok = assembleFormWithJackson(importReq.getSurveyId());
            //Clear caches
            List<Long> ids = new ArrayList<Long>();
            ids.add(id);
            SurveyUtils.notifyReportService(ids, "invalidate");

            // now update the status
            status.setInError(ok);
            status.setValue("finished");
            Long duration = new Date().getTime() - start.getTime();
            if (duration > maxDuration) {
                status.setMaxDurationMs(duration);
                status.setMaxDurationDate(start);
            }
            statusDao.save(status);
        }

        return response;
    }

    // Manual triggering of publication should start here
    static public void runAsTask(Long surveyId) {
        log.info("Forking to task for long assembly");
        TaskOptions options = TaskOptions.Builder
                .withUrl("/app_worker/surveyassembly")
                .param(SurveyAssemblyRequest.ACTION_PARAM,
                        SurveyAssemblyRequest.ASSEMBLE_SURVEY)
                .param(SurveyAssemblyRequest.SURVEY_ID_PARAM, surveyId.toString());
        Queue queue = QueueFactory.getQueue("surveyAssembly");
        queue.add(options);

        Survey s = new SurveyDAO().getById(surveyId);
        SurveyGroup sg = s != null ? new SurveyGroupDAO().getByKey(s.getSurveyGroupId()) : null;
        if (sg != null && sg.getNewLocaleSurveyId() != null &&
                sg.getNewLocaleSurveyId().longValue() == surveyId.longValue()) {
            // This is the registration form. Schedule datapoint name re-assembly
            DataProcessorRestServlet.scheduleDatapointNameAssembly(sg.getKey().getId(), null);
        }

    }


    @Override
    protected void writeOkResponse(RestResponse resp) throws Exception {
        HttpServletResponse httpResp = getResponse();
        httpResp.setStatus(HttpServletResponse.SC_OK);
        // httpResp.setContentType("text/plain");
        httpResp.getWriter().print("OK");
        httpResp.flushBuffer();
    }

    /*
     * NEW! Assembly through Jackson classes.
     *
     */
    private boolean assembleFormWithJackson(Long formId) {
        log.debug("Starting Jackson assembly of form " + formId);
        SurveyDAO surveyDao = new SurveyDAO();
        Survey form = surveyDao.loadFullForm(formId);

        SurveyGroupDAO surveyGroupDao = new SurveyGroupDAO();
        SurveyGroup survey = surveyGroupDao.getByKey(form.getSurveyGroupId());
        Long transactionId = randomNumber.nextLong();

        XmlForm jacksonForm = new XmlForm(form, survey);
        String formXML;
        try {
            formXML = PublishedForm.generate(jacksonForm);
        } catch (IOException e) {
            log.error("Failed to convert form to XML: "+ e.getMessage());
            return false;
        }

        boolean uploadOk = false;
        log.debug("Uploading " + formId);
        UploadStatusContainer uc = uploadFormXML(
                Long.toString(formId), //latest version in plain filename
                Long.toString(formId) + "v" + form.getVersion(), //archive copy
                formXML.toString());
        Message message = new Message();
        message.setActionAbout("surveyAssembly");
        message.setObjectId(formId);
        message.setObjectTitle(survey.getCode() + " / " + form.getName());
        if (uc.getUploadedZip1() && uc.getUploadedZip2()) {
            log.debug("Finishing assembly of " + formId);
            form.setStatus(Survey.Status.PUBLISHED);
            surveyDao.save(form); //remember PUBLISHED status
            String messageText = "Published.  Please check: " + uc.getUrl();
            message.setShortMessage(messageText);
            message.setTransactionUUID(transactionId.toString());
            MessageDao messageDao = new MessageDao();
            messageDao.save(message);
            uploadOk = true;

            //invalidate any cached reports in flow-services
            List<Long> ids = new ArrayList<Long>();
            ids.add(formId);
            SurveyUtils.notifyReportService(ids, "invalidate");
        } else {
            String messageText = "Failed to publish: " + formId + "\n" + uc.getMessage();
            message.setTransactionUUID(transactionId.toString());
            message.setShortMessage(messageText);
            MessageDao messageDao = new MessageDao();
            messageDao.save(message);
            log.warn("Failed to upload assembled form, id " + formId + "\n"
                    + uc.getMessage());
        }
        log.debug("Completed form assembly for " + formId);
        return uploadOk;
    }

    /**  Upload a zipped form file twice to S3 under different filenames.
     * @param fileName1
     * @param fileName2
     * @param formXML
     * @return
     */
    public UploadStatusContainer uploadFormXML(String fileName1, String fileName2, String formXML) {
        Properties props = System.getProperties();
        String bucketName = props.getProperty("s3bucket");
        String directory = props.getProperty(SURVEY_UPLOAD_DIR);

        UploadStatusContainer uc = new UploadStatusContainer();
        uc.setUploadedZip1(uploadZippedXml(formXML, bucketName, directory, fileName1));
        uc.setUploadedZip2(uploadZippedXml(formXML, bucketName, directory, fileName2));
        uc.setUrl(props.getProperty(SURVEY_UPLOAD_URL)
                + props.getProperty(SURVEY_UPLOAD_DIR)
                + "/" + fileName1 + ".zip");
        return uc;
    }

    private boolean uploadZippedXml(String content, String bucketName, String directory, String fileName) {
        ByteArrayOutputStream os2 = ZipUtil.generateZip(content, fileName + ".xml");

        try {
           return S3Util.put(bucketName,
                    directory + "/" + fileName + ".zip",
                    os2.toByteArray(),
                    "application/zip",
                    true);
        } catch (IOException e) {
            log.error("Error uploading zipfile: " + e.getMessage(), e);
            return false;
        }
    }



}
