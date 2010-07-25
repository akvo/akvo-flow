package com.gallatinsystems.survey.device.remote;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.gallatinsystems.survey.device.remote.dto.PointOfInterestDto;
import com.gallatinsystems.survey.device.util.HttpUtil;

/**
 * 
 * service class to fetch "nearby item" data from the server
 * 
 * @author Christopher Fagiani
 */
public class PointOfInterestService {
	private static final String TAG = "PointOfInterestService";
	private static final String DEFAULT_SERVICE_URL = "http://watermapmonitordev.appspot.com";
	private static final String ACTION_PARAM = "/pointofinterest?action=getnearby";
	private static final String LAT_PARAM = "&lat=";
	private static final String LON_PARAM = "&lon=";
	private static final String COUNTRY_PARAM = "&country=";
	private static final String CURSOR_PARAM = "&cursor=";
	private static final String CURSOR_JSON_KEY = "cursor";
	private static final String POINTS_OF_INTEREST_JSON_KEY = "pointsOfInterest";
	private static final String NULL = "null";
	private String currentCursor;

	/**
	 * 
	 * calls a service to get all the access points near the position passed in.
	 * NOTE: this service is STATEFUL in that it maintains a cursor value
	 * therefore the method is NOT threadsafe.
	 */
	public ArrayList<PointOfInterestDto> getNearbyAccessPoints(Double lat,
			Double lon, String country, String serviceBase, boolean useCursor) {

		ArrayList<PointOfInterestDto> dtoList = new ArrayList<PointOfInterestDto>();
		try {
			String url = null;
			if (serviceBase != null) {
				url = serviceBase + ACTION_PARAM;
			} else {
				url = DEFAULT_SERVICE_URL + ACTION_PARAM;
			}
			if (country == null || country.trim().length() == 0) {
				url = url + LAT_PARAM + lat + LON_PARAM + lon;
			} else {
				url = url + COUNTRY_PARAM + country;
			}
			if (useCursor && currentCursor != null
					&& !NULL.equalsIgnoreCase(currentCursor)) {
				url = url + CURSOR_PARAM + currentCursor;
			}else if (!useCursor){
				currentCursor = null;
			}
			String response = HttpUtil.httpGet(url);
			if (response != null) {
				JSONObject json = new JSONObject(response);
				if (json != null) {
					JSONArray arr = json
							.getJSONArray(POINTS_OF_INTEREST_JSON_KEY);
					if (arr != null) {
						for (int i = 0; i < arr.length(); i++) {
							if (arr.getJSONObject(i) != null) {
								dtoList.add(convertToPointOfInterestDto(arr
										.getJSONObject(i)));
							}
						}
					}
					currentCursor = json.getString(CURSOR_JSON_KEY);
				}
			}
		} catch (Exception e) {
			Log.e(TAG, "Could not get access points", e);
		}

		return dtoList;
	}

	/**
	 * returns a boolean value indicating if the server has more data. This is
	 * done by checking for the current cursor value. If the service has not yet
	 * been invoked, the return value is false.
	 * 
	 * @return
	 */
	public boolean hasMore() {
		if (currentCursor != null && !NULL.equalsIgnoreCase(currentCursor)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * converts a JSON object to an PointOfInterestDto
	 */
	private static PointOfInterestDto convertToPointOfInterestDto(
			JSONObject json) {
		PointOfInterestDto dto = null;
		if (json != null && json.has("name")) {
			dto = new PointOfInterestDto();
			try {
				if(json.has("latitude")){
					dto.setLatitude(json.getDouble("latitude"));
				}
				if(json.has("longitude")){
					dto.setLongitude(json.getDouble("longitude"));
				}
				dto.setName(json.getString("name"));
				dto.setType(json.getString("type"));
				dto.setPropertyNames(convertToStringList(json
						.getJSONArray("propertyNames")));
				dto.setPropertyValues(convertToStringList(json
						.getJSONArray("propertyValues")));

			} catch (JSONException e) {
				Log.e(TAG, "Could not parse points", e);
			}
		}
		return dto;
	}

	/**
	 * iterates over a json array of strings and converts them to a List of
	 * strings.
	 * 
	 * @param arr
	 * @return
	 * @throws JSONException
	 */
	private static List<String> convertToStringList(JSONArray arr)
			throws JSONException {
		List<String> list = new ArrayList<String>();
		if (arr != null) {
			for (int i = 0; i < arr.length(); i++) {
				list.add(arr.getString(i));
			}
		}
		return list;
	}
}
