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

	public Double getLng() {
		return lng;
	}

	public void setLng(Double lon) {
		this.lng = lon;
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
	private Double lng;
	private String geonameId;
	
	private String sub1=null;
	private String sub2=null;
	private String sub3=null;
	private String sub4=null;
	private String sub5=null;
	private String sub6=null;
	public String getSub1() {
		return sub1;
	}

	public void setSub1(String sub1) {
		this.sub1 = sub1;
	}

	public String getSub2() {
		return sub2;
	}

	public void setSub2(String sub2) {
		this.sub2 = sub2;
	}

	public String getSub3() {
		return sub3;
	}

	public void setSub3(String sub3) {
		this.sub3 = sub3;
	}

	public String getSub4() {
		return sub4;
	}

	public void setSub4(String sub4) {
		this.sub4 = sub4;
	}

	public String getSub5() {
		return sub5;
	}

	public void setSub5(String sub5) {
		this.sub5 = sub5;
	}

	public String getSub6() {
		return sub6;
	}

	public void setSub6(String sub6) {
		this.sub6 = sub6;
	}
	

}