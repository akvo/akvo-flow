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

import com.gallatinsystems.common.util.PropertyUtil;

public class ApkRedirectServlet extends HttpServlet {

	private static final long serialVersionUID = 8394168365501522124L;
	private static final String ANDROID = "androidPhone";
	private static final String FIELDSURVEY = "fieldSurvey";
	private static final String SATSTAT_URL = "satStatUrl";
	private static final String SATSTAT_APP_PATH = "/satstat";
	private static final String FLOW_APP_PATH = "/app";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
			IOException {
	    String servletPath = req.getServletPath();
	    String redirectUrl = null;

	    if(FLOW_APP_PATH.equals(servletPath)) {
		final DeviceApplicationDao dao = new DeviceApplicationDao();
		final List<DeviceApplication> apps = dao
			.listByDeviceTypeAndAppCode(ANDROID, FIELDSURVEY, 1);

		if (apps != null && apps.size() > 0) {
		    redirectUrl = apps.get(0).getFileName();
		}
	    } else if(SATSTAT_APP_PATH.equals(servletPath)) {
		redirectUrl = PropertyUtil.getProperty(SATSTAT_URL);
	    }

	    if(StringUtils.isBlank(redirectUrl)) {
		resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
		resp.getWriter().append("NOT FOUND");
		return;
	    }
	    resp.sendRedirect(redirectUrl);
	}
}
