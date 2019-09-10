/*
 *  Copyright (C) 2012,2017,2019 Stichting Akvo (Akvo Foundation)
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

package org.akvo.flow.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.akvo.flow.dao.SurveyAssignmentDao;
import org.akvo.flow.domain.persistent.SurveyAssignment;
import org.akvo.flow.rest.dto.SurveyAssignmentDto;
import org.akvo.flow.rest.dto.SurveyAssignmentPayload;
import org.springframework.beans.BeanUtils;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.waterforpeople.mapping.app.web.rest.ResourceNotFoundException;
import org.waterforpeople.mapping.app.web.rest.dto.RestStatusDto;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.device.dao.DeviceDAO;
import com.gallatinsystems.device.domain.Device;
import com.gallatinsystems.device.domain.DeviceSurveyJobQueue;
import com.gallatinsystems.survey.dao.DeviceSurveyJobQueueDAO;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.domain.Survey;
import com.google.appengine.api.datastore.KeyFactory;

@Controller
@RequestMapping("/survey_assignments")
public class SurveyAssignmentRestService {

    private SurveyAssignmentDao surveyAssignmentDao = new SurveyAssignmentDao();

    private DeviceDAO deviceDao = new DeviceDAO();

    private SurveyDAO surveyDao = new SurveyDAO();

    private DeviceSurveyJobQueueDAO deviceSurveyJobQueueDAO = new DeviceSurveyJobQueueDAO();

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
            throw new ResourceNotFoundException(
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
        RestStatusDto statusDto = new RestStatusDto();
        statusDto.setStatus("failed");
        if (sa != null) {
            deleteExistingDeviceSurveyJobQueueItems(sa);
            surveyAssignmentDao.delete(sa);
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
        final SurveyAssignment assignment = surveyAssignmentDao.getByKey(dto.getKeyId());
        if (assignment == null) {
            throw new ResourceNotFoundException(
                    "Survey Assignment with id: " + dto.getKeyId() + " not found");
        }

        deleteExistingDeviceSurveyJobQueueItems(assignment);

        final SurveyAssignment sa = marshallToDomain(dto);
        surveyAssignmentDao.save(sa);
        List<DeviceSurveyJobQueue> deviceSurveyJobQueues = generateDeviceSurveyJobQueueItems(sa);
        deviceSurveyJobQueueDAO.save(deviceSurveyJobQueues);

        final HashMap<String, SurveyAssignmentDto> response = new HashMap<String, SurveyAssignmentDto>();
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
        List<DeviceSurveyJobQueue> deviceSurveyJobQueues = generateDeviceSurveyJobQueueItems(sa);
        deviceSurveyJobQueueDAO.save(deviceSurveyJobQueues);

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
        dto.setDeviceIds(sa.getDeviceIds());
        dto.setFormIds(sa.getFormIds());

        return dto;
    }

    private SurveyAssignment marshallToDomain(SurveyAssignmentDto dto) {
        final SurveyAssignment sa = new SurveyAssignment();

        BeanUtils.copyProperties(dto, sa);
        if (dto.getKeyId() != null) {
            sa.setKey(KeyFactory.createKey("SurveyAssignment", dto.getKeyId()));
        }
        sa.setDeviceIds(dto.getDeviceIds());
        sa.setFormIds(dto.getFormIds());

        return sa;
    }

    private void deleteExistingDeviceSurveyJobQueueItems(SurveyAssignment assignment) {
        List<DeviceSurveyJobQueue> deviceAssignmentsToDelete = deviceSurveyJobQueueDAO.listJobByAssignment(assignment.getKey().getId());
        deviceSurveyJobQueueDAO.delete(deviceAssignmentsToDelete);
    }

    /**
     * creates and saves DeviceSurveyJobQueue objects for each device/survey pair in the assignment.
     */
    private List<DeviceSurveyJobQueue> generateDeviceSurveyJobQueueItems(SurveyAssignment assignment) {
        List<DeviceSurveyJobQueue> deviceSurveyJobQueues = new ArrayList<>();
        List<Survey> forms = surveyDao.listByKeys(assignment.getFormIds());
        List<Device> devices = deviceDao.listByKeys(assignment.getDeviceIds());

        for (Survey form : forms) {
            for(Device device : devices) {
                deviceSurveyJobQueues.add(constructQueueObject(device, form, assignment));
            }
        }
        return deviceSurveyJobQueues;
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
