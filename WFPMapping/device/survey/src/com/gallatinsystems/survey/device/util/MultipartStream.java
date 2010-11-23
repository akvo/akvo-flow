package com.gallatinsystems.survey.device.util;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MultipartStream {
	private static final String BOUNDRY = "***xxx";
	private static final String PREFIX = "--";
	private static final String ENDLINE = "\r\n";
	private static final int DELIMITER_BYTES = (PREFIX.getBytes().length
			+ BOUNDRY.getBytes().length + ENDLINE.getBytes().length);
	private static final int BUFFER_SIZE = 2048;
	private static final String FORM_PARAM_TEXT_START = "Content-Disposition: form-data; name=\"";
	private static final String FILE_PARAM_TEXT_MID = "\"; filename=\"";
	private ArrayList<String> names;
	private ArrayList<String> values;
	private ArrayList<String> formParamTextList;
	private ArrayList<File> files;
	private ArrayList<String> fileParamTextList;
	private int totalBytes;
	private ArrayList<String> mimeTypeTextList;
	private URL url;

	private DataOutputStream out;

	public MultipartStream(URL url) {
		names = new ArrayList<String>();
		values = new ArrayList<String>();
		files = new ArrayList<File>();
		formParamTextList = new ArrayList<String>();
		fileParamTextList = new ArrayList<String>();
		mimeTypeTextList = new ArrayList<String>();
		this.url = url;
		totalBytes = DELIMITER_BYTES + PREFIX.getBytes().length;
	}

	public void addFormField(String name, String value) {
		names.add(name);
		if (value == null) {
			value = "";
		}
		values.add(value);
		totalBytes += DELIMITER_BYTES;
		String formParamText = FORM_PARAM_TEXT_START + name + "\"";
		formParamTextList.add(formParamText);
		totalBytes += formParamText.getBytes().length;
		totalBytes += (3 * ENDLINE.getBytes().length);
		totalBytes += value.getBytes().length;
	}

	private void writeFormField(String name, String value, String formParamText)
			throws IOException {
		out.writeBytes(PREFIX);
		out.writeBytes(BOUNDRY);
		out.writeBytes(ENDLINE);
		out.writeBytes(formParamText);
		out.writeBytes(ENDLINE);
		out.writeBytes(ENDLINE);
		out.writeBytes(value);
		out.writeBytes(ENDLINE);
		out.flush();
	}

	public void addFile(String key, String filePath, String mimeType) {
		File file = new File(filePath);
		files.add(file);
		totalBytes += DELIMITER_BYTES;

		String destName = filePath;
		if (destName.contains("/")) {
			destName = destName.substring(destName.lastIndexOf("/") + 1);
		} else if (destName.contains("\\")) {
			destName = destName.substring(destName.lastIndexOf("\\") + 1);
		}
		String fileParamText = FORM_PARAM_TEXT_START + key
				+ FILE_PARAM_TEXT_MID + destName + "\"";
		totalBytes += (fileParamText.getBytes().length);
		fileParamTextList.add(fileParamText);

		if (mimeType != null) {
			String mimeTypeText = "Content-Type: " + mimeType;
			mimeTypeTextList.add(mimeTypeText);
			totalBytes += mimeTypeText.getBytes().length
					+ ENDLINE.getBytes().length;
		} else {
			mimeTypeTextList.add("");
		}

		totalBytes += file.length();
		totalBytes += 3 * ENDLINE.getBytes().length;
	}

	private void writeFiles() throws IOException {
		for (int i = 0; i < files.size(); i++) {
			out.writeBytes(PREFIX);
			out.writeBytes(BOUNDRY);
			out.writeBytes(ENDLINE);

			out.writeBytes(fileParamTextList.get(i));
			out.writeBytes(ENDLINE);
			String mimeTypeText = mimeTypeTextList.get(i);
			if (mimeTypeText != null && mimeTypeText.length() > 0) {
				out.writeBytes(mimeTypeText);
				out.writeBytes(ENDLINE);
			}
			out.writeBytes(ENDLINE);

			byte[] buffer = new byte[BUFFER_SIZE];
			int bytesRead = 0;
			FileInputStream fis = new FileInputStream(files.get(i));

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
	}

	private void close() throws IOException {
		out.writeBytes(PREFIX);
		out.writeBytes(BOUNDRY);
		out.writeBytes(PREFIX);
		out.writeBytes(ENDLINE);
		out.flush();
		out.close();
	}

	public int execute() throws java.io.IOException {
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
		urlConn.setFixedLengthStreamingMode(totalBytes);

		// set up request props
		urlConn.setRequestProperty("Accept", "*/*");
		urlConn.setRequestProperty("Content-Type",
				"multipart/form-data; boundary=" + BOUNDRY);
		urlConn.setRequestProperty("Connection", "Keep-Alive");
		urlConn.setRequestProperty("Cache-Control", "no-cache");
		urlConn.setRequestProperty("Content-Length", totalBytes + "");
		out = new DataOutputStream(urlConn.getOutputStream());
		for (int i = 0; i < names.size(); i++) {
			writeFormField(names.get(i), values.get(i), formParamTextList
					.get(i));
		}
		writeFiles();
		close();

		int code = urlConn.getResponseCode();
		return code;
	}
}
