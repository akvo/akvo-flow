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

package org.waterforpeople.mapping.app.gwt.client.device;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("devicerpcservice")
public interface DeviceService extends RemoteService {

	public List<DeviceDto> listDevice();

	/**
	 * lists all devices and groups them by group name
	 * 
	 * @return
	 */
	public HashMap<String, ArrayList<DeviceDto>> listDeviceByGroup();

	/**
	 * finds a device by its phone number
	 * 
	 * @param phoneNumber
	 * @return
	 */
	public DeviceDto findDeviceByPhoneNumber(String phoneNumber);

}
