package org.waterforpeople.mapping.app.web.dto;

import javax.servlet.http.HttpServletRequest;

import org.waterforpeople.mapping.app.web.KMLGenerator;
import org.waterforpeople.mapping.domain.AccessPoint;
import org.waterforpeople.mapping.domain.AccessPoint.AccessPointType;

import com.gallatinsystems.framework.rest.RestError;
import com.gallatinsystems.framework.rest.RestRequest;

public class PlacemarkRestRequest extends RestRequest {
	private static final String COUNTRY_PARAM = "country";
	private String country;
	private static final String NEED_DETAILS_PARM = "needDetailsFlag";
	private Boolean needDetailsFlag = null;
	private static final String COMMUNITY_CODE_PARAM = "communityCode";
	private static final String POINT_TYPE_PARAM = "pointType";
	private String communityCode = null;
	private AccessPoint.AccessPointType pointType = null;
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
		if (req.getParameter(COMMUNITY_CODE_PARAM) != null) {
			setCommunityCode(req.getParameter(COMMUNITY_CODE_PARAM));
		}
		if (req.getParameter(POINT_TYPE_PARAM) != null) {
			String pointTypeValue = req.getParameter(POINT_TYPE_PARAM);
			if (AccessPoint.AccessPointType.HEALTH_POSTS.equals(pointTypeValue))
				setPointType(AccessPointType.HEALTH_POSTS);
			else if (AccessPointType.PUBLIC_INSTITUTION.equals(pointTypeValue)
					|| KMLGenerator.PUBLIC_INSTITUTION_FUNCTIONING_BLACK_ICON_URL
							.equals(pointTypeValue)
					|| KMLGenerator.PUBLIC_INSTITUTION_FUNCTIONING_GREEN_ICON_URL
							.equals(pointTypeValue)
					|| KMLGenerator.PUBLIC_INSTITUTION_FUNCTIONING_RED_ICON_URL
							.equals(pointTypeValue)
					|| KMLGenerator.PUBLIC_INSTITUTION_FUNCTIONING_YELLOW_ICON_URL
							.equals(pointTypeValue))
				setPointType(AccessPointType.PUBLIC_INSTITUTION);
			else if (AccessPointType.SCHOOL.equals(pointTypeValue)
					|| KMLGenerator.SCHOOL_INSTITUTION_FUNCTIONING_BLACK_ICON_URL
							.equals(pointTypeValue)
					|| KMLGenerator.SCHOOL_INSTITUTION_FUNCTIONING_GREEN_ICON_URL
							.equals(pointTypeValue)
					|| KMLGenerator.SCHOOL_INSTITUTION_FUNCTIONING_RED_ICON_URL
							.equals(pointTypeValue)
					|| KMLGenerator.SCHOOL_INSTITUTION_FUNCTIONING_YELLOW_ICON_URL
							.equals(pointTypeValue))
				setPointType(AccessPointType.SCHOOL);
			else if (pointTypeValue.equals(AccessPointType.WATER_POINT
					.toString())
					|| KMLGenerator.WATER_POINT_FUNCTIONING_BLACK_ICON_URL
							.equals(pointTypeValue)
					|| KMLGenerator.WATER_POINT_FUNCTIONING_GREEN_ICON_URL
							.equals(pointTypeValue)
					|| KMLGenerator.WATER_POINT_FUNCTIONING_RED_ICON_URL
							.equals(pointTypeValue)
					|| KMLGenerator.WATER_POINT_FUNCTIONING_YELLOW_ICON_URL
							.equals(pointTypeValue))
				setPointType(AccessPointType.WATER_POINT);
			else
				addError(new RestError(RestError.BAD_DATATYPE_CODE,
						RestError.BAD_DATATYPE_MESSAGE, POINT_TYPE_PARAM));
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
		if (country == null && super.getAction() == null) {
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

	public void setPointType(AccessPoint.AccessPointType pointType) {
		this.pointType = pointType;
	}

	public AccessPoint.AccessPointType getPointType() {
		return pointType;
	}

	public void setCommunityCode(String communityCode) {
		this.communityCode = communityCode;
	}

	public String getCommunityCode() {
		return communityCode;
	}

}
