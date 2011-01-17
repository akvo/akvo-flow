package com.gallatinsystems.gis.map.domain;

import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;
import com.google.appengine.api.datastore.Text;

@PersistenceCapable
public class OGRFeature extends BaseDomain {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	/**
	 * 
	 */
	private String name;
	private String clname;
	private Integer count;
	private String firstCCNA;
	private String firtDNAM;
	private Integer sumTotal;
	private Integer sumMale;
	private Integer sumFemale;
	private Integer sumHH;
	private String firstCCOD;
	private String firstDCOD;
	private String firstCLCO;

	private Text polygon;

}
