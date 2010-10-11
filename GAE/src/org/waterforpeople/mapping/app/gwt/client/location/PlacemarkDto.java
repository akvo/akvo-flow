package org.waterforpeople.mapping.app.gwt.client.location;

import java.io.Serializable;

public class PlacemarkDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3151596501944117022L;
	private Double latitude = null;
	private Double longitude = null;
	private Long altitude = null;
	private String placemarkContents = null;
	private String iconUrl = null;
	
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
	public Long getAltitude() {
		return altitude;
	}
	public void setAltitude(Long altitude) {
		this.altitude = altitude;
	}
	public String getPlacemarkContents() {
		return placemarkContents;
	}
	public void setPlacemarkContents(String placemarkContents) {
		this.placemarkContents = placemarkContents;
	}
	public String getIconUrl() {
		return iconUrl;
	}
	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}
	

}
