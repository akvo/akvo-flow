package com.gallatinsystems.gis.app.gwt.server;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import com.gallatinsystems.gis.app.gwt.client.GISSupportService;
import com.gallatinsystems.gis.geography.dao.CountryDao;
import com.gallatinsystems.gis.geography.domain.Country;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * 
 * Service to support listing some of the GIS data stored in the system.
 * 
 */
public class GISSupportServiceImpl extends RemoteServiceServlet implements
		GISSupportService {

	private static final long serialVersionUID = -3064880631626252519L;

	/**
	 * lists all country codes stored in the system.
	 */
	@Override
	public TreeMap<String, String> listCountryCodes() {
		CountryDao countryDao = new CountryDao();
		TreeMap<String, String> countryMap = new TreeMap<String, String>();
		List<Country> countryList = countryDao.list("all");
		for (Country country : countryList) {
			countryMap
					.put(country.getIsoAlpha2Code(), country.getDisplayName());
		}
		return countryMap;

	}

	/**
	 * lists all coordinate types the system understands
	 */
	@Override
	public TreeMap<String, String> listCoordinateTypes() {
		TreeMap<String, String> ctMap = new TreeMap<String, String>();
		ctMap.put("lat/lng", "Geographic latitude and longitude");
		ctMap.put("utm", "Universal Transverse Mercator");
		return ctMap;
	}

	/**
	 * lists all utm zones
	 */
	@Override
	public ArrayList<Integer> listUTMZones() {
		ArrayList<Integer> utmZones = new ArrayList<Integer>();
		for (Integer i = 1; i < 60; i++) {
			utmZones.add(i);
		}
		return utmZones;
	}

	/**
	 * lists all feature types the system supports
	 */
	@Override
	public TreeMap<String, String> listFeatureTypes() {
		TreeMap<String, String> featureTypes = new TreeMap<String, String>();
		featureTypes.put("COUNTRY", "Country");
		featureTypes.put("SUB_COUNTRY_OTHER", "Sub-Country Other");
		return featureTypes;
	}

}
