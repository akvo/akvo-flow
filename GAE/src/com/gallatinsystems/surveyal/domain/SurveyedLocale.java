/*
 *  Copyright (C) 2010-2014 Stichting Akvo (Akvo Foundation)
 *
 *  This file is part of Akvo FLOW.
 *
 *  Akvo FLOW is free software: you can redistribute it and modify it under the terms of
 *  the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 *  either version 3 of the License or any later version.
 *
 *  Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Affero General Public License included below for more details.
 *
 *  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */

package com.gallatinsystems.surveyal.domain;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;

import org.apache.commons.lang.StringUtils;

import com.gallatinsystems.framework.domain.BaseDomain;
import com.gallatinsystems.gis.map.MapUtils;

/**
 * Domain structure to represent a location about which there is data gathered
 * through one or more surveys. The sublevel1-6 fields are geographical
 * subdivisions (state/province/sector/cell, etc) where the exact definition of
 * what each level corresponds to depends on the country in which the point is
 * located.
 *
 * Details about SurveyedLocales are stored using SurveyalValue which has a
 * loose association with this object.
 *
 * @author Christopher Fagiani
 *
 */
@PersistenceCapable
public class SurveyedLocale extends BaseDomain {
	private static final long serialVersionUID = -7908506708459480822L;
	private String organization;
	private String systemIdentifier;
	private String identifier;
	private String displayName;
	private String countryCode;
	private String sublevel1;
	private String sublevel2;
	private String sublevel3;
	private String sublevel4;
	private String sublevel5;
	private String sublevel6;
	private List<Long> surveyInstanceContrib;
	private List<String> geocells;
	private String localeType;
	private Double latitude;
	private Double longitude;
	private boolean ambiguous;
	private String currentStatus;
	private Long surveyGroupId;
	private Date lastSurveyedDate;
	private Long lastSurveyalInstanceId;
	private Long creationSurveyId;
	@NotPersistent
	private List<SurveyalValue> surveyalValues;

	public Long getLastSurveyalInstanceId() {
		return lastSurveyalInstanceId;
	}

	public void setLastSurveyalInstanceId(Long lastSurveyalInstanceId) {
		this.lastSurveyalInstanceId = lastSurveyalInstanceId;
	}

	public SurveyedLocale() {
		ambiguous = false;
	}

	public Date getLastSurveyedDate() {
		return lastSurveyedDate;
	}

	public void setLastSurveyedDate(Date lastSurveyedDate) {
		this.lastSurveyedDate = lastSurveyedDate;
	}

	public boolean isAmbiguous() {
		return ambiguous;
	}

	public void setAmbiguous(boolean ambiguous) {
		this.ambiguous = ambiguous;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getSublevel1() {
		return sublevel1;
	}

	public void setSublevel1(String sublevel1) {
		this.sublevel1 = sublevel1;
	}

	public String getSublevel2() {
		return sublevel2;
	}

	public void setSublevel2(String sublevel2) {
		this.sublevel2 = sublevel2;
	}

	public String getSublevel3() {
		return sublevel3;
	}

	public void setSublevel3(String sublevel3) {
		this.sublevel3 = sublevel3;
	}

	public String getSublevel4() {
		return sublevel4;
	}

	public void setSublevel4(String sublevel4) {
		this.sublevel4 = sublevel4;
	}

	public String getSublevel5() {
		return sublevel5;
	}

	public void setSublevel5(String sublevel5) {
		this.sublevel5 = sublevel5;
	}

	public String getSublevel6() {
		return sublevel6;
	}

	public void setSublevel6(String sublevel6) {
		this.sublevel6 = sublevel6;
	}

	public String getLocaleType() {
		return localeType;
	}

	public void setLocaleType(String localeType) {
		this.localeType = localeType;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public String getSystemIdentifier() {
		return systemIdentifier;
	}

	public void setSystemIdentifier(String systemIdentifier) {
		this.systemIdentifier = systemIdentifier;
	}

	public List<SurveyalValue> getSurveyalValues() {
		return surveyalValues;
	}

	public void setSurveyalValues(List<SurveyalValue> surveyalValues) {
		this.surveyalValues = surveyalValues;
	}

	public void setCurrentStatus(String currentStatus) {
		this.currentStatus = currentStatus;
	}

	public Long getSurveyGroupId() {
		return surveyGroupId;
	}

	public void setSurveyGroupId(Long surveyGroupId) {
		this.surveyGroupId = surveyGroupId;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public List<Long> getSurveyInstanceContrib() {
		return surveyInstanceContrib;
	}

	public void setSurveyInstanceContrib(List<Long> surveyInstanceContrib) {
		this.surveyInstanceContrib = surveyInstanceContrib;
	}

	public String getCurrentStatus() {
		return currentStatus;
	}

	public List<String> getGeocells() {
		return geocells;
	}

	public void setGeocells(List<String> geocells) {
		this.geocells = geocells;
	}

	public Long getCreationSurveyId() {
		return creationSurveyId;
	}

	public void setCreationSurveyId(Long creationSurveyId) {
		this.creationSurveyId = creationSurveyId;
	}

	/**
	 * Split up a geoLocation string and return a map containing Latitude, Longitude
	 * entries.
	 *
	 * @params
	 * 		geoLocation
	 *
	 * @return
	 * 		a map containing latitude and longitude entries
	 * 		null if a null string is provided
	 */
	public static Map<String, Object> parseGeoLocation(String geoLocation) throws NumberFormatException {
	    Map<String, Object> geoLocationMap =  null;

	    String[] tokens = StringUtils.split(geoLocation, "\\|");
	    if (tokens != null && tokens.length >= 2) {
		geoLocationMap = new HashMap<String, Object>();
		geoLocationMap.put(MapUtils.LATITUDE, Double.parseDouble(tokens[0]));
		geoLocationMap.put(MapUtils.LONGITUDE, Double.parseDouble(tokens[1]));
		//if(tokens.length > 2) {
		// TODO: currently a string is generated for altitude. need to fix
		//geoLocationMap.put(ALTITUDE, Long.parseLong(tokens[2]));
		//}
	    }

	    return geoLocationMap;
	}
}
