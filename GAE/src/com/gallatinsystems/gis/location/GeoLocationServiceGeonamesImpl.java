package com.gallatinsystems.gis.location;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.gallatinsystems.survey.xml.SurveyXMLAdapter;

/**
 * This service utilizes the geonames web services (ws.geonames.org) to look up
 * geographic information given latitude/longitude coordinates.
 * 
 * @author Christopher Fagiani
 */

public class GeoLocationServiceGeonamesImpl implements GeoLocationService {
	private static final Logger log = Logger.getLogger(SurveyXMLAdapter.class
			.getName());
	private static final String COUNTRY_SERVICE_URL = "http://ws.geonames.org/countryCode?";
	private static final String PLACE_SERVICE_URL = "http://ws.geonames.org/findNearbyPlaceName?";
	private static final String LAT_PARAM = "lat";
	private static final String LON_PARAM = "lng";

	/**
	 * returns the 2-letter country code for the lat/lon location passed in
	 */
	public String getCountryCodeForPoint(String lat, String lon) {
		String countryCode = null;
		countryCode = callApi(COUNTRY_SERVICE_URL, lat, lon);
		if (countryCode != null) {
			countryCode = countryCode.trim();
		}
		return countryCode;
	}

	/**
	 * returns a geo place object that is closest to the lat/lon passed in.
	 */
	public GeoPlace findGeoPlace(String lat, String lon) {
		GeoPlaces places = parseXml(callApi(PLACE_SERVICE_URL, lat, lon));
		if (places != null && places.getGeoname() != null) {
			return places.getGeoname().get(0);
		} else {
			return null;
		}
	}

	/**
	 * Forms a service url and calls the api, returning the entire response body
	 * as a string
	 */
	private String callApi(String base, String lat, String lon) {
		String result = null;
		try {
			result = invokeApi(base, lat, lon);
		} catch (IOException ie) {
			// retry because of timeout
			log.log(Level.INFO, "Timeout for " + base,ie);
			try {
				result = invokeApi(base, lat, lon);
			} catch (IOException e) {
				log.log(Level.SEVERE, "Couldn't invoke geonames on the retry"
						+ base, e);
			} catch (Exception e) {
				log.log(Level.SEVERE, "Could not invoke geonames api vai url "
						+ base, e);
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "Could not invoke geonames api vai url "
					+ base, e);
		}
		return result;
	}

	private String invokeApi(String base, String lat, String lon)
			throws IOException, Exception {
		URL url = new URL(base + LAT_PARAM + "=" + lat + "&" + LON_PARAM + "="
				+ lon);
		BufferedReader reader = new BufferedReader(new InputStreamReader(url
				.openStream()));
		String line = null;
		StringBuilder builder = new StringBuilder();
		while ((line = reader.readLine()) != null) {
			builder.append(line);
		}
		reader.close();
		return builder.toString();
	}

	private GeoPlaces parseXml(String xmlString) {
		GeoPlaces places = null;
		try {
			JAXBContext jc = JAXBContext.newInstance(GeoPlaces.class,
					GeoPlace.class);
			Unmarshaller unmarshaller;
			unmarshaller = jc.createUnmarshaller();
			StringReader sr = new StringReader(xmlString);
			places = (GeoPlaces) unmarshaller.unmarshal(sr);

		} catch (JAXBException e) {
			log.log(Level.SEVERE, "Could not parse api response", e);
		}
		return places;

	}
}