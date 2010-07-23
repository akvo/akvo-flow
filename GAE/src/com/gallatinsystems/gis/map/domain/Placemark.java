package com.gallatinsystems.gis.map.domain;

import javax.jdo.annotations.PersistenceCapable;

import com.google.appengine.api.datastore.Text;

@PersistenceCapable
public class Placemark extends MapFragment {

	private static final long serialVersionUID = 6608874202518262533L;
	@SuppressWarnings("unused")
	private Long latitude = null;
	@SuppressWarnings("unused")
	private Long longitude = null;
	@SuppressWarnings("unused")
	private String countryCode = null;
	@SuppressWarnings("unused")
	private String name = null;
	@SuppressWarnings("unused")
	private Text description=null;
	@SuppressWarnings("unused")
	private MapFragment style = null;

}
