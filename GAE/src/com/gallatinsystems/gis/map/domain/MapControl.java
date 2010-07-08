package com.gallatinsystems.gis.map.domain;

import java.util.Date;

import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;

@PersistenceCapable
public class MapControl extends BaseDomain {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4966511822743910055L;

	private Date startDate = null;
	private Date endDate = null;

}
