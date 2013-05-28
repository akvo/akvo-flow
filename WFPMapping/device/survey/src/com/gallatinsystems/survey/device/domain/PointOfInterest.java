/*
 *  Copyright (C) 2010-2012 Stichting Akvo (Akvo Foundation)
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

package com.gallatinsystems.survey.device.domain;

import java.io.Serializable;
import java.util.ArrayList;


public class PointOfInterest implements Serializable {
	private static final long serialVersionUID = -545858029643355078L;
	private static final String PACKED_FIELD_DELIMITER = "#~#";

	private Long id;
	private String type;
	private String name;
	private String country;
	private Double latitude;
	private Double longitude;
	private Double distance;
	private ArrayList<String> propertyNames;
	private ArrayList<String> propertyValues;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public Double getDistance() {
		return distance;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}

	public ArrayList<String> getPropertyNames() {
		return propertyNames;
	}

	public void setPropertyNames(ArrayList<String> propertyNames) {
		this.propertyNames = propertyNames;
	}

	public ArrayList<String> getPropertyValues() {
		return propertyValues;
	}

	public void setPropertyValues(ArrayList<String> propertyValues) {
		this.propertyValues = propertyValues;
	}

	/**
	 * returns all property values as a packed-string
	 * 
	 * @return
	 */
	public String getPropertyValuesString() {
		return getStringFromList(propertyValues);
	}

	/**
	 * returns all property names as a packed-string
	 * 
	 * @return
	 */
	public String getPropertyNamesString() {
		return getStringFromList(propertyNames);
	}

	public void setPropertyValues(String packedString) {
		propertyValues = getListFromString(packedString);
	}

	public void setPropertyNames(String packedString) {
		propertyNames = getListFromString(packedString);
	}

	private ArrayList<String> getListFromString(String packedString) {
		ArrayList<String> result = null;
		if (packedString != null) {
			result = new ArrayList<String>();
			String[] vals = packedString.split(PACKED_FIELD_DELIMITER);
			for (int i = 0; i < vals.length; i++) {
				result.add(vals[i]);
			}
		}
		return result;
	}

	private String getStringFromList(ArrayList<String> stringList) {
		String result = null;
		if (stringList != null && stringList.size() > 0) {
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < stringList.size(); i++) {
				builder.append(stringList.get(i));
				if (i < stringList.size() - 1) {
					builder.append(PACKED_FIELD_DELIMITER);
				}
			}
			result = builder.toString();
		}
		return result;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (name != null) {
			builder.append(name).append("\n");
		}
		if (latitude != null && longitude != null) {
			builder.append(latitude).append(",").append(longitude);
		}
		return builder.toString();
	}
}
