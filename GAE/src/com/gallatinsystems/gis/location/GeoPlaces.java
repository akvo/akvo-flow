package com.gallatinsystems.gis.location;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * wrapper for easy xml parsing
 * 
 * @author Christopher Fagiani
 * 
 */
@XmlType
@XmlRootElement(name = "geonames")
public class GeoPlaces {

	private List<GeoPlace> geoname;

	public List<GeoPlace> getGeoname() {
		return geoname;
	}

	public void setGeoname(List<GeoPlace> geoname) {
		this.geoname = geoname;
	}
}
