package com.gallatinsystems.survey.device.remote;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.gallatinsystems.survey.device.remote.dto.AccessPointDto;
import com.gallatinsystems.survey.device.util.HttpUtil;

/**
 * 
 * service class to fetch "nearby item" data from the server
 * 
 * @author Christopher Fagiani
 */
public class AccessPointService {
	private static final String TAG = "AccessPointService";
	private static final String SERVICE_URL = "http://watermapmonitordev.appspot.com/accesspoint?action=getnearby&lat=";

	/**
	 * 
	 * calls a service to get all the access points near the position passed in.
	 */
	public static ArrayList<AccessPointDto> getNearbyAccessPoints(Double lat,
			Double lon) {
		// todo: remove this
		lat = -15.88008;
		lon = 35.06023;
		ArrayList<AccessPointDto> dtoList = new ArrayList<AccessPointDto>();
		try {
			String response = HttpUtil.httpGet(SERVICE_URL + lat + "&lon="
					+ lon);
			if (response != null) {
				JSONObject json = new JSONObject(response);
				if (json != null) {
					JSONArray arr = json.getJSONArray("accessPointDto");
					if (arr != null) {
						for (int i = 0; i < arr.length(); i++) {
							if (arr.getJSONObject(i) != null) {
								dtoList.add(convertToAccessPointDto(arr
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
	 * converts a JSON object to an AccessPointDto
	 */
	private static AccessPointDto convertToAccessPointDto(JSONObject json) {
		AccessPointDto dto = null;
		if (json != null) {
			dto = new AccessPointDto();
			try {
				dto.setLat(json.getDouble("latitude"));
				dto.setLon(json.getDouble("longitude"));
				dto.setCommunityCode(json.getString("communityCode"));
				dto.setTechType(json.getString("typeTechnology"));
				dto.setStatus(json.getString("pointStatus"));
			} catch (JSONException e) {
				Log.e(TAG, "Could not parse points", e);
			}
		}
		return dto;
	}

}
