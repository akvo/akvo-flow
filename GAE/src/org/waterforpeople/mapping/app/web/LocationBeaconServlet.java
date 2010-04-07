package org.waterforpeople.mapping.app.web;

import javax.servlet.http.HttpServletRequest;

import org.waterforpeople.mapping.app.web.dto.LocationBeaconRequest;

import com.gallatinsystems.device.dao.DeviceDAO;
import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;

/**
 * Simple service that can capture Location Beacons and update device records
 * with their last-known position
 * 
 * @author Christopher Fagiani
 * 
 */
public class LocationBeaconServlet extends AbstractRestApiServlet {

	private static final long serialVersionUID = 8337560827269082733L;

	@Override
	protected RestRequest convertRequest() throws Exception {
		HttpServletRequest req = getRequest();
		RestRequest restRequest = new LocationBeaconRequest();
		restRequest.populateFromHttpRequest(req);
		return restRequest;

	}

	@Override
	protected RestResponse handleRequest(RestRequest req) throws Exception {
		RestResponse resp = new RestResponse();
		LocationBeaconRequest lbReq = (LocationBeaconRequest) req;
		DeviceDAO deviceDao = new DeviceDAO();
		deviceDao.updateDeviceLocation(lbReq.getPhoneNumber(), lbReq.getLat(),
				lbReq.getLon(), lbReq.getAccuracy());
		return resp;
	}

	@Override
	protected void writeOkResponse(RestResponse resp) throws Exception {
		getResponse().setStatus(200);
	}

}
