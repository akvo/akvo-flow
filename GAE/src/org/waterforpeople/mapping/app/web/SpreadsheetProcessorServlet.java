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

package org.waterforpeople.mapping.app.web;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.waterforpeople.mapping.adapter.SpreadsheetAccessPointAdapter;
import org.waterforpeople.mapping.domain.AccessPoint;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;

public class SpreadsheetProcessorServlet extends AbstractRestApiServlet {

	private static final long serialVersionUID = -2270875281089527752L;
	private static final Logger log = Logger
			.getLogger(SpreadsheetProcessorServlet.class.getName());

	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		String spreadsheetName = req.getParameter("spreadsheetName");
		String listColumns = req.getParameter("listColumns");
		SpreadsheetAccessPointAdapter sapa = new SpreadsheetAccessPointAdapter(
				null, null);
		String clearAccessPointFlag = req.getParameter("clearAccessPointFlag");

		if (clearAccessPointFlag.equals("doIt")) {
			BaseDAO<AccessPoint> baseDAO = new BaseDAO<AccessPoint>(
					AccessPoint.class);
			List<AccessPoint> apList = baseDAO.list(Constants.ALL_RESULTS);
			for (AccessPoint item : apList) {
				try {
					resp.getWriter().println("Deleting: " + item.toString());
				} catch (IOException e) {
					log.log(Level.SEVERE, "Could not write to response", e);
				}
				baseDAO.delete(item);
			}

			try {
				resp.getWriter().println(
						"FINISHED ACCESSPOINT TABLE SHOULD BE EMPTY");
			} catch (IOException e) {
				log.log(Level.SEVERE, "Could not process spreadsheet", e);
			}
		} else if (!spreadsheetName.trim().isEmpty()) {
			if (listColumns != null && listColumns.equals("true")) {
				try {
					StringBuilder sb = new StringBuilder();

					for (String item : sapa.listColumns(spreadsheetName)) {
						sb.append("column: " + item + "\n");
					}
					resp.getWriter().print(sb.toString());
				} catch (Exception e) {
					log.log(Level.SEVERE, "Could not process sheet", e);
				}
			} else {
				try {
					sapa.processSpreadsheetOfAccessPoints(spreadsheetName);
				} catch (Exception e1) {
					log.log(Level.SEVERE, "Could not process ap sheet", e1);
				}
				try {
					resp.getWriter().print("AccessPoints have been loaded");
				} catch (IOException e) {
					log.log(Level.SEVERE, "Could not write to response", e);
				}
			}
		}
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) {

	}

	@Override
	protected RestRequest convertRequest() throws Exception {
		// no-op
		return null;
	}

	@Override
	protected RestResponse handleRequest(RestRequest req) throws Exception {
		// no-op
		return null;
	}

	@Override
	protected void writeOkResponse(RestResponse resp) throws Exception {
		// no-op

	}

}
