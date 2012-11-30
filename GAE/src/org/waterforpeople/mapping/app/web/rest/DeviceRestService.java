/*
 *  Copyright (C) 2012 Stichting Akvo (Akvo Foundation)
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

import javax.inject.Inject;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.waterforpeople.mapping.app.gwt.client.device.DeviceDto;
import org.waterforpeople.mapping.app.util.DtoMarshaller;
import org.waterforpeople.mapping.app.web.rest.dto.DevicePayload;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.device.dao.DeviceDAO;
import com.gallatinsystems.device.domain.Device;

@Controller
@RequestMapping("/devices")
public class DeviceRestService {

	@Inject
	private DeviceDAO deviceDao;

	@RequestMapping(method = RequestMethod.GET, value = "")
	@ResponseBody
	public Map<String, List<DeviceDto>> listDevices() {
		final Map<String, List<DeviceDto>> response = new HashMap<String, List<DeviceDto>>();
		final List<DeviceDto> deviceList = new ArrayList<DeviceDto>();
		final List<Device> devices = deviceDao.list(Constants.ALL_RESULTS);

		if (devices != null) {
			for (Device d : devices) {
				DeviceDto deviceDto = new DeviceDto();
				DtoMarshaller.copyToDto(d, deviceDto);
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
			Device s;

			// if the deviceDto has a key, try to get the device.
			if (keyId != null) {
				s = deviceDao.getByKey(keyId);
				// if we find the device, update it's properties
				if (s != null) {
					// copy the properties, except the createdDateTime property,
					// because it is set in the Dao.
					BeanUtils.copyProperties(deviceDto, s, new String[] {
							"createdDateTime"});
					s = deviceDao.save(s);
					dto = new DeviceDto();
					DtoMarshaller.copyToDto(s, dto);
					statusDto.setStatus("ok");
				}
			}
		}
		response.put("meta", statusDto);
		response.put("device", dto);
		return response;
	}








}
