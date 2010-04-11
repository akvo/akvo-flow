package org.waterforpeople.mapping.domain;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.gallatinsystems.framework.domain.BaseDomain;
import com.gallatinsystems.gis.geography.domain.Country;

@PersistenceCapable
public class Community extends BaseDomain {

	private static final long serialVersionUID = 3253521922960208695L;
	@Persistent
	private String communityCode;
	@Persistent
	private String name;	
	@Persistent
	private Double lat;
	@Persistent
	private Double lon;
	@Persistent
	private String countryCode;

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
