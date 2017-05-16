/*
 *  Copyright (C) 2012-2017 Stichting Akvo (Akvo Foundation)
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

package org.waterforpeople.mapping.app.web.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.akvo.flow.domain.DataUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.waterforpeople.mapping.analytics.dao.SurveyQuestionSummaryDao;
import org.waterforpeople.mapping.app.gwt.client.surveyinstance.QuestionAnswerStoreDto;
import org.waterforpeople.mapping.app.util.DtoMarshaller;
import org.waterforpeople.mapping.app.web.DataProcessorRestServlet;
import org.waterforpeople.mapping.app.web.rest.dto.QuestionAnswerStorePayload;
import org.waterforpeople.mapping.app.web.rest.dto.RestStatusDto;
import org.waterforpeople.mapping.dao.QuestionAnswerStoreDao;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.QuestionAnswerStore;
import org.waterforpeople.mapping.serialization.response.MediaResponse;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.survey.dao.CascadeNodeDao;
import com.gallatinsystems.survey.dao.QuestionDao;
import com.gallatinsystems.survey.dao.SurveyUtils;
import com.gallatinsystems.survey.domain.CascadeNode;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.surveyal.dao.SurveyedLocaleDao;
import com.gallatinsystems.surveyal.domain.SurveyalValue;

@Controller
@RequestMapping("/question_answers")
public class QuestionAnswerRestService {

    private QuestionAnswerStoreDao questionAnswerStoreDao = new QuestionAnswerStoreDao();

    private QuestionDao questionDao = new QuestionDao();

    private CascadeNodeDao cascadeNodeDao = new CascadeNodeDao();

    // list questionAnswerStores by id
    @RequestMapping(method = RequestMethod.GET, value = "")
    @ResponseBody
    public Map<String, List<QuestionAnswerStoreDto>> listQABySurveyInstanceId(
            HttpServletRequest httpRequest,
            @RequestParam(value = "surveyInstanceId", defaultValue = "") Long surveyInstanceId) {
        final Map<String, List<QuestionAnswerStoreDto>> response = new HashMap<String, List<QuestionAnswerStoreDto>>();
        List<QuestionAnswerStoreDto> results = new ArrayList<QuestionAnswerStoreDto>();
        List<QuestionAnswerStore> questionAnswerStores = null;
        SurveyInstanceDAO siDao = new SurveyInstanceDAO();
        QuestionDao qDao = new QuestionDao();

        // get list of question-answers
        if (surveyInstanceId != null) {
            questionAnswerStores = siDao.listQuestionAnswerStore(
                    surveyInstanceId, null);

            if (questionAnswerStores != null && questionAnswerStores.size() > 0) {

                // get the list of questions belonging to this surveyInstance in
                // the right order
                List<Question> qList = qDao
                        .listQuestionsInOrder(questionAnswerStores.get(0)
                                .getSurveyId(), null);

                results = new ArrayList<>();

                // sort the questionAnswers in the order of the questions
                int notFoundCount = 0;
                if (qList != null) {
                    for (QuestionAnswerStore qas : questionAnswerStores) {
                        QuestionAnswerStoreDto qasDto = new QuestionAnswerStoreDto();
                        DtoMarshaller.copyToDto(qas, qasDto);
                        int idx = -1;
                        for (int i = 0; i < qList.size(); i++) {
                            if (Long.parseLong(qas.getQuestionID()) == qList
                                    .get(i).getKey().getId()) {
                                qasDto.setQuestionText(qList.get(i).getText());
                                qasDto.setTextualQuestionId(qList.get(i).getQuestionId());
                                idx = i;
                                break;
                            }
                        }

                        // Store not found items at the beginning
                        if (idx < 0) {
                            results.add(notFoundCount++, qasDto);
                            continue;
                        }

                        idx += notFoundCount;
                        while (results.size() < idx + 1) {
                            // Make sure we have enough room for the item
                            results.add(null);
                        }
                        processApiResponse(qasDto, httpRequest);
                        results.add(idx, qasDto);
                    }
                }
            }
        }

        // FIXME: use a better solution for removing null items...
        while (results.remove(null))
            ;

        response.put("question_answers", results);
        return response;
    }

    /**
     * Process the response returned to take into account formats for the API versions
     */
    private void processApiResponse(QuestionAnswerStoreDto response,
            HttpServletRequest httpRequest) {
        if (httpRequest.getRequestURI().startsWith(Constants.API_V1_PREFIX)) {
            // V1 API
            formatResponseAPIV1(response);
        } else {
            // Latest API
            formatResponseLatestAPI(response);
        }
    }

    /**
     * Format Question response according to API v1
     */
    private void formatResponseAPIV1(QuestionAnswerStoreDto response) {
        String value = response.getValue();
        String type = response.getType();

        if (StringUtils.isEmpty(value)) {
            return;
        }

        switch (type) {
            case "OPTION":
            case "OTHER":
                if (value.startsWith("[")) {
                    response.setValue(DataUtils.jsonResponsesToPipeSeparated(value));
                }
                break;
            case "IMAGE":
            case "VIDEO":
                response.setValue(MediaResponse.format(value, MediaResponse.VERSION_STRING));
                break;
            default:
                break;
        }
    }

    /**
     * Format Question response according to the most up-to-date API format
     */
    private void formatResponseLatestAPI(QuestionAnswerStoreDto response) {
        String value = response.getValue();
        String type = response.getType();

        if (StringUtils.isEmpty(value)) {
            return;
        }

        switch (type) {
            case "IMAGE":
            case "VIDEO":
                value = MediaResponse.format(value, MediaResponse.VERSION_GEOTAGGING);
                response.setValue(value);
                break;
            default:
                break;
        }
    }

    // find a single questionAnswerStore by the questionAnswerStoreId
    // TODO include question text in dto
    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    @ResponseBody
    public Map<String, QuestionAnswerStoreDto> findQuestionAnswerStore(
            @PathVariable("id") Long id) {
        final Map<String, QuestionAnswerStoreDto> response = new HashMap<String, QuestionAnswerStoreDto>();
        QuestionAnswerStoreDao qaDao = new QuestionAnswerStoreDao();
        QuestionAnswerStore s = qaDao.getByKey(id);
        QuestionAnswerStoreDto dto = null;
        if (s != null) {
            dto = new QuestionAnswerStoreDto();
            DtoMarshaller.copyToDto(s, dto);

            // This endpoint is only used in the FLOW dashboard.
            // Latest API format can be safely used.
            formatResponseLatestAPI(dto);
        }

        response.put("question_answer", dto);
        return response;
    }

    // update existing questionAnswerStore
    @RequestMapping(method = RequestMethod.PUT, value = "/{id}")
    @ResponseBody
    public Map<String, Object> saveExistingQuestionAnswerStore(
            @RequestBody QuestionAnswerStorePayload payLoad) {
        final QuestionAnswerStoreDto requestDto = payLoad
                .getQuestion_answer();

        final RestStatusDto statusDto = new RestStatusDto();
        statusDto.setStatus("failed");

        final Map<String, Object> response = new HashMap<String, Object>();
        response.put("meta", statusDto);

        final QuestionAnswerStoreDto responseDto = new QuestionAnswerStoreDto();

        // if the POST data contains a valid questionAnswerStoreDto, continue.
        // Otherwise,
        // server will respond with 400 Bad Request
        if (requestDto != null) {
            Long keyId = requestDto.getKeyId();
            Question q = questionDao.getByKey(Long.parseLong(requestDto.getQuestionID()));
            QuestionAnswerStore qa;
            // if the questionAnswerStoreDto has a key, try to get the
            // questionAnswerStore.
            if (keyId != null && q != null) {
                qa = questionAnswerStoreDao.getByKey(keyId);
                // if we find the questionAnswerStore, update it's properties
                if (qa != null) {
                    // Before updating the properties, fix the questionAnswerSummary counts if it is
                    // an OPTION question
                    if (Question.Type.OPTION.equals(q.getType())) {
                        // decrease count of current item
                        SurveyQuestionSummaryDao.incrementCount(qa, -1);

                        // increase count of new item
                        String newVal = requestDto.getValue();
                        if (newVal != null && newVal.trim().length() > 0) {
                            SurveyQuestionSummaryDao.incrementCount(
                                    constructQAS(qa.getQuestionID(), newVal), 1);
                        }
                    } else if (Question.Type.CASCADE.equals(q.getType())) {
                        JSONArray cascadeResponse = null;
                        boolean isValidJson = true;
                        boolean isValidResponse = true;
                        try {
                            cascadeResponse = new JSONArray(requestDto.getValue());

                            isValidResponse = isValidCascadeResponse(q, cascadeResponse);
                        } catch (JSONException e) {
                            isValidJson = false;
                        }

                        // validate individual nodes
                        if (!isValidJson || !isValidResponse) {
                            statusDto.setMessage("_invalid_cascade_response");
                            return response;
                        }
                    }
                    // copy the properties, except the createdDateTime property,
                    // because it is set in the Dao.
                    BeanUtils.copyProperties(requestDto, qa,
                            new String[] {
                                    "createdDateTime", "status",
                                    "version", "lastUpdateDateTime",
                                    "displayName", "questionGroupList", "questionText"
                            });
                    qa = questionAnswerStoreDao.save(qa);

                    // next, update the corresponding surveyalValue object
                    // find surveyalValue based on surveyInstanceId and questionId
                    Long surveyInstanceId = qa.getSurveyInstanceId();
                    String questionId = qa.getQuestionID();
                    SurveyedLocaleDao slDao = new SurveyedLocaleDao();
                    List<SurveyalValue> svals = slDao.listSVByQuestionAndSurveyInstance(
                            surveyInstanceId, Long.parseLong(questionId));
                    Long surveyedLocaleId = null;
                    if (svals != null && svals.size() > 0) {
                        SurveyalValue sval = svals.get(0);
                        sval.setStringValue(qa.getValue());
                        slDao.save(sval);
                        // Populate locale id from the only entity containing this attribute
                        surveyedLocaleId = sval.getSurveyedLocaleId();
                    }

                    // Update datapoint names for this survey, if applies
                    if (q.getLocaleNameFlag() && surveyedLocaleId != null) {
                        DataProcessorRestServlet.scheduleDatapointNameAssembly(
                                null, surveyedLocaleId, true);
                    }

                    // return result to the Dashboard
                    DtoMarshaller.copyToDto(qa, responseDto);
                    // give back the question text as we received it
                    responseDto.setQuestionText(requestDto.getQuestionText());
                    statusDto.setStatus("ok");

                    try {
                        // A PUT is done when editing a QuestionAnswerStore, we
                        // need to invalidate a cached report
                        List<Long> surveyIds = new ArrayList<Long>();
                        surveyIds.add(requestDto.getSurveyId());
                        SurveyUtils
                                .notifyReportService(surveyIds, "invalidate");
                    } catch (Exception e) {
                        // no-op
                    }
                }
            }
        }

        response.put("question_answer", responseDto);
        return response;
    }

    /**
     * Compare submitted cascade response with nodes from the datastore to determine cascade
     * response validity
     *
     * @param question
     * @param response
     * @return
     * @throws JSONException
     */
    private boolean isValidCascadeResponse(Question question, JSONArray response)
            throws JSONException {
        boolean valid = false;

        List<String> responseNodeNames = new ArrayList<String>();
        for (int i = 0; i < response.length(); i++) {
            responseNodeNames.add(response.getJSONObject(i).getString("name"));
        }

        List<CascadeNode> nodes = cascadeNodeDao.listByName(question.getCascadeResourceId(),
                responseNodeNames);
        List<List<CascadeNode>> cascadePathsList = createCascadeNodePaths(nodes);
        for (List<CascadeNode> path : cascadePathsList) {
            if (path.size() != response.length()) {
                continue;
            }
            List<String> pathNodeNames = new ArrayList<String>();
            for (int i = 0; i < path.size(); i++) {
                pathNodeNames.add(path.get(i).getName());
            }
            if (responseNodeNames.equals(pathNodeNames)) {
                valid = true;
                break;
            }
        }
        return valid;
    }

    /**
     * Given a list of cascade nodes, split it up into a number of lists containing the cascade
     * paths within that list of nodes
     *
     * @param nodes
     * @return
     */
    private List<List<CascadeNode>> createCascadeNodePaths(List<CascadeNode> nodes) {
        List<List<CascadeNode>> pathsList = new ArrayList<List<CascadeNode>>();
        Map<Long, CascadeNode> nodesMap = new HashMap<Long, CascadeNode>();

        for (CascadeNode node : nodes) {
            nodesMap.put(node.getKey().getId(), node);
        }

        for (CascadeNode node : nodes) {
            CascadeNode currentNode = node;
            List<CascadeNode> path = new ArrayList<CascadeNode>();
            if (currentNode.getParentNodeId().equals(0L)) {
                path.add(currentNode);
            } else {
                while (currentNode != null && !currentNode.getParentNodeId().equals(0L)) {
                    path.add(currentNode);
                    currentNode = nodesMap.get(currentNode.getParentNodeId());
                }
                if (currentNode != null) {
                    path.add(currentNode); // add first element in path
                }
            }
            Collections.reverse(path);
            pathsList.add(path);
        }
        return pathsList;
    }

    /**
     * helper method to create a new QuestionAnswerStore object using the values passed in. Same
     * method as in SurveyQuestionSummaryUpdater
     *
     * @param id
     * @param value
     * @return
     */
    private QuestionAnswerStore constructQAS(String id, String value) {
        QuestionAnswerStore qas = new QuestionAnswerStore();
        qas.setQuestionID(id);
        qas.setValue(value);
        return qas;
    }

}
