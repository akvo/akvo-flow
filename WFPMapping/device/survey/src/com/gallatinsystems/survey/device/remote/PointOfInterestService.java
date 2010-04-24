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
	private static final String SERVICE_URL = "http://watermapmonitordev.appspot.com/pointofinterest?action=getnearby&lat=";

	/**
	 * 
	 * calls a service to get all the access points near the position passed in.
	 */
	public static ArrayList<PointOfInterestDto> getNearbyAccessPoints(
			Double lat, Double lon) {
		// TODO: remove this. this is just for testing
		lat = -15.88008;
		lon = 35.06023;
		ArrayList<PointOfInterestDto> dtoList = new ArrayList<PointOfInterestDto>();
		try {
			String response = HttpUtil.httpGet(SERVICE_URL + lat + "&lon="
					+ lon);
			if (response != null) {
				JSONObject json = new JSONObject(response);
				if (json != null) {
					JSONArray arr = json.getJSONArray("pointOfInterestDto");
					if (arr != null) {
						for (int i = 0; i < arr.length(); i++) {
							if (arr.getJSONObject(i) != null) {
								dtoList.add(convertToPointOfInterestDto(arr
										.getJSONObject(i)));
							}
						}
					}
				}
			}
		} catch (Exception e) {
			Log.e(TAG, "Could not get access points", e);
		}

		return dtoList;
	}

	/**
	 * converts a JSON object to an PointOfInterestDto
	 */
	private static PointOfInterestDto convertToPointOfInterestDto(
			JSONObject json) {
		PointOfInterestDto dto = null;
		if (json != null) {
			dto = new PointOfInterestDto();
			try {
				dto.setLatitude(json.getDouble("latitude"));
				dto.setLongitude(json.getDouble("longitude"));
				dto.setName(json.getString("communityCode"));
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
