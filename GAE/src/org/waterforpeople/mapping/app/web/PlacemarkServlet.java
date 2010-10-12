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
				piReq.getCountry(), null, null, null, null, null, null, null,
				null, null, piReq.getCursor());
		return convertToResponse(results, AccessPointDao.getCursor(results));
	}

	private RestResponse convertToResponse(List<AccessPoint> apList,
			String cursor) {
		PlacemarkRestResponse resp = new PlacemarkRestResponse();
		if (apList != null) {
			List<PlacemarkDto> dtoList = new ArrayList<PlacemarkDto>();
			KMLGenerator kmlGen =new KMLGenerator();
			for (AccessPoint ap : apList) {
				PlacemarkDto pdto = new PlacemarkDto();
				pdto.setLatitude(ap.getLatitude());
				pdto.setLongitude(ap.getLongitude());
				pdto.setIconUrl(getUrlFromStatus(ap.getPointStatus()));
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

	private String getUrlFromStatus(Status pointStatus) {
		// TODO Auto-generated method stub
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
