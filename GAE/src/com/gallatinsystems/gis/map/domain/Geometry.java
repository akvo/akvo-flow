package com.gallatinsystems.gis.map.domain;

import java.util.ArrayList;

import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;

@PersistenceCapable
public class Geometry extends BaseDomain {

	/**
	 * 
	 */
	private static final long serialVersionUID = -694616882164561459L;
	private GeometryType type = null;
	private ArrayList<Coordinate> coordinates = null;

	public GeometryType getType() {
		return type;
	}

	public void setType(GeometryType type) {
		this.type = type;
	}

	public ArrayList<Coordinate> getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(ArrayList<Coordinate> coordinates) {
		this.coordinates = coordinates;
	}

	public void addCoordinate(Double x, Double y) {
		if (coordinates == null)
			coordinates = new ArrayList<Coordinate>();
		Coordinate coordinate = new Coordinate(x, y);
		coordinates.add(coordinate);
	}

	public enum GeometryType{
		MULITPOLYGON,POLYGON, POINT,RECTANGLE
	}
}
