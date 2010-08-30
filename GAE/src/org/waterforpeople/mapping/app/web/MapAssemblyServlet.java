package org.waterforpeople.mapping.app.web;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.waterforpeople.mapping.app.web.dto.MapAssemblyRestRequest;
import org.waterforpeople.mapping.helper.KMLHelper;

import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;

public class MapAssemblyServlet extends AbstractRestApiServlet {
	private static final Logger log = Logger.getLogger(MapAssemblyServlet.class
			.getName());
	/**
	 * 
	 */
	private static final long serialVersionUID = 7615652730572144228L;

	@Override
	protected RestRequest convertRequest() throws Exception {
		HttpServletRequest req = getRequest();
		RestRequest restRequest = new MapAssemblyRestRequest();
		restRequest.populateFromHttpRequest(req);
		return restRequest;
	}

	@Override
	protected RestResponse handleRequest(RestRequest req) throws Exception {
		MapAssemblyRestRequest importReq = (MapAssemblyRestRequest) req;
		if ("buildMap".equals(importReq.getAction())) {
			KMLHelper kmlHelper = new KMLHelper();
			//kmlHelper.buildMap();
		}
		return null;
	}

	@Override
	protected void writeOkResponse(RestResponse resp) throws Exception {
		// TODO Auto-generated method stub

	}

}
