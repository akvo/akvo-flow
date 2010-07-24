package com.gallatinsystems.survey.device.remote.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Dto for point of interest service
 * 
 * @author Christopher Fagiani
 * 
 */
public class PointOfInterestDto implements Serializable {
	private static final long serialVersionUID = -545858029643355078L;
	private String type;
	private String name;
	private Double latitude;
	private Double longitude;
	private List<String> propertyNames;
	private List<String> propertyValues;

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

	public List<String> getPropertyNames() {
		return propertyNames;
	}

	public void setPropertyNames(List<String> propertyNames) {
		this.propertyNames = propertyNames;
	}

	public List<String> getPropertyValues() {
		return propertyValues;
	}

	public void setPropertyValues(List<String> propertyValues) {
		this.propertyValues = propertyValues;
	}

	public String toString(){
		StringBuilder builder= new StringBuilder();
		if(name != null){
			builder.append(name).append("\n");
		}
		if(latitude != null && longitude != null){
			builder.append(latitude).append(",").append(longitude);
		}
		return builder.toString();
	}
}
