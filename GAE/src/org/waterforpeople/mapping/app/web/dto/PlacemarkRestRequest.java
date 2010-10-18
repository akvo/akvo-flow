package org.waterforpeople.mapping.app.web.dto;

import javax.servlet.http.HttpServletRequest;

import com.gallatinsystems.framework.rest.RestError;
import com.gallatinsystems.framework.rest.RestRequest;

public class PlacemarkRestRequest extends RestRequest {
	private static final String COUNTRY_PARAM = "country";
	private String country;
	private static final String NEED_DETAILS_PARM = "needDetailsFlag";
	private Boolean needDetailsFlag=null;
	/**
	 * 
	 */
	private static final long serialVersionUID = -3977305417999591917L;

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
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
		try {
			if (req.getParameter(NEED_DETAILS_PARM) != null) {
				setNeedDetailsFlag(new Boolean(req.getParameter(
						NEED_DETAILS_PARM).toLowerCase()));
			}
		} catch (Exception ex) {
			addError(new RestError(RestError.MISSING_PARAM_ERROR_CODE,
					RestError.MISSING_PARAM_ERROR_MESSAGE, NEED_DETAILS_PARM));
		}
	}

	@Override
	protected void populateErrors() {
		if (country == null) {
			addError(new RestError(RestError.MISSING_PARAM_ERROR_CODE,
					RestError.MISSING_PARAM_ERROR_MESSAGE, COUNTRY_PARAM));
		}
	}

	public void setNeedDetailsFlag(Boolean needDetailsFlag) {
		this.needDetailsFlag = needDetailsFlag;
	}

	public Boolean getNeedDetailsFlag() {
		return needDetailsFlag;
	}

}
