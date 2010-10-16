package org.waterforpeople.mapping.app.web.dto;

import javax.servlet.http.HttpServletRequest;

import com.gallatinsystems.framework.rest.RestError;
import com.gallatinsystems.framework.rest.RestRequest;

/**
 * encapsulates requests for summary data
 * 
 * @author Christopher Fagaini
 * 
 */
public class SummaryDataRequest extends RestRequest {

	private static final long serialVersionUID = -4988289418881606314L;

	public static final String GET_AP_METRIC_SUMMARY_ACTION = "getAPMetricSummary";

	public static final String ORG_PARAM = "org";
	public static final String COUNTRY_PARAM = "country";
	public static final String DISTRICT_PARAM = "district";
	public static final String YEAR_PARAM = "year";

	private String country;
	private String organization;
	private String district;
	private Long year;

	@Override
	protected void populateErrors() {
		if (GET_AP_METRIC_SUMMARY_ACTION.equalsIgnoreCase(getAction())) {
			if (country == null && organization == null && district == null) {
				addError(new RestError(RestError.MISSING_PARAM_ERROR_CODE,
						RestError.MISSING_PARAM_ERROR_MESSAGE,
						"At least 1 parameter (org, country, district) must be specified"));
			}
		}
	}

	@Override
	protected void populateFields(HttpServletRequest req) throws Exception {
		country = req.getParameter(COUNTRY_PARAM);
		organization = req.getParameter(ORG_PARAM);
		district = req.getParameter(DISTRICT_PARAM);
		if (req.getParameter(YEAR_PARAM) != null) {
			year = Long.parseLong(req.getParameter(YEAR_PARAM));
		}
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public Long getYear() {
		return year;
	}

	public void setYear(Long year) {
		this.year = year;
	}

}
