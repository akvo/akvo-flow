package com.gallatinsystems.gis.map.domain;

import javax.jdo.annotations.PersistenceCapable;

import com.google.appengine.api.datastore.Text;

@PersistenceCapable
public class Placemark extends MapFragment {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6608874202518262533L;
	private Long latitude = null;
	private Long longitude = null;
	private String countryCode = null;
	private String name = null;
	private Text description=null;
	private MapFragment style = null;

}
