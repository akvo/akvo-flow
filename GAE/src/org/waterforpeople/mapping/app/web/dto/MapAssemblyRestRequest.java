package org.waterforpeople.mapping.app.web.dto;

import javax.servlet.http.HttpServletRequest;

import com.gallatinsystems.framework.rest.RestRequest;

public class MapAssemblyRestRequest extends RestRequest {
	private final String countryCodeParam = "countryCode";
	private final String techTypeCodeParam = "techType";

	private String countryCode = null;
	private String techType = null;

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getTechType() {
		return techType;
	}

	public void setTechType(String techType) {
		this.techType = techType;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 96808403833612464L;

	@Override
	protected void populateErrors() {

	}

	@Override
	protected void populateFields(HttpServletRequest req) throws Exception {
		if (req.getParameter(countryCodeParam) != null)
			setCountryCode(req.getParameter(countryCodeParam));
		if(req.getParameter(techTypeCodeParam)!=null)
			setTechType(req.getParameter(techTypeCodeParam));
	}

}
