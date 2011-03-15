package org.waterforpeople.mapping.app.web;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.waterforpeople.mapping.analytics.dao.AccessPointMetricSummaryDao;
import org.waterforpeople.mapping.analytics.domain.AccessPointMetricSummary;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointMetricSummaryDto;
import org.waterforpeople.mapping.app.util.DtoMarshaller;
import org.waterforpeople.mapping.app.web.dto.SummaryDataRequest;
import org.waterforpeople.mapping.app.web.dto.SummaryDataResponse;

import com.gallatinsystems.common.util.PropertyUtil;
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
	private static final String IMAGE_ROOT = "imageroot";

	private static final Logger log = Logger
	.getLogger(SummaryDataRestServlet.class.getName());

	private static final long serialVersionUID = 7550953090927763716L;
	private AccessPointMetricSummaryDao apMetricSummaryDao;
	private static String imageRoot;
	public SummaryDataRestServlet() {
		setMode(JSON_MODE);
		apMetricSummaryDao = new AccessPointMetricSummaryDao();
		imageRoot = PropertyUtil.getProperty(IMAGE_ROOT);
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
			response.setDtoList(convertAccessPointMetric(
					seachAPMetrics(dataReq), dataReq.getIncludePlacemarkFlag()));
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
		// prototype.setDistrict(dataReq.getDistrict());
		prototype.setMetricName(dataReq.getMetricName());
		if (dataReq.getSubValue() != null)
			prototype.setSubLevelName(dataReq.getSubValue());
		if (dataReq.getSubLevel() != null)
			prototype.setSubLevel(dataReq.getSubLevel());
		if(dataReq.getAccessPointType()!=null)
			prototype.setMetricValue(dataReq.getAccessPointType());
		if(dataReq.getParentSubPath()!=null)
			prototype.setParentSubName(dataReq.getParentSubPath());
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
			List<AccessPointMetricSummary> summaryList, Boolean includePlacemark) {
		List<AccessPointMetricSummaryDto> dtoList = new ArrayList<AccessPointMetricSummaryDto>();
		if (summaryList != null) {
			for (AccessPointMetricSummary summary : summaryList) {
				AccessPointMetricSummaryDto dto = new AccessPointMetricSummaryDto();
				DtoMarshaller.copyToDto(summary, dto);
				if (includePlacemark) {
					dto.setIconUrl(getIconUrl(dto));
					try {
						dto.setPlacemarkContents(generatePlacemarkContents(dto));
					} catch (Exception ex) {
						log.log(Level.INFO, "couldn't bind summary placemark: " + ex.getMessage());
					}
				}
				dtoList.add(dto);
			}
		}
		return dtoList;
	}

	private String getIconUrl(AccessPointMetricSummaryDto dto) {
		return imageRoot + "/images/solidOrange64.png";
	}

	private String generatePlacemarkContents(AccessPointMetricSummaryDto dto)
			throws Exception {
		KMLGenerator kmlGen = new KMLGenerator();

		return kmlGen.bindSummaryPlacemark(dto,
				"summaryPlacemarkExternalMap.vm");
	}
}
