package org.waterforpeople.mapping.app.web.dto;

import javax.servlet.http.HttpServletRequest;

import org.waterforpeople.mapping.app.web.KMLGenerator;
import org.waterforpeople.mapping.domain.AccessPoint;
import org.waterforpeople.mapping.domain.AccessPoint.AccessPointType;

import com.gallatinsystems.framework.rest.RestError;
import com.gallatinsystems.framework.rest.RestRequest;

public class PlacemarkRestRequest extends RestRequest {
	public static final String GET_AP_DETAILS_ACTION = "getAPDetails";
	public static final String LIST_PLACEMARK = "listPlacemarks";
	private static final String COUNTRY_PARAM = "country";
	private static final String NEED_DETAILS_PARM = "needDetailsFlag";
	private static final String COMMUNITY_CODE_PARAM = "communityCode";
	private static final String POINT_TYPE_PARAM = "pointType";
	private static final String DISPLAY_TYPE_PARAM = "display";
	private static final String IGNORE_CACHE_PARAM ="ignoreCache";
	private static final String SUB_LEVEL_PARAM = "subLevel";
	private static final String SUB_LEVEL_VALUE = "subLevelValue";
	private static final String LAT1_PARAM = "lat1";
	private static final String LONG1_PARAM = "long1";
	private static final String LAT2_PARAM = "lat2";
	private static final String LONG2_PARAM = "long2";
	private String country;
	private Boolean needDetailsFlag = null;
	private String communityCode = null;
	private String display;
	private AccessPoint.AccessPointType pointType = null;
	private Boolean ignoreCache = false;
	private Integer subLevel = null;
	private String subLevelValue = null;
	private Double lat1 = null;
	private Double lat2 = null;
	private Double long1 = null;
	private Double long2 = null;
	
	public Double getLat1() {
		return lat1;
	}

	public void setLat1(Double lat1) {
		this.lat1 = lat1;
	}

	public Double getLat2() {
		return lat2;
	}

	public void setLat2(Double lat2) {
		this.lat2 = lat2;
	}

	public Double getLong1() {
		return long1;
	}

	public void setLong1(Double long1) {
		this.long1 = long1;
	}

	public Double getLong2() {
		return long2;
	}

	public void setLong2(Double long2) {
		this.long2 = long2;
	}
	public String getSubLevelValue() {
		return subLevelValue;
	}

	public void setSubLevelValue(String subLevelValue) {
		this.subLevelValue = subLevelValue;
	}

	public Integer getSubLevel() {
		return subLevel;
	}

	public void setSubLevel(Integer subLevel) {
		this.subLevel = subLevel;
	}

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
		if(req.getParameter(SUB_LEVEL_PARAM)!=null){
			setSubLevel(Integer.parseInt(req.getParameter(SUB_LEVEL_PARAM)));
		}
		if(req.getParameter(SUB_LEVEL_VALUE)!=null){
			setSubLevelValue(req.getParameter(SUB_LEVEL_VALUE));
		}
		if (req.getParameter(COMMUNITY_CODE_PARAM) != null) {
			setCommunityCode(req.getParameter(COMMUNITY_CODE_PARAM));
		}
		display = req.getParameter(DISPLAY_TYPE_PARAM);
		if(req.getParameter(IGNORE_CACHE_PARAM)!=null){
			setIgnoreCache(Boolean.parseBoolean(req.getParameter(IGNORE_CACHE_PARAM)));
		}
		if(req.getParameter(LAT1_PARAM)!=null){
			setLat1(Double.parseDouble(req.getParameter(LAT1_PARAM)));
		}
		if(req.getParameter(LAT2_PARAM)!=null){
			setLat2(Double.parseDouble(req.getParameter(LAT2_PARAM)));
		}
		if(req.getParameter(LONG1_PARAM)!=null){
			setLong1(Double.parseDouble(req.getParameter(LONG1_PARAM)));
		}
		if(req.getParameter(LONG2_PARAM)!=null){
			setLong2(Double.parseDouble(req.getParameter(LONG2_PARAM)));
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

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	public String getCacheKey() {
		String key = getAction();
		if (key == null) {
			key = LIST_PLACEMARK;
			key += country + (display != null ? display : "")
					+ (getCursor() != null ? getCursor() : "");
		} else if (GET_AP_DETAILS_ACTION.equals(key)) {
			key += "-" + communityCode + (display != null ? display : "")
					+ (pointType != null ? pointType : "");
		}
		return key;
	}

	public void setIgnoreCache(Boolean ignoreCache) {
		this.ignoreCache = ignoreCache;
	}

	public Boolean getIgnoreCache() {
		return ignoreCache;
	}
}
