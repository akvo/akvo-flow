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

package com.gallatinsystems.survey.device.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Simple utility to make http calls and read the responses
 * 
 * @author Christopher Fagiani
 * 
 */
public class HttpUtil {

	private static final int BUF_SIZE = 2048;
	private static final int PARSE_BUF_SIZE = 8192;

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
		HttpUriRequest request = new HttpGet(url);
		request.setHeader("Accept-Encoding", "gzip");
		request.setHeader("User-Agent", "gzip");
		response = client.execute(request);
		if (response.getStatusLine().getStatusCode() != 200) {
			throw new HttpException("Server error: "
					+ response.getStatusLine().getStatusCode());
		} else {
			responseString = parseResponse(response);

		}
		return responseString;
	}

	/**
	 * does an HTTP Post to the url specified using the params passed in
	 * 
	 * @param url
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static String httpPost(String url, Map<String, String> params)
			throws Exception {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		HttpResponse response = null;
		String responseString = null;

		if (params != null) {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			for (Entry<String, String> pair : params.entrySet()) {
				nameValuePairs.add(new BasicNameValuePair(pair.getKey(), pair
						.getValue()));
			}
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs,
					HTTP.UTF_8));
		}
		response = httpClient.execute(httpPost);
		if (response.getStatusLine().getStatusCode() != 200) {
			throw new HttpException("Server error: "
					+ response.getStatusLine().getStatusCode());
		} else {
			responseString = parseResponse(response);
		}
		return responseString;
	}

	/**
	 * fetches an image from a remote url and returns it to the caller as a
	 * bitmap
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static Bitmap getRemoteImage(String url, String cacheDir)
			throws Exception {
		BufferedInputStream reader = null;
		Bitmap bitMap = null;
		String fileName = url;
		// extract just the filename portion of the url
		if (fileName.contains("/")
				&& fileName.lastIndexOf("/") < fileName.length() + 1) {
			fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
		}
		// now check the cache
		if (cacheDir != null && cacheDir.trim().length() > 0) {
			File f = new File(cacheDir + "/" + fileName);
			if (f.exists()) {
				// if the file exists, return the local version
				bitMap = BitmapFactory.decodeFile(f.getAbsolutePath());
				return bitMap;
			}
		}
		// if we get here, then we had a cache miss (or aren't using the cache)
		try {
			if (cacheDir == null || cacheDir.trim().length() == 0) {
				// if we aren't using the cache, download directly into the
				// bitmap
				DefaultHttpClient client = new DefaultHttpClient();
				HttpResponse response = client.execute(new HttpGet(url));
				reader = new BufferedInputStream(response.getEntity()
						.getContent());
				bitMap = BitmapFactory.decodeStream(reader);
			} else {
				// if we are using the cache, download the file manually.
				// we need to do this instead of loading the bitmap and calling
				// compress since
				// that may not preserve the original file type so subsequent
				// call will encounter cache misses.
				FileUtil.findOrCreateDir(cacheDir);
				httpDownload(url, cacheDir + "/" + fileName);
				bitMap = BitmapFactory.decodeFile(cacheDir + "/" + fileName);
			}

		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		return bitMap;

	}

	/**
	 * downloads the resource at url and saves the contents to file. This method
	 * will close the write it binds to the fileOutputStream passed in
	 * 
	 * @param url
	 * @param file
	 * @throws Exception
	 */
	public static void httpDownload(String url, FileOutputStream file)
			throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(new HttpGet(url));
		if (response.getStatusLine().getStatusCode() < 400) {
			BufferedOutputStream writer = null;
			BufferedInputStream reader = null;
			try {
				writer = new BufferedOutputStream(file);
				reader = new BufferedInputStream(response.getEntity()
						.getContent());

				byte[] buffer = new byte[BUF_SIZE];
				int bytesRead = reader.read(buffer);

				while (bytesRead > 0) {
					writer.write(buffer, 0, bytesRead);
					bytesRead = reader.read(buffer);
				}
				writer.flush();
			} finally {
				if (writer != null) {
					writer.close();
				}
				if (reader != null) {
					reader.close();
				}
			}
		} else {
			throw new Exception("Error performing httpGet: "
					+ response.getStatusLine().toString());
		}
	}

	/**
	 * downloads the resource at url and saves the contents to file
	 * 
	 * @param url
	 * @param file
	 * @throws Exception
	 */
	public static void httpDownload(String url, String destFile)
			throws Exception {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(destFile);
			httpDownload(url, out);

		} finally {
			if (out != null) {
				out.close();
			}
		}
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
		BufferedReader reader = null;
		try {
			Header contentEncoding = response
					.getFirstHeader("Content-Encoding");
			if (contentEncoding != null
					&& contentEncoding.getValue().equalsIgnoreCase("gzip")) {
				reader = new BufferedReader(new InputStreamReader(
						new GZIPInputStream(response.getEntity().getContent())),PARSE_BUF_SIZE);
			} else {
				reader = new BufferedReader(new InputStreamReader(response
						.getEntity().getContent()),PARSE_BUF_SIZE);
			}
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
		return result;
	}
}
