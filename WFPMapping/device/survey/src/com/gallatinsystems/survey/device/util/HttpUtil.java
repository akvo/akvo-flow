package com.gallatinsystems.survey.device.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
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

	private static final int BUF_SIZE = 2048;

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
		response = client.execute(new HttpGet(url));
		if (response.getStatusLine().getStatusCode() != 200) {
			throw new HttpException("Server error: "
					+ response.getStatusLine().getStatusCode());
		} else {
			responseString = parseResponse(response);

		}
		return responseString;
	}

	/**
	 * downloads the resource at url and saves the contents to file
	 * 
	 * @param url
	 * @param file
	 * @throws Exception
	 */
	public static void httpDownload(String url, String file) throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(new HttpGet(url));
		BufferedOutputStream writer = new BufferedOutputStream(
				new FileOutputStream(file));
		BufferedInputStream reader = new BufferedInputStream(response
				.getEntity().getContent());
		byte[] buffer = new byte[BUF_SIZE];
		int bytesRead = reader.read(buffer);

		while (bytesRead > 0) {
			writer.write(buffer, 0, bytesRead);
			bytesRead = reader.read(buffer);
		}
		writer.close();
		reader.close();
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
