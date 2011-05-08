package com.gallatinsystems.gis.map.domain;

import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;
@PersistenceCapable
public class GeoMeasure extends BaseDomain {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3995985427976277084L;

	/**
		 * 
		 */
	private String name = null;
	private String value = null;
	private String type = null;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
