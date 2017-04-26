/*
 *  Copyright (C) 2010-2017 Stichting Akvo (Akvo Foundation)
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
import com.gallatinsystems.common.util.PropertyUtil;
import com.gallatinsystems.common.util.S3Util;
import com.gallatinsystems.common.util.ZipUtil;
import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;
import com.gallatinsystems.messaging.dao.MessageDao;
import com.gallatinsystems.messaging.domain.Message;
import com.gallatinsystems.survey.dao.*;
import com.gallatinsystems.survey.domain.*;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.QuestionGroup;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.survey.domain.SurveyXMLFragment.FRAGMENT_TYPE;
import com.gallatinsystems.survey.domain.xml.*;
import com.gallatinsystems.survey.xml.SurveyXMLAdapter;
import com.google.appengine.api.backends.BackendServiceFactory;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.utils.SystemProperty;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.waterforpeople.mapping.app.web.dto.SurveyAssemblyRequest;
import org.waterforpeople.mapping.dao.SurveyContainerDao;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

public class SurveyAssemblyServlet extends AbstractRestApiServlet {
    private static final Logger log = Logger
            .getLogger(SurveyAssemblyServlet.class.getName());

    private static final int BACKEND_QUESTION_THRESHOLD = 80;
    private static final String BACKEND_PUBLISH_PROP = "backendpublish";
    private static final long serialVersionUID = -6044156962558183224L;
    private static final String OPTION_RENDER_MODE_PROP = "optionRenderMode";
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
        if (SurveyAssemblyRequest.ASSEMBLE_SURVEY.equalsIgnoreCase(importReq
                .getAction())) {

            QuestionDao questionDao = new QuestionDao();
            boolean useBackend = false;
            // make sure we're not already running on a backend and that we are
            // allowed to use one
            if (!importReq.getIsForwarded()
                    && "true".equalsIgnoreCase(PropertyUtil
                            .getProperty(BACKEND_PUBLISH_PROP))) {
                // if we're allowed to use a backend, then check to see if we
                // need to (based on survey size)
                List<Question> questionList = questionDao
                        .listQuestionsBySurvey(importReq.getSurveyId());
                if (questionList != null
                        && questionList.size() > BACKEND_QUESTION_THRESHOLD) {
                    useBackend = true;
                }
            }
            if (useBackend) {
                com.google.appengine.api.taskqueue.TaskOptions options = com.google.appengine.api.taskqueue.TaskOptions.Builder
                        .withUrl("/app_worker/surveyassembly")
                        .param(SurveyAssemblyRequest.ACTION_PARAM,
                                SurveyAssemblyRequest.ASSEMBLE_SURVEY)
                        .param(SurveyAssemblyRequest.IS_FWD_PARAM, "true")
                        .param(SurveyAssemblyRequest.SURVEY_ID_PARAM,
                                importReq.getSurveyId().toString());
                // change the host so the queue invokes the backend
                options = options
                        .header("Host",
                                BackendServiceFactory.getBackendService()
                                        .getBackendAddress("dataprocessor"));
                com.google.appengine.api.taskqueue.Queue queue = com.google.appengine.api.taskqueue.QueueFactory
                        .getQueue("surveyAssembly");
                queue.add(options);
            } else {
                assembleSurveyOnePass(importReq.getSurveyId());
            }

            List<Long> ids = new ArrayList<Long>();
            ids.add(importReq.getSurveyId());
            SurveyUtils.notifyReportService(ids, "invalidate");

        } else if (SurveyAssemblyRequest.DISPATCH_ASSEMBLE_QUESTION_GROUP
                .equalsIgnoreCase(importReq.getAction())) {
            this.dispatchAssembleQuestionGroup(importReq.getSurveyId(),
                    importReq.getQuestionGroupId(),
                    importReq.getTransactionId());
        } else if (SurveyAssemblyRequest.ASSEMBLE_QUESTION_GROUP
                .equalsIgnoreCase(importReq.getAction())) {
            assembleQuestionGroups(importReq.getSurveyId(),
                    importReq.getTransactionId());
        } else if (SurveyAssemblyRequest.DISTRIBUTE_SURVEY
                .equalsIgnoreCase(importReq.getAction())) {
            uploadSurvey(importReq.getSurveyId(), importReq.getTransactionId());
        } else if (SurveyAssemblyRequest.CLEANUP.equalsIgnoreCase(importReq
                .getAction())) {
            cleanupFragments(importReq.getSurveyId(),
                    importReq.getTransactionId());
        }

        return response;
    }

    /**
     * uploads full survey XML to S3
     *
     * @param surveyId
     */
    private void uploadSurvey(Long surveyId, Long transactionId) {
        SurveyContainerDao scDao = new SurveyContainerDao();
        SurveyContainer sc = scDao.findBySurveyId(surveyId);
        Properties props = System.getProperties();
        String document = sc.getSurveyDocument().getValue();
        String bucketName = props.getProperty("s3bucket");
        boolean uploadedFile = false;
        boolean uploadedZip = false;

        try {
            uploadedFile = S3Util.put(bucketName,
                    props.getProperty(SURVEY_UPLOAD_DIR) + "/" + sc.getSurveyId() + ".xml",
                    document.getBytes("UTF-8"), "text/xml", true);
        } catch (IOException e) {
            log.error("Error uploading file " + e.getMessage(), e);
        }

        ByteArrayOutputStream os = ZipUtil.generateZip(document,
                sc.getSurveyId() + ".xml");

        try {
            uploadedZip = S3Util.put(bucketName,
                    props.getProperty(SURVEY_UPLOAD_DIR) + "/" + sc.getSurveyId() + ".zip",
                    os.toByteArray(),
                    "application/zip", true);
        } catch (Exception e) {
            log.error("Error uploading zip file: " + e.getMessage(), e);
        }

        sendQueueMessage(SurveyAssemblyRequest.CLEANUP, surveyId, null,
                transactionId);

        Message message = new Message();
        message.setActionAbout("surveyAssembly");
        message.setObjectId(surveyId);

        if (uploadedFile && uploadedZip) {
            // increment the version so devices know to pick up the changes
            SurveyDAO surveyDao = new SurveyDAO();

            String messageText = "Published.  Please check: "
                    + props.getProperty(SURVEY_UPLOAD_URL)
                    + props.getProperty(SURVEY_UPLOAD_DIR) + "/" + surveyId
                    + ".xml";
            message.setShortMessage(messageText);

            Survey s = surveyDao.getById(surveyId);
            if (s != null) {
                message.setObjectTitle(s.getPath() + "/" + s.getName());
            }

            message.setTransactionUUID(transactionId.toString());
            MessageDao messageDao = new MessageDao();
            messageDao.save(message);
        } else {
            String messageText = "Failed to publish: " + surveyId + "\n";
            message.setTransactionUUID(transactionId.toString());
            message.setShortMessage(messageText);
            MessageDao messageDao = new MessageDao();
            messageDao.save(message);
        }
    }

    /**
     * deletes fragments for the survey
     *
     * @param surveyId
     */
    private void cleanupFragments(Long surveyId, Long transactionId) {
        SurveyXMLFragmentDao sxmlfDao = new SurveyXMLFragmentDao();
        sxmlfDao.deleteFragmentsForSurvey(surveyId, transactionId);
    }

    @Override
    protected void writeOkResponse(RestResponse resp) throws Exception {
        HttpServletResponse httpResp = getResponse();
        httpResp.setStatus(HttpServletResponse.SC_OK);
        // httpResp.setContentType("text/plain");
        httpResp.getWriter().print("OK");
        httpResp.flushBuffer();
    }

    private void assembleSurveyOnePass(Long surveyId) {
        /**************
         * 1, Select survey based on surveyId 2. Retrieve all question groups fire off queue tasks
         */
        log.warn("Starting assembly of " + surveyId);
        // Swap with proper UUID
        SurveyDAO surveyDao = new SurveyDAO();
        Survey s = surveyDao.getById(surveyId);
        SurveyGroupDAO surveyGroupDao = new SurveyGroupDAO();
        SurveyGroup sg = surveyGroupDao.getByKey(s.getSurveyGroupId());
        Long transactionId = randomNumber.nextLong();
        String lang = "en";
        if (s != null && s.getDefaultLanguageCode() != null) {
            lang = s.getDefaultLanguageCode();
        }
        final String versionAttribute = s.getVersion() == null ? "" : "version='"
                + s.getVersion() + "'";
        final String app = String.format("app=\"%s\"",
                StringEscapeUtils.escapeXml(SystemProperty.applicationId.get()));
        String name = s.getName();
        String surveyGroupId = "";
        String surveyGroupName = "";
        String registrationForm = "";
        String surveyIdKeyValue = "surveyId=\""+surveyId+"\"";
        if (sg != null) {
            surveyGroupId = "surveyGroupId=\"" + sg.getKey().getId() + "\"";
            surveyGroupName = "surveyGroupName=\"" + StringEscapeUtils.escapeXml(sg.getCode())
                    + "\"";
            if (Boolean.TRUE.equals(sg.getMonitoringGroup())) {
                registrationForm = " registrationSurvey=\""
                        + sg.getNewLocaleSurveyId() + "\"";
            }
        }
        String surveyHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><survey"
                + " name=\"" + StringEscapeUtils.escapeXml(name)
                + "\"" + " defaultLanguageCode=\"" + lang + "\" " + versionAttribute + " " + app
                + registrationForm + " " + surveyGroupId + " " + surveyGroupName + " "
                + surveyIdKeyValue + ">";
        String surveyFooter = "</survey>";
        QuestionGroupDao qgDao = new QuestionGroupDao();
        TreeMap<Integer, QuestionGroup> qgList = qgDao
                .listQuestionGroupsBySurvey(surveyId);
        if (qgList != null) {
            StringBuilder surveyXML = new StringBuilder();
            surveyXML.append(surveyHeader);
            for (QuestionGroup item : qgList.values()) {
                log.warn("Assembling group " + item.getKey().getId()
                        + " for survey " + surveyId);
                surveyXML.append(buildQuestionGroupXML(item));
            }

            surveyXML.append(surveyFooter);
            log.warn("Uploading " + surveyId);
            UploadStatusContainer uc = uploadSurveyXML(surveyId,
                    surveyXML.toString());
            Message message = new Message();
            message.setActionAbout("surveyAssembly");
            message.setObjectId(surveyId);
            message.setObjectTitle(sg.getCode() + " / " + s.getName());
            // String messageText = CONSTANTS.surveyPublishOkMessage() + " "
            // + url;
            if (uc.getUploadedFile() && uc.getUploadedZip()) {
                // increment the version so devices know to pick up the changes
                log.warn("Finishing assembly of " + surveyId);
                s.setStatus(Survey.Status.PUBLISHED);
                surveyDao.save(s);
                String messageText = "Published.  Please check: " + uc.getUrl();
                message.setShortMessage(messageText);
                message.setTransactionUUID(transactionId.toString());
                MessageDao messageDao = new MessageDao();
                messageDao.save(message);
            } else {
                // String messageText =
                // CONSTANTS.surveyPublishErrorMessage();
                String messageText = "Failed to publish: " + surveyId + "\n"
                        + uc.getMessage();
                message.setTransactionUUID(transactionId.toString());
                message.setShortMessage(messageText);
                MessageDao messageDao = new MessageDao();
                messageDao.save(message);
            }
            log.warn("Completed onepass assembly method for " + surveyId);
        }
    }

    public UploadStatusContainer uploadSurveyXML(Long surveyId, String surveyXML) {
        Properties props = System.getProperties();
        String bucketName = props.getProperty("s3bucket");
        String document = surveyXML;
        boolean uploadedFile = false;
        boolean uploadedZip = false;

        try {
            uploadedFile = S3Util.put(bucketName, props.getProperty(SURVEY_UPLOAD_DIR) + "/"
                    + surveyId
                    + ".xml", document.getBytes("UTF-8"), "text/xml", true);
        } catch (IOException e) {
            log.error("Error uploading file: " + e.getMessage(), e);
        }

        ByteArrayOutputStream os = ZipUtil.generateZip(document, surveyId
                + ".xml");

        UploadStatusContainer uc = new UploadStatusContainer();

        try {
            uploadedZip = S3Util.put(bucketName, props.getProperty(SURVEY_UPLOAD_DIR) + "/"
                    + surveyId + ".zip", os.toByteArray(), "application/zip", true);
        } catch (IOException e) {
            log.error("Error uploading file: " + e.getMessage(), e);
        }
        uc.setUploadedFile(uploadedFile);
        uc.setUploadedZip(uploadedZip);
        uc.setUrl(props.getProperty(SURVEY_UPLOAD_URL)
                + props.getProperty(SURVEY_UPLOAD_DIR) + "/" + surveyId
                + ".xml");
        return uc;
    }

    public String buildQuestionGroupXML(QuestionGroup item) {
        QuestionDao questionDao = new QuestionDao();
        QuestionGroupDao questionGroupDao = new QuestionGroupDao();
        QuestionGroup group = questionGroupDao.getByKey(item.getKey().getId());
        TreeMap<Integer, Question> questionList = questionDao
                .listQuestionsByQuestionGroup(item.getKey().getId(), true);

        StringBuilder sb = new StringBuilder("<questionGroup")
                .append(Boolean.TRUE.equals(group.getRepeatable()) ? " repeatable=\"true\"" : "")
                .append("><heading>").append(StringEscapeUtils.escapeXml(group.getCode()))
                .append("</heading>");

        if (questionList != null) {
            for (Question q : questionList.values()) {
                sb.append(marshallQuestion(q));
            }
        }
        return sb.toString() + "</questionGroup>";
    }

    /**
     * sends a message to the task queue for survey assembly
     *
     * @param action
     * @param surveyId
     * @param questionGroups
     */
    private void sendQueueMessage(String action, Long surveyId,
            String questionGroups, Long transactionId) {
        Queue surveyAssemblyQueue = QueueFactory.getQueue("surveyAssembly");
        TaskOptions task = TaskOptions.Builder.withUrl("/app_worker/surveyassembly")
                .param("action",
                        action).param("surveyId", surveyId.toString());
        if (questionGroups != null) {
            task.param("questionGroupId", questionGroups);
        }
        if (transactionId != null) {
            task.param("transactionId", transactionId.toString());
        }
        surveyAssemblyQueue.add(task);
    }

    private void dispatchAssembleQuestionGroup(Long surveyId,
            String questionGroupIds, Long transactionId) {
        boolean isLast = true;
        String currentId = questionGroupIds;
        String remainingIds = null;
        if (questionGroupIds.contains(",")) {
            isLast = false;
            currentId = questionGroupIds.substring(0,
                    questionGroupIds.indexOf(","));
            remainingIds = questionGroupIds.substring(questionGroupIds
                    .indexOf(",") + 1);
        }

        QuestionDao questionDao = new QuestionDao();
        QuestionGroupDao questionGroupDao = new QuestionGroupDao();
        QuestionGroup group = questionGroupDao.getByKey(Long
                .parseLong(currentId));
        TreeMap<Integer, Question> questionList = questionDao
                .listQuestionsByQuestionGroup(Long.parseLong(currentId), true);

        StringBuilder sb = new StringBuilder("<questionGroup")
                .append(Boolean.TRUE.equals(group.getRepeatable()) ? " repeatable=\"true\"" : "")
                .append("><heading>").append(group.getCode()).append("</heading>");

        if (questionList != null) {
            for (Question q : questionList.values()) {
                sb.append(marshallQuestion(q));
            }
        }
        SurveyXMLFragment sxf = new SurveyXMLFragment();
        sxf.setSurveyId(surveyId);
        sxf.setQuestionGroupId(Long.parseLong(currentId));
        sxf.setFragmentOrder(group.getOrder());
        sxf.setFragment(new Text(sb.append("</questionGroup>").toString()));
        sxf.setTransactionId(transactionId);

        sxf.setFragmentType(FRAGMENT_TYPE.QUESTION_GROUP);
        SurveyXMLFragmentDao sxmlfDao = new SurveyXMLFragmentDao();
        sxmlfDao.save(sxf);
        if (isLast) {
            // Assemble the fragments
            sendQueueMessage(SurveyAssemblyRequest.ASSEMBLE_QUESTION_GROUP,
                    surveyId, null, transactionId);

        } else {
            sendQueueMessage(
                    SurveyAssemblyRequest.DISPATCH_ASSEMBLE_QUESTION_GROUP,
                    surveyId, remainingIds, transactionId);
        }
    }

    private String marshallQuestion(Question q) {

        SurveyXMLAdapter sax = new SurveyXMLAdapter();
        ObjectFactory objFactory = new ObjectFactory();
        com.gallatinsystems.survey.domain.xml.Question qXML = objFactory
                .createQuestion();
        qXML.setId(new String("" + q.getKey().getId() + ""));
        // ToDo fix
        qXML.setMandatory("false");
        if (q.getText() != null) {
            com.gallatinsystems.survey.domain.xml.Text t = new com.gallatinsystems.survey.domain.xml.Text();
            t.setContent(q.getText());
            qXML.setText(t);
        }
        List<Help> helpList = new ArrayList<Help>();
        // this is here for backward compatibility
        // however, we don't use the helpMedia at the moment
        if (q.getTip() != null) {
            Help tip = new Help();
            com.gallatinsystems.survey.domain.xml.Text t = new com.gallatinsystems.survey.domain.xml.Text();
            t.setContent(q.getTip());
            tip.setText(t);
            tip.setType("tip");
            if (q.getTip() != null && q.getTip().trim().length() > 0
                    && !"null".equalsIgnoreCase(q.getTip().trim())) {

                TranslationDao tDao = new TranslationDao();
                Map<String, Translation> tipTrans = tDao.findTranslations(
                        Translation.ParentType.QUESTION_TIP, q.getKey().getId());
                // any translations for question tooltip?

                List<AltText> translationList = new ArrayList<AltText>();
                for (Translation trans : tipTrans
                        .values()) {
                    AltText aText = new AltText();
                    aText.setContent(trans.getText());
                    aText.setLanguage(trans.getLanguageCode());
                    aText.setType("translation");
                    translationList.add(aText);
                }
                if (translationList.size() > 0) {
                    tip.setAltText(translationList);
                }
                helpList.add(tip);
            }
        }
        if (q.getQuestionHelpMediaMap() != null) {
            for (QuestionHelpMedia helpItem : q.getQuestionHelpMediaMap()
                    .values()) {
                Help tip = new Help();
                com.gallatinsystems.survey.domain.xml.Text t = new com.gallatinsystems.survey.domain.xml.Text();
                t.setContent(helpItem.getText());
                if (helpItem.getType() == QuestionHelpMedia.Type.TEXT) {
                    tip.setType("tip");
                } else {
                    tip.setType(helpItem.getType().toString().toLowerCase());
                    tip.setValue(helpItem.getResourceUrl());
                }
                if (helpItem.getTranslationMap() != null) {
                    List<AltText> translationList = new ArrayList<AltText>();
                    for (Translation trans : helpItem.getTranslationMap()
                            .values()) {
                        AltText aText = new AltText();
                        aText.setContent(trans.getText());
                        aText.setLanguage(trans.getLanguageCode());
                        aText.setType("translation");
                        translationList.add(aText);
                    }
                    if (translationList.size() > 0) {
                        tip.setAltText(translationList);
                    }
                }
                helpList.add(tip);
            }
        }
        if (helpList.size() > 0) {
            qXML.setHelp(helpList);
        }

        boolean hasValidation = false;
       if (q.getType() == Question.Type.NUMBER
                && (q.getAllowDecimal() != null || q.getAllowSign() != null
                        || q.getMinVal() != null || q.getMaxVal() != null)) {
            ValidationRule validationRule = objFactory.createValidationRule();
            validationRule.setValidationType("numeric");
            validationRule.setAllowDecimal(q.getAllowDecimal() != null ? q
                    .getAllowDecimal().toString().toLowerCase() : "false");
            validationRule.setSigned(q.getAllowSign() != null ? q
                    .getAllowSign().toString().toLowerCase() : "false");
            if (q.getMinVal() != null) {
                validationRule.setMinVal(q.getMinVal().toString());
            }
            if (q.getMaxVal() != null) {
                validationRule.setMaxVal(q.getMaxVal().toString());
            }
            qXML.setValidationRule(validationRule);
            hasValidation = true;
        }

        qXML.setAltText(formAltText(q.getTranslationMap()));

        if (q.getType().equals(Question.Type.FREE_TEXT)) {
            qXML.setType(FREE_QUESTION_TYPE);
            // add requireDoubleEntry flag if the field is true in the question
            if (q.getRequireDoubleEntry() != null && q.getRequireDoubleEntry()) {
                qXML.setRequireDoubleEntry(q.getRequireDoubleEntry().toString());
            }
        } else if (q.getType().equals(Question.Type.GEO)) {
            qXML.setType(GEO_QUESTION_TYPE);
        } else if (q.getType().equals(Question.Type.NUMBER)) {
            qXML.setType(FREE_QUESTION_TYPE);
            if (!hasValidation) {
                ValidationRule vrule = new ValidationRule();
                vrule.setValidationType("numeric");
                vrule.setSigned("false");
                qXML.setValidationRule(vrule);
            }
            // add requireDoubleEntry flag if the field is true in the question
            if (q.getRequireDoubleEntry() != null && q.getRequireDoubleEntry()) {
                qXML.setRequireDoubleEntry(q.getRequireDoubleEntry().toString());
            }
        } else if (q.getType().equals(Question.Type.OPTION)) {
            qXML.setType(OPTION_QUESTION_TYPE);
        } else if (q.getType().equals(Question.Type.PHOTO)) {
            qXML.setType(PHOTO_QUESTION_TYPE);
        } else if (q.getType().equals(Question.Type.VIDEO)) {
            qXML.setType(VIDEO_QUESTION_TYPE);
        } else if (q.getType().equals(Question.Type.SCAN)) {
            qXML.setType(SCAN_QUESTION_TYPE);
        } else if (q.getType().equals(Question.Type.STRENGTH)) {
            qXML.setType(STRENGTH_QUESTION_TYPE);
        } else if (q.getType().equals(Question.Type.DATE)) {
            qXML.setType(DATE_QUESTION_TYPE);
        } else if (q.getType().equals(Question.Type.CASCADE)) {
            qXML.setType(CASCADE_QUESTION_TYPE);
        } else if (q.getType().equals(Question.Type.GEOSHAPE)) {
            qXML.setType(GEOSHAPE_QUESTION_TYPE);
            qXML.setAllowPoints(Boolean.toString(q.getAllowPoints()));
            qXML.setAllowLine(Boolean.toString(q.getAllowLine()));
            qXML.setAllowPolygon(Boolean.toString(q.getAllowPolygon()));
        } else if (q.getType().equals(Question.Type.SIGNATURE)) {
            qXML.setType(SIGNATURE_QUESTION_TYPE);
        } else if (q.getType().equals(Question.Type.CADDISFLY)) {
            qXML.setType(CADDISFLY_QUESTION_TYPE);
        }

        if (q.getType().equals(Question.Type.CADDISFLY) && q.getCaddisflyResourceUuid() != null) {
            qXML.setCaddisflyResourceUuid(q.getCaddisflyResourceUuid());
        }

        if (q.getType().equals(Question.Type.CASCADE) && q.getCascadeResourceId() != null) {
            CascadeResourceDao crDao = new CascadeResourceDao();
            CascadeResource cr = crDao.getByKey(q.getCascadeResourceId());
            if (cr != null) {
                qXML.setCascadeResource(cr.getResourceId());
                List<String> levelNames = cr.getLevelNames();
                if (levelNames != null && levelNames.size() > 0) {
                    Levels levels = objFactory.createLevels();
                    ArrayList<Level> levelList = new ArrayList<Level>();
                    for (int i = 0; i < cr.getNumLevels(); i++) {
                        Level levelItem = objFactory.createLevel();
                        com.gallatinsystems.survey.domain.xml.Text t = new com.gallatinsystems.survey.domain.xml.Text();
                        t.setContent(levelNames.get(i));
                        levelItem.addContent(t);
                        levelList.add(levelItem);
                        // TODO sort out translations
                    }
                    levels.setLevel(levelList);
                    qXML.setLevels(levels);
                }
            }
        }

        if (q.getOrder() != null) {
            qXML.setOrder(q.getOrder().toString());
        }
        if (q.getMandatoryFlag() != null) {
            qXML.setMandatory(q.getMandatoryFlag().toString());
        }
        qXML.setLocaleNameFlag("false");
        if (q.getLocaleNameFlag() != null) {
            qXML.setLocaleNameFlag(q.getLocaleNameFlag().toString());
        }
        if (q.getLocaleLocationFlag() != null) {
            if (q.getLocaleLocationFlag()) {
                qXML.setLocaleLocationFlag("true");
            }
        }
        if (Boolean.TRUE.equals(q.getAllowMultipleFlag())) {
            qXML.setAllowMultiple("true");
        }
        // Both GEO and GEOSHAPE question types can block manual input
        if (Boolean.TRUE.equals(q.getGeoLocked())) {
            qXML.setLocked("true");
        }
        Dependency dependency = objFactory.createDependency();
        if (q.getDependentQuestionId() != null) {
            dependency.setQuestion(q.getDependentQuestionId().toString());
            dependency.setAnswerValue(q.getDependentQuestionAnswer());
            qXML.setDependency(dependency);
        }

        if (q.getQuestionOptionMap() != null
                && q.getQuestionOptionMap().size() > 0) {
            Options options = objFactory.createOptions();
            if (q.getAllowOtherFlag() != null) {
                options.setAllowOther(q.getAllowOtherFlag().toString());
            }
            if (q.getAllowMultipleFlag() != null) {
                options.setAllowMultiple(q.getAllowMultipleFlag().toString());
            }
            if (options.getAllowMultiple() == null
                    || "false".equals(options.getAllowMultiple())) {
                options.setRenderType(PropertyUtil
                        .getProperty(OPTION_RENDER_MODE_PROP));
            }

            ArrayList<Option> optionList = new ArrayList<Option>();
            for (QuestionOption qo : q.getQuestionOptionMap().values()) {
                Option option = objFactory.createOption();
                com.gallatinsystems.survey.domain.xml.Text t = new com.gallatinsystems.survey.domain.xml.Text();
                t.setContent(qo.getText());
                option.addContent(t);
                option.setCode(qo.getCode());

                // to maintain backwards compatibility with older app versions, we set the value
                // attribute and text to the same
                option.setValue(qo.getText());
                List<AltText> altTextList = formAltText(qo.getTranslationMap());
                if (altTextList != null) {
                    for (AltText alt : altTextList) {
                        option.addContent(alt);
                    }
                }
                optionList.add(option);
            }
            options.setOption(optionList);

            qXML.setOptions(options);
        }

        if (q.getScoringRules() != null) {
            Scoring scoring = new Scoring();

            for (ScoringRule rule : q.getScoringRules()) {
                Score score = new Score();
                if (scoring.getType() == null) {
                    scoring.setType(rule.getType().toLowerCase());
                }
                score.setRangeHigh(rule.getRangeMax());
                score.setRangeLow(rule.getRangeMin());
                score.setValue(rule.getValue());
                scoring.addScore(score);
            }
            if (scoring.getScore() != null && scoring.getScore().size() > 0) {
                qXML.setScoring(scoring);
            }
        }

        if ("true".equalsIgnoreCase(String.valueOf(q.getAllowExternalSources()))) {
            qXML.setAllowExternalSources(String.valueOf(q.getAllowExternalSources()));
        }

        String questionDocument = null;
        try {
            questionDocument = sax.marshal(qXML);
        } catch (JAXBException e) {
            log.warn("Could not marshal question: " + qXML, e);
        }

        questionDocument = questionDocument
                .replace(
                        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>",
                        "");
        return questionDocument;
    }

    private List<AltText> formAltText(Map<String, Translation> translationMap) {
        List<AltText> altTextList = new ArrayList<AltText>();
        if (translationMap != null) {
            for (Translation lang : translationMap.values()) {
                AltText alt = new AltText();
                alt.setContent(lang.getText());
                alt.setType("translation");
                alt.setLanguage(lang.getLanguageCode());
                altTextList.add(alt);
            }
        }

        return altTextList;
    }

    private void assembleQuestionGroups(Long surveyId, Long transactionId) {
        SurveyXMLFragmentDao sxmlfDao = new SurveyXMLFragmentDao();
        List<SurveyXMLFragment> sxmlfList = sxmlfDao.listSurveyFragments(
                surveyId, SurveyXMLFragment.FRAGMENT_TYPE.QUESTION_GROUP,
                transactionId);
        StringBuilder sbQG = new StringBuilder();
        for (SurveyXMLFragment item : sxmlfList) {
            sbQG.append(item.getFragment().getValue());
        }
        StringBuilder completeSurvey = new StringBuilder();
        String surveyHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><survey>";
        String surveyFooter = "</survey>";
        completeSurvey.append(surveyHeader);
        completeSurvey.append(sbQG.toString());
        sbQG = null;
        completeSurvey.append(surveyFooter);

        SurveyContainerDao scDao = new SurveyContainerDao();
        SurveyContainer sc = scDao.findBySurveyId(surveyId);
        if (sc == null) {
            sc = new SurveyContainer();
        }
        sc.setSurveyDocument(new Text(completeSurvey.toString()));
        sc.setSurveyId(surveyId);

        scDao.save(sc);

        sendQueueMessage(SurveyAssemblyRequest.DISTRIBUTE_SURVEY, surveyId,
                null, transactionId);
    }
}
