package com.gallatinsystems.survey.device.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * Simple utility to make http calls and read the responses
 * 
 * @author Christopher Fagiani
 * 
 */
public class HttpUtil {

	/**
	 * executes an HTTP GET and returns the result as a String
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static String httpGet(String url) throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpResponse response = null;
		String responseString = null;
		response = (client.execute(new HttpGet(url)));
		if (response.getStatusLine().getStatusCode() != 200) {
			throw new HttpException("Server error: "
					+ response.getStatusLine().getStatusCode());
		} else {
			responseString = parseResponse(response);

		}
		return responseString;
	}

	/**
	 * parses the response from the HttpResponse
	 * 
	 * @param response
	 * @return
	 * @throws Exception
	 */
	private static String parseResponse(HttpResponse response) throws Exception {
		String result = null;
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				response.getEntity().getContent()));
		StringBuilder sb = new StringBuilder();
		String line = null;

		while ((line = reader.readLine()) != null) {
			sb.append(line + "\n");
		}
		result = sb.toString();
		reader.close();
		return result;
	}
}
