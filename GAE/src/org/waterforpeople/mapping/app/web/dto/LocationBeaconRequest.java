package org.waterforpeople.mapping.app.web.dto;

import javax.servlet.http.HttpServletRequest;

import com.gallatinsystems.framework.rest.RestError;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.exception.RestValidationException;

/**
 * data structure to capture location beacon calls
 * 
 * @author Christopher Fagiani
 * 
 */
public class LocationBeaconRequest extends RestRequest {

	private static final String PHONE_PARAM = "phoneNumber";
	private static final String LAT_PARAM = "lat";
	private static final String LON_PARAM = "lon";
	private static final String ACC_PARAM = "acc";
	private static final String VER_PARAM = "ver";
	private static final long serialVersionUID = 4549010911554976717L;
	private String phoneNumber;
	private Double lat;
	private Double lon;
	private Double accuracy;
	private String appVersion;

	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getLon() {
		return lon;
	}

	public void setLon(Double lon) {
		this.lon = lon;
	}

	public Double getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(Double accuracy) {
		this.accuracy = accuracy;
	}

	@Override
	protected void populateErrors() {
		if (lon != null && lat == null) {
			addError(new RestError(RestError.MISSING_PARAM_ERROR_CODE,
					RestError.MISSING_PARAM_ERROR_MESSAGE, LAT_PARAM
							+ " cannot be null"));
		}
		if (lat != null && lon == null) {
			addError(new RestError(RestError.MISSING_PARAM_ERROR_CODE,
					RestError.MISSING_PARAM_ERROR_MESSAGE, LON_PARAM
							+ " cannot be null"));
		}
		if (phoneNumber == null) {
			addError(new RestError(RestError.MISSING_PARAM_ERROR_CODE,
					RestError.MISSING_PARAM_ERROR_MESSAGE, PHONE_PARAM
							+ " cannot be null"));
		}
	}

	@Override
	protected void populateFields(HttpServletRequest req) throws Exception {
		phoneNumber = req.getParameter(PHONE_PARAM);
		appVersion = req.getParameter(VER_PARAM);
		try {
			if (req.getParameter(LAT_PARAM) != null) {
				lat = Double.parseDouble(req.getParameter(LAT_PARAM));
			}
			if (req.getParameter(LON_PARAM) != null) {
				lon = Double.parseDouble(req.getParameter(LON_PARAM));
			}
			if (req.getParameter(ACC_PARAM) != null) {
				accuracy = Double.parseDouble(req.getParameter(ACC_PARAM));
			}
		} catch (NumberFormatException e) {
			throw new RestValidationException(new RestError(
					RestError.BAD_DATATYPE_CODE,
					RestError.BAD_DATATYPE_MESSAGE,
					"lat, lon and acc must be doubles"),
					"Lat/lon must be doubles", e);
		}

	}
}
