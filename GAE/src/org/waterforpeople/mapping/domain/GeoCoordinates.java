package org.waterforpeople.mapping.domain;

public class GeoCoordinates {
	private Double latitude;
	private Double longitude;
	private Double altitude;

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

	public Double getAltitude() {
		return altitude;
	}

	public void setAltitude(Double altitude) {
		this.altitude = altitude;
	}
	
	public GeoCoordinates extractGeoCoordinate(String line) {
		String[] coordinates = line.split("\\|");
		this.setLatitude(new Double(coordinates[0]));
		this.setLongitude(new Double(coordinates[1]));
		this.setAltitude(new Double(coordinates[2]));
		return this;
	}	

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("GeoCoordinates:");
		sb.append("\n--Latitude: " + this.latitude);
		sb.append("\n--Longitude: " + this.longitude);
		sb.append("\n--Altitude: " + this.altitude);
		return sb.toString();
	}

}
