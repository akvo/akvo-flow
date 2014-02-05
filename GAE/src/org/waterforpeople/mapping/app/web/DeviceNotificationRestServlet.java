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

package org.waterforpeople.mapping.app.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.waterforpeople.mapping.app.web.dto.DeviceNotificationRequest;
import org.waterforpeople.mapping.app.web.dto.DeviceNotificationResponse;

import com.gallatinsystems.device.dao.DeviceDAO;
import com.gallatinsystems.device.dao.DeviceFileJobQueueDAO;
import com.gallatinsystems.device.domain.Device;
import com.gallatinsystems.device.domain.DeviceFileJobQueue;
import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;

public class DeviceNotificationRestServlet extends AbstractRestApiServlet {

	private static final long serialVersionUID = -2243167279214074216L;

	public DeviceNotificationRestServlet() {
		super();
		setMode(JSON_MODE);
	}

	@Override
	protected RestRequest convertRequest() throws Exception {
		HttpServletRequest req = getRequest();
		RestRequest restRequest = new DeviceNotificationRequest();
		restRequest.populateFromHttpRequest(req);
		return restRequest;
	}

	@Override
	protected RestResponse handleRequest(RestRequest req) throws Exception {
		DeviceNotificationRequest dnReq = (DeviceNotificationRequest) req;
		Device d = getDevice(dnReq);

		if (d == null) {
			return new RestResponse();
		}

		DeviceNotificationResponse resp = new DeviceNotificationResponse();
		DeviceFileJobQueueDAO jobDao = new DeviceFileJobQueueDAO();

		List<DeviceFileJobQueue> missingByDevice = jobDao.listByDeviceId(d
				.getKey().getId());
		List<DeviceFileJobQueue> missingUnknown = jobDao.listByUnknownDevice();

		resp.setMissingFiles(missingByDevice);
		resp.setMissingUnknown(missingUnknown);

		return resp;
	}

	@Override
	protected void writeOkResponse(RestResponse resp) throws Exception {
		getResponse().setStatus(200);
		JSONObject obj = new JSONObject(resp);
		getResponse().getWriter().println(obj.toString());
	}

	private Device getDevice(DeviceNotificationRequest req) {
		DeviceDAO deviceDao = new DeviceDAO();

		if (req.getImei() != null) {
			return deviceDao.getByImei(req.getImei().trim());

		}

		if (req.getPhoneNumber() != null) {
			return deviceDao.get(req.getPhoneNumber().trim());
		}

		return null;
	}

}
