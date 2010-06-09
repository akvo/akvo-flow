package com.gallatinsystems.gis.map.domain;

import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;
@PersistenceCapable
public class Map extends BaseDomain {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3539159195617449428L;
	private String name = null;
	private String desc = null;
	private MAPTYPE mapType = null;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public MAPTYPE getMapType() {
		return mapType;
	}

	public void setMapType(MAPTYPE mapType) {
		this.mapType = mapType;
	}

	public enum MAPTYPE {
		GOOGLE_EARTH, GOOGLE_MAP, GRASS
	};

}
