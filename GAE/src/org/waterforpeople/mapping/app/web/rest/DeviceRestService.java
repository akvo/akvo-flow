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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.waterforpeople.mapping.app.gwt.client.device.DeviceDto;
import org.waterforpeople.mapping.app.util.DtoMarshaller;
import org.waterforpeople.mapping.app.web.rest.dto.DevicePayload;
import org.waterforpeople.mapping.app.web.rest.dto.RestStatusDto;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.device.dao.DeviceDAO;
import com.gallatinsystems.device.dao.DeviceGroupDAO;
import com.gallatinsystems.device.domain.Device;
import com.gallatinsystems.device.domain.DeviceGroup;

@Controller
@RequestMapping("/devices")
public class DeviceRestService {

    private DeviceDAO deviceDao = new DeviceDAO();

    private DeviceGroupDAO deviceGroupDao = new DeviceGroupDAO();

    @RequestMapping(method = RequestMethod.GET, value = "")
    @ResponseBody
    public Map<String, List<DeviceDto>> listDevices(
            @RequestParam(value = "ids[]", defaultValue = "") Long[] ids) {
        final Map<String, List<DeviceDto>> response = new HashMap<String, List<DeviceDto>>();
        final List<DeviceDto> deviceList = new ArrayList<DeviceDto>();
        List<Device> devices = null;

        if (ids[0] == null) {
            devices = deviceDao.list(Constants.ALL_RESULTS);
        } else {
            devices = deviceDao.listByKeys(ids);
        }

        if (devices != null) {
            // get the device group names
            List<DeviceGroup> deviceGroups = deviceGroupDao
                    .list(Constants.ALL_RESULTS);
            final Map<String, String> dgNames = new HashMap<String, String>();
            for (DeviceGroup dg : deviceGroups) {
                dgNames.put(Long.toString(dg.getKey().getId()), dg.getCode());
            }
            for (Device d : devices) {
                DeviceDto deviceDto = new DeviceDto(d);
                String deviceGroupName = "";
                if (d.getDeviceGroup() != null) {
                    deviceGroupName = dgNames.get(d.getDeviceGroup().trim());
                }
                deviceDto.setDeviceGroupName(deviceGroupName);
                deviceDto.setLastPositionDate(d.getLastLocationBeaconTime());
                deviceList.add(deviceDto);
            }
        }
        response.put("devices", deviceList);
        return response;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    @ResponseBody
    public Map<String, DeviceDto> findDevice(@PathVariable("id") Long id) {
        final Map<String, DeviceDto> response = new HashMap<String, DeviceDto>();
        final Device d = deviceDao.getByKey(id);
        DeviceDto deviceDto = null;
        if (d != null) {
            deviceDto = new DeviceDto();
            DtoMarshaller.copyToDto(d, deviceDto);
            deviceDto.setLastPositionDate(d.getLastLocationBeaconTime());

            // add device group name
            if (d.getDeviceGroup() != null && d.getDeviceGroup() != "") {
                DeviceGroup dg = deviceGroupDao.getByKey(Long.parseLong(d.getDeviceGroup()));
                if (dg != null) {
                    deviceDto.setDeviceGroupName(dg.getCode());
                }
            }
        }
        response.put("device", deviceDto);
        return response;
    }

    // update existing device
    @RequestMapping(method = RequestMethod.PUT, value = "/{id}")
    @ResponseBody
    public Map<String, Object> saveExistingDevice(
            @RequestBody DevicePayload payLoad) {
        final DeviceDto deviceDto = payLoad.getDevice();
        final Map<String, Object> response = new HashMap<String, Object>();
        DeviceDto dto = null;

        RestStatusDto statusDto = new RestStatusDto();
        statusDto.setStatus("failed");

        // if the POST data contains a valid deviceDto, continue. Otherwise,
        // server will respond with 400 Bad Request
        if (deviceDto != null) {
            Long keyId = deviceDto.getKeyId();
            Device d;

            // if the deviceDto has a key, try to get the device.
            if (keyId != null) {
                d = deviceDao.getByKey(keyId);
                // if we find the device, update it's properties
                if (d != null) {
                    // copy the properties, except the createdDateTime property,
                    // because it is set in the Dao, and the deviceGroupName, which is just for the
                    // client.
                    BeanUtils.copyProperties(deviceDto, d,
                            new String[] {
                                    "createdDateTime", "lastPositionDate", "deviceGroupName"
                            });
                    d = deviceDao.save(d);
                    dto = new DeviceDto();
                    DtoMarshaller.copyToDto(d, dto);

                    // add device group name
                    if (d.getDeviceGroup() != null && d.getDeviceGroup() != "") {
                        DeviceGroup dg = deviceGroupDao
                                .getByKey(Long.parseLong(d.getDeviceGroup()));
                        if (dg != null) {
                            deviceDto.setDeviceGroupName(dg.getCode());
                        }
                    }
                    dto.setLastPositionDate(d.getLastLocationBeaconTime());
                    statusDto.setStatus("ok");
                }
            }
        }
        response.put("meta", statusDto);
        response.put("device", dto);
        return response;
    }

}
