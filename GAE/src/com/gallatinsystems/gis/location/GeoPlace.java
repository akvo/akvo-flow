package com.gallatinsystems.gis.location;

import javax.xml.bind.annotation.XmlType;

/**
 * Data structure to parse response from geonames api calls
 * 
 * @author Christopher Fagiani
 * 
 */
@XmlType(name = "geoname")
public class GeoPlace {

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
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

	public String getGeonameId() {
		return geonameId;
	}

	public void setGeonameId(String geonameId) {
		this.geonameId = geonameId;
	}

	private String name;
	private String countryCode;
	private String countryName;
	private Double lat;
	private Double lon;
	private String geonameId;

}