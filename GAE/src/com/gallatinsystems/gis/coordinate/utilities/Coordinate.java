package com.gallatinsystems.gis.coordinate.utilities;

public class Coordinate {
	
	private Double longitude = 0.0;
	private Double latitude = 0.0;

	public Coordinate(Double latitude, Double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public Double getLatitude() {
		return latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	@Override
	public String toString(){
		return "Latitude: " + this.getLatitude() + " Longitude: " + this.getLongitude();
	}
}