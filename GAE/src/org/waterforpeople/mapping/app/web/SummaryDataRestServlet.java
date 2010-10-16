package org.waterforpeople.mapping.app.web;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.waterforpeople.mapping.analytics.dao.AccessPointMetricSummaryDao;
import org.waterforpeople.mapping.analytics.domain.AccessPointMetricSummary;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointMetricSummaryDto;
import org.waterforpeople.mapping.app.util.DtoMarshaller;
import org.waterforpeople.mapping.app.web.dto.SummaryDataRequest;
import org.waterforpeople.mapping.app.web.dto.SummaryDataResponse;

import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;

/**
 * handles requests for summary data
 * 
 * @author Christopher Fagiani
 * 
 */
public class SummaryDataRestServlet extends AbstractRestApiServlet {

	private static final long serialVersionUID = 7550953090927763716L;
	private AccessPointMetricSummaryDao apMetricSummaryDao;

	public SummaryDataRestServlet() {
		setMode(JSON_MODE);
		apMetricSummaryDao = new AccessPointMetricSummaryDao();
	}

	@Override
	protected RestRequest convertRequest() throws Exception {
		HttpServletRequest req = getRequest();
		RestRequest restRequest = new SummaryDataRequest();
		restRequest.populateFromHttpRequest(req);
		return restRequest;
	}

	@Override
	protected RestResponse handleRequest(RestRequest req) throws Exception {
		SummaryDataRequest dataReq = (SummaryDataRequest) req;
		SummaryDataResponse response = new SummaryDataResponse();
		if (SummaryDataRequest.GET_AP_METRIC_SUMMARY_ACTION
				.equalsIgnoreCase(dataReq.getAction())) {
			response
					.setDtoList(convertAccessPointMetric(seachAPMetrics(dataReq)));
		}
		return response;
	}

	@Override
	protected void writeOkResponse(RestResponse resp) throws Exception {
		getResponse().setStatus(200);
		JSONObject obj = new JSONObject(resp, true);
		getResponse().getWriter().println(obj.toString());
	}

	/**
	 * searches the ap metrics based on values in the request
	 * 
	 * @param dataReq
	 * @return
	 */
	private List<AccessPointMetricSummary> seachAPMetrics(
			SummaryDataRequest dataReq) {
		AccessPointMetricSummary prototype = new AccessPointMetricSummary();
		prototype.setCountry(dataReq.getCountry());
		prototype.setOrganization(dataReq.getOrganization());
		prototype.setDistrict(dataReq.getDistrict());
		prototype.setYear(dataReq.getYear());
		return apMetricSummaryDao.listMetrics(prototype);
	}

	/**
	 * converts all summary objects in the list to a AccessPointMetricSummaryDto
	 * and return in a list
	 * 
	 * @param summaryList
	 * @return
	 */
	private List<AccessPointMetricSummaryDto> convertAccessPointMetric(
			List<AccessPointMetricSummary> summaryList) {
		List<AccessPointMetricSummaryDto> dtoList = new ArrayList<AccessPointMetricSummaryDto>();
		if (summaryList != null) {
			for (AccessPointMetricSummary summary : summaryList) {
				AccessPointMetricSummaryDto dto = new AccessPointMetricSummaryDto();
				DtoMarshaller.copyToDto(summary, dto);
				dtoList.add(dto);
			}
		}
		return dtoList;
	}
}
