package com.gallatinsystems.survey.device;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.gallatinsystems.survey.device.dao.SurveyDbAdapter;
import com.gallatinsystems.survey.device.domain.QuestionResponse;

/**
 * this activity will extract all submitted, unsent data from the database and
 * will form a zip file containing the data and corresponding image files (if
 * any). It will upload the data to the server and then will delete the data on
 * the device.
 * 
 * this activity can either export the zip file or export and send the zip file
 * (if it is invoked with the SEND type set under the TYPE_KEY in the extras
 * bundle)
 * 
 * @author Christopher Fagiani
 * 
 */
// TODO: Change this to a Service rather than an activity
public class DataSyncActivity extends Activity {

	private static final String TAG = "DATA_SYNC_ACTIVITY";

	public static final String EXPORT = "EXPORT";
	public static final String SEND = "SEND";
	public static final String TYPE_KEY = "TYPE";

	private static final int COMPLETE_ID = 1;
	// TODO: get S3 upload url
	private static final String UPLOAD_URL = "http://something-something-something-darkside";
	private static final String ENDLINE = "\r\n";
	private static final int BUF_SIZE = 2000;

	private SurveyDbAdapter databaseAdaptor;
	private static final String TEMP_FILE_NAME = "/wfp";
	private static final String ZIP_IMAGE_DIR = "images/";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String type = savedInstanceState != null ? savedInstanceState
				.getString(TYPE_KEY) : EXPORT;
		databaseAdaptor = new SurveyDbAdapter(this);
		databaseAdaptor.open();
		String fileName = formZip();
		if (fileName != null && SEND.equals(type)) {
			// TODO: may need to spawn a thread to do this
			sendFile(fileName);
			// TODO: call databaseAdaptor.markDataAsSent(idList)
			fireNotification(SEND);
		} else if (fileName != null) {
			fireNotification(EXPORT);
		}
		databaseAdaptor.close();
		finish();
	}

	/**
	 * displays a notification in the system status bar indicating the
	 * completion of the export/save operation
	 * 
	 * @param type
	 */
	private void fireNotification(String type) {
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
		// TODO: get a better icon
		int icon = android.R.drawable.ic_dialog_alert;
		CharSequence tickerText = null;
		if (SEND.equals(type)) {
			tickerText = getResources().getText(R.string.uploadcomplete);
		} else {
			tickerText = getResources().getText(R.string.exportcomplete);
		}
		long when = System.currentTimeMillis();
		Context context = getApplicationContext();

		Notification notification = new Notification(icon, tickerText, when);
		Intent notificationIntent = new Intent(this, DataSyncActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);
		notification.setLatestEventInfo(context, tickerText, tickerText,
				contentIntent);
		mNotificationManager.notify(COMPLETE_ID, notification);
	}

	/**
	 * create a zip file containing all the submitted data and images
	 * 
	 * @return
	 */
	private String formZip() {
		Cursor data = databaseAdaptor.fetchUnsentData();
		String fileName = null;
		try {
			if (data != null) {
				startManagingCursor(data);
				StringBuilder buf = new StringBuilder();
				ArrayList<String> imagePaths = new ArrayList<String>();
				do {
					String value = data.getString(data
							.getColumnIndexOrThrow(SurveyDbAdapter.ANSWER_COL));
					String type = data
							.getString(data
									.getColumnIndexOrThrow(SurveyDbAdapter.ANSWER_TYPE_COL));
					buf
							.append(data
									.getString(data
											.getColumnIndexOrThrow(SurveyDbAdapter.SURVEY_RESPONDENT_ID_COL)));
					buf
							.append(",")
							.append(
									data
											.getString(data
													.getColumnIndexOrThrow(SurveyDbAdapter.QUESTION_COL)));
					buf.append(",").append(type);
					buf.append(",").append(value);
					buf.append("\n");
					if (QuestionResponse.IMAGE_TYPE.equals(type)) {
						imagePaths.add(value);
					}
				} while (data.moveToNext());

				File zipFile = new File(Environment
						.getExternalStorageDirectory().getAbsolutePath()
						+ TEMP_FILE_NAME + System.nanoTime() + ".zip");
				fileName = zipFile.getAbsolutePath();

				ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(
						zipFile));
				zos.putNextEntry(new ZipEntry("data.txt"));
				String tempString = buf.toString();
				byte[] buffer = new byte[BUF_SIZE];
				for (int i = 0; i < tempString.getBytes().length; i += BUF_SIZE) {
					int size = i + BUF_SIZE < tempString.getBytes().length ? BUF_SIZE
							: tempString.getBytes().length - i;
					tempString.getBytes(i, i + size, buffer, 0);
					zos.write(buffer, 0, size);
				}
				zos.closeEntry();

				// write images
				for (int i = 0; i < imagePaths.size(); i++) {
					try {
						BufferedInputStream bin = new BufferedInputStream(
								new FileInputStream(imagePaths.get(i)));
						String name = ZIP_IMAGE_DIR;
						if (imagePaths.get(i).contains("/")) {
							name = name
									+ imagePaths
											.get(i)
											.substring(
													imagePaths.get(i)
															.lastIndexOf("/") + 1);
						} else {
							name = name + imagePaths.get(i);
						}
						zos.putNextEntry(new ZipEntry(name));
						int bytesRead = bin.read(buffer);
						while (bytesRead > 0) {
							zos.write(buffer, 0, bytesRead);
							bytesRead = bin.read(buffer);
						}
						bin.close();
						zos.closeEntry();
					} catch (Exception e) {
						Log.e(TAG, "Could not add image " + imagePaths.get(i)
								+ " to zip: " + e.getMessage());
					}
				}
				zos.close();
			}
		} catch (Exception e) {
			Log.e(TAG, "Could not save zip: " + e.getMessage());
			fileName = null;
		}

		return fileName;
	}

	/**
	 * sends the zip file containing data/images to the server via an http
	 * upload
	 * 
	 * @param fileAbsolutePath
	 */
	private void sendFile(String fileAbsolutePath) {
		HttpURLConnection conn = null;
		DataOutputStream out = null;
		try {

			FileInputStream fileInputStream = new FileInputStream(new File(
					fileAbsolutePath));

			// setup the url connection
			URL url = new URL(UPLOAD_URL);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);

			// configure the request
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			// TODO: set appropriate parameters
			/*
			 * conn.setRequestProperty("Content-Type",
			 * "multipart/form-data;boundary=" + boundary);
			 */
			conn.setRequestProperty("Content-Type", "multipart/form-data;");
			out = new DataOutputStream(conn.getOutputStream());

			// out.writeBytes(twoHyphens + boundary + lineEnd);
			out
					.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\""
							+ fileAbsolutePath + "\"" + ENDLINE);
			out.writeBytes(ENDLINE);

			// read file and write it into form...
			byte[] buffer = new byte[BUF_SIZE];
			int bytesRead = fileInputStream.read(buffer);
			while (bytesRead > 0) {
				out.write(buffer, 0, bytesRead);
				bytesRead = fileInputStream.read(buffer);
			}

			// send multipart form data necesssary after file data...

			out.writeBytes(ENDLINE);
			// dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

			fileInputStream.close();
			out.flush();
			out.close();

		} catch (Exception ex) {
			Log.e(TAG, "error: " + ex.getMessage(), ex);
		}
	}
}
