/*
 *  Copyright (C) 2010-2012 Stichting Akvo (Akvo Foundation)
 *
 *  This file is part of Akvo FLOW.
 *
 *  Akvo FLOW is free software: you can redistribute it and modify it under the terms of
 *  the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 *  either version 3 of the License or any later version.
 *
 *  Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Affero General Public License included below for more details.
 *
 *  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */

package org.waterforpeople.mapping.dataexport;

import java.io.File;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointDto;
import org.waterforpeople.mapping.app.web.dto.AccessPointRequest;
import org.waterforpeople.mapping.app.web.dto.AccessPointResponse;

import com.gallatinsystems.framework.dataexport.applet.AbstractDataExporter;

/**
 * 
 * This class can export access point search results to a tab delimited file. It
 * will attempt to output all fields in the AccessPointDto. As of now, columns
 * are in lexical order.
 * 
 * @author Christopher Fagiani
 */
public class AccessPointExporter extends AbstractDataExporter {

	private static final String SERVLET_URL = "/accesspoint?action=search&";
	private static final String RESPONSE_KEY = "accessPointDto";
	private static final String NULL_STR = "null";
	private List<String> headers;
	private static final DateFormat DATE_FMT = DateFormat.getDateInstance();
	private static final DateFormat IN_DATE_FMT = new SimpleDateFormat(
			"EEE MMM dd HH:mm:ss zzz yyyy");

	/**
	 * Executes the export. It will fetch data from the server in a loop until
	 * the call returns no results. On each iteration of the loop, it will write
	 * the data to the file.
	 */
	@Override
	public void export(Map<String, String> criteria, File file,
			String serverBase, Map<String,String> options) {

		PrintWriter pw = null;
		try {
			pw = new PrintWriter(file);
			headers = writeHeader(pw);

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
			e.printStackTrace();
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
	}

	/**
	 * fetches the data from the server using the fullUrl (url including all
	 * parameters). The resulting data is read into a string and will be parsed
	 * into pojos.
	 */
	private AccessPointResponse fetchData(String fullUrl) throws Exception {
		return parseJson(fetchDataFromServer(fullUrl));
	}

	/**
	 * converts the json string into an AccessPointResponse pojo.
	 */
	public AccessPointResponse parseJson(String response) throws Exception {
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
	 * converts a JSON object to an AccessPointDto
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
						} else if (field.getType() == Date.class) {
							String dateString = json.getString(header);
							if (dateString != null
									&& !NULL_STR.equalsIgnoreCase(dateString
											.trim())) {
								field.set(point, IN_DATE_FMT.parse(json
										.getString(header)));
							}
						} else if (field.getType() == Double.class) {
							String doubleString = json.getString(header);
							if (doubleString != null
									&& !NULL_STR.equalsIgnoreCase(doubleString
											.trim())) {
								field.set(point,
										Double.parseDouble(doubleString));
							}
						} else if (field.getType() == Boolean.class) {
							String booleanString = json.getString(header);
							if (booleanString != null
									&& !NULL_STR.equalsIgnoreCase(booleanString
											.trim())) {
								field.set(point,
										Boolean.parseBoolean(booleanString));
							}
						} else if (field.getType() == Integer.class) {
							String integerString = json.getString(header);
							if (integerString != null
									&& !NULL_STR.equalsIgnoreCase(integerString
											.trim())) {
								field.set(point,
										Integer.parseInt(integerString));
							}
						}else if(field.getType() == Long.class){
							String longString = json.getString(header);
							if(longString!=null && !NULL_STR.equalsIgnoreCase(longString.trim())){
								field.set(point,Long.parseLong(longString));
							}
						}
						// TODO: handle status fields. Not sure yet if they can
						// be treated as string or if i have to treat them as
						// classes since they're an enum
					}
				}
			} catch (Exception e) {
				System.out.println("Can't parse json: " + e);
				e.printStackTrace();
			}
		}
		return point;
	}

	/**
	 * uses reflection to get the comprehensive list of fields in the
	 * AccessPointDto object. It then sorts the fields lexically and writes them
	 * to the file as the column headers.
	 */
	private List<String> writeHeader(PrintWriter pw) {
		List<String> headers = new ArrayList<String>();
		Field[] fields = AccessPointDto.class.getDeclaredFields();
		if (fields != null) {
			for (int i = 0; i < fields.length; i++) {
				if (fields[i].getType() == String.class
						|| fields[i].getType() == Date.class
						|| fields[i].getType() == AccessPointDto.AccessPointType.class
						|| fields[i].getType() == AccessPointDto.Status.class
						|| fields[i].getType() == Double.class
						|| fields[i].getType() == Boolean.class) {
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

	/**
	 * uses reflection to fetch the values from the AccessPointDto object and,
	 * for each field, writes the value to the file
	 */
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
						pw.write(temp.trim());
					}
				} else if (field.getType() == Double.class) {
					Double temp = (Double) field.get(dto);
					if (temp != null) {
						pw.write(temp.toString());
					}
				} else if (field.getType() == AccessPointDto.AccessPointType.class) {
					AccessPointDto.AccessPointType type = (AccessPointDto.AccessPointType) field
							.get(dto);
					if (type != null) {
						pw.write(type.toString());
					}
				} else if (field.getType() == AccessPointDto.Status.class) {
					AccessPointDto.Status status = (AccessPointDto.Status) field
							.get(dto);
					if (status != null) {
						pw.write(status.toString());
					}
				} else if (field.getType() == Boolean.class) {
					Boolean temp = (Boolean) field.get(dto);
					if (temp != null) {
						pw.write(temp.toString());
					}
				}
				j++;
			}
			pw.write("\n");
		}
	}
}
