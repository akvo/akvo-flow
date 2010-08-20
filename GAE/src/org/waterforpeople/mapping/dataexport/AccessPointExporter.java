package org.waterforpeople.mapping.dataexport;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointDto;
import org.waterforpeople.mapping.app.web.dto.AccessPointRequest;
import org.waterforpeople.mapping.app.web.dto.AccessPointResponse;

import com.gallatinsystems.framework.dataexport.applet.DataExporter;

public class AccessPointExporter implements DataExporter {

	private static String test = "{\"cursor\":\"E9oBZ2piahJ3YXRlcm1hcG1vbml0b3JkZXZyTAsSNgoAGgtBY2Nlc3NQb2ludCMwBXIhGglwb2ludFR5cGUgACoSGhBTQU5JVEFUSU9OX1BPSU5UJAwLEgtBY2Nlc3NQb2ludBisEwyCAQDgAQAU\",\"accessPointDto\":[{\"costPer\":null,\"costPerUnitOfMeasure\":null,\"photoURL\":null,\"otherStatus\":\"Functional\",\"altitude\":0,\"numberOfHouseholdsUsingPoint\":null,\"countryCode\":\"AQ\",\"pointPhotoCaption\":null,\"collectionDate\":\"Tue Mar 16 00:00:00 UTC 2010\",\"constructionDate\":null,\"costPerCurrency\":null,\"communityCode\":\"CH001\",\"description\":null,\"technologyTypeOther\":null,\"farthestHouseholdfromPoint\":null,\"year\":2010,\"longitude\":15.13895,\"latitude\":-88.1301,\"currentManagementStructurePoint\":null,\"typeTechnology\":null,\"pointStatus\":{},\"keyId\":2463,\"pointType\":{}},{\"costPer\":null,\"costPerUnitOfMeasure\":null,\"photoURL\":null,\"otherStatus\":\"Functional\",\"altitude\":0,\"numberOfHouseholdsUsingPoint\":null,\"countryCode\":\"AQ\",\"pointPhotoCaption\":null,\"collectionDate\":\"Tue Mar 16 00:00:00 UTC 2010\",\"constructionDate\":null,\"costPerCurrency\":null,\"communityCode\":\"CH003\",\"description\":null,\"technologyTypeOther\":null,\"farthestHouseholdfromPoint\":null,\"year\":2010,\"longitude\":15.12563,\"latitude\":-88.14027,\"currentManagementStructurePoint\":null,\"typeTechnology\":null,\"pointStatus\":{},\"keyId\":2464,\"pointType\":{}},{\"costPer\":null,\"costPerUnitOfMeasure\":null,\"photoURL\":null,\"otherStatus\":\"Functional\",\"altitude\":0,\"numberOfHouseholdsUsingPoint\":null,\"countryCode\":\"AQ\",\"pointPhotoCaption\":null,\"collectionDate\":\"Fri Mar 19 00:00:00 UTC 2010\",\"constructionDate\":null,\"costPerCurrency\":null,\"communityCode\":\"CH002\",\"description\":null,\"technologyTypeOther\":null,\"farthestHouseholdfromPoint\":null,\"year\":2010,\"longitude\":15.12763,\"latitude\":-88.20085,\"currentManagementStructurePoint\":null,\"typeTechnology\":null,\"pointStatus\":{},\"keyId\":2465,\"pointType\":{}},{\"costPer\":null,\"costPerUnitOfMeasure\":null,\"photoURL\":null,\"otherStatus\":\"Functional\",\"altitude\":0,\"numberOfHouseholdsUsingPoint\":null,\"countryCode\":\"AQ\",\"pointPhotoCaption\":null,\"collectionDate\":\"Tue Mar 16 00:00:00 UTC 2010\",\"constructionDate\":null,\"costPerCurrency\":null,\"communityCode\":\"CH004\",\"description\":null,\"technologyTypeOther\":null,\"farthestHouseholdfromPoint\":null,\"year\":2010,\"longitude\":15.13503,\"latitude\":-88.13686,\"currentManagementStructurePoint\":null,\"typeTechnology\":null,\"pointStatus\":{},\"keyId\":2466,\"pointType\":{}},{\"costPer\":null,\"costPerUnitOfMeasure\":null,\"photoURL\":null,\"otherStatus\":\"Functional\",\"altitude\":0,\"numberOfHouseholdsUsingPoint\":null,\"countryCode\":\"AQ\",\"pointPhotoCaption\":null,\"collectionDate\":\"Wed Mar 17 00:00:00 UTC 2010\",\"constructionDate\":null,\"costPerCurrency\":null,\"communityCode\":\"CH005\",\"description\":null,\"technologyTypeOther\":null,\"farthestHouseholdfromPoint\":null,\"year\":2010,\"longitude\":15.1978,\"latitude\":-88.20903,\"currentManagementStructurePoint\":null,\"typeTechnology\":null,\"pointStatus\":{},\"keyId\":2467,\"pointType\":{}},{\"costPer\":null,\"costPerUnitOfMeasure\":null,\"photoURL\":null,\"otherStatus\":\"Functional\",\"altitude\":0,\"numberOfHouseholdsUsingPoint\":null,\"countryCode\":null,\"pointPhotoCaption\":null,\"collectionDate\":\"Fri Mar 12 00:00:00 UTC 2010\",\"constructionDate\":null,\"costPerCurrency\":null,\"communityCode\":\"CH006\",\"description\":null,\"technologyTypeOther\":null,\"farthestHouseholdfromPoint\":null,\"year\":2010,\"longitude\":0,\"latitude\":0,\"currentManagementStructurePoint\":null,\"typeTechnology\":null,\"pointStatus\":{},\"keyId\":2468,\"pointType\":{}},{\"costPer\":null,\"costPerUnitOfMeasure\":null,\"photoURL\":null,\"otherStatus\":\"Functional\",\"altitude\":0,\"numberOfHouseholdsUsingPoint\":null,\"countryCode\":null,\"pointPhotoCaption\":null,\"collectionDate\":\"Mon Mar 15 00:00:00 UTC 2010\",\"constructionDate\":null,\"costPerCurrency\":null,\"communityCode\":\"CH007\",\"description\":null,\"technologyTypeOther\":null,\"farthestHouseholdfromPoint\":null,\"year\":2010,\"longitude\":0,\"latitude\":0,\"currentManagementStructurePoint\":null,\"typeTechnology\":null,\"pointStatus\":{},\"keyId\":2469,\"pointType\":{}},{\"costPer\":null,\"costPerUnitOfMeasure\":null,\"photoURL\":null,\"otherStatus\":\"Functional\",\"altitude\":0,\"numberOfHouseholdsUsingPoint\":null,\"countryCode\":\"AQ\",\"pointPhotoCaption\":null,\"collectionDate\":\"Thu Mar 18 00:00:00 UTC 2010\",\"constructionDate\":null,\"costPerCurrency\":null,\"communityCode\":\"CH011\",\"description\":null,\"technologyTypeOther\":null,\"farthestHouseholdfromPoint\":null,\"year\":2010,\"longitude\":15.10719,\"latitude\":-88.15065,\"currentManagementStructurePoint\":null,\"typeTechnology\":null,\"pointStatus\":{},\"keyId\":2470,\"pointType\":{}},{\"costPer\":null,\"costPerUnitOfMeasure\":null,\"photoURL\":null,\"otherStatus\":\"Functional\",\"altitude\":0,\"numberOfHouseholdsUsingPoint\":null,\"countryCode\":\"AQ\",\"pointPhotoCaption\":null,\"collectionDate\":\"Fri Mar 12 00:00:00 UTC 2010\",\"constructionDate\":null,\"costPerCurrency\":null,\"communityCode\":\"CH008\",\"description\":null,\"technologyTypeOther\":null,\"farthestHouseholdfromPoint\":null,\"year\":2010,\"longitude\":15.91108,\"latitude\":-88.11142,\"currentManagementStructurePoint\":null,\"typeTechnology\":null,\"pointStatus\":{},\"keyId\":2471,\"pointType\":{}},{\"costPer\":null,\"costPerUnitOfMeasure\":null,\"photoURL\":null,\"otherStatus\":\"Functional\",\"altitude\":0,\"numberOfHouseholdsUsingPoint\":null,\"countryCode\":\"AQ\",\"pointPhotoCaption\":null,\"collectionDate\":\"Thu Mar 18 00:00:00 UTC 2010\",\"constructionDate\":null,\"costPerCurrency\":null,\"communityCode\":\"CH009\",\"description\":null,\"technologyTypeOther\":null,\"farthestHouseholdfromPoint\":null,\"year\":2010,\"longitude\":15.12944,\"latitude\":-88.16258,\"currentManagementStructurePoint\":null,\"typeTechnology\":null,\"pointStatus\":{},\"keyId\":2472,\"pointType\":{}},{\"costPer\":null,\"costPerUnitOfMeasure\":null,\"photoURL\":null,\"otherStatus\":\"Functional\",\"altitude\":0,\"numberOfHouseholdsUsingPoint\":null,\"countryCode\":null,\"pointPhotoCaption\":null,\"collectionDate\":\"Mon Mar 15 00:00:00 UTC 2010\",\"constructionDate\":null,\"costPerCurrency\":null,\"communityCode\":\"CH010\",\"description\":null,\"technologyTypeOther\":null,\"farthestHouseholdfromPoint\":null,\"year\":2010,\"longitude\":0,\"latitude\":0,\"currentManagementStructurePoint\":null,\"typeTechnology\":null,\"pointStatus\":{},\"keyId\":2473,\"pointType\":{}},{\"costPer\":null,\"costPerUnitOfMeasure\":null,\"photoURL\":null,\"otherStatus\":\"Functional\",\"altitude\":0,\"numberOfHouseholdsUsingPoint\":null,\"countryCode\":\"AQ\",\"pointPhotoCaption\":null,\"collectionDate\":\"Wed Mar 17 00:00:00 UTC 2010\",\"constructionDate\":null,\"costPerCurrency\":null,\"communityCode\":\"CH012\",\"description\":null,\"technologyTypeOther\":null,\"farthestHouseholdfromPoint\":null,\"year\":2010,\"longitude\":15.19403,\"latitude\":-88.21888,\"currentManagementStructurePoint\":null,\"typeTechnology\":null,\"pointStatus\":{},\"keyId\":2474,\"pointType\":{}},{\"costPer\":null,\"costPerUnitOfMeasure\":null,\"photoURL\":null,\"otherStatus\":\"Functional\",\"altitude\":0,\"numberOfHouseholdsUsingPoint\":null,\"countryCode\":\"AQ\",\"pointPhotoCaption\":null,\"collectionDate\":\"Fri Mar 19 00:00:00 UTC 2010\",\"constructionDate\":null,\"costPerCurrency\":null,\"communityCode\":\"CH013\",\"description\":null,\"technologyTypeOther\":null,\"farthestHouseholdfromPoint\":null,\"year\":2010,\"longitude\":15.14981,\"latitude\":-88.18618,\"currentManagementStructurePoint\":null,\"typeTechnology\":null,\"pointStatus\":{},\"keyId\":2475,\"pointType\":{}},{\"costPer\":null,\"costPerUnitOfMeasure\":null,\"photoURL\":\"\",\"otherStatus\":null,\"altitude\":0,\"numberOfHouseholdsUsingPoint\":null,\"countryCode\":\"AQ\",\"pointPhotoCaption\":\"\",\"collectionDate\":\"Thu Mar 11 00:00:00 UTC 2010\",\"constructionDate\":null,\"costPerCurrency\":null,\"communityCode\":\"CH014\",\"description\":\"\",\"technologyTypeOther\":null,\"farthestHouseholdfromPoint\":\"\",\"year\":2010,\"longitude\":15.11812,\"latitude\":-88.18198,\"currentManagementStructurePoint\":\"\",\"typeTechnology\":null,\"pointStatus\":{},\"keyId\":2476,\"pointType\":{}}]}";
	private static final String SERVLET_URL = "/accesspoint?action=search&";
	private static final String RESPONSE_KEY = "accessPointDto";
	private static final String NULL_STR = "null";
	private List<String> headers;
	private static final DateFormat DATE_FMT = DateFormat.getDateInstance();
	private static final DateFormat IN_DATE_FMT = new SimpleDateFormat(
			"EEE MMM dd HH:mm:ss zzz yyyy");

	public AccessPointExporter() {

	}

	public static void main(String[] args) {
		AccessPointExporter exp = new AccessPointExporter();
		try {
			PrintWriter pw = new PrintWriter("c:\\temp\\test.x");
			exp.headers = exp.writeHeader(pw);
			AccessPointResponse resp = exp.parseJson(test);
			exp.writeData(resp.getAccessPointDto(), exp.headers, pw);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void export(Map<String, String> criteria, File file,
			String serverBase) {

		PrintWriter pw = null;
		try {
			pw = new PrintWriter(file);
			headers = writeHeader(pw);

			if (serverBase.trim().endsWith("/")) {
				serverBase = serverBase.trim().substring(0,
						serverBase.lastIndexOf("/"));
			}
			AccessPointResponse resp = fetchData(serverBase + SERVLET_URL
					+ formParams(criteria));

			while (resp != null && resp.getAccessPointDto() != null
					&& resp.getAccessPointDto().size() > 0) {
				// write what we have
				writeData(resp.getAccessPointDto(), headers, pw);
				// replace the cursor before calling again
				criteria.put(AccessPointRequest.CURSOR_PARAM, resp.getCursor());
				// then call again
				resp = fetchData(serverBase + SERVLET_URL
						+ formParams(criteria));

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
	}

	private String formParams(Map<String, String> criteria) {
		StringBuilder builder = new StringBuilder();
		int count = 0;
		for (Entry<String, String> crit : criteria.entrySet()) {
			if (crit.getValue() != null && crit.getValue().trim().length() > 0) {
				if (count > 0) {
					builder.append("&");
				}
				builder.append(crit.getKey().trim()).append("=").append(
						crit.getValue().trim());
				count++;
			}
		}
		return builder.toString();
	}

	private AccessPointResponse fetchData(String fullUrl) throws Exception {
		BufferedReader reader = null;
		String result = null;
		try {
			URL url = new URL(fullUrl);
			System.out.println("Calling: " + url.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setDoOutput(true);

			reader = new BufferedReader(new InputStreamReader(conn
					.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line = null;

			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			result = sb.toString();
		} finally {
			if (reader != null) {
				reader.close();
			}
		}

		return parseJson(result);
	}

	private AccessPointResponse parseJson(String response) throws Exception {
		System.out.println("response: " + response);
		AccessPointResponse apResp = new AccessPointResponse();
		List<AccessPointDto> dtoList = new ArrayList<AccessPointDto>();
		if (response != null) {
			JSONObject json = new JSONObject(response);
			if (json != null) {
				JSONArray arr = json.getJSONArray(RESPONSE_KEY);
				if (arr != null) {
					for (int i = 0; i < arr.length(); i++) {
						if (arr.getJSONObject(i) != null) {
							dtoList.add(convertToAccessPointDto(arr
									.getJSONObject(i)));
						}
					}
					apResp.setAccessPointDto(dtoList);
				}
				apResp.setCursor(json.getString("cursor"));
			}
		}
		return apResp;
	}

	/**
	 * converts a JSON object to an PointOfInterest
	 */
	public AccessPointDto convertToAccessPointDto(JSONObject json) {
		AccessPointDto point = null;
		if (json != null) {
			point = new AccessPointDto();
			try {

				for (String header : headers) {
					if (json.has(header)) {
						Field field = AccessPointDto.class
								.getDeclaredField(header);
						field.setAccessible(true);
						if (field.getType() == String.class) {
							String valString = json.getString(header);
							if (valString != null
									&& !NULL_STR.equalsIgnoreCase(valString
											.trim())) {
								field.set(point, json.getString(header));
							}
						}
						if (field.getType() == Date.class) {
							String dateString = json.getString(header);
							if (dateString != null
									&& !NULL_STR.equalsIgnoreCase(dateString
											.trim())) {
								field.set(point, IN_DATE_FMT.parse(json
										.getString(header)));
							}
						}
					}
				}
			} catch (Exception e) {
				System.out.println("Can't parse json: " + e);
				e.printStackTrace();
			}
		}
		return point;
	}

	private List<String> writeHeader(PrintWriter pw) {
		List<String> headers = new ArrayList<String>();
		Field[] fields = AccessPointDto.class.getDeclaredFields();
		if (fields != null) {
			for (int i = 0; i < fields.length; i++) {
				if (fields[i].getType() == String.class
						|| fields[i].getType() == Date.class
						|| fields[i].getType() == AccessPointDto.AccessPointType.class
						|| fields[i].getType() == AccessPointDto.Status.class) {
					headers.add(fields[i].getName());
				}
			}
		}
		Collections.sort(headers);
		int i = 0;
		for (String col : headers) {
			if (i > 0) {
				pw.write("\t");
			}
			pw.write(col);
			i++;
		}
		pw.write("\n");
		return headers;
	}

	private void writeData(List<AccessPointDto> dtoList, List<String> cols,
			PrintWriter pw) throws Exception {
		for (AccessPointDto dto : dtoList) {

			int j = 0;
			for (String col : cols) {
				if (j > 0) {
					pw.write("\t");
				}
				Field field = AccessPointDto.class.getDeclaredField(col);
				field.setAccessible(true);
				if (field.getType() == Date.class) {
					Date temp = (Date) field.get(dto);
					if (temp != null) {
						pw.write(DATE_FMT.format(temp));
					}
				} else if (field.getType() == String.class) {
					String temp = (String) field.get(dto);
					if (temp != null) {
						pw.write(temp);
					}
				}
				j++;
			}
			pw.write("\n");
		}
	}

}
