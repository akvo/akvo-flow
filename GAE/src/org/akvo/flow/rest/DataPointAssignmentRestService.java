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
import org.springframework.web.bind.annotation.ResponseBody;
import org.waterforpeople.mapping.app.web.rest.ResourceNotFoundException;
import org.waterforpeople.mapping.app.web.rest.dto.RestStatusDto;

import com.gallatinsystems.common.Constants;
import com.google.appengine.api.datastore.KeyFactory;


@Controller
@RequestMapping("/datapoint_assignments")
public class DataPointAssignmentRestService {

    private DataPointAssignmentDao dataPointAssignmentDao = new DataPointAssignmentDao();

    @RequestMapping(method = RequestMethod.GET, value = "")
    @ResponseBody
    public Map<String, List<DataPointAssignmentDto>> listAll() {
        final HashMap<String, List<DataPointAssignmentDto>> response = new HashMap<String, List<DataPointAssignmentDto>>();
        final List<DataPointAssignmentDto> results = new ArrayList<DataPointAssignmentDto>();

        for (DataPointAssignment dpa : dataPointAssignmentDao.list(Constants.ALL_RESULTS)) {
            results.add(marshallToDto(dpa));
        }

        response.put("datapoint_assignments", results);
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
                    "Survey Assignment with id: " + id + " not found");
        }

        response.put("datapoint_assignment", marshallToDto(dpa));
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
    public Map<String, DataPointAssignmentDto> updateSurveyAssignment(
            @PathVariable("id")
            Long id,
            @RequestBody
            DataPointAssignmentPayload payload) {

        final DataPointAssignmentDto dto = payload.getDatapoint_assignment();
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
        response.put("datapoint_assignment", marshallToDto(dpa));

        return response;
    }

    @RequestMapping(method = RequestMethod.POST, value = "")
    @ResponseBody
    public Map<String, DataPointAssignmentDto> newSurveyAssignment(
            @RequestBody
            DataPointAssignmentPayload payload) {

        final DataPointAssignmentDto dto = payload.getDatapoint_assignment();
        final DataPointAssignment dpa  = marshallToDomain(dto);

        dataPointAssignmentDao.save(dpa); //fills in new key

        final HashMap<String, DataPointAssignmentDto> response = new HashMap<String, DataPointAssignmentDto>();
        response.put("datapoint_assignment", marshallToDto(dpa));
        return response;
    }

    private DataPointAssignmentDto marshallToDto(DataPointAssignment dpa) {
        final DataPointAssignmentDto dto = new DataPointAssignmentDto();

        BeanUtils.copyProperties(dpa, dto);
        if (dpa.getKey() != null) {
            dto.setKeyId(dpa.getKey().getId());
        }
        //Lists must be copied separately
        dto.setDataPointIds(dpa.getDataPointIds());

        return dto;
    }

    private DataPointAssignment marshallToDomain(DataPointAssignmentDto dto) {
        final DataPointAssignment dpa = new DataPointAssignment();

        BeanUtils.copyProperties(dto, dpa);
        if (dto.getKeyId() != null) {
            dpa.setKey(KeyFactory.createKey("DataPointAssignment", dto.getKeyId()));
        }
        //Lists must be copied separately
        dpa.setDataPointIds(dto.getDataPointIds());

        return dpa;
    }


}
