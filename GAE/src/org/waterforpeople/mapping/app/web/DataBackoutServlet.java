/*
 *  Copyright (C) 2010-2015,2018-2019 Stichting Akvo (Akvo Foundation)
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

import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;

import org.akvo.flow.domain.DataUtils;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;
import org.waterforpeople.mapping.analytics.dao.SurveyQuestionSummaryDao;
import org.waterforpeople.mapping.analytics.domain.SurveyQuestionSummary;
import org.waterforpeople.mapping.app.gwt.client.surveyinstance.QuestionAnswerStoreDto;
import org.waterforpeople.mapping.app.util.DtoMarshaller;
import org.waterforpeople.mapping.app.web.dto.DataBackoutRequest;
import org.waterforpeople.mapping.app.web.dto.QuestionAnswerResponse;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.QuestionAnswerStore;
import org.waterforpeople.mapping.domain.SurveyInstance;

import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;
import com.gallatinsystems.survey.dao.QuestionDao;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.surveyal.dao.SurveyedLocaleDao;
import com.google.appengine.api.datastore.Entity;

/**
 * servlet for backing out survey response data (and corresponding summarizations)
 *
 * @author Christopher Fagiani
 */
public class DataBackoutServlet extends AbstractRestApiServlet {

    private static final long serialVersionUID = 4608959174864994769L;

    private QuestionDao qDao;
    private SurveyQuestionSummaryDao questionSummaryDao;
    private SurveyInstanceDAO instanceDao;
    private SurveyedLocaleDao localeDao;
    private static final ThreadLocal<DateFormat> OUT_FMT = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss z");
        };
    };

    public DataBackoutServlet() {
        setMode(PLAINTEXT_MODE);
        qDao = new QuestionDao();
        localeDao = new SurveyedLocaleDao();
        questionSummaryDao = new SurveyQuestionSummaryDao();
        instanceDao = new SurveyInstanceDAO();
    }

    @Override
    protected RestRequest convertRequest() throws Exception {
        HttpServletRequest req = getRequest();
        RestRequest restRequest = new DataBackoutRequest();
        restRequest.populateFromHttpRequest(req);
        return restRequest;
    }

    @Override
    protected RestResponse handleRequest(RestRequest req) throws Exception {
        DataBackoutRequest boReq = (DataBackoutRequest) req;
        RestResponse response = new RestResponse();
        if (DataBackoutRequest.GET_QUESTION_ACTION.equals(boReq.getAction())) {
            response.setMessage(listQuestionIds(boReq.getSurveyId()));
        } else if (DataBackoutRequest.DELETE_QUESTION_SUMMARY_ACTION
                .equals(boReq.getAction())) {
            deleteQuestionSummary(boReq.getQuestionId());
        } else if (DataBackoutRequest.LIST_INSTANCE_ACTION.equals(boReq.getAction())) {
            response.setMessage(listSurveyInstance(boReq.getSurveyId(),
                    boReq.includeDate(),
                    boReq.getLastCollection(),
                    boReq.getFromDate(),
                    boReq.getToDate(),
                    boReq.getLimit()));
        } else if (DataBackoutRequest.COUNT_INSTANCE_ACTION.equals(boReq.getAction())) {
            response.setMessage(countFormInstance(boReq.getSurveyId(),
                    boReq.getFromDate(),
                    boReq.getToDate()));
        } else if (DataBackoutRequest.DELETE_SURVEY_INSTANCE_ACTION
                .equals(boReq.getAction())) {
            deleteSurveyInstance(boReq.getSurveyInstanceId());
        } else if (DataBackoutRequest.LIST_INSTANCE_RESPONSE_ACTION
                .equals(boReq.getAction())) {
            response.setMessage(listResponses(boReq.getSurveyInstanceId()));
        } else if (DataBackoutRequest.LIST_QUESTION_RESPONSE_ACTION
                .equals(boReq.getAction())) {
            response = listQuestionResponse(boReq.getQuestionId(),
                    boReq.getCursor());
        }
        return response;
    }

    /**
     * lists all responses for a single question *
     *
     * @param surveyId
     * @param questionId
     * @return
     */
    private QuestionAnswerResponse listQuestionResponse(Long questionId, String cursor) {
        List<QuestionAnswerStore> answers = instanceDao
                .listQuestionAnswerStoreForQuestion(questionId.toString(), cursor);
        return convertToAnswerResponse(answers,
                SurveyInstanceDAO.getCursor(answers));
    }

    /**
     * lists all questionAnswerStore records for a given instance... in a csv like format TODO: We
     * should probably quote the values somehow, otherwise, what happens if a response contains \n?
     *
     * @param surveyInstanceId
     * @return
     */
    private String listResponses(Long surveyInstanceId) {
        StringBuilder result = new StringBuilder();
        if (surveyInstanceId != null) {
            List<QuestionAnswerStore> qasList = instanceDao
                    .listQuestionAnswerStore(surveyInstanceId, null);
            if (qasList != null) {
                boolean isFirst = true;
                for (QuestionAnswerStore qas : qasList) {
                    if (!isFirst) {
                        result.append("\n");
                    } else {
                        isFirst = false;
                    }
                    String questionId = qas.getQuestionID();
                    Integer iteration = qas.getIteration();
                    iteration = iteration == null ? 0 : iteration;
                    String value = qas.getValue();

                    // strip image data that will not be used in the excel export
                    if (Question.Type.SIGNATURE.toString().equals(qas.getType())) {
                        value = DataUtils.parseSignatory(value);
                    }
                    value = value == null ? "" : value;

                    result.append(questionId)
                            .append(",")
                            .append(iteration)
                            .append(",")
                            .append(Base64.encodeBase64URLSafeString(value
                                    .getBytes(StandardCharsets.UTF_8)));
                }
            }
        }
        return result.toString();
    }



    /**
     * returns a comma separated list of survyeInstanceIds for the survey passed in
     *
     * @param surveyId
     * @return
     */
    private String listSurveyInstance(Long surveyId, boolean includeDate,
            boolean lastCollection, Date fromDate, Date toDate, Integer limit) {
        boolean keysOnly = true;
        if (includeDate || lastCollection) {
            keysOnly = false;
        }
        Iterable<Entity> instances = instanceDao.listRawEntity(keysOnly, fromDate,
                toDate, limit, surveyId);
        StringBuilder buffer = new StringBuilder();
        List<Long> processed = new ArrayList<Long>();
        if (instances != null) {
            boolean isFirst = true;
            for (Entity result : instances) {
                if (lastCollection
                        && processed.contains(result.getProperty("surveyedLocaleId"))) {
                    continue; // skip
                }
                if (!isFirst) {
                    buffer.append(",");
                } else {
                    isFirst = false;
                }
                buffer.append(result.getKey().getId());
                if (includeDate && result.getProperty("collectionDate") != null) {
                    buffer.append("|").append(
                            OUT_FMT.get().format(result.getProperty("collectionDate")));
                }
                if (lastCollection) {
                    processed.add((Long) result.getProperty("surveyedLocaleId"));
                }
            }
        }
        return buffer.toString();
    }

    /**
     * returns a count of formInstances for the form and dates passed in
     *
     * @param formId
     * @param fromDate
     * @param toDate
     * @return
     */
    private String countFormInstance(Long formId, @Nullable Date fromDate, @Nullable Date toDate) {
        Iterable<Entity> instances = instanceDao.listRawEntity(true, fromDate, toDate, null, formId);
        long count = 0;
        if (instances != null) {
            for (Entity e : instances) {
                count++;
            }
        }
        return Long.toString(count);
    }

    /**
     * deletes a survey instance and it's associated questionAnswerStore objects
     *
     * @param surveyInstanceId
     */
    private void deleteSurveyInstance(Long surveyInstanceId) {
        SurveyInstance instance = instanceDao.getByKey(surveyInstanceId);
        if (instance != null) {
            instanceDao.deleteSurveyInstance(instance);
        }
    }

    /**
     * deletes all the SurveyQuestionSummary objects for a specific questionId
     *
     * @param questionId
     */
    private void deleteQuestionSummary(Long questionId) {
        List<SurveyQuestionSummary> summaries = questionSummaryDao
                .listByQuestion(questionId.toString());
        if (summaries != null) {
            questionSummaryDao.delete(summaries);
        }
    }

    /**
     * returns a comma separated list of question IDs contained in the survey passed in
     *
     * @param surveyId
     * @return
     */
    private String listQuestionIds(Long surveyId) {
        List<Question> questions = qDao.listQuestionsBySurvey(surveyId);
        StringBuilder buffer = new StringBuilder();
        if (questions != null) {
            boolean isFirst = true;
            for (Question q : questions) {
                if (!isFirst) {
                    buffer.append(",");
                } else {
                    isFirst = false;
                }
                buffer.append(q.getKey().getId());
            }
        }
        return buffer.toString();
    }

    @Override
    protected void writeOkResponse(RestResponse resp) throws Exception {
        getResponse().setStatus(200);
        if (resp instanceof QuestionAnswerResponse) {
            QuestionAnswerResponse ansResponse = (QuestionAnswerResponse) resp;
            JSONObject result = new JSONObject(ansResponse);
            getResponse().getWriter().println(result.toString());
        } else {
            getResponse().getWriter().println(resp.getMessage());
        }
    }

    /**
     * converts the domain objects to dtos and then installs them in an QuestionAnswerResponse
     * object
     */
    protected QuestionAnswerResponse convertToAnswerResponse(
            List<QuestionAnswerStore> answerList, String cursor) {
        QuestionAnswerResponse resp = new QuestionAnswerResponse();
        if (answerList != null) {
            List<QuestionAnswerStoreDto> dtoList = new ArrayList<QuestionAnswerStoreDto>();
            for (QuestionAnswerStore ans : answerList) {
                QuestionAnswerStoreDto qasDto = new QuestionAnswerStoreDto();
                DtoMarshaller.copyToDto(ans, qasDto);
                dtoList.add(qasDto);
            }
            resp.setAnswers(dtoList);
        }
        resp.setCursor(cursor);
        return resp;
    }
}
