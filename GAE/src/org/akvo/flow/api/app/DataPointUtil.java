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

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.gallatinsystems.device.dao.DeviceDAO;
import com.gallatinsystems.device.domain.Device;
import com.gallatinsystems.surveyal.dao.SurveyedLocaleDao;
import org.akvo.flow.dao.DataPointAssignmentDao;
import org.akvo.flow.dao.SurveyAssignmentDao;
import org.akvo.flow.domain.DataUtils;
import org.akvo.flow.domain.persistent.DataPointAssignment;
import org.akvo.flow.domain.persistent.SurveyAssignment;
import org.waterforpeople.mapping.app.web.dto.SurveyInstanceDto;
import org.waterforpeople.mapping.app.web.dto.SurveyedLocaleDto;
import org.waterforpeople.mapping.dao.QuestionAnswerStoreDao;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.QuestionAnswerStore;
import org.waterforpeople.mapping.domain.SurveyInstance;
import org.waterforpeople.mapping.serialization.response.MediaResponse;

import com.gallatinsystems.survey.dao.QuestionDao;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.surveyal.domain.SurveyedLocale;
import com.google.api.server.spi.config.Nullable;

import static com.gallatinsystems.common.Constants.ALL_DATAPOINTS;


public class DataPointUtil {
    private static final Logger log = Logger.getLogger(DataPointUtil.class.getName());
    SurveyedLocaleDao surveyedLocaleDao = new SurveyedLocaleDao();
    DataPointAssignmentDao dataPointAssignmentDao = new DataPointAssignmentDao();
    private static final int LIMIT_DATAPOINTS_1000 = 1000;

    public List<SurveyedLocaleDto> getSimpleSurveyedLocaleDtosList(List<SurveyedLocale> slList) {
        List<SurveyedLocaleDto> dtoList = new ArrayList<>();

        for (SurveyedLocale surveyedLocale : slList) {
            SurveyedLocaleDto dto = createSimpleSurveyedLocaleDto(surveyedLocale);
            dtoList.add(dto);
        }
        return dtoList;
    }

    public List<SurveyedLocaleDto> getSurveyedLocaleDtosList(List<SurveyedLocale> slList, Long surveyId) {
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

    public List<SurveyedLocale> getAssignedDataPoints(String androidId, Long surveyId, String cursor) throws Exception {

        //Find the device (if any)
        DeviceDAO deviceDao = new DeviceDAO();
        Device device = deviceDao.getDevice(androidId, null, null);
        if (device == null) {
            throw new Exception("Device not found");
        }
        log.fine("Found device: " + device);

        // verify assignments exist
        long deviceId = device.getKey().getId();

        List<DataPointAssignment> dataPointAssignments =
                dataPointAssignmentDao.listByDeviceAndSurvey(deviceId, surveyId);
        List<SurveyAssignment> deviceSurveyAssignments = new ArrayList<>();

        if (dataPointAssignments.isEmpty()) {
            SurveyAssignmentDao saDao = new SurveyAssignmentDao();
            deviceSurveyAssignments.addAll(saDao.listByDeviceAndSurvey(deviceId, surveyId));
        }

        if (dataPointAssignments.isEmpty() && deviceSurveyAssignments.isEmpty()) {
            log.log(Level.WARNING, "No assignments found for surveyId: " + surveyId + " - deviceId: " + deviceId);
            throw new Exception("No datapoints assigned found");
        }

        return getDataPointList(dataPointAssignments.get(0), surveyId, device.getKey().getId(), cursor);
    }

    public List<SurveyedLocale> getDataPointList(DataPointAssignment assignment, Long surveyId, Long deviceId, String cursor) {
        if (assignment == null || allDataPointsAreAssigned(assignment)) {
            return getAllDataPoints(deviceId, surveyId, cursor);
        } else {
            return getAssignedDataPoints(assignment);
        }
    }

    private boolean allDataPointsAreAssigned(DataPointAssignment assignment) {
        if (assignment == null) {
            return false;
        }

        Set<Long> assignedDataPoints = new HashSet<>(assignment.getDataPointIds());
        return ALL_DATAPOINTS.equals(assignedDataPoints);
    }

    private List<SurveyedLocale> getAllDataPoints(Long deviceId, Long surveyId, String cursor) {
        return surveyedLocaleDao.listLocalesBySurveyGroupAndUpdateDate(surveyId, null, cursor, LIMIT_DATAPOINTS_1000);
    }

    /*
     * Return only datapoints that have been explicitly assigned to a device
     */
    private List<SurveyedLocale> getAssignedDataPoints(DataPointAssignment assignment) {
        Set<Long> assignedDataPointIds = new HashSet<>();
        assignedDataPointIds.addAll(assignment.getDataPointIds());
        return surveyedLocaleDao.listByKeys(new ArrayList<>(assignedDataPointIds));
    }

    private SurveyedLocaleDto createSimpleSurveyedLocaleDto(SurveyedLocale surveyedLocale) {
        SurveyedLocaleDto dto = new SurveyedLocaleDto();
        dto.setId(surveyedLocale.getIdentifier());
        dto.setSurveyGroupId(surveyedLocale.getSurveyGroupId());
        dto.setDisplayName(surveyedLocale.getDisplayName());
        dto.setLat(surveyedLocale.getLatitude());
        dto.setLon(surveyedLocale.getLongitude());
        dto.setLastUpdateDateTime(surveyedLocale.getLastUpdateDateTime());

        return dto;
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
}
