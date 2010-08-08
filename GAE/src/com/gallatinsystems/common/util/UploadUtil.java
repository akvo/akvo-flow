package com.gallatinsystems.common.util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UploadUtil {
	private static final String BOUNDRY = "***xxx";
	private static final String PREFIX = "--";
	private static final String ENDLINE = "\r\n";
	private static final int REDIRECT_CODE = 303;
	private static final int OK_CODE = 200;
	private static final String XML_MIME_TYPE = "text/xml";
	private static Logger logger = Logger.getLogger(UploadUtil.class
			.getName());

	private DataOutputStream out;

	private UploadUtil(OutputStream os) {
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

	public void writeFile(String key, String fileName, String fileContent,
			String mimeType) throws IOException {
		out.writeBytes(PREFIX);
		out.writeBytes(BOUNDRY);
		out.writeBytes(ENDLINE);
		String destName = fileName;
		if (destName.contains("/")) {
			destName = destName.substring(destName.lastIndexOf("/") + 1);
		} else if (destName.contains("\\")) {
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

		byte[] allBytes = fileContent.getBytes("UTF-8");

		out.write(allBytes);

		out.writeBytes(ENDLINE);
		out.flush();
	}

	public void close() throws IOException {
		out.writeBytes(PREFIX);
		out.writeBytes(BOUNDRY);
		out.writeBytes(PREFIX);
		out.writeBytes(ENDLINE);
		out.flush();
		out.close();
	}

	public static HttpURLConnection createConnection(URL url)
			throws java.io.IOException {
		HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
		if (urlConn instanceof HttpURLConnection) {
			HttpURLConnection httpConn = (HttpURLConnection) urlConn;
			httpConn.setRequestMethod("POST");
		}
		// connection level settings. This doesn't seem to have any effect with
		// https!
		urlConn.setDoInput(true);
		urlConn.setDoOutput(true);
		urlConn.setUseCaches(false);
		urlConn.setDefaultUseCaches(false);
		// set up request props
		urlConn.setRequestProperty("Accept", "*/*");
		urlConn.setRequestProperty("Content-Type",
				"multipart/form-data; boundary=" + BOUNDRY);
		urlConn.setRequestProperty("Connection", "Keep-Alive");
		urlConn.setRequestProperty("Cache-Control", "no-cache");
		return urlConn;
	}

	/**
	 * sends the zip file containing data/images to the server via an http
	 * upload
	 * 
	 * @param fileAbsolutePath
	 */
	public static boolean sendStringAsFile(String fileName,
			String fileContents, String dir, String uploadUrl, String s3ID,
			String policy, String sig, String contentType) {

		try {
			HttpURLConnection conn = UploadUtil.createConnection(new URL(
					uploadUrl));
			UploadUtil stream = new UploadUtil(conn.getOutputStream());
			stream.writeFormField("key", dir + "/${filename}");
			stream.writeFormField("AWSAccessKeyId", s3ID);
			stream.writeFormField("acl", "public-read");
			stream.writeFormField("success_action_redirect",
					"http://www.gallatinsystems.com/SuccessUpload.html");
			stream.writeFormField("policy", policy);
			stream.writeFormField("signature", sig);
			stream.writeFormField("Content-Type", contentType);
			stream.writeFile("file", fileName, fileContents, XML_MIME_TYPE);
			stream.close();
			int code = conn.getResponseCode();
			if (code != REDIRECT_CODE && code != OK_CODE) {
				logger.log(Level.SEVERE,
						"Server returned a bad code after upload: " + code);
				return false;
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Could not send upload" + e.getMessage(),
					e);
			return false;
		}
		return true;
	}
}
