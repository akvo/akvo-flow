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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointSummaryDto;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointSummaryService;
import org.waterforpeople.mapping.app.gwt.server.accesspoint.AccessPointSummaryServiceImpl;
import org.waterforpeople.mapping.app.web.dto.ChartUrlRequest;
import org.waterforpeople.mapping.domain.AccessPoint;

import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;

/**
 * this servlet is a convenience utility for generating google chart api URLs so
 * we can use them easily outside the dashboard
 * 
 * @author Christopher Fagiani
 * 
 */
public class ChartUrlServlet extends AbstractRestApiServlet {

	private static final long serialVersionUID = 505295603257969099L;
	private static final int WIDTH = 400;
	private static final int HEIGHT = 400;
	private static final String AP_STATUS_TITLE = "Point Status";

	private static final String BASE_URL = "http://chart.apis.google.com/chart?chs="
			+ HEIGHT + "x" + WIDTH;
	private static final String AMP = "&amp;";
	private static final String COMMA = "%2C";
	private static final String PIPE = "%7C";

	private static final String PIE_CHART = "cht=p";
	private static final String CHART_TITLE = "chtt=";
	private static final String CHART_LEGEND = "chdl=";
	private static final String CHART_LEGEND_OPTS = "chdlp=r";
	private static final String CHART_LEGEND_VALS = "chl=";
	// color key: functioning, func with problems, broken, no system
	private static final String CHART_COLORS = "chco=bed73d%2Cfcba63%2Ce54046%2C231f20";
	private static final String CHART_DATA = "chd=t%3A";

	private AccessPointSummaryService apStatusSummaryService;

	public ChartUrlServlet() {
		setMode(JSON_MODE);
		apStatusSummaryService = new AccessPointSummaryServiceImpl();
	}

	@Override
	protected RestRequest convertRequest() throws Exception {
		HttpServletRequest req = getRequest();
		RestRequest restRequest = new ChartUrlRequest();
		restRequest.populateFromHttpRequest(req);
		return restRequest;
	}

	@Override
	protected RestResponse handleRequest(RestRequest req) throws Exception {
		ChartUrlRequest chartReq = (ChartUrlRequest) req;
		RestResponse response = new RestResponse();
		if (ChartUrlRequest.GET_AP_STATUS_SUMMARY_ACTION
				.equalsIgnoreCase(chartReq.getAction())) {
			response.setMessage(getAPSummaryForCountry(chartReq.getCountry()));
		}
		return response;
	}

	@Override
	protected void writeOkResponse(RestResponse resp) throws Exception {
		getResponse().setStatus(200);
		JSONObject result = new JSONObject(resp);
		getResponse().getWriter().println(result.toString());
	}

	/**
	 * constructs a google charts api url for a pie chart showing AP status
	 * breakdown in a country
	 * 
	 * @param country
	 * @return
	 */
	private String getAPSummaryForCountry(String country) {
		StringBuilder chartUrl = new StringBuilder();
		// use the existing service method since it handles the roll-up logic
		// for us
		AccessPointSummaryDto[] summaries = apStatusSummaryService
				.listAccessPointStatusSummary(country, null, null, null, null);
		if (summaries != null && summaries.length > 0) {
			chartUrl.append(BASE_URL).append(AMP).append(PIE_CHART).append(AMP)
					.append(CHART_TITLE).append(AP_STATUS_TITLE).append(AMP)
					.append(CHART_LEGEND_OPTS).append(AMP).append(CHART_COLORS)
					.append(AMP);
			StringBuilder labels = new StringBuilder(CHART_LEGEND);
			StringBuilder labelVals = new StringBuilder(CHART_LEGEND_VALS);
			StringBuilder data = new StringBuilder(CHART_DATA);
			// we need to add the data in a specific order (and collapse some
			// outdated status values) so the chart colors match the legend
			Map<String, Long> countMap = new HashMap<String, Long>();
			for (int i = 0; i < summaries.length; i++) {
				countMap.put(summaries[i].getStatus(), summaries[i].getCount());
			}

			labels.append("Functioning").append(PIPE);
			labels.append("Functioning with problems").append(PIPE);
			labels.append("Broken").append(PIPE);
			labels.append("No improved system");

			data.append(
					nullSafeAdd(
							countMap.get(AccessPoint.Status.FUNCTIONING_HIGH
									.toString()), 0L)).append(COMMA);
			labelVals.append(
					nullSafeAdd(
							countMap.get(AccessPoint.Status.FUNCTIONING_HIGH
									.toString()), 0L)).append(PIPE);
			
			data.append(
					nullSafeAdd(countMap.get(AccessPoint.Status.FUNCTIONING_OK
							.toString()), countMap
							.get(AccessPoint.Status.FUNCTIONING_WITH_PROBLEMS
									.toString()))).append(COMMA);
			labelVals.append(
					nullSafeAdd(countMap.get(AccessPoint.Status.FUNCTIONING_OK
							.toString()), countMap
							.get(AccessPoint.Status.FUNCTIONING_WITH_PROBLEMS
									.toString()))).append(PIPE);
			data.append(
					nullSafeAdd(countMap.get(AccessPoint.Status.BROKEN_DOWN
							.toString()), 0L)).append(COMMA);
			labelVals.append(
					nullSafeAdd(countMap.get(AccessPoint.Status.BROKEN_DOWN
							.toString()), 0L)).append(PIPE);
			
			data.append(nullSafeAdd(countMap
					.get(AccessPoint.Status.NO_IMPROVED_SYSTEM.toString()),
					countMap.get(AccessPoint.Status.OTHER.toString())));
			labelVals.append(nullSafeAdd(countMap
					.get(AccessPoint.Status.NO_IMPROVED_SYSTEM.toString()),
					countMap.get(AccessPoint.Status.OTHER.toString())));

			chartUrl.append(labels).append(AMP).append(labelVals).append(AMP).append(data);
		}
		return chartUrl.toString();
	}

	private Long nullSafeAdd(Long val1, Long val2) {
		if (val1 == null) {
			val1 = 0L;
		}
		if (val2 == null) {
			val2 = 0L;
		}
		return val1 + val2;
	}
}
