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

import com.gallatinsystems.device.domain.DeviceSurveyJobQueue;
import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;
import com.gallatinsystems.survey.dao.DeviceSurveyJobQueueDAO;
import com.gallatinsystems.survey.dao.QuestionDao;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.surveyal.dao.SurveyedLocaleDao;
import com.gallatinsystems.surveyal.domain.SurveyedLocale;
import com.google.api.server.spi.config.Nullable;
import org.akvo.flow.domain.DataUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.waterforpeople.mapping.app.web.dto.SurveyInstanceDto;
import org.waterforpeople.mapping.app.web.dto.SurveyedLocaleDto;
import org.waterforpeople.mapping.app.web.dto.SurveyedLocaleRequest;
import org.waterforpeople.mapping.app.web.dto.SurveyedLocaleResponse;
import org.waterforpeople.mapping.dao.QuestionAnswerStoreDao;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.QuestionAnswerStore;
import org.waterforpeople.mapping.domain.SurveyInstance;
import org.waterforpeople.mapping.serialization.response.MediaResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JSON service for returning the list of records for a specific surveyId
 *
 * @author Mark Tiele Westra
 */
public class SurveyedLocaleServlet extends AbstractRestApiServlet {
    private static final long serialVersionUID = 8748650927754433019L;
    private SurveyedLocaleDao surveyedLocaleDao;
    private static final Integer SL_PAGE_SIZE = 30;

    public SurveyedLocaleServlet() {
        setMode(JSON_MODE);
        surveyedLocaleDao = new SurveyedLocaleDao();
    }

    @Override
    protected RestRequest convertRequest() throws Exception {
        HttpServletRequest req = getRequest();
        RestRequest restRequest = new SurveyedLocaleRequest();
        restRequest.populateFromHttpRequest(req);
        return restRequest;
    }

    /**
     * calls the surveyedLocaleDao to get the list of surveyedLocales for a certain surveyGroupId
     * passed in via the request, or the total number of available surveyedLocales if the
     * checkAvailable flag is set.
     */
    @Override
    protected RestResponse handleRequest(RestRequest req) throws Exception {
        SurveyedLocaleRequest slReq = (SurveyedLocaleRequest) req;
        List<SurveyedLocale> slList;
        if (slReq.getSurveyGroupId() != null) {
            DeviceSurveyJobQueueDAO dsjqDAO = new DeviceSurveyJobQueueDAO();
            SurveyDAO surveyDao = new SurveyDAO();
            List<DeviceSurveyJobQueue> deviceSurveyJobQueues = dsjqDAO
                    .get(slReq.getPhoneNumber(), slReq.getImei(), slReq.getAndroidId());
            for (DeviceSurveyJobQueue dsjq : deviceSurveyJobQueues) {
                Survey s = surveyDao.getById(dsjq.getSurveyID());
                if (s != null && s.getSurveyGroupId().longValue() == slReq.getSurveyGroupId()
                        .longValue()) {
                    slList = surveyedLocaleDao.listLocalesBySurveyGroupAndDate(
                            slReq.getSurveyGroupId(), slReq.getLastUpdateTime(), SL_PAGE_SIZE);
                    return convertToResponse(slList, slReq.getSurveyGroupId());
                }
            }
        }
        // A valid assignment has not been found for the given device
        RestResponse res = new RestResponse();
        res.setCode(String.valueOf(HttpServletResponse.SC_FORBIDDEN));
        res.setMessage("Invalid assignment");
        return res;
    }

    /**
     * converts the domain objects to dtos and then installs them in a RecordDataResponse object
     *
     */
    private SurveyedLocaleResponse convertToResponse(List<SurveyedLocale> slList,
            Long surveyGroupId) {
        SurveyedLocaleResponse resp = new SurveyedLocaleResponse();

        if (slList == null) {
            resp.setCode(String.valueOf(HttpServletResponse.SC_INTERNAL_SERVER_ERROR));
            resp.setMessage("Internal Server Error");
            return resp;
        }
        // set meta data
        resp.setCode(String.valueOf(HttpServletResponse.SC_OK));
        resp.setResultCount(slList.size());

        List<SurveyedLocaleDto> dtoList = getSurveyedLocaleDtosList(slList, surveyGroupId);

        resp.setSurveyedLocaleData(dtoList);
        return resp;
    }

    private List<SurveyedLocaleDto> getSurveyedLocaleDtosList(List<SurveyedLocale> slList,
            Long surveyGroupId) {
        List<SurveyedLocaleDto> dtoList = new ArrayList<>();
        HashMap<Long, String> questionTypeMap = new HashMap<>();
        QuestionDao questionDao = new QuestionDao();

        List<Long> surveyedLocalesIds = getSurveyedLocalesIds(slList);
        Map<Long, List<SurveyInstance>> surveyInstancesMap = getSurveyInstances(surveyedLocalesIds);
        Map<Long, List<QuestionAnswerStore>> questionAnswerStore = getQuestionAnswerStoreMap(
                surveyInstancesMap);

        // for each surveyedLocale, store the SurveyalValue in a map
        for (SurveyedLocale surveyedLocale : slList) {
            long surveyedLocaleId = surveyedLocale.getKey().getId();
            SurveyedLocaleDto dto = createSurveyedLocaleDto(surveyGroupId, questionDao,
                    questionTypeMap, surveyedLocale, questionAnswerStore,
                    surveyInstancesMap.get(surveyedLocaleId));
            dtoList.add(dto);
        }
        return dtoList;
    }

    private SurveyedLocaleDto createSurveyedLocaleDto(Long surveyGroupId, QuestionDao questionDao,
            HashMap<Long, String> questionTypeMap, SurveyedLocale surveyedLocale,
            Map<Long, List<QuestionAnswerStore>> questionAnswerStoreMap,
            @Nullable List<SurveyInstance> surveyInstances) {
        SurveyedLocaleDto dto = new SurveyedLocaleDto();
        dto.setId(surveyedLocale.getIdentifier());
        dto.setSurveyGroupId(surveyGroupId);
        dto.setDisplayName(surveyedLocale.getDisplayName());
        dto.setLat(surveyedLocale.getLatitude());
        dto.setLon(surveyedLocale.getLongitude());
        dto.setLastUpdateDateTime(surveyedLocale.getLastUpdateDateTime());

        if (surveyInstances != null) {
            for (SurveyInstance surveyInstance : surveyInstances) {
                Long surveyInstanceId = surveyInstance.getObjectId();
                List<QuestionAnswerStore> answerStores = questionAnswerStoreMap
                        .get(surveyInstanceId);
                SurveyInstanceDto siDto = createSurveyInstanceDto(questionDao, questionTypeMap,
                        answerStores, surveyInstance);
                dto.getSurveyInstances().add(siDto);
            }
        }
        return dto;
    }

    /**
     * Returns a map of QuestionAnswerStore lists,
     * keys: surveyInstanceId, value: list of QuestionAnswerStore for that surveyInstance
     */
    private Map<Long, List<QuestionAnswerStore>> getQuestionAnswerStoreMap(
            Map<Long, List<SurveyInstance>> surveyInstanceMap) {
        QuestionAnswerStoreDao questionAnswerStoreDao = new QuestionAnswerStoreDao();
        List<Long> surveyInstancesIds = getSurveyInstancesIds(surveyInstanceMap);
        List<QuestionAnswerStore> surveyalValueList = questionAnswerStoreDao
                .fetchItemsByIdBatches(surveyInstancesIds, "surveyInstanceId");
        Map<Long, List<QuestionAnswerStore>> questionAnswerStoreMap = new HashMap<>();
        if (surveyalValueList != null && surveyalValueList.size() > 0) {
            for (QuestionAnswerStore questionAnswerStore : surveyalValueList) {
                // put them in a map with the surveyInstanceId as key
                Long surveyInstanceId = questionAnswerStore.getSurveyInstanceId();
                if (questionAnswerStoreMap.containsKey(surveyInstanceId)) {
                    questionAnswerStoreMap.get(surveyInstanceId).add(questionAnswerStore);
                } else {
                    ArrayList<QuestionAnswerStore> questionAnswerStores = new ArrayList<>();
                    questionAnswerStores.add(questionAnswerStore);
                    questionAnswerStoreMap.put(surveyInstanceId, questionAnswerStores);
                }
            }
        }
        return questionAnswerStoreMap;
    }

    private List<Long> getSurveyInstancesIds(Map<Long, List<SurveyInstance>> surveyInstanceMap) {
        List<Long> surveyInstancesIds = new ArrayList<>();
        Collection<List<SurveyInstance>> values = surveyInstanceMap.values();
        for (List<SurveyInstance> surveyInstances : values) {
            for (SurveyInstance surveyInstance: surveyInstances) {
                surveyInstancesIds.add(surveyInstance.getObjectId());
            }
        }
        return surveyInstancesIds;
    }

    /**
     * Fetches SurveyInstances using the surveyedLocalesIds and puts them in a map:
     * key: SurveyedLocalesId, value: list of SurveyInstances
     */
    private Map<Long, List<SurveyInstance>> getSurveyInstances(List<Long> surveyedLocalesIds) {
        SurveyInstanceDAO surveyInstanceDAO = new SurveyInstanceDAO();
        List<SurveyInstance> values = surveyInstanceDAO.fetchItemsByIdBatches(surveyedLocalesIds,
                "surveyedLocaleId");
        Map<Long, List<SurveyInstance>> surveyInstancesMap = new HashMap<>();
        for (SurveyInstance surveyInstance : values) {
            Long surveyedLocaleId = surveyInstance.getSurveyedLocaleId();
            if (surveyInstancesMap.containsKey(surveyedLocaleId)) {
                surveyInstancesMap.get(surveyedLocaleId).add(surveyInstance);
            } else {
                List<SurveyInstance> instances = new ArrayList<>();
                instances.add(surveyInstance);
                surveyInstancesMap.put(surveyedLocaleId, instances);
            }
        }
        return surveyInstancesMap;
    }

    private List<Long> getSurveyedLocalesIds(List<SurveyedLocale> slList) {
        if (slList == null) {
            return Collections.emptyList();
        }
        List<Long> surveyedLocaleIds = new ArrayList<>(slList.size());
        for (SurveyedLocale surveyedLocale : slList) {
            surveyedLocaleIds.add(surveyedLocale.getKey().getId());
        }
        return surveyedLocaleIds;
    }

    private SurveyInstanceDto createSurveyInstanceDto(QuestionDao qDao,
            HashMap<Long, String> questionTypeMap,
            @Nullable List<QuestionAnswerStore> questionAnswerStores,
            @Nullable SurveyInstance surveyInstance) {
        SurveyInstanceDto surveyInstanceDto = new SurveyInstanceDto();
        if (surveyInstance != null) {
            surveyInstanceDto.setUuid(surveyInstance.getUuid());
            surveyInstanceDto.setSubmitter(surveyInstance.getSubmitterName());
            surveyInstanceDto.setSurveyId(surveyInstance.getSurveyId());
            surveyInstanceDto.setCollectionDate(surveyInstance.getCollectionDate().getTime());
        }
        if (questionAnswerStores != null) {
            for (QuestionAnswerStore questionAnswerStore : questionAnswerStores) {
                Long questionId = questionAnswerStore.getQuestionIDLong();
                if (questionId == null) {
                    continue;// The question was deleted before storing the response.
                }
                String type = getQuestionType(qDao, questionTypeMap, questionAnswerStore);
                String value = getAnswerValue(questionAnswerStore, type);
                surveyInstanceDto.addProperty(questionId, value, type);
            }
        }
        return surveyInstanceDto;
    }

    private String getAnswerValue(QuestionAnswerStore questionAnswerStore, String type) {
        // Make all responses backwards compatible
        String answerValue = questionAnswerStore.getValue();
        String value = answerValue != null ? answerValue : "";
        switch (type) {
            case "OPTION":
            case "OTHER":
                if (value.startsWith("[")) {
                    value = DataUtils.jsonResponsesToPipeSeparated(value);
                }
                break;
            case "IMAGE":
            case "VIDEO":
                value = MediaResponse.format(value, MediaResponse.VERSION_STRING);
                break;
            default:
                break;
        }
        return value;
    }

    private String getQuestionType(QuestionDao questionDao, HashMap<Long, String> questionTypeMap,
            QuestionAnswerStore questionAnswerStore) {
        String type = questionAnswerStore.getType();
        if (type == null || "".equals(type)) {
            type = "VALUE";
        } else if ("PHOTO".equals(type)) {
            type = "IMAGE";
        } else if ("OPTION".equals(type)) {
            // first see if we have the question in the map already
            Long questionId = questionAnswerStore.getQuestionIDLong();
            if (questionTypeMap.containsKey(questionId)) {
                type = questionTypeMap.get(questionId);
            } else {
                // find question by id
                Question question = questionDao.getByKey(questionId);
                if (question != null) {
                    // if the question has the allowOtherFlag set,
                    // use OTHER as the device question type
                    if (question.getAllowOtherFlag()) {
                        type = "OTHER";
                    }
                    questionTypeMap.put(questionId, type);
                }
            }
        }
        return type;
    }

    /**
     * writes response as a JSON string
     */
    @Override
    protected void writeOkResponse(RestResponse resp) throws Exception {
        int sc;
        try {
            sc = Integer.valueOf(resp.getCode());
        } catch (NumberFormatException ignored) {
            // Status code was not properly set in the RestResponse
            sc = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        }
        getResponse().setStatus(sc);
        if (sc == HttpServletResponse.SC_OK) {
            ObjectMapper jsonMapper = new ObjectMapper();
            jsonMapper.writeValue(getResponse().getWriter(), resp);
            getResponse().getWriter().println();
        } else {
            getResponse().getWriter().println(resp.getMessage());
        }
    }
}
