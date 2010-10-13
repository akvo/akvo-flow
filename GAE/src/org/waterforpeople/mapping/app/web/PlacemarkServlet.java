package org.waterforpeople.mapping.app.web;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.waterforpeople.mapping.app.gwt.client.location.PlacemarkDto;
import org.waterforpeople.mapping.app.web.dto.PlacemarkRestRequest;
import org.waterforpeople.mapping.app.web.dto.PlacemarkRestResponse;
import org.waterforpeople.mapping.dao.AccessPointDao;
import org.waterforpeople.mapping.domain.AccessPoint;
import org.waterforpeople.mapping.domain.AccessPoint.AccessPointType;
import org.waterforpeople.mapping.domain.AccessPoint.Status;

import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;

public class PlacemarkServlet extends AbstractRestApiServlet {
	private static final Logger log = Logger.getLogger(PlacemarkServlet.class
			.getName());

	/**
	 * 
	 */
	private static final long serialVersionUID = -9031594440737716966L;

	@Override
	protected RestRequest convertRequest() throws Exception {
		HttpServletRequest req = getRequest();
		RestRequest restRequest = new PlacemarkRestRequest();
		restRequest.populateFromHttpRequest(req);
		return restRequest;

	}

	@Override
	protected RestResponse handleRequest(RestRequest req) throws Exception {
		PlacemarkRestRequest piReq = (PlacemarkRestRequest) req;
		AccessPointDao apDao = new AccessPointDao();
		List<AccessPoint> results = apDao.searchAccessPoints(
				piReq.getCountry(), null, null, null, "WATER_POINT", null,
				null, null, null, null, piReq.getCursor());
		return convertToResponse(results, AccessPointDao.getCursor(results));
	}

	private RestResponse convertToResponse(List<AccessPoint> apList,
			String cursor) {
		PlacemarkRestResponse resp = new PlacemarkRestResponse();
		if (apList != null) {
			List<PlacemarkDto> dtoList = new ArrayList<PlacemarkDto>();
			KMLGenerator kmlGen = new KMLGenerator();
			for (AccessPoint ap : apList) {
				PlacemarkDto pdto = new PlacemarkDto();
				pdto.setLatitude(ap.getLatitude());
				pdto.setLongitude(ap.getLongitude());
				pdto.setIconUrl(getUrlFromStatus(ap.getPointStatus(), ap.getPointType()));
				String placemarkString = null;
				try {
					placemarkString = kmlGen.bindPlacemark(ap,
							"placemarkExternalMap.vm");
					pdto.setPlacemarkContents(placemarkString);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (placemarkString != null)
					dtoList.add(pdto);
			}
			resp.setPlacemarks(dtoList);
		}
		resp.setCursor(cursor);
		return resp;
	}

	private String getUrlFromStatus(Status status,
			AccessPoint.AccessPointType pointType) {
		if (status == null) {
			return "Unknown";
		}
		if (AccessPointType.WATER_POINT.equals(pointType)) {
			if (status.equals(AccessPoint.Status.FUNCTIONING_HIGH)) {
				return KMLGenerator.WATER_POINT_FUNCTIONING_GREEN_ICON_URL;
			} else if (status.equals(AccessPoint.Status.FUNCTIONING_OK)
					|| status
							.equals(AccessPoint.Status.FUNCTIONING_WITH_PROBLEMS)) {
				return KMLGenerator.WATER_POINT_FUNCTIONING_YELLOW_ICON_URL;
			} else if (status.equals(AccessPoint.Status.BROKEN_DOWN)) {
				return KMLGenerator.WATER_POINT_FUNCTIONING_RED_ICON_URL;
			} else if (status.equals(AccessPoint.Status.NO_IMPROVED_SYSTEM)) {
				return KMLGenerator.WATER_POINT_FUNCTIONING_BLACK_ICON_URL;
			} else {
				return KMLGenerator.WATER_POINT_FUNCTIONING_BLACK_ICON_URL;
			}
		}else if(AccessPointType.PUBLIC_INSTITUTION.equals(pointType)){
			if (status.equals(AccessPoint.Status.FUNCTIONING_HIGH)) {
				return KMLGenerator.PUBLIC_INSTITUTION_FUNCTIONING_GREEN_ICON_URL;
			} else if (status.equals(AccessPoint.Status.FUNCTIONING_OK)
					|| status
							.equals(AccessPoint.Status.FUNCTIONING_WITH_PROBLEMS)) {
				return KMLGenerator.PUBLIC_INSTITUTION_FUNCTIONING_YELLOW_ICON_URL;
			} else if (status.equals(AccessPoint.Status.BROKEN_DOWN)) {
				return KMLGenerator.PUBLIC_INSTITUTION_FUNCTIONING_RED_ICON_URL;
			} else if (status.equals(AccessPoint.Status.NO_IMPROVED_SYSTEM)) {
				return KMLGenerator.PUBLIC_INSTITUTION_FUNCTIONING_BLACK_ICON_URL;
			} else {
				return KMLGenerator.PUBLIC_INSTITUTION_FUNCTIONING_BLACK_ICON_URL;
			}
		}else if(AccessPointType.SCHOOL.equals(pointType)){
			if (status.equals(AccessPoint.Status.FUNCTIONING_HIGH)) {
				return KMLGenerator.SCHOOL_INSTITUTION_FUNCTIONING_GREEN_ICON_URL;
			} else if (status.equals(AccessPoint.Status.FUNCTIONING_OK)
					|| status
							.equals(AccessPoint.Status.FUNCTIONING_WITH_PROBLEMS)) {
				return KMLGenerator.SCHOOL_INSTITUTION_FUNCTIONING_YELLOW_ICON_URL;
			} else if (status.equals(AccessPoint.Status.BROKEN_DOWN)) {
				return KMLGenerator.SCHOOL_INSTITUTION_FUNCTIONING_RED_ICON_URL;
			} else if (status.equals(AccessPoint.Status.NO_IMPROVED_SYSTEM)) {
				return KMLGenerator.SCHOOL_INSTITUTION_FUNCTIONING_BLACK_ICON_URL;
			} else {
				return KMLGenerator.SCHOOL_INSTITUTION_FUNCTIONING_BLACK_ICON_URL;
			}
		}
		return null;

	}

	@Override
	protected void writeOkResponse(RestResponse resp) throws Exception {
		getResponse().setStatus(200);
		PlacemarkRestResponse piResp = (PlacemarkRestResponse) resp;
		JSONObject result = new JSONObject(piResp);
		getResponse().getWriter().println(result.toString());
	}
}
