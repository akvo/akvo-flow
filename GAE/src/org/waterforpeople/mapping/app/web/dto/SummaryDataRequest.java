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
	public static final String SUB_VALUE_PARAM = "subValue";
	public static final String METRIC_NAME_PARAM = "metricName";
	public static final String SUB_LEVEL_PARAM = "subLevel";
	public static final String INCLUDE_PLACEMARK_PARAM = "includePlacemark";

	private String country;
	private String organization;
	private String district;
	private Long year;
	private String subValue = null;
	private Integer subLevel = null;
	private Boolean includePlacemarkFlag = false;

	public Integer getSubLevel() {
		return subLevel;
	}

	public void setSubLevel(Integer subLevel) {
		this.subLevel = subLevel;
	}

	public String getSubValue() {
		return subValue;
	}

	public void setSubValue(String subValue) {
		this.subValue = subValue;
	}

	public String getMetricName() {
		return metricName;
	}

	public void setMetricName(String metricName) {
		this.metricName = metricName;
	}

	private String metricName = null;

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
		if (req.getParameter(SUB_VALUE_PARAM) != null) {
			setSubValue(req.getParameter(SUB_VALUE_PARAM));
		}
		if (req.getParameter(METRIC_NAME_PARAM) != null) {
			setMetricName(req.getParameter(METRIC_NAME_PARAM));
		}
		if (req.getParameter(SUB_LEVEL_PARAM) != null) {
			String subLevel = req.getParameter(SUB_LEVEL_PARAM);
			subLevel = subLevel.replace("i", "");
			setSubLevel(Integer.parseInt(subLevel));
		}
		if (req.getParameter(INCLUDE_PLACEMARK_PARAM) != null) {
			setIncludePlacemarkFlag(Boolean.parseBoolean(req
					.getParameter(INCLUDE_PLACEMARK_PARAM)));
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

	public void setIncludePlacemarkFlag(Boolean includePlacemarkFlag) {
		this.includePlacemarkFlag = includePlacemarkFlag;
	}

	public Boolean getIncludePlacemarkFlag() {
		return includePlacemarkFlag;
	}

}
