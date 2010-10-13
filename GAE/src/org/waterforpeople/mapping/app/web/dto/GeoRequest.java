package org.waterforpeople.mapping.app.web.dto;

import javax.servlet.http.HttpServletRequest;

import com.gallatinsystems.framework.rest.RestError;
import com.gallatinsystems.framework.rest.RestRequest;

/**
 * request for GeoServlet calls
 * 
 * @author Christopher Fagiani
 * 
 */
public class GeoRequest extends RestRequest {
	private static final long serialVersionUID = 3938671447495497433L;

	public static final String LIST_COUNTRY_ACTION = "getCountries";
	public static final String LIST_COMMUNITY_ACTION = "getCommunities";
	public static final String COUNTRY_PARAM = "country";
	private String country;

	@Override
	protected void populateErrors() {
		if (LIST_COMMUNITY_ACTION.equals(getAction()) && country == null) {
			addError(new RestError(RestError.MISSING_PARAM_ERROR_CODE,
					RestError.MISSING_PARAM_ERROR_MESSAGE, COUNTRY_PARAM));
		}
	}

	@Override
	protected void populateFields(HttpServletRequest req) throws Exception {
		country = req.getParameter(COUNTRY_PARAM);
		if (country != null) {
			country = country.trim().toUpperCase();
			if (country.length() == 0) {
				country = null;
			}
		}
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
}
