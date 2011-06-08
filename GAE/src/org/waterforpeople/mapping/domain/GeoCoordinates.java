package org.waterforpeople.mapping.domain;

public class GeoCoordinates {
	private Double latitude;
	private Double longitude;
	private Double altitude;
	private String code;

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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public static GeoCoordinates extractGeoCoordinate(String line) {
		GeoCoordinates gc = null;
		if (line != null && line.trim().length() > 0
				&& !line.trim().equals("||") && !line.startsWith("||")) {
			gc = new GeoCoordinates();
			String[] coordinates = line.split("\\|");
			if (coordinates.length > 1) {
				gc.setLatitude(new Double(coordinates[0]));
				gc.setLongitude(new Double(coordinates[1]));
			}else{
				return null;
			}
			if (coordinates.length > 2) {
				gc.setAltitude(new Double(coordinates[2]));
			}
			if (coordinates.length > 3) {
				gc.setCode(coordinates[3]);
			}
		}
		return gc;
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
