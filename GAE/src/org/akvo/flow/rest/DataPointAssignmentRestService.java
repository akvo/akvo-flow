/*
 *  Copyright (C) 2019,2020 Stichting Akvo (Akvo Foundation)
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

import org.akvo.flow.dao.DataPointAssignmentDao;
import org.akvo.flow.domain.persistent.DataPointAssignment;
import org.akvo.flow.rest.dto.DataPointAssignmentDto;
import org.akvo.flow.rest.dto.DataPointAssignmentPayload;
import org.springframework.beans.BeanUtils;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.waterforpeople.mapping.app.web.rest.ResourceNotFoundException;
import org.waterforpeople.mapping.app.web.rest.dto.RestStatusDto;

import com.gallatinsystems.common.Constants;
import com.google.appengine.api.datastore.KeyFactory;


@Controller
@RequestMapping("/data_point_assignments")
public class DataPointAssignmentRestService {

    private DataPointAssignmentDao dataPointAssignmentDao = new DataPointAssignmentDao();

    @RequestMapping(method = RequestMethod.GET, value = "")
    @ResponseBody
    public Map<String, List<DataPointAssignmentDto>> listSomeOrAll(
            @RequestParam(value = "surveyAssignmentId", required = false) Long surveyAssignmentId,
            @RequestParam(value = "deviceId", required = false) Long deviceId
        ) {
        final HashMap<String, List<DataPointAssignmentDto>> response = new HashMap<String, List<DataPointAssignmentDto>>();
        final List<DataPointAssignmentDto> results = new ArrayList<DataPointAssignmentDto>();

        if (deviceId != null && surveyAssignmentId != null) {
            for (DataPointAssignment dpa : dataPointAssignmentDao.listByDeviceAndSurveyAssignment(deviceId,surveyAssignmentId)) {
                results.add(marshallToDto(dpa));
            }
        } else if (surveyAssignmentId != null) {
            for (DataPointAssignment dpa : dataPointAssignmentDao.listBySurveyAssignment(surveyAssignmentId)) {
                results.add(marshallToDto(dpa));
            }
        } else if (deviceId != null) {
            for (DataPointAssignment dpa : dataPointAssignmentDao.listByDevice(deviceId)) {
                results.add(marshallToDto(dpa));
            }
        } else { //All of them
            for (DataPointAssignment dpa : dataPointAssignmentDao.list(Constants.ALL_RESULTS)) {
                results.add(marshallToDto(dpa));
            }
        }
        response.put("data_point_assignments", results);
        return response;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    @ResponseBody
    public Map<String, DataPointAssignmentDto> getById(@PathVariable("id")
    Long id) {
        final HashMap<String, DataPointAssignmentDto> response = new HashMap<String, DataPointAssignmentDto>();
        final DataPointAssignment dpa = dataPointAssignmentDao.getByKey(id);

        if (dpa == null) {
            throw new ResourceNotFoundException(
                    "Datapoint Assignment with id: " + id + " not found");
        }

        response.put("data_point_assignment", marshallToDto(dpa));
        return response;
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    @ResponseBody
    public Map<String, RestStatusDto> deleteById(@PathVariable("id")
    Long id) {
        final HashMap<String, RestStatusDto> response = new HashMap<String, RestStatusDto>();
        final DataPointAssignment dpa = dataPointAssignmentDao.getByKey(id);
        RestStatusDto statusDto = new RestStatusDto();
        statusDto.setStatus("failed");
        if (dpa != null) {
            //Nothing depends on this; just delete
            dataPointAssignmentDao.delete(dpa);
            statusDto.setStatus("ok");
        }

        response.put("meta", statusDto);
        return response;
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{id}")
    @ResponseBody
    public Map<String, DataPointAssignmentDto> updateDataPointAssignment(
            @PathVariable("id")
            Long id,
            @RequestBody
            DataPointAssignmentPayload payload) {

        final DataPointAssignmentDto dto = payload.getData_point_assignment();
        if (!id.equals(dto.getKeyId())) {
            throw new HttpMessageNotReadableException("Ids don't match: " + id
                    + " <> " + dto.getKeyId());
        }
        final DataPointAssignment assignment = dataPointAssignmentDao.getByKey(dto.getKeyId());
        if (assignment == null) {
            throw new ResourceNotFoundException(
                    "DataPointAssignment with id: " + dto.getKeyId() + " not found");
        }

        final DataPointAssignment dpa = marshallToDomain(dto);
        dataPointAssignmentDao.save(dpa);

        final HashMap<String, DataPointAssignmentDto> response = new HashMap<String, DataPointAssignmentDto>();
        response.put("data_point_assignment", marshallToDto(dpa));

        return response;
    }

    @RequestMapping(method = RequestMethod.POST, value = "")
    @ResponseBody
    public Map<String, Object> newDataPointAssignment(
            @RequestBody
            DataPointAssignmentPayload payload) {

        final Map<String, Object> response = new HashMap<String, Object>();

        RestStatusDto statusDto = new RestStatusDto();
        statusDto.setStatus("failed");
        statusDto.setMessage("missing required parameters");

        final DataPointAssignment dpa  = marshallToDomain(payload.getData_point_assignment());
        if (dpa != null
                && dpa.getDeviceId() != null
                && dpa.getSurveyId() != null
                && dpa.getSurveyAssignmentId() != null) {

            dataPointAssignmentDao.save(dpa); //fills in new key
            response.put("data_point_assignment", marshallToDto(dpa));
            statusDto.setStatus("ok");
            statusDto.setMessage("");
        }
        response.put("meta", statusDto);
        return response;
    }

    private DataPointAssignmentDto marshallToDto(DataPointAssignment dpa) {
        final DataPointAssignmentDto dto = new DataPointAssignmentDto();

        BeanUtils.copyProperties(dpa, dto);
        if (dpa.getKey() != null) {
            dto.setKeyId(dpa.getKey().getId());
        }

        return dto;
    }

    private DataPointAssignment marshallToDomain(DataPointAssignmentDto dto) {
        final DataPointAssignment dpa = new DataPointAssignment();

        BeanUtils.copyProperties(dto, dpa);
        if (dto.getKeyId() != null) {
            dpa.setKey(KeyFactory.createKey("DataPointAssignment", dto.getKeyId()));
        }

        return dpa;
    }


}
