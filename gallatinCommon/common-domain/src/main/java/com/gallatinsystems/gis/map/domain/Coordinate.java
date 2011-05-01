package com.gallatinsystems.gis.map.domain;

import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;

@PersistenceCapable
public class Coordinate extends BaseDomain {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6783925382995168188L;

	private Double x = null;
	private Double y = null;

	public Coordinate(Double x, Double y) {
		setX(x);
		setY(y);
	}

	public void setX(Double x) {
		this.x = x;
	}

	public Double getX() {
		return x;
	}

	public void setY(Double y) {
		this.y = y;
	}

	public Double getY() {
		return y;
	}

}
