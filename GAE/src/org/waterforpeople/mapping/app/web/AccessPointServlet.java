package org.waterforpeople.mapping.app.web;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointDto;
import org.waterforpeople.mapping.app.util.AccessPointServiceSupport;
import org.waterforpeople.mapping.app.web.dto.AccessPointRequest;
import org.waterforpeople.mapping.app.web.dto.AccessPointResponse;
import org.waterforpeople.mapping.dao.AccessPointDao;
import org.waterforpeople.mapping.domain.AccessPoint;

import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;
import com.gallatinsystems.gis.location.GeoLocationService;
import com.gallatinsystems.gis.location.GeoLocationServiceGeonamesImpl;

/**
 * JSON service for returning the list of access points near a specific lat/lon
 * point.
 * 
 * @author Christopher Fagiani
 */
public class AccessPointServlet extends AbstractRestApiServlet {

	private static final long serialVersionUID = 8748650927754433019L;
	private static final int MAX_DISTANCE_METERS = 10000;
	private AccessPointDao accessPointDao;
	private GeoLocationService geoService;

	public AccessPointServlet() {
		setMode(JSON_MODE);
		accessPointDao = new AccessPointDao();
		geoService = new GeoLocationServiceGeonamesImpl();
	}

	@Override
	protected RestRequest convertRequest() throws Exception {
		HttpServletRequest req = getRequest();
		RestRequest restRequest = new AccessPointRequest();
		restRequest.populateFromHttpRequest(req);
		return restRequest;
	}

	/**
	 * calls the accessPointDao to get the list of access points near the point
	 * passed in via the request
	 */
	@Override
	protected RestResponse handleRequest(RestRequest req) throws Exception {
		AccessPointRequest apReq = (AccessPointRequest) req;
		List<AccessPoint> result = null;
		String cursor = null;
		if (AccessPointRequest.NEARBY_ACTION.equalsIgnoreCase(req.getAction())) {
			result = accessPointDao.listNearbyAccessPoints(apReq.getLat(),
					apReq.getLon(), apReq.getCountry() == null ? geoService
							.getCountryCodeForPoint(apReq.getLat().toString(),
									apReq.getLon().toString()) : apReq
							.getCountry(), MAX_DISTANCE_METERS, apReq
							.getCursor());
		} else if (AccessPointRequest.SEARCH_ACTION.equalsIgnoreCase(req
				.getAction())) {
			result = accessPointDao.searchAccessPoints(apReq.getCountry(),
					apReq.getCommunity(), apReq.getCollectionDateFrom(), apReq
							.getCollectionDateTo(), apReq.getType(), null,
					apReq.getConstructionDateFrom(), apReq
							.getConstructionDateTo(), null, null, apReq
							.getCursor());		
		}		
		if(result != null && result.size()>0){
			cursor = AccessPointDao.getCursor(result);
		}

		return convertToResponse(result,cursor);
	}

	/**
	 * converts the domain objects to dtos and then installs them in an
	 * AccessPointResponse object
	 */
	protected AccessPointResponse convertToResponse(List<AccessPoint> apList, String cursor) {
		AccessPointResponse resp = new AccessPointResponse();
		if (apList != null) {
			List<AccessPointDto> dtoList = new ArrayList<AccessPointDto>();
			for (AccessPoint ap : apList) {
				dtoList.add(AccessPointServiceSupport.copyCanonicalToDto(ap));
			}
			resp.setAccessPointDto(dtoList);
			resp.setCursor(cursor);
		}
		return resp;
	}

	/**
	 * writes response as a JSON string
	 */
	@Override
	protected void writeOkResponse(RestResponse resp) throws Exception {
		getResponse().setStatus(200);
		JSONObject obj = new JSONObject(resp);
		getResponse().getWriter().println(obj.toString());
	}
}
