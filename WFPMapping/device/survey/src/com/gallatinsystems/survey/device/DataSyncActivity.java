package com.gallatinsystems.survey.device;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

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
import com.gallatinsystems.survey.device.util.MultipartStream;

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

	private static final String NOTIFICATION_URL = "http://watermappingmonitoring.appspot.com/processor?action=submit&fileName=";
	private static final String UPLOAD_URL = "http://waterforpeople.s3.amazonaws.com/";
	private static final String S3_KEY = "1JZZVDSNFFQYF23ZYJ02";
	private static final String S3_POLICY = "eyJleHBpcmF0aW9uIjogIjIwMTAtMTAtMDJUMDA6MDA6MDBaIiwgICJjb25kaXRpb25zIjogWyAgICAgeyJidWNrZXQiOiAid2F0ZXJmb3JwZW9wbGUifSwgICAgIFsic3RhcnRzLXdpdGgiLCAiJGtleSIsICJkZXZpY2V6aXAvIl0sICAgIHsiYWNsIjogInB1YmxpYy1yZWFkIn0sICAgIHsic3VjY2Vzc19hY3Rpb25fcmVkaXJlY3QiOiAiaHR0cDovL3d3dy5nYWxsYXRpbnN5c3RlbXMuY29tL1N1Y2Nlc3NVcGxvYWQuaHRtbCJ9LCAgICBbInN0YXJ0cy13aXRoIiwgIiRDb250ZW50LVR5cGUiLCAiIl0sICAgIFsiY29udGVudC1sZW5ndGgtcmFuZ2UiLCAwLCAzMTQ1NzI4XSAgXX0=";
	private static final String S3_SIG = "7/fo9v4qamQJjnbga529k3iZMZE=";
	private static final int BUF_SIZE = 2048;

	private SurveyDbAdapter databaseAdaptor;
	private static final String TEMP_FILE_NAME = "/wfp";
	private static final String ZIP_IMAGE_DIR = "images/";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle extras = getIntent().getExtras();
		String type = extras != null ? extras.getString(TYPE_KEY) : EXPORT;
		databaseAdaptor = new SurveyDbAdapter(this);
		databaseAdaptor.open();
		String fileName = formZip();
		String destName = fileName;
		if (destName.contains("/")) {
			destName = destName.substring(destName.lastIndexOf("/") + 1);
		} else if (destName.contains("\\")) {
			destName = destName.substring(destName.lastIndexOf("\\") + 1);
		}
		if (fileName != null && SEND.equals(type)) {
			// TODO: may need to spawn a thread to do this
			sendFile(fileName);
			if (sendProcessingNotification(destName)) {
				// TODO: call databaseAdaptor.markDataAsSent(idList)
				fireNotification(SEND, destName);
			}else{
				//TODO: handle failure?
			}
		} else if (fileName != null) {
			fireNotification(EXPORT, destName);
		}
		databaseAdaptor.close();
		finish();
	}

	private boolean sendProcessingNotification(String fileName) {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpResponse response = null;
		boolean success = false;
		try {
			response = (client
					.execute(new HttpGet(NOTIFICATION_URL + fileName)));
			if (response.getStatusLine().getStatusCode() != 200) {
				Log.e(TAG, "Serivce returned a bad status code");
			} else {
				success = true;
			}
		} catch (Exception e) {
			Log.e(TAG, "Could not send processing call", e);
		}
		return success;
	}

	/**
	 * displays a notification in the system status bar indicating the
	 * completion of the export/save operation
	 * 
	 * @param type
	 */
	private void fireNotification(String type, String fileName) {
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
		notification.setLatestEventInfo(context, tickerText, fileName,
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
	private boolean sendFile(String fileAbsolutePath) {

		try {
			URLConnection conn = MultipartStream.createConnection(new URL(
					UPLOAD_URL));
			MultipartStream stream = new MultipartStream(conn.getOutputStream());
			stream.writeFormField("key", "devicezip/${filename}");
			stream.writeFormField("AWSAccessKeyId", S3_KEY);
			stream.writeFormField("acl", "public-read");
			stream.writeFormField("success_action_redirect",
					"http://www.gallatinsystems.com/SuccessUpload.html");
			stream.writeFormField("policy", S3_POLICY);
			stream.writeFormField("signature", S3_SIG);
			stream.writeFormField("Content-Type", "application/zip");
			stream.writeFile("file", fileAbsolutePath, null);
			stream.close();
			// TODO: check error code!
			try {
				DataInputStream inStream = new DataInputStream(conn
						.getInputStream());
				String str;

				while ((str = inStream.readLine()) != null) {
					Log.e(TAG, "Server Response" + str);
				}
				inStream.close();

			} catch (Exception ioex) {
				Log.e(TAG, "error: " + ioex.getMessage(), ioex);
			}

		} catch (Exception e) {
			Log.e(TAG, "Could not send upload" + e.getMessage(), e);
			return false;
		}
		return true;

	}
}
