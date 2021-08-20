/*
 *  Copyright (C) 2010-2015, 2019, 2021 Stichting Akvo (Akvo Foundation)
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.akvo.flow.dao.MessageDao;
import org.akvo.flow.domain.Message;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto.QuestionType;
import org.waterforpeople.mapping.app.web.dto.DataProcessorRequest;
import org.waterforpeople.mapping.app.web.dto.RawDataImportRequest;
import org.waterforpeople.mapping.dao.QuestionAnswerStoreDao;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.QuestionAnswerStore;
import org.waterforpeople.mapping.domain.SurveyInstance;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;
import com.gallatinsystems.survey.dao.QuestionDao;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.dao.SurveyGroupDAO;
import com.gallatinsystems.survey.dao.SurveyUtils;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.Question.Type;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.survey.domain.SurveyGroup;
import com.gallatinsystems.surveyal.app.web.SurveyalRestRequest;
import com.gallatinsystems.surveyal.dao.SurveyedLocaleDao;
import com.gallatinsystems.surveyal.domain.SurveyedLocale;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

public class RawDataRestServlet extends AbstractRestApiServlet {
    private static final Logger log = Logger.getLogger("RawDataRestServlet");
    private static final long serialVersionUID = 2409014651721639814L;

    private static final String DATA_SUMMARIZATION_QUEUE = "dataSummarization";
    private static final String DATA_SUMMARIZATION_URL = "/app_worker/datasummarization";
    private static final String SURVEYAL_URL = "/app_worker/surveyalservlet";
    private static final String OBJECT_KEY_PARAM = "objectKey";
    private static final String TYPE_PARAM = "type";
    private static final String SI_TYPE = "SurveyInstance";

    private SurveyInstanceDAO instanceDao;
    private QuestionAnswerStoreDao qasDao;
    private SurveyedLocaleDao slDao;
    private QuestionDao qDao;

    public RawDataRestServlet() {
        instanceDao = new SurveyInstanceDAO();
        qasDao = new QuestionAnswerStoreDao();
        slDao = new SurveyedLocaleDao();
        qDao = new QuestionDao();
    }

    @Override
    protected RestRequest convertRequest() throws Exception {
        HttpServletRequest req = getRequest();
        RestRequest restRequest = new RawDataImportRequest();
        restRequest.populateFromHttpRequest(req);
        return restRequest;
    }

    @Override
    protected RestResponse handleRequest(RestRequest req) throws Exception {
        RawDataImportRequest importReq = (RawDataImportRequest) req;
        if (RawDataImportRequest.SAVE_SURVEY_INSTANCE_ACTION.equals(importReq.getAction())) {

            Survey s = importReq.getForm();

            SurveyGroup sg = importReq.getSurvey();

            SurveyInstance instance = null;
            if (importReq.isNewFormInstance()) {
                instance = createInstance(importReq);
            } else {
                instance = importReq.getFormInstance();
            }

            SurveyedLocale dataPoint = null;
            if (importReq.isNewFormInstance()) {
                // create new surveyed locale and launch task to complete processing
                dataPoint.setIdentifier(SurveyedLocale.generateBase32Uuid());
                instance.setSurveyedLocaleIdentifier(dataPoint.getIdentifier());

                dataPoint.setSurveyGroupId(sg.getKey().getId());
                dataPoint.setCreationSurveyId(s.getKey().getId());

                dataPoint = slDao.save(dataPoint);
                instance.setSurveyedLocaleId(dataPoint.getKey().getId());
                instanceDao.save(instance);
            } else {
                dataPoint = importReq.getDataPoint();
            }

            // questionId -> iteration -> QAS
            Map<Long, Map<Integer, QuestionAnswerStore>> existingAnswers = qasDao
                    .mapByQuestionIdAndIteration(qasDao.listBySurveyInstance(instance.getKey()
                            .getId()));

            Map<Long, Map<Integer, String[]>> incomingResponses = importReq.getResponseMap();

            if (incomingResponses.isEmpty()) {
                log.log(Level.WARNING, "incomingResponses is empty");
            }

            List<QuestionAnswerStore> updatedAnswers = new ArrayList<QuestionAnswerStore>();

            for (Entry<Long, Map<Integer, String[]>> responseEntry : incomingResponses
                    .entrySet()) {
                Long questionId = responseEntry.getKey();
                Map<Integer, String[]> iterationMap = responseEntry.getValue();

                if (iterationMap.isEmpty()) {
                    log.log(Level.WARNING, "iterationMap is empty");
                }

                for (Entry<Integer, String[]> iterationEntry : iterationMap.entrySet()) {
                    Integer iteration = iterationEntry.getKey();
                    String response = iterationEntry.getValue()[0];
                    String type = iterationEntry.getValue()[1];
                    QuestionAnswerStore answer = null;
                    if (existingAnswers.containsKey(questionId)) {
                        if (existingAnswers.get(questionId).containsKey(iteration)) {
                            answer = existingAnswers.get(questionId).get(iteration);
                        }
                    }

                    // New answer/iteration
                    if (answer == null) {
                        answer = new QuestionAnswerStore();
                        answer.setQuestionID(questionId.toString());
                        answer.setSurveyInstanceId(instance.getKey().getId());
                        answer.setSurveyId(s.getKey().getId());
                        answer.setCollectionDate(instance.getCollectionDate());
                        answer.setType(type);
                        answer.setIteration(iteration);
                    }

                    answer.setValue(response);
                    updatedAnswers.add(answer);
                }
            }

            log.log(Level.INFO, "Updating " + updatedAnswers.size() + " question answers");
            qasDao.save(updatedAnswers);

            // remove entities with no updated response
            List<QuestionAnswerStore> deletedAnswers = new ArrayList<QuestionAnswerStore>();

            for (Long questionId : existingAnswers.keySet()) {
                for (Integer iteration : existingAnswers.get(questionId).keySet()) {
                    if (incomingResponses.containsKey(questionId)) {
                        if (!incomingResponses.get(questionId).containsKey(iteration)) {
                            // Iteration has been deleted
                            deletedAnswers.add(existingAnswers.get(questionId).get(iteration));
                        }
                    }
                }
            }
            log.log(Level.INFO, "Deleting " + deletedAnswers.size() + " question answers");
            qasDao.delete(deletedAnswers);

            if (importReq.isRegistrationForm()) {
                // Update datapoint name and location for this locale
                dataPoint.assembleDisplayName(
                        qDao.listDisplayNameQuestionsBySurveyId(s.getKey().getId()), updatedAnswers);

                updateDataPointLocation(dataPoint, updatedAnswers);

                slDao.save(dataPoint);
            }

            if (importReq.isNewFormInstance()) {
                Queue defaultQueue = QueueFactory.getDefaultQueue();
                TaskOptions processNewInstanceOptions = TaskOptions.Builder
                        .withUrl("/app_worker/surveyalservlet")
                        .param(SurveyalRestRequest.ACTION_PARAM,
                                SurveyalRestRequest.INGEST_INSTANCE_ACTION)
                        .param(SurveyalRestRequest.SURVEY_INSTANCE_PARAM,
                                Long.toString(instance.getKey().getId()))
                        .countdownMillis(Constants.TASK_DELAY);
                defaultQueue.add(processNewInstanceOptions);
            }
        } else if (RawDataImportRequest.RESET_SURVEY_INSTANCE_ACTION
                .equals(importReq.getAction())) {
            SurveyInstance instance = instanceDao.getByKey(importReq.getSurveyInstanceId());
            List<QuestionAnswerStore> oldAnswers = instanceDao
                    .listQuestionAnswerStore(importReq.getSurveyInstanceId(), null);

            if (oldAnswers != null && oldAnswers.size() > 0) {
                instanceDao.delete(oldAnswers);
                if (instance != null) {
                    instance.setLastUpdateDateTime(new Date());
                    if (importReq.getSubmitter() != null
                            && importReq.getSubmitter().trim().length() > 0
                            && !"null".equalsIgnoreCase(importReq
                                    .getSubmitter().trim())) {
                        instance.setSubmitterName(importReq.getSubmitter());
                    }
                    instance.setSurveyId(importReq.getSurveyId());
                    if (importReq.getSurveyDuration() != null) {
                        instance.setSurveyalTime(importReq.getSurveyDuration());
                    }
                    instanceDao.save(instance);
                }
            } else {
                if (instance == null) {
                    instance = new SurveyInstance();
                    instance.setKey(KeyFactory.createKey(
                            SurveyInstance.class.getSimpleName(),
                            importReq.getSurveyInstanceId()));
                    instance.setSurveyId(importReq.getSurveyId());
                    instance.setCollectionDate(importReq.getCollectionDate());
                    instance.setSubmitterName(importReq.getSubmitter());
                    instance.setUserID(1L);
                    instance.setUuid(UUID.randomUUID().toString());
                    if (importReq.getSurveyDuration() != null) {
                        instance.setSurveyalTime(importReq.getSurveyDuration());
                    }
                    instanceDao.save(instance);
                } else {
                    instance.setLastUpdateDateTime(new Date());
                    if (importReq.getSubmitter() != null
                            && importReq.getSubmitter().trim().length() > 0
                            && !"null".equalsIgnoreCase(importReq
                                    .getSubmitter().trim())) {
                        instance.setSubmitterName(importReq.getSubmitter());
                    }
                    instance.setSurveyId(importReq.getSurveyId());
                    if (importReq.getSurveyDuration() != null) {
                        instance.setSurveyalTime(importReq.getSurveyDuration());
                    }
                    instanceDao.save(instance);
                }
            }
        } else if (RawDataImportRequest.SAVE_FIXED_FIELD_SURVEY_INSTANCE_ACTION
                .equals(importReq.getAction())) {

            if (importReq.getFixedFieldValues() != null
                    && importReq.getFixedFieldValues().size() > 0) {
                // this method assumes we're always creating a new instance
                SurveyInstance inst = createInstance(importReq);
                QuestionDao questionDao = new QuestionDao();
                List<Question> questionList = questionDao
                        .listQuestionsBySurvey(importReq.getSurveyId());

                if (questionList != null
                        && questionList.size() >= importReq.getFixedFieldValues().size()) {
                    List<QuestionAnswerStore> answers = new ArrayList<QuestionAnswerStore>();
                    for (int i = 0; i < importReq.getFixedFieldValues().size(); i++) {
                        String val = importReq.getFixedFieldValues().get(i);
                        if (val != null && val.trim().length() > 0) {
                            QuestionAnswerStore ans = new QuestionAnswerStore();
                            ans.setQuestionID(questionList.get(i).getKey().getId() + "");
                            ans.setValue(val);
                            Type type = questionList.get(i).getType();
                            if (Type.GEO == type) {
                                ans.setType(QuestionType.GEO.toString());
                            } else if (Type.PHOTO == type) {
                                ans.setType("IMAGE");
                            } else {
                                ans.setType("VALUE");
                            }
                            ans.setSurveyId(importReq.getSurveyId());
                            ans.setSurveyInstanceId(importReq.getSurveyInstanceId());
                            ans.setCollectionDate(importReq.getCollectionDate());
                            answers.add(ans);
                        }
                    }
                    if (answers.size() > 0) {
                        QuestionAnswerStoreDao qasDao = new QuestionAnswerStoreDao();
                        qasDao.save(answers);
                        sendProcessingMessages(inst);
                    }

                } else {
                    log("No questions found for the survey id " + importReq.getSurveyId());
                }
                // todo: send processing message
            }
        } else if (RawDataImportRequest.UPDATE_SUMMARIES_ACTION
                .equalsIgnoreCase(importReq.getAction())) {
            if (importReq.getSurveyId() == null
                    || new SurveyDAO().getById(importReq.getSurveyId()) == null) {
                // ensure survey id present for summary update
                return null;
            }
            // first rebuild the summaries
            log.log(Level.INFO, "Rebuilding summaries for surveyId "
                    + importReq.getSurveyId().toString());
            TaskOptions options = TaskOptions.Builder
                    .withUrl("/app_worker/dataprocessor")
                    .param(DataProcessorRequest.ACTION_PARAM,
                            DataProcessorRequest.REBUILD_QUESTION_SUMMARY_ACTION)
                    .param(DataProcessorRequest.SURVEY_ID_PARAM,
                            importReq.getSurveyId().toString());
            com.google.appengine.api.taskqueue.Queue queue = QueueFactory.getDefaultQueue();
            queue.add(options);
        } else if (RawDataImportRequest.SAVE_MESSAGE_ACTION
                .equalsIgnoreCase(importReq.getAction())) {

            List<Long> ids = new ArrayList<Long>();
            ids.add(importReq.getSurveyId());
            SurveyUtils.notifyReportService(ids, "invalidate");

            MessageDao mdao = new MessageDao();
            Message msg = new Message();
            SurveyDAO sdao = new SurveyDAO();
            Survey s = sdao.getById(importReq.getSurveyId());

            msg.setShortMessage("Spreadsheet processed");
            msg.setObjectId(importReq.getSurveyId());
            msg.setObjectTitle(s.getPath() + "/" + s.getName());
            msg.setActionAbout("spreadsheetProcessed");
            mdao.save(msg);

        }
        return null;
    }

    private void updateDataPointLocation(SurveyedLocale dataPoint,
            List<QuestionAnswerStore> updatedAnswers) {
        for (QuestionAnswerStore answer : updatedAnswers) {
            if (Type.GEO.toString().equals(answer.getType())) {
                dataPoint.setGeoLocation(answer.getValue());
            }
        }
    }

    private void sendProcessingMessages(SurveyInstance domain) {
        // send async request to summarize the instance
        QueueFactory.getQueue(DATA_SUMMARIZATION_QUEUE).add(
                TaskOptions.Builder.withUrl(DATA_SUMMARIZATION_URL)
                        .param(OBJECT_KEY_PARAM, domain.getKey().getId() + "")
                        .param(TYPE_PARAM, SI_TYPE));
        QueueFactory.getDefaultQueue().add(
                TaskOptions.Builder.withUrl(SURVEYAL_URL)
                        .param(SurveyalRestRequest.ACTION_PARAM,
                                SurveyalRestRequest.INGEST_INSTANCE_ACTION)
                        .param(SurveyalRestRequest.SURVEY_INSTANCE_PARAM,
                                domain.getKey().getId() + ""));
    }

    /**
     * constructs and persists a new surveyInstance using the data from the import request
     *
     * @param importReq
     * @return
     */
    private SurveyInstance createInstance(RawDataImportRequest importReq) {
        SurveyInstance inst = new SurveyInstance();
        inst.setUserID(1L);
        inst.setSurveyId(importReq.getSurveyId());
        inst.setCollectionDate(importReq.getCollectionDate() != null
                ? importReq.getCollectionDate()
                : new Date());
        inst.setDeviceIdentifier("IMPORTER");
        inst.setUuid(UUID.randomUUID().toString());
        inst.setSurveyedLocaleId(importReq.getSurveyedLocaleId());
        inst.setUuid(UUID.randomUUID().toString());
        inst.setSubmitterName(importReq.getSubmitter());
        inst.setSurveyalTime(importReq.getSurveyDuration());
        inst.setFormVersion(importReq.getFormVersion());

        // set the key so the subsequent logic can populate it in the
        // QuestionAnswerStore objects
        inst = instanceDao.save(inst);

        importReq.setSurveyInstanceId(inst.getKey().getId());
        if (importReq.getCollectionDate() == null) {
            importReq.setCollectionDate(inst.getCollectionDate());
        }

        return inst;
    }

    private void updateMessageBoard(long objectId, String shortMessage) {
        MessageDao mDao = new MessageDao();
        Message message = new Message();

        message.setObjectId(objectId);
        message.setActionAbout("importData");
        message.setShortMessage(shortMessage);
        mDao.save(message);
    }

    @Override
    protected void writeOkResponse(RestResponse resp) throws Exception {
        // no-op

    }

}
