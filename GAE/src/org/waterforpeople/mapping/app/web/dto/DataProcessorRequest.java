package org.waterforpeople.mapping.app.web.dto;

import javax.servlet.http.HttpServletRequest;

import com.gallatinsystems.framework.rest.RestRequest;

/**
 * Handles requests to the DataProcessing Rest Service
 * 
 * @author Christopher Fagiani
 * 
 */
public class DataProcessorRequest extends RestRequest {
	private static final long serialVersionUID = -4553663867954174523L;
	public static final String PROJECT_FLAG_UPDATE_ACTION = "projectFlagUpdate";
	public static final String COUNTRY_PARAM = "country";
	private String country;

	@Override
	protected void populateFields(HttpServletRequest req) throws Exception {
		country = req.getParameter(COUNTRY_PARAM);

	}

	@Override
	protected void populateErrors() {
		// no-op

	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

}
