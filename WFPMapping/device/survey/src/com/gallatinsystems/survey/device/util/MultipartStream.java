package com.gallatinsystems.survey.device.util;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class MultipartStream {
	private static final String BOUNDRY = "***xxx";
	private static final String PREFIX = "--";
	private static final String ENDLINE = "\r\n";
	private static final int BUFFER_SIZE = 2048;

	private DataOutputStream out;

	public MultipartStream(OutputStream os) {
		out = new DataOutputStream(os);
	}

	public void writeFormField(String name, String value) throws IOException {
		out.writeBytes(PREFIX);
		out.writeBytes(BOUNDRY);
		out.writeBytes(ENDLINE);

		out.writeBytes("Content-Disposition: form-data; name=\"" + name + "\"");
		out.writeBytes(ENDLINE);
		out.writeBytes(ENDLINE);

		out.writeBytes(value != null ? value : "");
		out.writeBytes(ENDLINE);
		out.flush();
	}

	public void writeFile(String key, String filePath, String mimeType)
			throws IOException {
		out.writeBytes(PREFIX);
		out.writeBytes(BOUNDRY);
		out.writeBytes(ENDLINE);
		String destName = filePath;
		if (destName.contains("/")) {
			destName = destName.substring(destName.lastIndexOf("/") + 1);
		}else if (destName.contains("\\")){
			destName = destName.substring(destName.lastIndexOf("\\") + 1);
		}

		out.writeBytes("Content-Disposition: form-data; name=\"" + key
				+ "\"; filename=\"" + destName + "\"");
		out.writeBytes(ENDLINE);
		if (mimeType != null) {
			out.writeBytes("Content-Type: " + mimeType);
			out.writeBytes(ENDLINE);
		}
		out.writeBytes(ENDLINE);

		byte[] buffer = new byte[BUFFER_SIZE];
		int bytesRead = 0;
		FileInputStream fis = new FileInputStream(new File(filePath));
		while ((bytesRead = fis.read(buffer)) != -1) {
			out.write(buffer, 0, bytesRead);
		}
		try {
			fis.close();
		} catch (Exception e) {
		}
		out.writeBytes(ENDLINE);
		out.flush();
	}
	
	public void close() throws IOException{
		out.writeBytes(PREFIX);
		out.writeBytes(BOUNDRY);
		out.writeBytes(PREFIX);
		out.writeBytes(ENDLINE);
		out.flush();
		out.close();	
	}

	public static URLConnection createConnection(URL url)
			throws java.io.IOException {
		URLConnection urlConn = url.openConnection();
		if (urlConn instanceof HttpURLConnection) {
			HttpURLConnection httpConn = (HttpURLConnection) urlConn;
			httpConn.setRequestMethod("POST");
		}
		//connection level settings. This doesn't seem to have any effect with https!
		urlConn.setDoInput(true);
		urlConn.setDoOutput(true);
		urlConn.setUseCaches(false);
		urlConn.setDefaultUseCaches(false);
		//set up request props
		urlConn.setRequestProperty("Accept", "*/*");
		urlConn.setRequestProperty("Content-Type", 
				"multipart/form-data; boundary="+BOUNDRY);
		urlConn.setRequestProperty("Connection", "Keep-Alive");
		urlConn.setRequestProperty("Cache-Control", "no-cache");
		return urlConn;
	}
}
