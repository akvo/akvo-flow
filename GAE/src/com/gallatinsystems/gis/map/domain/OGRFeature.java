package com.gallatinsystems.gis.map.domain;

import java.util.ArrayList;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.gallatinsystems.framework.domain.BaseDomain;

@PersistenceCapable
public class OGRFeature extends BaseDomain {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	private String name = null;
	private String projectCoordinateSystemIdentifier = null;
	private String geoCoordinateSystemIdentifier = null;
	private String datumIdentifier = null;
	private Double spheroid = null;
	private String countryCode = null;
	private Double x1 = null;
	private Double x2 = null;
	private Double y1 = null;
	private Double y2 = null;
	private Double reciprocalOfFlattening = null;
	private FeatureType featureType= null;
	
	public enum FeatureType {COUNTRY, SUB_COUNTRY_OTHER};

	public String getProjectCoordinateSystemIdentifier() {
		return projectCoordinateSystemIdentifier;
	}

	public void setProjectCoordinateSystemIdentifier(
			String projectCoordinateSystemIdentifier) {
		this.projectCoordinateSystemIdentifier = projectCoordinateSystemIdentifier;
	}

	public String getGeoCoordinateSystemIdentifier() {
		return geoCoordinateSystemIdentifier;
	}

	public void setGeoCoordinateSystemIdentifier(
			String geoCoordinateSystemIdentifier) {
		this.geoCoordinateSystemIdentifier = geoCoordinateSystemIdentifier;
	}

	public String getDatumIdentifier() {
		return datumIdentifier;
	}

	public void setDatumIdentifier(String datumIdentifier) {
		this.datumIdentifier = datumIdentifier;
	}

	public Double getSpheroid() {
		return spheroid;
	}

	public void setSpheroid(Double spheroid) {
		this.spheroid = spheroid;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public Double[] getBoundingBox() {
		return new Double[] { x1, y1, x2, y2 };
	}

	public void setBoundingBox(Double[] boundingBox) {
		x1 = boundingBox[0];
		y1 = boundingBox[1];
		x2 = boundingBox[2];
		y2 = boundingBox[3];
	}

	public void addBoundingBox(Double x1, Double y1, Double x2, Double y2) {
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;

	}

	public ArrayList<GeoMeasure> getPropertyList() {
		return propertyList;
	}

	public void setPropertyList(ArrayList<GeoMeasure> propertyList) {
		this.propertyList = propertyList;
	}

	@Persistent
	private ArrayList<GeoMeasure> propertyList = null;

	private Geometry geometry = null;

	public ArrayList<GeoMeasure> getpropertyList() {
		return propertyList;
	}

	public void setpropertyList(ArrayList<GeoMeasure> propertyList) {
		this.propertyList = propertyList;
	}

	public Geometry getGeometry() {
		if (geometry == null)
			return new Geometry();
		return geometry;
	}

	public void setGeometry(Geometry geometry) {
		this.geometry = geometry;
	}

	public void addGeoMeasure(String name, String type, String value) {
		if (propertyList == null)
			propertyList = new ArrayList<GeoMeasure>();
		GeoMeasure geoMeasure = new GeoMeasure();
		geoMeasure.setName(name);
		geoMeasure.setType(type);
		geoMeasure.setValue(value);
		propertyList.add(geoMeasure);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setReciprocalOfFlattening(Double reciprocalOfFlattening) {
		this.reciprocalOfFlattening = reciprocalOfFlattening;
	}

	public Double getReciprocalOfFlattening() {
		return reciprocalOfFlattening;
	}

	public void setFeatureType(FeatureType featureType) {
		this.featureType = featureType;
	}

	public FeatureType getFeatureType() {
		return featureType;
	}
}
