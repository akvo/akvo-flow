package com.gallatinsystems.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import services.S3Driver;

public class DeviceFileUploadCompare {
	private String serverBase = null;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DeviceFileUploadCompare dfuc = new DeviceFileUploadCompare();
		dfuc.setServerBase(args[2]);
		dfuc.checkAllFiles(args[0], args[1]);
	}

	private void checkAllFiles(String key, String identifier) {
		compare(key, identifier);
	}

	private void compare(String key, String identifier) {
		S3Driver s3 = new S3Driver(key, identifier);
		String bucketName = "waterforpeople";
		String fileDir = "devicezip";
		String fileListPath = null;
		List<String> fileList = s3.listAllFiles(bucketName, fileDir,
				fileListPath);
		for (String item : fileList) {
			if (item.endsWith(".zip")) {
				findFile(item);
			}
		}

	}

	private void findFile(String fileName) {
		String serviceUrl = "/devicefilesrestapi?action=%s&"
				+ "deviceFullPath=%s";
		String urlRequest = null;
		String actionParam = "findDeviceFile";
		String prefix = "http://waterforpeople.s3.amazonaws.com/";
		urlRequest = String.format(serviceUrl, actionParam, prefix + fileName);
		try {
			sendRequest(getServerBase(), urlRequest);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void sendRequest(String serverBase, String urlString)
			throws IOException {
		URL url = new URL("http://" + serverBase + urlString);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setDoOutput(true);
		OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		wr.write(urlString);
		wr.flush();
		System.out.println("      Sent: " + url.toString());

		// Get the response
		BufferedReader rd = new BufferedReader(new InputStreamReader(
				conn.getInputStream()));
		String line;
		while ((line = rd.readLine()) != null) {
			System.out.println("    Got: " + line);
		}
		wr.close();
		rd.close();
	}

	public void setServerBase(String serverBase) {
		this.serverBase = serverBase;
	}

	public String getServerBase() {
		return serverBase;
	}

}
