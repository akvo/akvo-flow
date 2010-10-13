package org.waterforpeople.mapping.app.web;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.waterforpeople.mapping.app.gwt.client.community.CommunityDto;
import org.waterforpeople.mapping.app.gwt.client.community.CountryDto;
import org.waterforpeople.mapping.app.web.dto.GeoRequest;
import org.waterforpeople.mapping.app.web.dto.GeoResponse;
import org.waterforpeople.mapping.dao.CommunityDao;
import org.waterforpeople.mapping.domain.Community;

import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestError;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;
import com.gallatinsystems.framework.rest.exception.RestException;
import com.gallatinsystems.gis.geography.dao.CountryDao;
import com.gallatinsystems.gis.geography.domain.Country;

/**
 * servlet to return country/communities as JSON objects
 * 
 * @author Christopher Fagiani
 * 
 */
public class GeoServlet extends AbstractRestApiServlet {

	private static final long serialVersionUID = -7534864780109561623L;
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(GeoServlet.class
			.getName());
	private CountryDao countryDao;
	private CommunityDao communityDao;

	public GeoServlet() {
		countryDao = new CountryDao();
		communityDao = new CommunityDao();
	}

	@Override
	protected RestRequest convertRequest() throws Exception {
		HttpServletRequest req = getRequest();
		RestRequest restRequest = new GeoRequest();
		restRequest.populateFromHttpRequest(req);
		return restRequest;
	}

	/**
	 * calls the accessPointDao to get the list of access points near the point
	 * passed in via the request
	 */
	@Override
	protected RestResponse handleRequest(RestRequest req) throws Exception {
		GeoRequest geoReq = (GeoRequest) req;
		if (GeoRequest.LIST_COUNTRY_ACTION.equalsIgnoreCase(geoReq.getAction())) {
			return convertToResponse(countryDao.list(CountryDao.CURSOR_TYPE.all
					.toString()), null);

		} else if (GeoRequest.LIST_COMMUNITY_ACTION.equalsIgnoreCase(geoReq
				.getAction())) {
			return convertToResponse(null, communityDao
					.listCommunityByCountry(geoReq.getCountry()));
		} else {
			throw new RestException(new RestError(
					RestError.MISSING_PARAM_ERROR_CODE, "Unrecognized Action",
					"Action is not valid"), "Action not valid", null);
		}
	}

	/**
	 * converts the domain objects to dtos and then installs them in an
	 * GeoResponse object
	 */
	protected GeoResponse convertToResponse(List<Country> countries,
			List<Community> communities) {
		GeoResponse resp = new GeoResponse();
		if (countries != null) {
			List<CountryDto> dtoList = new ArrayList<CountryDto>();
			for (Country c : countries) {
				CountryDto dto = new CountryDto();
				dto.setIsoAlpha2Code(c.getIsoAlpha2Code());
				dto.setIsoAlpha3Code(c.getIsoAlpha3Code());
				dto.setIsoNumeric3Code(c.getIsoNumeric3Code());
				dto.setName(c.getName());
				dtoList.add(dto);
			}
			resp.setCountries(dtoList);
		} else if (communities != null) {
			List<CommunityDto> dtoList = new ArrayList<CommunityDto>();
			for (Community c : communities) {
				CommunityDto dto = new CommunityDto();
				dto.setCommunityCode(c.getCommunityCode());
				dtoList.add(dto);
			}
			resp.setCommunities(dtoList);
		}
		return resp;
	}

	/**
	 * writes response as a JSON string
	 */
	@Override
	protected void writeOkResponse(RestResponse resp) throws Exception {
		getResponse().setStatus(200);
		if (resp != null) {
			GeoResponse geoResp = (GeoResponse) resp;
			JSONObject result = new JSONObject(geoResp);
			getResponse().getWriter().println(result.toString());
		}
	}
}
