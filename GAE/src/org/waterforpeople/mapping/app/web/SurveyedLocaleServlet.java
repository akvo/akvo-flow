/*
 *  Copyright (C) 2010-2016 Stichting Akvo (Akvo Foundation)
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
import com.gallatinsystems.surveyal.domain.SurveyalValue;
import com.gallatinsystems.surveyal.domain.SurveyedLocale;
import org.akvo.flow.domain.DataUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.waterforpeople.mapping.app.web.dto.SurveyInstanceDto;
import org.waterforpeople.mapping.app.web.dto.SurveyedLocaleDto;
import org.waterforpeople.mapping.app.web.dto.SurveyedLocaleRequest;
import org.waterforpeople.mapping.app.web.dto.SurveyedLocaleResponse;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.SurveyInstance;
import org.waterforpeople.mapping.serialization.response.MediaResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
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
    private static final Integer SL_PAGE_SIZE = 300;

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
        List<SurveyedLocale> slList = null;
        if (slReq.getSurveyGroupId() != null) {
            DeviceSurveyJobQueueDAO dsjqDAO = new DeviceSurveyJobQueueDAO();
            SurveyDAO surveyDao = new SurveyDAO();
            for (DeviceSurveyJobQueue dsjq : dsjqDAO.get(slReq.getPhoneNumber(),
                    slReq.getImei(), slReq.getAndroidId())) {
                Survey s = surveyDao.getById(dsjq.getSurveyID());
                if (s != null
                        && s.getSurveyGroupId().longValue() == slReq.getSurveyGroupId()
                                .longValue()) {
                    slList = surveyedLocaleDao.listLocalesBySurveyGroupAndDate(
                            slReq.getSurveyGroupId(), slReq.getLastUpdateTime(), SL_PAGE_SIZE);
                    return convertToResponse(slList, slReq.getSurveyGroupId()
                    );
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
    protected SurveyedLocaleResponse convertToResponse(List<SurveyedLocale> slList,
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

        SurveyedLocaleDao slDao = new SurveyedLocaleDao();
        SurveyInstanceDAO sDao = new SurveyInstanceDAO();
        QuestionDao qDao = new QuestionDao();

        List<SurveyedLocaleDto> dtoList = getSurveyedLocaleDtosList(slList, surveyGroupId, slDao,
                sDao, qDao);

        resp.setSurveyedLocaleData(dtoList);
        return resp;
    }

    private List<SurveyedLocaleDto> getSurveyedLocaleDtosList(List<SurveyedLocale> slList,
            Long surveyGroupId, SurveyedLocaleDao slDao, SurveyInstanceDAO sDao, QuestionDao qDao) {
        // set Locale data
        List<SurveyedLocaleDto> dtoList = new ArrayList<>();
        HashMap<Long, String> questionTypeMap = new HashMap<>();
        Map<Long, List<SurveyalValue>> surveyalValuesMap = getSurveyalValues(slList, slDao);
        // for each surveyedLocale, get the surveyalValues and store them in a map
        for (SurveyedLocale sl : slList) {
            long surveyedLocaleId = sl.getKey().getId();
            List<SurveyalValue> svList =  surveyalValuesMap.get(surveyedLocaleId);
            Map<Long, List<SurveyalValue>> instanceMap = getSurveyalListMap(svList);
            SurveyedLocaleDto dto = createSurveyedLocaleDto(surveyGroupId, qDao, questionTypeMap,
                    sl, instanceMap, sDao);
            dtoList.add(dto);
        }
        return dtoList;
    }

    private SurveyedLocaleDto createSurveyedLocaleDto(Long surveyGroupId, QuestionDao qDao,
            HashMap<Long, String> questionTypeMap, SurveyedLocale sl,
            Map<Long, List<SurveyalValue>> instanceMap, SurveyInstanceDAO sDao) {
        // put them in the dto
        SurveyedLocaleDto dto = new SurveyedLocaleDto();
        dto.setId(sl.getIdentifier());
        dto.setSurveyGroupId(surveyGroupId);
        dto.setDisplayName(sl.getDisplayName());
        dto.setLat(sl.getLatitude());
        dto.setLon(sl.getLongitude());
        dto.setLastUpdateDateTime(sl.getLastUpdateDateTime());

        for (Long instanceId : instanceMap.keySet()) {
            SurveyInstanceDto siDto = createSurveyInstanceDto(qDao, questionTypeMap,
                    sDao, instanceId, instanceMap.get(instanceId));
            dto.getSurveyInstances().add(siDto);
        }
        return dto;
    }

    private Map<Long, List<SurveyalValue>> getSurveyalListMap(List<SurveyalValue> svList) {
        Map<Long, List<SurveyalValue>> instanceMap = new HashMap<>();
        if (svList != null && svList.size() > 0) {
            for (SurveyalValue surveyalValue : svList) {
                // put them in a map with the surveyInstance as key
                Long surveyInstanceId = surveyalValue.getSurveyInstanceId();
                if (instanceMap.containsKey(surveyInstanceId)) {
                    instanceMap.get(surveyInstanceId).add(surveyalValue);
                } else {
                    ArrayList<SurveyalValue> surveyalValues = new ArrayList<>();
                    surveyalValues.add(surveyalValue);
                    instanceMap.put(surveyInstanceId, surveyalValues);
                }
            }
        }
        return instanceMap;
    }

    private Map<Long, List<SurveyalValue>> getSurveyalValues(List<SurveyedLocale> slList,
            SurveyedLocaleDao slDao) {
        List<Long> surveyedLocalesIds = getSurveyedLocalesIds(slList);
        List<SurveyalValue> values = slDao.listValuesByLocalesIdList(surveyedLocalesIds);
        Map<Long, List<SurveyalValue>> surveyalValuesMap = new HashMap<>();
        for (SurveyalValue surveyalValue : values) {
            Long surveyedLocaleId = surveyalValue.getSurveyedLocaleId();
            if (surveyalValuesMap.containsKey(surveyedLocaleId)) {
                surveyalValuesMap.get(surveyedLocaleId).add(surveyalValue);
            } else {
                List<SurveyalValue> valuesList = new ArrayList<>();
                valuesList.add(surveyalValue);
                surveyalValuesMap.put(surveyedLocaleId, valuesList);
            }
        }
        return surveyalValuesMap;
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
            HashMap<Long, String> questionTypeMap, SurveyInstanceDAO sDao, Long instanceId,
            List<SurveyalValue> surveyalValues) {
        SurveyInstanceDto siDto = new SurveyInstanceDto();
        SurveyInstance si = sDao.getByKey(instanceId);
        if (si != null) {
            siDto.setUuid(si.getUuid());
            siDto.setSubmitter(si.getSubmitterName());
            siDto.setSurveyId(si.getSurveyId());
            siDto.setCollectionDate(si.getCollectionDate().getTime());
        }
        for (SurveyalValue sv : surveyalValues) {
            if (sv.getSurveyQuestionId() == null) {
                continue;// The question was deleted before storing the response.
            }
            String type = getQuestionType(qDao, questionTypeMap, sv);
            String value = getAnswerValue(sv, type);
            siDto.addProperty(sv.getSurveyQuestionId(), value, type);
        }
        return siDto;
    }

    private String getAnswerValue(SurveyalValue sv, String type) {
        // Make all responses backwards compatible
        String value = sv.getStringValue() != null ? sv.getStringValue() : "";
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

    private String getQuestionType(QuestionDao qDao, HashMap<Long, String> questionTypeMap,
            SurveyalValue sv) {
        String type = sv.getQuestionType();
        if (type == null || "".equals(type)) {
            type = "VALUE";
        } else if ("PHOTO".equals(type)) {
            type = "IMAGE";
        } else if ("OPTION".equals(type)) {
            // first see if we have the question in the map already
            if (questionTypeMap.containsKey(sv.getSurveyQuestionId())) {
                type = questionTypeMap.get(sv.getSurveyQuestionId());
            } else {
                // find question by id
                Question q = qDao.getByKey(sv.getSurveyQuestionId());
                if (q != null) {
                    // if the question has the allowOtherFlag set,
                    // use OTHER as the device question type
                    if (q.getAllowOtherFlag()) {
                        type = "OTHER";
                    }
                    questionTypeMap.put(sv.getSurveyQuestionId(), type);
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
