package com.gallatinsystems.gis.location;

/**
 * Service interface for location services
 * 
 * @author Christopher Fagiani
 * 
 */
public interface GeoLocationService {
	/**
	 * returns the 2-letter country code for the lat/lon location passed in
	 */
	public String getCountryCodeForPoint(String lat, String lon);

	/**
	 * returns a geo place object that is closest to the lat/lon passed in.
	 */
	public GeoPlace findGeoPlace(String lat, String lon);

	/**
	 * tries to find the most detailed geoPlace possible given the lat/lon
	 * provided.
	 * 
	 * @param lat
	 * @param lon
	 * @return
	 */
	public GeoPlace findDetailedGeoPlace(String lat, String lon);
}