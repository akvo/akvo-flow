/*
 *  Copyright (C) 2012,2017 Stichting Akvo (Akvo Foundation)
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.waterforpeople.mapping.app.web.rest.dto.RestStatusDto;
import org.waterforpeople.mapping.app.web.rest.dto.SurveyAssignmentDto;
import org.waterforpeople.mapping.app.web.rest.dto.SurveyAssignmentPayload;
import org.waterforpeople.mapping.domain.SurveyAssignment;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.device.dao.DeviceDAO;
import com.gallatinsystems.device.domain.Device;
import com.gallatinsystems.device.domain.DeviceSurveyJobQueue;
import com.gallatinsystems.framework.analytics.summarization.DataSummarizationRequest;
import com.gallatinsystems.framework.domain.DataChangeRecord;
import com.gallatinsystems.survey.dao.DeviceSurveyJobQueueDAO;
import com.gallatinsystems.survey.dao.SurveyAssignmentDAO;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.domain.Survey;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

@Controller
@RequestMapping("/survey_assignments")
public class SurveyAssignmentRestService {

    private SurveyAssignmentDAO surveyAssignmentDao = new SurveyAssignmentDAO();

    private DeviceDAO deviceDao;
    private SurveyDAO surveyDao;
    private DeviceSurveyJobQueueDAO deviceSurveyJobQueueDAO;

    @RequestMapping(method = RequestMethod.GET, value = "")
    @ResponseBody
    public Map<String, List<SurveyAssignmentDto>> listAll() {
        final HashMap<String, List<SurveyAssignmentDto>> response = new HashMap<String, List<SurveyAssignmentDto>>();
        final List<SurveyAssignmentDto> results = new ArrayList<SurveyAssignmentDto>();

        for (SurveyAssignment sa : surveyAssignmentDao.list(Constants.ALL_RESULTS)) {
            results.add(marshallToDto(sa));
        }

        response.put("survey_assignments", results);
        return response;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    @ResponseBody
    public Map<String, SurveyAssignmentDto> getById(@PathVariable("id")
    Long id) {
        final HashMap<String, SurveyAssignmentDto> response = new HashMap<String, SurveyAssignmentDto>();
        final SurveyAssignment sa = surveyAssignmentDao.getByKey(id);

        if (sa == null) {
            throw new HttpMessageNotReadableException(
                    "Survey Assignment with id: " + id + " not found");
        }

        response.put("survey_assignment", marshallToDto(sa));
        return response;
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    @ResponseBody
    public Map<String, RestStatusDto> deleteById(@PathVariable("id")
    Long id) {
        final HashMap<String, RestStatusDto> response = new HashMap<String, RestStatusDto>();
        final SurveyAssignment sa = surveyAssignmentDao.getByKey(id);
        DeviceSurveyJobQueueDAO deviceSurveysDao = new DeviceSurveyJobQueueDAO();
        final List<DeviceSurveyJobQueue> deviceSurveys = deviceSurveysDao.listJobByAssignment(id);
        RestStatusDto statusDto = new RestStatusDto();
        statusDto.setStatus("failed");
        if (sa != null && deviceSurveys != null) {
            surveyAssignmentDao.delete(sa);
            deviceSurveysDao.delete(deviceSurveys);
            statusDto.setStatus("ok");
        }

        response.put("meta", statusDto);
        return response;
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{id}")
    @ResponseBody
    public Map<String, SurveyAssignmentDto> updateSurveyAssignment(
            @PathVariable("id")
            Long id,
            @RequestBody
            SurveyAssignmentPayload payload) {

        final SurveyAssignmentDto dto = payload.getSurvey_assignment();
        if (!id.equals(dto.getKeyId())) {
            throw new HttpMessageNotReadableException("Ids don't match: " + id
                    + " <> " + dto.getKeyId());
        }
        final SurveyAssignment oldAssignment = surveyAssignmentDao.getByKey(dto.getKeyId());
        final HashMap<String, SurveyAssignmentDto> response = new HashMap<String, SurveyAssignmentDto>();
        if (oldAssignment == null) {
            throw new HttpMessageNotReadableException(
                    "Survey Assignment with id: " + dto.getKeyId() + " not found");
        }
        final SurveyAssignment sa = marshallToDomain(dto);
        surveyAssignmentDao.save(sa);
        generateDeviceJobQueueItems(sa, oldAssignment);
        response.put("survey_assignment", marshallToDto(sa));

        return response;
    }

    @RequestMapping(method = RequestMethod.POST, value = "")
    @ResponseBody
    public Map<String, SurveyAssignmentDto> newSurveyAssignment(
            @RequestBody
            SurveyAssignmentPayload payload) {
        final SurveyAssignmentDto dto = payload.getSurvey_assignment();
        final SurveyAssignment sa  = marshallToDomain(dto);
        surveyAssignmentDao.save(sa); //fills in new key
        generateDeviceJobQueueItems(sa, null);

        final HashMap<String, SurveyAssignmentDto> response = new HashMap<String, SurveyAssignmentDto>();
        response.put("survey_assignment", marshallToDto(sa));
        return response;
    }

    private SurveyAssignmentDto marshallToDto(SurveyAssignment sa) {
        final SurveyAssignmentDto dto = new SurveyAssignmentDto();

        BeanUtils.copyProperties(sa, dto);
        if (sa.getKey() != null) {
            dto.setKeyId(sa.getKey().getId());
        }
        dto.setDevices(sa.getDeviceIds());
        dto.setSurveys(sa.getSurveyIds());

        return dto;
    }

    private SurveyAssignment marshallToDomain(SurveyAssignmentDto dto) {
        final SurveyAssignment sa = new SurveyAssignment();

        BeanUtils.copyProperties(dto, sa);
        if (dto.getKeyId() != null) {
            sa.setKey(KeyFactory.createKey("SurveyAssignment", dto.getKeyId()));
        }
        sa.setDeviceIds(dto.getDevices());
        sa.setSurveyIds(dto.getSurveys());

        return sa;
    }

    /**
     * creates and saves DeviceSurveyJobQueue objects for each device/survey pair in the assignment.
     * If this takes too long to do, may need to make it async
     * 
     * @param assignment
     */
    private void generateDeviceJobQueueItems(SurveyAssignment assignment,
            SurveyAssignment oldAssignment) {
        List<Long> surveyIdsToSave = new ArrayList<Long>(assignment.getSurveyIds());
        List<Long> deviceIdsToSave = new ArrayList<Long>(assignment.getDeviceIds());
        List<Long> surveyIdsToDelete = new ArrayList<Long>();
        List<Long> deviceIdsToDelete = new ArrayList<Long>();
        surveyAssignmentDao = new SurveyAssignmentDAO();
        deviceDao = new DeviceDAO();
        surveyDao = new SurveyDAO();
        deviceSurveyJobQueueDAO = new DeviceSurveyJobQueueDAO();

        if (oldAssignment != null) {
            if (oldAssignment.getSurveyIds() != null) {
                surveyIdsToSave.removeAll(oldAssignment.getSurveyIds());
                surveyIdsToDelete = new ArrayList<Long>(oldAssignment.getSurveyIds());
                surveyIdsToDelete.removeAll(assignment.getSurveyIds());
            }
            if (oldAssignment.getDeviceIds() != null) {
                deviceIdsToSave.removeAll(oldAssignment.getDeviceIds());
                deviceIdsToDelete = new ArrayList<Long>(oldAssignment.getDeviceIds());
                deviceIdsToDelete.removeAll(assignment.getDeviceIds());
            }
        }
        List<DeviceSurveyJobQueue> queueList = new ArrayList<DeviceSurveyJobQueue>();
        Map<Long, Survey> surveyMap = new HashMap<Long, Survey>();
        Map<Long, Device> deviceMap = new HashMap<Long, Device>();
        if (deviceIdsToSave != null) {
            // for each new device, we need to save a record for ALL survey IDs
            // in the assignment
            for (Long id : deviceIdsToSave) {
                Device d = deviceMap.get(id);
                if (d == null) {
                    d = deviceDao.getByKey(id);
                    deviceMap.put(d.getKey().getId(), d);
                }
                for (Long sId : assignment.getSurveyIds()) {
                    Survey survey = surveyMap.get(sId);
                    if (survey == null) {
                        survey = surveyDao.getByKey(sId);
                        surveyMap.put(sId, survey);
                    }
                    queueList.add(constructQueueObject(d, survey, assignment));
                }
            }
        }
        // if we added any surveys, we need to save a record for ALL the devices
        // BUT we don't need to process the items that we already saved above
        if (surveyIdsToSave != null) {
            for (Long sId : surveyIdsToSave) {
                Survey survey = surveyMap.get(sId);
                if (survey == null) {
                    survey = surveyDao.getByKey(sId);
                    surveyMap.put(sId, survey);
                }
                for (Long id : assignment.getDeviceIds()) {
                    // only proceed if we haven't already saved the record above
                    if (!deviceIdsToSave.contains(id)) {
                        Device d = deviceMap.get(id);
                        if (d == null) {
                            d = deviceDao.getByKey(id);
                            deviceMap.put(d.getKey().getId(), d);
                        }
                        queueList.add(constructQueueObject(d, survey, assignment));
                    }
                }
            }
        }

        if (queueList.size() > 0) {
            deviceSurveyJobQueueDAO.save(queueList);
        }
        if (deviceIdsToDelete.size() > 0 || surveyIdsToDelete.size() > 0) {
            StringBuilder builder = new StringBuilder("d");
            for (int i = 0; i < deviceIdsToDelete.size(); i++) {
                if (i > 0) {
                    builder.append("xx");
                }
                Device d = deviceMap.get(deviceIdsToDelete.get(i));
                if (d == null) {
                    d = deviceDao.getByKey(deviceIdsToDelete.get(i));
                    deviceMap.put(d.getKey().getId(), d);
                }
                builder.append(d.getPhoneNumber());
            }
            builder.append("s");
            for (int i = 0; i < surveyIdsToDelete.size(); i++) {
                if (i > 0) {
                    builder.append("xx");
                }
                builder.append(surveyIdsToDelete.get(i).toString());
            }

            DataChangeRecord change = new DataChangeRecord(
                    SurveyAssignment.class.getName(), assignment.getKey()
                            .getId() + "", builder.toString(), "n/");
            Queue queue = QueueFactory.getQueue("dataUpdate");
            queue.add(TaskOptions.Builder
                    .withUrl("/app_worker/dataupdate")
                    .param(DataSummarizationRequest.OBJECT_KEY,
                            assignment.getKey().getId() + "")
                    .param(DataSummarizationRequest.OBJECT_TYPE,
                            "DeviceSurveyJobQueueChange")
                    .param(DataSummarizationRequest.VALUE_KEY,
                            change.packString()));
        }
    }

    private DeviceSurveyJobQueue constructQueueObject(Device d, Survey survey,
            SurveyAssignment assignment) {
        DeviceSurveyJobQueue queueItem = new DeviceSurveyJobQueue();
        queueItem.setDevicePhoneNumber(d.getPhoneNumber());
        queueItem.setEffectiveStartDate(assignment.getStartDate());
        queueItem.setEffectiveEndDate(assignment.getEndDate());
        queueItem.setSurveyID(survey.getKey().getId());
        queueItem.setName(survey.getName());
        queueItem.setLanguage(assignment.getLanguage());
        queueItem.setAssignmentId(assignment.getKey().getId());
        queueItem.setSurveyDistributionStatus(DeviceSurveyJobQueue.DistributionStatus.UNSENT);
        queueItem.setImei(d.getEsn());
        queueItem.setAndroidId(d.getAndroidId());
        return queueItem;
    }

}
