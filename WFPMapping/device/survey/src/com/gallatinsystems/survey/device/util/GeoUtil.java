package com.gallatinsystems.survey.device.util;

import android.location.Location;

import com.google.android.maps.GeoPoint;

/**
 * simple utility class for handling Locations
 * 
 * @author Christopher Fagiani
 * 
 */
public class GeoUtil {
	public static GeoPoint convertToPoint(String lat, String lon) {
		Double latitude = Double.parseDouble(lat) * 1E6;
		Double longitude = Double.parseDouble(lon) * 1E6;
		return new GeoPoint(latitude.intValue(), longitude.intValue());
	}

	public static GeoPoint convertToPoint(Location loc) {
		Double latitude = loc.getLatitude() * 1E6;
		Double longitude = loc.getLongitude() * 1E6;
		return new GeoPoint(latitude.intValue(), longitude.intValue());
	}

	public static String decodeLocation(int val) {
		return "" + ((double) val / (double) 1E6);
	}
}
