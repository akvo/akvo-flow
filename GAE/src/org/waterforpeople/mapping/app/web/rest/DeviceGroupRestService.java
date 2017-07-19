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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.waterforpeople.mapping.app.gwt.client.device.DeviceGroupDto;
import org.waterforpeople.mapping.app.web.rest.dto.DeviceGroupPayload;
import org.waterforpeople.mapping.app.web.rest.dto.RestStatusDto;
import com.gallatinsystems.common.Constants;
import com.gallatinsystems.device.dao.DeviceGroupDAO;
import com.gallatinsystems.device.domain.DeviceGroup;

@Controller
@RequestMapping("/device_groups")
public class DeviceGroupRestService {

    private DeviceGroupDAO deviceGroupDao = new DeviceGroupDAO();

    // TODO put in meta information?
    // list all deviceGroups
    @RequestMapping(method = RequestMethod.GET, value = "")
    @ResponseBody
    public Map<String, List<DeviceGroupDto>> listDeviceGroups() {
        final Map<String, List<DeviceGroupDto>> response = new HashMap<String, List<DeviceGroupDto>>();
        List<DeviceGroupDto> results = new ArrayList<DeviceGroupDto>();
        List<DeviceGroup> deviceGroups = deviceGroupDao.list(Constants.ALL_RESULTS);
        if (deviceGroups != null) {
            for (DeviceGroup dg : deviceGroups) {
                DeviceGroupDto dto = marshallToDto(dg);
                results.add(dto);
            }
        }
        response.put("device_groups", results);
        return response;
    }

    // find a single deviceGroup by the deviceGroupId
    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    @ResponseBody
    public Map<String, DeviceGroupDto> findDeviceGroup(
            @PathVariable("id")
            Long id) {
        final Map<String, DeviceGroupDto> response = new HashMap<String, DeviceGroupDto>();
        DeviceGroup dg = deviceGroupDao.getByKey(id);
        DeviceGroupDto dto = null;
        if (dg != null) {
            dto = marshallToDto(dg);
        }
        response.put("device_group", dto);
        return response;

    }

    // delete deviceGroup by id
    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    @ResponseBody
    public Map<String, RestStatusDto> deleteDeviceGroupById(
            @PathVariable("id")
            Long id) {
        final Map<String, RestStatusDto> response = new HashMap<String, RestStatusDto>();
        DeviceGroup dg = deviceGroupDao.getByKey(id);
        RestStatusDto statusDto = null;
        statusDto = new RestStatusDto();
        statusDto.setStatus("failed");

        // check if deviceGroup exists in the datastore
        if (dg != null) {
            // delete deviceGroup group
            deviceGroupDao.delete(dg);
            statusDto.setStatus("ok");
        }
        response.put("meta", statusDto);
        return response;
    }

    // update existing deviceGroup
    @RequestMapping(method = RequestMethod.PUT, value = "/{id}")
    @ResponseBody
    public Map<String, Object> saveExistingDeviceGroup(
            @RequestBody
            DeviceGroupPayload payLoad) {
        final DeviceGroupDto deviceGroupDto = payLoad.getDevice_group();
        final Map<String, Object> response = new HashMap<String, Object>();
        DeviceGroupDto dto = null;

        RestStatusDto statusDto = new RestStatusDto();
        statusDto.setStatus("failed");

        // if the POST data contains a valid deviceGroupDto, continue.
        // Otherwise,
        // server will respond with 400 Bad Request
        if (deviceGroupDto != null) {
            Long keyId = deviceGroupDto.getKeyId();
            DeviceGroup dg;

            // if the deviceGroupDto has a key, try to get the deviceGroup.
            if (keyId != null) {
                dg = deviceGroupDao.getByKey(keyId);
                // if we find the deviceGroup, update it's properties
                if (dg != null) {
                    // copy the properties, except the createdDateTime property,
                    // because it is set in the Dao.
                    BeanUtils.copyProperties(deviceGroupDto, dg,
                            new String[] {
                                "createdDateTime"
                            });
                    dg = deviceGroupDao.save(dg);
                    dto = marshallToDto(dg);
                    statusDto.setStatus("ok");
                }
            }
        }
        response.put("meta", statusDto);
        response.put("device_group", dto);
        return response;
    }

    // create new deviceGroup
    @RequestMapping(method = RequestMethod.POST, value = "")
    @ResponseBody
    public Map<String, Object> saveNewDeviceGroup(
            @RequestBody
            DeviceGroupPayload payLoad) {
        final DeviceGroupDto deviceGroupDto = payLoad.getDevice_group();
        final Map<String, Object> response = new HashMap<String, Object>();
        DeviceGroupDto dto = null;

        RestStatusDto statusDto = new RestStatusDto();
        statusDto.setStatus("failed");

        // if the POST data contains a valid deviceGroupDto, continue.
        // Otherwise,
        // server will respond with 400 Bad Request
        if (deviceGroupDto != null) {
            DeviceGroup dg = new DeviceGroup();

            // copy the properties, except the createdDateTime property, because
            // it is set in the Dao.
            BeanUtils.copyProperties(deviceGroupDto, dg,
                    new String[] {
                        "createdDateTime"
                    });
            dg = deviceGroupDao.save(dg);

            dto = marshallToDto(dg);
            statusDto.setStatus("ok");
        }

        response.put("meta", statusDto);
        response.put("device_group", dto);
        return response;
    }
    
    private DeviceGroupDto marshallToDto(DeviceGroup sa) {
        final DeviceGroupDto dto = new DeviceGroupDto();

        BeanUtils.copyProperties(sa, dto);
        if (sa.getKey() != null) {
            dto.setKeyId(sa.getKey().getId());
        }

        return dto;
    }

}
