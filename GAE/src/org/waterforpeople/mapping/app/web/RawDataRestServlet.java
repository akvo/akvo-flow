/*
 *  Copyright (C) 2010-2012 Stichting Akvo (Akvo Foundation)
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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto.QuestionType;
import org.waterforpeople.mapping.app.gwt.client.surveyinstance.QuestionAnswerStoreDto;
import org.waterforpeople.mapping.app.gwt.server.survey.SurveyServiceImpl;
import org.waterforpeople.mapping.app.gwt.server.surveyinstance.SurveyInstanceServiceImpl;
import org.waterforpeople.mapping.app.web.dto.DataProcessorRequest;
import org.waterforpeople.mapping.app.web.dto.RawDataImportRequest;
import org.waterforpeople.mapping.dao.QuestionAnswerStoreDao;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.QuestionAnswerStore;
import org.waterforpeople.mapping.domain.SurveyInstance;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.common.util.PropertyUtil;
import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;
import com.gallatinsystems.messaging.dao.MessageDao;
import com.gallatinsystems.messaging.domain.Message;
import com.gallatinsystems.survey.dao.QuestionDao;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.dao.SurveyGroupDAO;
import com.gallatinsystems.survey.dao.SurveyUtils;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.SurveyGroup;
import com.gallatinsystems.survey.domain.Question.Type;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.surveyal.app.web.SurveyalRestRequest;
import com.gallatinsystems.surveyal.dao.SurveyedLocaleDao;
import com.gallatinsystems.surveyal.domain.SurveyedLocale;
import com.google.appengine.api.backends.BackendServiceFactory;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

public class RawDataRestServlet extends AbstractRestApiServlet {
    private static final Logger log = Logger.getLogger("RawDataRestServlet");
    private static final long serialVersionUID = 2409014651721639814L;

    private SurveyInstanceDAO instanceDao;
    private SurveyDAO sDao;
    private SurveyGroupDAO sgDao;

    public RawDataRestServlet() {
        instanceDao = new SurveyInstanceDAO();
        sDao = new SurveyDAO();
        sgDao = new SurveyGroupDAO();
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
        SurveyInstanceServiceImpl sisi = new SurveyInstanceServiceImpl();
        RawDataImportRequest importReq = (RawDataImportRequest) req;
        if (RawDataImportRequest.SAVE_SURVEY_INSTANCE_ACTION.equals(importReq
                .getAction())) {

            Survey s = null;
            if (importReq.getSurveyId() != null) {
                s = sDao.getByKey(importReq.getSurveyId());
            }
            if (s == null) {
                updateMessageBoard(importReq.getSurveyId(), "Survey id [" + importReq.getSurveyId()
                        + "] doesn't exist");
                return null;
            }

            boolean isNewInstance = importReq.getSurveyInstanceId() == null;
            SurveyInstanceDAO siDao = new SurveyInstanceDAO();
            SurveyInstance instance = null;
            if (isNewInstance) {
                instance = createInstance(importReq);
            } else {
                instance = instanceDao.getByKey(importReq.getSurveyInstanceId());
                if (instance == null) {
                    updateMessageBoard(importReq.getSurveyInstanceId(), "Survey instance id ["
                            + importReq.getSurveyInstanceId() + "] doesn't exist");
                    return null;
                }
            }

            if (!instance.getSurveyId().equals(importReq.getSurveyId())) {
                updateMessageBoard(
                        importReq.getSurveyInstanceId(),
                        "Wrong survey selected when importing instance id ["
                                + importReq.getSurveyInstanceId() + "]");
                return null;
            }

            List<QuestionAnswerStoreDto> dtoList = new ArrayList<QuestionAnswerStoreDto>();
            for (Map.Entry<Long, String[]> item : importReq
                    .getQuestionAnswerMap().entrySet()) {
                QuestionAnswerStoreDto qasDto = new QuestionAnswerStoreDto();
                qasDto.setQuestionID(item.getKey().toString());
                qasDto.setSurveyInstanceId(instance.getKey().getId());
                qasDto.setValue(item.getValue()[0]);
                qasDto.setType(item.getValue()[1]);
                qasDto.setSurveyId(importReq.getSurveyId());
                qasDto.setCollectionDate(importReq.getCollectionDate());
                dtoList.add(qasDto);
            }

            sisi.updateQuestions(dtoList, true, false);

            if (isNewInstance) {
                // create new surveyed locale and launch task to complete processing
                SurveyedLocale locale = new SurveyedLocale();
                locale.setIdentifier(SurveyedLocale.generateBase32Uuid());
                instance.setSurveyedLocaleIdentifier(locale.getIdentifier());

                SurveyGroup sg = null;
                if (s.getSurveyGroupId() != null) {
                    sg = new SurveyGroupDAO().getByKey(s.getSurveyGroupId());
                }
                if (sg != null) {
                    String privacyLevel = sg.getPrivacyLevel() != null ? sg.getPrivacyLevel()
                            .toString() : SurveyGroup.PrivacyLevel.PRIVATE.toString();
                    locale.setLocaleType(privacyLevel);
                    locale.setSurveyGroupId(sg.getKey().getId());
                    locale.setCreationSurveyId(s.getKey().getId());
                }

                locale = new SurveyedLocaleDao().save(locale);
                instance.setSurveyedLocaleId(locale.getKey().getId());

                Queue defaultQueue = QueueFactory.getDefaultQueue();
                TaskOptions processSurveyedLocaleOptions = TaskOptions.Builder
                        .withUrl("/app_worker/surveyalservlet")
                        .param(SurveyalRestRequest.ACTION_PARAM,
                                SurveyalRestRequest.INGEST_INSTANCE_ACTION)
                        .param(SurveyalRestRequest.SURVEY_INSTANCE_PARAM,
                                Long.toString(instance.getKey().getId()))
                        .countdownMillis(Constants.TASK_DELAY);
                defaultQueue.add(processSurveyedLocaleOptions);

                // data summarisation
                List<QuestionAnswerStore> qasList = instanceDao.listQuestionAnswerStoreByType(
                        new Long(importReq.getSurveyInstanceId()), "GEO");
                if (qasList != null && qasList.size() > 0) {
                    Queue summQueue = QueueFactory.getQueue("dataSummarization");
                    summQueue.add(TaskOptions.Builder
                            .withUrl("/app_worker/dataprocessor")
                            .param(
                                    DataProcessorRequest.ACTION_PARAM,
                                    DataProcessorRequest.SURVEY_INSTANCE_SUMMARIZER)
                            .param("surveyInstanceId", importReq.getSurveyInstanceId() + "")
                            .param("qasId", qasList.get(0).getKey().getId() + "")
                            .param("delta", 1 + ""));
                }
            }
        } else if (RawDataImportRequest.RESET_SURVEY_INSTANCE_ACTION
                .equals(importReq.getAction())) {
            SurveyInstance instance = instanceDao.getByKey(importReq
                    .getSurveyInstanceId());
            List<QuestionAnswerStore> oldAnswers = instanceDao
                    .listQuestionAnswerStore(importReq.getSurveyInstanceId(),
                            null);
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
                        && questionList.size() >= importReq
                                .getFixedFieldValues().size()) {
                    List<QuestionAnswerStore> answers = new ArrayList<QuestionAnswerStore>();
                    for (int i = 0; i < importReq.getFixedFieldValues().size(); i++) {
                        String val = importReq.getFixedFieldValues().get(i);
                        if (val != null && val.trim().length() > 0) {
                            QuestionAnswerStore ans = new QuestionAnswerStore();
                            ans.setQuestionID(questionList.get(i).getKey()
                                    .getId()
                                    + "");
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
                            ans.setSurveyInstanceId(importReq
                                    .getSurveyInstanceId());
                            ans.setCollectionDate(importReq.getCollectionDate());
                            answers.add(ans);
                        }
                    }
                    if (answers.size() > 0) {
                        QuestionAnswerStoreDao qasDao = new QuestionAnswerStoreDao();
                        qasDao.save(answers);
                        sisi.sendProcessingMessages(inst);
                    }

                } else {
                    log("No questions found for the survey id "
                            + importReq.getSurveyId());
                }
                // todo: send processing message
            }
        } else if (RawDataImportRequest.UPDATE_SUMMARIES_ACTION
                .equalsIgnoreCase(importReq.getAction())) {
            // first rebuild the summaries
            log.log(Level.INFO, "Rebuilding summaries for surveyId "
                    + importReq.getSurveyId().toString());
            TaskOptions options = TaskOptions.Builder.withUrl(
                    "/app_worker/dataprocessor").param(
                    DataProcessorRequest.ACTION_PARAM,
                    DataProcessorRequest.REBUILD_QUESTION_SUMMARY_ACTION);
            String backendPub = PropertyUtil.getProperty("backendpublish");
            if (backendPub != null && "true".equals(backendPub)) {
                // change the host so the queue invokes the backend
                options = options
                        .header("Host",
                                BackendServiceFactory.getBackendService()
                                        .getBackendAddress("dataprocessor"));
            }
            Long surveyId = importReq.getSurveyId();
            if (surveyId != null && surveyId > 0) {
                options.param(DataProcessorRequest.SURVEY_ID_PARAM,
                        surveyId.toString());
            }

            com.google.appengine.api.taskqueue.Queue queue = com.google.appengine.api.taskqueue.QueueFactory
                    .getDefaultQueue();
            queue.add(options);

            // now remap to access point
            if (surveyId != null) {
                SurveyServiceImpl ssi = new SurveyServiceImpl();
                ssi.rerunAPMappings(surveyId);
            }
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
        inst.setCollectionDate(importReq.getCollectionDate() != null ? importReq
                .getCollectionDate() : new Date());
        inst.setApproximateLocationFlag("False");
        inst.setDeviceIdentifier("IMPORTER");
        inst.setUuid(UUID.randomUUID().toString());
        inst.setSurveyedLocaleId(importReq.getSurveyedLocaleId());
        inst.setUuid(UUID.randomUUID().toString());
        inst.setSubmitterName(importReq.getSubmitter());
        inst.setSurveyalTime(importReq.getSurveyDuration());

        // set the key so the subsequent logic can populate it in the
        // QuestionAnswerStore objects
        SurveyInstanceDAO instDao = new SurveyInstanceDAO();
        inst = instDao.save(inst);

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
