/*
 *  Copyright (C) 2014 Stichting Akvo (Akvo Foundation)
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

package com.gallatinsystems.device.dao;

import java.util.ArrayList;
import java.util.List;

import com.gallatinsystems.device.domain.DeviceFileJobQueue;
import com.gallatinsystems.framework.dao.BaseDAO;

public class DeviceFileJobQueueDAO extends BaseDAO<DeviceFileJobQueue> {

	public DeviceFileJobQueueDAO() {
		super(DeviceFileJobQueue.class);
	}

	public List<DeviceFileJobQueue> listByDeviceId(Long deviceId) {
		return super.listByProperty("deviceId", deviceId, "Long");
	}

	public List<DeviceFileJobQueue> listByUnknownDevice() {
		return listByDeviceId(null);
	}

	public List<DeviceFileJobQueue> listByDeviceAndFile(Long deviceId,
			String fileName) {

		List<DeviceFileJobQueue> list = super.listByProperty("fileName",
				fileName, "String");
		List<DeviceFileJobQueue> result = new ArrayList<DeviceFileJobQueue>();

		for (DeviceFileJobQueue e : list) {
			// deviceId is null when we don't know the source of the image
			// some device is "reclaiming" it
			if (e.getDeviceId() == null || e.getDeviceId().equals(deviceId)) {
				result.add(e);
			}
		}

		return result;
	}
}
