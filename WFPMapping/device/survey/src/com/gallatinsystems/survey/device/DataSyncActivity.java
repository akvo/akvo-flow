package com.gallatinsystems.survey.device;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.gallatinsystems.survey.device.dao.SurveyDbAdapter;
import com.gallatinsystems.survey.device.domain.Question;
import com.gallatinsystems.survey.device.domain.QuestionResponse;

/**
 * this activity will extract all submitted, unsent data from the database and
 * will form a zip file containing the data and corresponding image files (if
 * any). It will upload the data to the server and then will delete the data on
 * the device.
 * 
 * @author Christopher Fagiani
 * 
 */
public class DataSyncActivity extends Activity {

	private static final String TAG = "DATA_SYNC_ACTIVITY";
	private static final int BUF_SIZE = 2000;
	private SurveyDbAdapter databaseAdaptor;
	private static final String TEMP_FILE_NAME = "/datafile";
	private static final String ZIP_IMAGE_DIR = "images/";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		databaseAdaptor = new SurveyDbAdapter(this);
		databaseAdaptor.open();
		String fileName = formZip();
		if (fileName != null) {
			// TODO: may need to spawn a thread to do this
			sendFile(fileName);
			// TODO: call databaseAdaptor.markDataAsSent(idList)
		}
		databaseAdaptor.close();
		finish();
	}

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
									+ imagePaths.get(i).substring(
											imagePaths.get(i).lastIndexOf("/")+1);
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

	private void sendFile(String fileAbsolutePath) {
		// TODO send file to server
	}
}
