package com.gallatinsystems.common.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.json.JSONObject;
import org.waterforpeople.mapping.app.gwt.client.devicefiles.DeviceFilesDto;
import org.waterforpeople.mapping.dataexport.service.BulkDataServiceClient;

import services.S3Driver;

public class DeviceFileUploadCompare {
	private String serverBase = null;

	class DeviceFileResponseInternalContainer {
		private Boolean foundFlag = null;
		private DeviceFilesDto deviceFilesDto = null;

		public Boolean getFoundFlag() {
			return foundFlag;
		}

		public void setFoundFlag(Boolean foundFlag) {
			this.foundFlag = foundFlag;
		}

		public DeviceFilesDto getDeviceFilesDto() {
			return deviceFilesDto;
		}

		public void setDeviceFilesDto(DeviceFilesDto deviceFilesDto) {
			this.deviceFilesDto = deviceFilesDto;
		}
	}

	private String awsident = null;

	public String getAwsident() {
		return awsident;
	}

	public void setAwsident(String awsident) {
		this.awsident = awsident;
	}

	public String getAwssecret() {
		return awssecret;
	}

	public void setAwssecret(String awssecret) {
		this.awssecret = awssecret;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	private String awssecret = null;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DeviceFileUploadCompare dfuc = new DeviceFileUploadCompare();
		dfuc.setServerBase(args[2]);
		dfuc.fileName = args[3];
		dfuc.checkAllFiles(args[0], args[1]);
	}

	private void checkAllFiles(String key, String identifier) {
		compare(key, identifier);
	}
	private ArrayList<S3Item> s3ItemList = new ArrayList<S3Item>();
	
	public void addS3Item(String name, Long sizeBytes, Date lastUpdateDate){
		S3Item item = new S3Item();
		item.setLastUpdateDate(lastUpdateDate);
		item.setName(name);
		item.setSizeBytes(sizeBytes);
		s3ItemList.add(item);
	}

	public class S3Item {
		private String name = null;
		private Long sizeBytes = null;
		private Date lastUpdateDate = null;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Long getSizeBytes() {
			return sizeBytes;
		}

		public void setSizeBytes(Long sizeBytes) {
			this.sizeBytes = sizeBytes;
		}

		public Date getLastUpdateDate() {
			return lastUpdateDate;
		}

		public void setLastUpdateDate(Date lastUpdateDate) {
			this.lastUpdateDate = lastUpdateDate;
		}

	}

	private void compare(String key, String identifier) {
		S3Driver s3 = new S3Driver(key, identifier);
		String bucketName = "waterforpeople";
		String fileDir = "devicezip";
		String fileListPath = null;
		List<String> fileList = s3.listAllFiles(bucketName, fileDir,
				fileListPath);
		HashMap<String, DeviceFileResponseInternalContainer> filesMap = new HashMap<String, DeviceFileResponseInternalContainer>();
		Integer iCount = 0;
		for (String item : fileList) {
			if (item.endsWith(".zip")) {
				System.out.print("iCount: " + iCount++ + "   ");
				DeviceFileResponseInternalContainer container = findFile(item);
				filesMap.put(item, container);
			}
		}
		writeReport(filesMap);
	}

	public void compare() {
		HashMap<S3Item, DeviceFileResponseInternalContainer> filesMap = new HashMap<S3Item, DeviceFileResponseInternalContainer>();
		Integer iCount = 0;
		for (S3Item item : s3ItemList) {
			if (item.getName().endsWith(".zip")) {
				System.out.print("iCount: " + iCount++ + "   ");
				DeviceFileResponseInternalContainer container = findFile(item
						.getName());
				filesMap.put(item, container);
			}
		}
		writeReportDetail(filesMap);
	}

	private void writeReport(
			HashMap<String, DeviceFileResponseInternalContainer> filesMap) {
		StringBuilder sb = new StringBuilder();
		sb.append("File on S3,Found in FLOW,Status,SurveyInstanceId,Processed Date\n");
		for (Entry<String, DeviceFileResponseInternalContainer> item : filesMap
				.entrySet()) {
			String s3file = item.getKey();
			DeviceFileResponseInternalContainer df = item.getValue();
			sb.append(s3file + ",");
			if (df != null && df.getFoundFlag() != null) {
				sb.append(df.getFoundFlag() + ",");
				if (df.getFoundFlag()) {
					sb.append(df.getDeviceFilesDto().getProcessedStatus() + ","
							+ df.getDeviceFilesDto().getSurveyInstanceId()
							+ "," + df.getDeviceFilesDto().getProcessDate()
							+ "\n");
				} else {
					sb.append("\n");
				}
			} else {
				sb.append("Couldn't obtain FLOW data for file\n");
			}
		}
		System.out.println(sb.toString());
		BufferedWriter out;
		try {
			out = new BufferedWriter(new FileWriter(fileName));
			out.write(sb.toString());
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void writeReportDetail(
			HashMap<S3Item, DeviceFileResponseInternalContainer> filesMap) {
		StringBuilder sb = new StringBuilder();
		sb.append("File on S3,S3 Last Update Date, Size (Bytes),Found in FLOW,Status,SurveyInstanceId,Processed Date\n");
		for (Entry<S3Item, DeviceFileResponseInternalContainer> item : filesMap
				.entrySet()) {
			S3Item s3file = item.getKey();
			DeviceFileResponseInternalContainer df = item.getValue();
			sb.append(s3file.getName() + "," + s3file.getLastUpdateDate() + ","
					+ s3file.getSizeBytes() + ",");
			if (df != null && df.getFoundFlag() != null) {
				sb.append(df.getFoundFlag() + ",");
				if (df.getFoundFlag()) {
					sb.append(df.getDeviceFilesDto().getProcessedStatus() + ","
							+ df.getDeviceFilesDto().getSurveyInstanceId()
							+ "," + df.getDeviceFilesDto().getProcessDate()
							+ "\n");
				} else {
					sb.append("\n");
				}
			} else {
				sb.append("Couldn't obtain FLOW data for file\n");
			}
		}
		System.out.println(sb.toString());
		BufferedWriter out;
		try {
			out = new BufferedWriter(new FileWriter(fileName));
			out.write(sb.toString());
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String fileName = null;

	private DeviceFileResponseInternalContainer findFile(String fileName) {
		String serviceUrl = "/devicefilesrestapi?action=%s&"
				+ "deviceFullPath=%s";
		String urlRequest = null;
		String actionParam = "findDeviceFile";
		String prefix = "http://waterforpeople.s3.amazonaws.com/";
		urlRequest = String.format(serviceUrl, actionParam, prefix + fileName);
		try {
			return sendRequest(getServerBase(), urlRequest);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private DeviceFileResponseInternalContainer sendRequest(String serverBase,
			String urlString) throws IOException {
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
		StringBuilder response = new StringBuilder();
		while ((line = rd.readLine()) != null) {
			System.out.println("    Got: " + line);
			response.append(line);
		}
		DeviceFileResponseInternalContainer container = parseResponse(response
				.toString());
		wr.close();
		rd.close();
		return container;
	}

	private DeviceFileResponseInternalContainer parseResponse(String response) {
		try {
			DeviceFileResponseInternalContainer container = new DeviceFileResponseInternalContainer();
			if (response.startsWith("{")) {
				JSONObject json = new JSONObject(response);
				if (json.has("foundFlag")) {
					Boolean foundFlag = Boolean.parseBoolean(json
							.getString("foundFlag"));
					container.setFoundFlag(foundFlag);
					if (foundFlag) {
						if (json.has("deviceFile")) {
							DeviceFilesDto item = BulkDataServiceClient
									.parseDeviceFile(json
											.getJSONObject("deviceFile"));
							container.setDeviceFilesDto(item);
						}
					}
				}
				return container;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public void setServerBase(String serverBase) {
		this.serverBase = serverBase;
	}

	public String getServerBase() {
		return serverBase;
	}

}
