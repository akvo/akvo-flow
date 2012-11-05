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

import java.util.List;

import org.waterforpeople.mapping.app.gwt.client.device.DeviceApplicationDto;
import org.waterforpeople.mapping.app.gwt.client.device.DeviceApplicationService;
import org.waterforpeople.mapping.app.util.DtoMarshaller;
import org.waterforpeople.mapping.dao.DeviceApplicationDao;
import org.waterforpeople.mapping.domain.DeviceApplication;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * service for managing deviceApplications
 * 
 * @author Christopher Fagiani
 * 
 */
public class DeviceApplicationServiceImpl extends RemoteServiceServlet
		implements DeviceApplicationService {

	private static final long serialVersionUID = 6909319182429580329L;
	private DeviceApplicationDao devAppDao;

	public DeviceApplicationServiceImpl() {
		devAppDao = new DeviceApplicationDao();
	}

	@Override
	public DeviceApplicationDto getLatestDeviceApplication(String type,
			String appCode) {
		List<DeviceApplication> devAppList = devAppDao
				.listByDeviceTypeAndAppCode(type, appCode, 1);
		DeviceApplicationDto dto = null;
		if (devAppList != null && devAppList.size() > 0) {
			dto = new DeviceApplicationDto();
			DtoMarshaller.copyToDto(devAppList.get(0), dto);
		}
		return dto;
	}

	@Override
	public DeviceApplicationDto save(DeviceApplicationDto dto) {
		if (dto != null) {
			DeviceApplication app = new DeviceApplication();
			DtoMarshaller.copyToCanonical(app, dto);
			app = devAppDao.save(app);
			dto.setKeyId(app.getKey().getId());
		}
		return dto;
	}

}
