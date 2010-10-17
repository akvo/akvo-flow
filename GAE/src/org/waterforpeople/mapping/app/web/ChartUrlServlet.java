package org.waterforpeople.mapping.app.web;

import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointSummaryDto;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointSummaryService;
import org.waterforpeople.mapping.app.gwt.server.accesspoint.AccessPointSummaryServiceImpl;
import org.waterforpeople.mapping.app.web.dto.ChartUrlRequest;

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

	private static final String PIE_CHART = "cht=p";
	private static final String CHART_TITLE = "chtt=";
	private static final String CHART_LEGEND = "chdl=";
	private static final String CHART_LEGEND_OPTS = "chdlp=r";
	private static final String CHART_COLORS = "chco=3399CC%2C80C65A%2CFF0000%2CFFCC33%2CBBCCED%2C3399CC";
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
			StringBuilder data = new StringBuilder(CHART_DATA);
			for (int i = 0; i < summaries.length; i++) {
				if (i > 0) {
					labels.append(COMMA);
					data.append(COMMA);
				}
				try {
					labels.append(URLEncoder.encode(summaries[i].getStatus(),
							"UTF-8"));
					data.append(summaries[i].getCount());
				} catch (Exception e) {
					log("Could not encode data", e);
				}
			}
			chartUrl.append(labels).append(AMP).append(data);
		}
		return chartUrl.toString();
	}

}
