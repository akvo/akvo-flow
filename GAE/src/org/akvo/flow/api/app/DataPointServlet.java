/*
 *  Copyright (C) 2019 Stichting Akvo (Akvo Foundation)
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

package org.akvo.flow.api.app;

import com.gallatinsystems.device.dao.DeviceDAO;
import com.gallatinsystems.device.domain.Device;
import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;
import com.gallatinsystems.survey.dao.QuestionDao;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.surveyal.dao.SurveyedLocaleDao;
import com.gallatinsystems.surveyal.domain.SurveyedLocale;
import com.google.api.server.spi.config.Nullable;

import org.akvo.flow.api.app.DataPointServlet;
import org.akvo.flow.dao.DataPointAssignmentDao;
import org.akvo.flow.domain.DataUtils;
import org.akvo.flow.domain.persistent.DataPointAssignment;
import org.akvo.flow.util.FlowJsonObjectWriter;
import org.waterforpeople.mapping.app.web.dto.SurveyInstanceDto;
import org.waterforpeople.mapping.app.web.dto.SurveyedLocaleDto;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * JSON service for returning the list of assigned data point records for a specific device and surveyId
 *
 */
public class DataPointServlet extends AbstractRestApiServlet {
    private static final long serialVersionUID = 8748650927754433019L;
    private static final Logger log = Logger.getLogger(DataPointServlet.class.getName());
    private SurveyedLocaleDao surveyedLocaleDao;
    private DataPointAssignmentDao dataPointAssignmentDao;

    public DataPointServlet() {
        setMode(JSON_MODE);
        surveyedLocaleDao = new SurveyedLocaleDao();
        dataPointAssignmentDao = new DataPointAssignmentDao();
    }

    @Override
    protected RestRequest convertRequest() throws Exception {
        HttpServletRequest req = getRequest();
        RestRequest restRequest = new DataPointRequest();
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
        DataPointRequest dpReq = (DataPointRequest) req;
        List<SurveyedLocale> dpList;
        RestResponse res = new RestResponse();
        if (dpReq.getSurveyId() != null) {
            //Find the device (if any)
            DeviceDAO deviceDao = new DeviceDAO();
            Device device = deviceDao.getDevice(dpReq.getAndroidId(), dpReq.getImei(),dpReq.getPhoneNumber());
            if (device != null) {
                log.fine("Found device: " + device);
                //Find which assignments we are part of
                List<DataPointAssignment> assList = dataPointAssignmentDao.listByDevice(device.getKey().getId());
                //Combine their point lists
                Set<Long> pointSet = new HashSet<>();
                for (DataPointAssignment ass: assList) {
                    pointSet.addAll(ass.getDataPointIds());
                }
                //Fetch the data points
                List<Long> pointList = new ArrayList<>();
                pointList.addAll(pointSet);
                dpList = surveyedLocaleDao.listByKeys(pointList);
                log.fine("Found assigned data points: " + dpList);
                res = convertToResponse(dpList, dpReq.getSurveyId(), new DataPointResponse());
                log.fine("returning result: " + res);
                return res;
            }
            res.setCode(String.valueOf(HttpServletResponse.SC_NOT_FOUND));
            res.setMessage("Unknown device");
        } else {
            res.setCode(String.valueOf(HttpServletResponse.SC_FORBIDDEN));
            res.setMessage("Invalid Survey");
        }
        return res;
    }

    /**
     * converts the domain objects to dtos and then installs them in a DataPointResponse object
     *
     */
    protected static DataPointResponse convertToResponse(List<SurveyedLocale> slList, Long surveyId, DataPointResponse resp) {
        if (slList == null) {
            resp.setCode(String.valueOf(HttpServletResponse.SC_INTERNAL_SERVER_ERROR));
            resp.setMessage("Internal Server Error");
            return resp;
        }
        // set meta data
        resp.setCode(String.valueOf(HttpServletResponse.SC_OK));
        resp.setResultCount(slList.size());

        List<SurveyedLocaleDto> dtoList = getSurveyedLocaleDtosList(slList, surveyId);

        resp.setSurveyedLocaleData(dtoList);
        return resp;
    }

    private static List<SurveyedLocaleDto> getSurveyedLocaleDtosList(List<SurveyedLocale> slList, Long surveyId) {
        List<SurveyedLocaleDto> dtoList = new ArrayList<>();
        HashMap<Long, String> questionTypeMap = new HashMap<>();
        QuestionDao questionDao = new QuestionDao();

        List<Long> surveyedLocalesIds = getSurveyedLocalesIds(slList);
        Map<Long, List<SurveyInstance>> surveyInstancesMap = getSurveyInstances(surveyedLocalesIds);
        Map<Long, List<QuestionAnswerStore>> questionAnswerStore = getQuestionAnswerStoreMap(
                surveyInstancesMap);

        for (SurveyedLocale surveyedLocale : slList) {
            long surveyedLocaleId = surveyedLocale.getKey().getId();
            SurveyedLocaleDto dto = createSurveyedLocaleDto(surveyId, questionDao,
                    questionTypeMap, surveyedLocale, questionAnswerStore,
                    surveyInstancesMap.get(surveyedLocaleId));
            dtoList.add(dto);
        }
        return dtoList;
    }

    private static SurveyedLocaleDto createSurveyedLocaleDto(Long surveyGroupId, QuestionDao questionDao,
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
    private static Map<Long, List<QuestionAnswerStore>> getQuestionAnswerStoreMap(
            Map<Long, List<SurveyInstance>> surveyInstanceMap) {
        QuestionAnswerStoreDao questionAnswerStoreDao = new QuestionAnswerStoreDao();
        List<Long> surveyInstancesIds = getSurveyInstancesIds(surveyInstanceMap);
        List<QuestionAnswerStore> questionAnswerList = questionAnswerStoreDao
                .fetchItemsByIdBatches(surveyInstancesIds, "surveyInstanceId");
        Map<Long, List<QuestionAnswerStore>> questionAnswerStoreMap = new HashMap<>();
        if (questionAnswerList != null && questionAnswerList.size() > 0) {
            for (QuestionAnswerStore questionAnswerStore : questionAnswerList) {
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

    private static List<Long> getSurveyInstancesIds(Map<Long, List<SurveyInstance>> surveyInstanceMap) {
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
    private static Map<Long, List<SurveyInstance>> getSurveyInstances(List<Long> surveyedLocalesIds) {
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

    private static List<Long> getSurveyedLocalesIds(List<SurveyedLocale> slList) {
        if (slList == null) {
            return Collections.emptyList();
        }
        List<Long> surveyedLocaleIds = new ArrayList<>(slList.size());
        for (SurveyedLocale surveyedLocale : slList) {
            surveyedLocaleIds.add(surveyedLocale.getKey().getId());
        }
        return surveyedLocaleIds;
    }

    private static SurveyInstanceDto createSurveyInstanceDto(QuestionDao qDao,
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

    private static String getAnswerValue(QuestionAnswerStore questionAnswerStore, String type) {
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

    private static String getQuestionType(QuestionDao questionDao, HashMap<Long, String> questionTypeMap,
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
            FlowJsonObjectWriter writer = new FlowJsonObjectWriter();
            writer.writeValue(getResponse().getOutputStream(), resp);
//Splat!            getResponse().getWriter().println();
        } else {
            getResponse().getWriter().println(resp.getMessage());
        }
    }
}
