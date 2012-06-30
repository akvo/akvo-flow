/*
 *  Copyright (C) 2010-2012 Stichting Akvo (Akvo Foundation)
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

package org.waterforpeople.mapping.app.gwt.server.device;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.waterforpeople.mapping.app.gwt.client.device.DeviceDto;
import org.waterforpeople.mapping.app.gwt.client.device.DeviceService;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.device.dao.DeviceDAO;
import com.gallatinsystems.device.domain.Device;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Service allowing listing/saving of Device objects
 * 
 * @author Christopher Fagiani
 * 
 */
public class DeviceServiceImpl extends RemoteServiceServlet implements
		DeviceService {

	private static final long serialVersionUID = -3606845978482271221L;
	private static final String UNASSIGNED_GROUP = "Unassigned";

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(DeviceServiceImpl.class
			.getName());

	@Override
	public List<DeviceDto> listDevice() {

		DeviceDAO deviceDao = new DeviceDAO();
		List<Device> devices = deviceDao.list(Constants.ALL_RESULTS);
		List<DeviceDto> deviceDtos = new ArrayList<DeviceDto>();
		if (devices != null) {

			for (int i = 0; i < devices.size(); i++) {
				deviceDtos.add(marshalDevice(devices.get(i)));
			}
		}
		Collections.sort(deviceDtos);
		return deviceDtos;
	}

	/**
	 * converts a device domain object to a dto
	 * 
	 * @param d
	 * @return
	 */
	private DeviceDto marshalDevice(Device d) {
		DeviceDto dto = new DeviceDto();
		dto.setPhoneNumber(d.getPhoneNumber());
		dto.setEsn(d.getEsn());
		dto.setLastKnownAccuracy(d.getLastKnownAccuracy());
		dto.setLastKnownLat(d.getLastKnownLat());
		dto.setLastKnownLon(d.getLastKnownLon());
		dto.setLastPositionDate(d.getLastLocationBeaconTime());
		dto.setDeviceGroup(d.getDeviceGroup());
		dto.setKeyId(d.getKey().getId());
		dto.setDeviceIdentifier(d.getDeviceIdentifier());
		return dto;
	}

	/**
	 * lists all devices and groups them by group name
	 * 
	 * @return
	 */
	public HashMap<String, ArrayList<DeviceDto>> listDeviceByGroup() {
		HashMap<String, ArrayList<DeviceDto>> groupedDevices = new HashMap<String, ArrayList<DeviceDto>>();
		List<DeviceDto> dtos = listDevice();
		if (dtos != null) {
			for (DeviceDto d : dtos) {
				String groupName = d.getDeviceGroup();
				if (groupName == null) {
					groupName = UNASSIGNED_GROUP;
				}
				ArrayList<DeviceDto> dtoList = groupedDevices.get(groupName);
				if (dtoList == null) {
					dtoList = new ArrayList<DeviceDto>();
					groupedDevices.put(groupName, dtoList);
				}
				dtoList.add(d);
			}
		}
		for (ArrayList<DeviceDto> list : groupedDevices.values()) {
			Collections.sort(list);
		}

		return groupedDevices;

	}

	/**
	 * finds a device by its phone number
	 * 
	 * @param phoneNumber
	 * @return
	 */
	public DeviceDto findDeviceByPhoneNumber(String phoneNumber) {
		DeviceDAO dao = new DeviceDAO();
		Device d = dao.get(phoneNumber);
		if (d != null) {
			return marshalDevice(d);
		} else {
			return null;
		}
	}
}
