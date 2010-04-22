package com.gallatinsystems.survey.device.remote.dto;

import java.io.Serializable;

/**
 * dto for responsed from the access point service
 * 
 * @author Christopher Fagiani
 * 
 */
public class AccessPointDto implements Serializable {
	private static final long serialVersionUID = 7202533894121885298L;
	private Double lat;
	private Double lon;
	private String techType;
	private String status;
	private String communityCode;

	public String getTechType() {
		return techType;
	}

	public void setTechType(String techType) {
		this.techType = techType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public String getCommunityCode() {
		return communityCode;
	}

	public void setCommunityCode(String communityCode) {
		this.communityCode = communityCode;
	}

	public String toString() {
		return communityCode;
	}
}
