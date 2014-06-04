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

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.waterforpeople.mapping.dao.DeviceApplicationDao;
import org.waterforpeople.mapping.domain.DeviceApplication;

public class ApkRedirectServlet extends HttpServlet {

	private static final long serialVersionUID = 8394168365501522124L;
	private static final String ANDROID = "androidPhone";
	private static final String FIELDSURVEY = "fieldSurvey";
	private static final String SATSTAT = "satStat";
	private static final String GPS_APP_PATH = "/gps";
	private static final String FLOW_APP_PATH = "/app";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
			IOException {
	    final DeviceApplicationDao dao = new DeviceApplicationDao();
	    String servletPath = req.getServletPath();
	    String appCode = "";

	    if(FLOW_APP_PATH.equals(servletPath)) {
		appCode = FIELDSURVEY;
	    } else if(GPS_APP_PATH.equals(servletPath)) {
		appCode = SATSTAT;
	    }

	    final List<DeviceApplication> apps = dao
		    .listByDeviceTypeAndAppCode(ANDROID, appCode, 1);
	    if (apps == null
		    || apps.size() == 0
		    || StringUtils.isBlank(apps.get(0).getFileName())) {
		resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
		resp.getWriter().append("NOT FOUND");
		return;
	    }

	    resp.sendRedirect(apps.get(0).getFileName());
	}
}
