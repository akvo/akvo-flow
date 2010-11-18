package com.gallatinsystems.survey.device.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.concurrent.Semaphore;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.gallatinsystems.survey.device.R;
import com.gallatinsystems.survey.device.dao.SurveyDbAdapter;
import com.gallatinsystems.survey.device.exception.PersistentUncaughtExceptionHandler;
import com.gallatinsystems.survey.device.util.ConstantUtil;
import com.gallatinsystems.survey.device.util.HttpUtil;
import com.gallatinsystems.survey.device.util.MultipartStream;
import com.gallatinsystems.survey.device.util.PropertyUtil;
import com.gallatinsystems.survey.device.util.StatusUtil;
import com.gallatinsystems.survey.device.util.ViewUtil;

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
 * 
 * @author Christopher Fagiani
 * 
 */
public class DataSyncService extends Service {

	private static final String TAG = "DATA_SYNC_ACTIVITY";
	private static final String NOTHING = "NADA";

	private static final int COMPLETE_ID = 1;

	private static final boolean INCLUDE_IMAGES_IN_ZIP = false;

	private static final String NOTIFICATION_PATH = "/processor?action=submit&fileName=";
	private static final String NOTIFICATION_PN_PARAM = "&phoneNumber=";
	private static final String CHECKSUM_PARAM = "&checksum=";
	private static final String DATA_CONTENT_TYPE = "application/zip";
	private static final String S3_DATA_FILE_PATH = "devicezip";
	private static final String IMAGE_CONTENT_TYPE = "image/jpeg";
	private static final String S3_IMAGE_FILE_PATH = "images";

	private static final int BUF_SIZE = 2048;

	private SurveyDbAdapter databaseAdaptor;
	private static final String TEMP_FILE_NAME = "/wfp";
	private static final String ZIP_IMAGE_DIR = "images/";
	private static final String SURVEY_DATA_FILE = "data.txt";
	private static final String REGION_DATA_FILE = "regions.txt";
	private Thread thread;
	private static final int REDIRECT_CODE = 303;
	private static final int OK_CODE = 200;
	private static Semaphore lock = new Semaphore(1);
	private static int counter = 0;
	private Properties props;

	public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * life cycle method for the service. This is called by the system when the
	 * service is started
	 */
	public int onStartCommand(final Intent intent, int flags, int startid) {
		thread = new Thread(new Runnable() {
			public void run() {
				if (intent != null) {
					Bundle extras = intent.getExtras();
					String type = extras != null ? extras
							.getString(ConstantUtil.OP_TYPE_KEY)
							: ConstantUtil.SEND;
					boolean forceFlag = extras != null ? extras.getBoolean(
							ConstantUtil.FORCE_KEY, false) : false;
					runSync(type, forceFlag);
				}
			}
		});
		thread.start();
		return Service.START_STICKY;
	}

	public void onCreate() {
		super.onCreate();
		Thread
				.setDefaultUncaughtExceptionHandler(PersistentUncaughtExceptionHandler
						.getInstance());
		props = PropertyUtil.loadProperties(getResources());
	}

	/**
	 * executes the data export/sync operation (based on the type passed in).
	 * 
	 * @param type
	 *            - either SYNC or EXPORT
	 */
	private void runSync(String type, boolean forceFlag) {
		try {
			lock.acquire();

			databaseAdaptor = new SurveyDbAdapter(this);
			databaseAdaptor.open();
			String uploadOption = databaseAdaptor
					.findPreference(ConstantUtil.CELL_UPLOAD_SETTING_KEY);
			String serverBase = databaseAdaptor
					.findPreference(ConstantUtil.SERVER_SETTING_KEY);
			if (serverBase != null && serverBase.trim().length() > 0) {
				serverBase = getResources().getStringArray(R.array.servers)[Integer
						.parseInt(serverBase)];
			} else {
				serverBase = props.getProperty(ConstantUtil.SERVER_BASE);
			}
			int uploadIndex = -1;
			if (uploadOption != null && uploadOption.trim().length() > 0) {
				uploadIndex = Integer.parseInt(uploadOption);
			}

			counter++;
			if (isAbleToRun(type, uploadIndex)) {
				String fileName = createFileName();
				HashSet<String>[] idList = formZip(fileName,
						(ConstantUtil.UPLOAD_DATA_ONLY_IDX == uploadIndex));
				String destName = fileName;
				if (destName.contains("/")) {
					destName = destName
							.substring(destName.lastIndexOf("/") + 1);
				} else if (destName.contains("\\")) {
					destName = destName
							.substring(destName.lastIndexOf("\\") + 1);
				}
				if (fileName != null
						&& (idList[0].size() > 0 || idList[1].size() > 0)) {
					String checksum = null;
					if (idList[2].size() > 0) {
						checksum = idList[2].iterator().next();
					}
					if (ConstantUtil.SEND.equals(type)) {
						if (idList[0] != null) {
							for (String id : idList[0]) {
								databaseAdaptor.updateTransmissionHistory(
										new Long(id), fileName,
										ConstantUtil.IN_PROGRESS_STATUS);
							}
						}
						boolean isOk = sendFile(fileName, S3_DATA_FILE_PATH,
								props.getProperty(ConstantUtil.DATA_S3_POLICY),
								props.getProperty(ConstantUtil.DATA_S3_SIG),
								DATA_CONTENT_TYPE);
						if (isOk) {
							if (sendProcessingNotification(serverBase,
									destName, checksum)) {
								if (idList[0].size() > 0) {
									databaseAdaptor
											.markDataAsSent(
													idList[0],
													""
															+ StatusUtil
																	.hasDataConnection(
																			this,
																			(ConstantUtil.UPLOAD_DATA_ONLY_IDX == uploadIndex)));
									// update the transmission history records
									// too
									for (String id : idList[0]) {
										databaseAdaptor
												.updateTransmissionHistory(
														new Long(id),
														fileName,
														ConstantUtil.COMPLETE_STATUS);
									}

								}
								if (idList[1].size() > 0) {
									databaseAdaptor.updatePlotStatus(idList[1],
											ConstantUtil.SENT_STATUS);
								}
								fireNotification(ConstantUtil.SEND, destName);
							} else {
								Log
										.e(
												TAG,
												"Could not update send status of data in the database. It will be resent on next execution of the service");
							}
						}
					} else {
						fireNotification(ConstantUtil.EXPORT, destName);
					}
				} else if (forceFlag) {
					fireNotification(NOTHING, null);
				}

			}
		} catch (InterruptedException e) {
			Log.e(TAG, "Data sync interrupted", e);
			PersistentUncaughtExceptionHandler.recordException(e);
		} finally {
			databaseAdaptor.close();
			lock.release();
		}
		counter--;
		if (counter == 0) {
			stopSelf();
		}
	}

	/**
	 * sends a message to the service with the file name that was just uploaded
	 * so it can start processing the file
	 * 
	 * @param fileName
	 * @return
	 */
	private boolean sendProcessingNotification(String serverBase,
			String fileName, String checksum) {
		boolean success = false;
		try {
			HttpUtil.httpGet(serverBase + NOTIFICATION_PATH + fileName
					+ NOTIFICATION_PN_PARAM + StatusUtil.getPhoneNumber(this)
					+ CHECKSUM_PARAM + checksum);
			success = true;
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
	private void fireNotification(String type, String extraText) {
		CharSequence tickerText = null;
		if (ConstantUtil.SEND.equals(type)) {
			tickerText = getResources().getText(R.string.uploadcomplete);
		} else if (ConstantUtil.EXPORT.equals(type)) {
			tickerText = getResources().getText(R.string.exportcomplete);
		} else if (ConstantUtil.PROGRESS.equals(type)) {
			tickerText = getResources().getText(R.string.uploadprogress);
		} else if (ConstantUtil.FILE_COMPLETE.equals(type)) {
			tickerText = getResources().getText(R.string.filecomplete);
		} else {
			tickerText = getResources().getText(R.string.nothingtoexport);
		}
		ViewUtil.fireNotification(tickerText.toString(),
				extraText != null ? extraText : "", this, COMPLETE_ID, null);
	}

	/**
	 * create a zip file containing all the submitted data and images
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private HashSet<String>[] formZip(String fileName, boolean dataOnly) {
		HashSet<String>[] idsToUpdate = new HashSet[3];
		idsToUpdate[0] = new HashSet<String>();
		idsToUpdate[1] = new HashSet<String>();
		idsToUpdate[2] = new HashSet<String>();
		StringBuilder surveyBuf = new StringBuilder();
		HashMap<String, ArrayList<String>> imagePaths = new HashMap<String, ArrayList<String>>();
		StringBuilder regionBuf = new StringBuilder();
		try {
			// extract survey data
			processSurveyData(surveyBuf, imagePaths, idsToUpdate[0]);

			// extract region data
			processRegionData(regionBuf, idsToUpdate[1]);

			// now write the data
			if (idsToUpdate[0].size() > 0 || idsToUpdate[1].size() > 0) {
				File zipFile = new File(fileName);
				fileName = zipFile.getAbsolutePath();
				Log.i(TAG, "Creating zip file: " + fileName);
				FileOutputStream fout = new FileOutputStream(zipFile);
				CheckedOutputStream checkedOutStream = new CheckedOutputStream(
						fout, new Adler32());
				ZipOutputStream zos = new ZipOutputStream(checkedOutStream);

				// write the survey data
				if (idsToUpdate[0].size() > 0) {
					writeTextToZip(zos, surveyBuf.toString(), SURVEY_DATA_FILE);
					for (String id : idsToUpdate[0]) {
						databaseAdaptor.createTransmissionHistory(new Long(id),
								fileName, null);
					}
				}
				if (idsToUpdate[1].size() > 0) {
					writeTextToZip(zos, regionBuf.toString(), REGION_DATA_FILE);
				}

				// write images if enabled
				if (!dataOnly) {
					byte[] buffer = new byte[BUF_SIZE];
					for (Entry<String, ArrayList<String>> paths : imagePaths
							.entrySet()) {

						for (int i = 0; i < paths.getValue().size(); i++) {
							if (INCLUDE_IMAGES_IN_ZIP) {
								try {
									BufferedInputStream bin = new BufferedInputStream(
											new FileInputStream(paths
													.getValue().get(i)));
									String name = ZIP_IMAGE_DIR;
									if (imagePaths.get(i).contains("/")) {
										name = name
												+ paths
														.getValue()
														.get(i)
														.substring(
																paths
																		.getValue()
																		.get(i)
																		.lastIndexOf(
																				"/") + 1);
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
									Log.e(TAG, "Could not add image "
											+ imagePaths.get(i) + " to zip: "
											+ e.getMessage());
								}
							} else {
								try {
									databaseAdaptor.createTransmissionHistory(
											new Long(paths.getKey()), paths
													.getValue().get(i),
											ConstantUtil.IN_PROGRESS_STATUS);
									boolean isOk = sendFile(
											paths.getValue().get(i),
											S3_IMAGE_FILE_PATH,
											props
													.getProperty(ConstantUtil.IMAGE_S3_POLICY),
											props
													.getProperty(ConstantUtil.IMAGE_S3_SIG),
											IMAGE_CONTENT_TYPE);
									if(isOk){
										databaseAdaptor.updateTransmissionHistory(new Long(paths.getKey()), paths
													.getValue().get(i), ConstantUtil.COMPLETE_STATUS);
									}else{
										databaseAdaptor.updateTransmissionHistory(new Long(paths.getKey()), paths
												.getValue().get(i), ConstantUtil.FAILED_STATUS);
									}
								} catch (Exception e) {
									Log.e(TAG, "Could not add image "
											+ imagePaths.get(i) + " to zip: "
											+ e.getMessage());
								}
							}
						}
					}
					Log.i(TAG, "Closed zip output stream for file: " + fileName
							+ ". Checksum: "
							+ checkedOutStream.getChecksum().getValue());
					idsToUpdate[2].add(""
							+ checkedOutStream.getChecksum().getValue());
				}
				zos.close();
			}
		} catch (Exception e) {
			Log.e(TAG, "Could not save zip: " + e.getMessage(), e);
			PersistentUncaughtExceptionHandler.recordException(e);
			fileName = null;
		}
		return idsToUpdate;
	}

	/**
	 * writes the contents of text to a zip entry within the Zip file behind zos
	 * named fileName
	 * 
	 * @param zos
	 * @param text
	 * @param fileName
	 * @throws IOException
	 */
	private void writeTextToZip(ZipOutputStream zos, String text,
			String fileName) throws IOException {
		Log.i(TAG, "Writing zip entry");
		zos.putNextEntry(new ZipEntry(fileName));
		byte[] buffer = new byte[BUF_SIZE];
		for (int i = 0; i < text.getBytes().length; i += BUF_SIZE) {
			int size = i + BUF_SIZE < text.getBytes().length ? BUF_SIZE : text
					.getBytes().length
					- i;
			text.getBytes(i, i + size - 1, buffer, 0);
			zos.write(buffer, 0, size);
		}
		zos.closeEntry();
		Log.i(TAG, "Entry Complete");
	}

	/**
	 * iterate over the plot data returned from the database and populate the
	 * string builder and collections passed in with the requisite information.
	 * 
	 * @param buf
	 *            - IN param. After execution this will contain the data to be
	 *            sent
	 * 
	 * @param plotIds
	 *            - IN param. After execution this will contain the ids of the
	 *            plots
	 */
	private void processRegionData(StringBuilder buf, HashSet<String> plotIds) {
		Cursor data = null;
		try {
			data = databaseAdaptor.listCompletePlotPoints();
			if (data != null && data.isFirst()) {
				do {
					String plotId = data
							.getString(data
									.getColumnIndexOrThrow(SurveyDbAdapter.PLOT_FK_COL));
					String plotPointId = data.getString(data
							.getColumnIndexOrThrow(SurveyDbAdapter.PK_ID_COL));
					if (plotPointId == null) {
						continue;
					}
					buf.append(plotId).append(",");
					plotIds.add(plotId);
					buf.append(plotPointId).append(",");
					buf
							.append(data
									.getString(data
											.getColumnIndexOrThrow(SurveyDbAdapter.DISP_NAME_COL)));
					buf
							.append(",")
							.append(
									data
											.getString(data
													.getColumnIndexOrThrow(SurveyDbAdapter.LAT_COL)));
					buf
							.append(",")
							.append(
									data
											.getString(data
													.getColumnIndexOrThrow(SurveyDbAdapter.LON_COL)));
					buf
							.append(",")
							.append(
									data
											.getString(data
													.getColumnIndexOrThrow(SurveyDbAdapter.ELEVATION_COL)));
					buf
							.append(",")
							.append(
									data
											.getString(data
													.getColumnIndexOrThrow(SurveyDbAdapter.CREATED_DATE_COL)));
					buf.append("\n");
				} while (data.moveToNext());
			}
		} catch (Exception e) {
			Log.e(TAG, "Could not extract survey data from db", e);
			PersistentUncaughtExceptionHandler.recordException(e);
		} finally {
			if (data != null) {
				data.close();
			}
		}
	}

	/**
	 * iterate over the survey data returned from the database and populate the
	 * string builder and collections passed in with the requisite information.
	 * 
	 * @param buf
	 *            - IN param. After execution this will contain the data to be
	 *            sent
	 * @param imagePaths
	 *            - IN param. After execution this will contain the list of
	 *            photo paths to send
	 * @param respondentIds
	 *            - IN param. After execution this will contain the ids of the
	 *            respondents
	 */
	private void processSurveyData(StringBuilder buf,
			HashMap<String, ArrayList<String>> imagePaths,
			HashSet<String> respondentIds) {
		Cursor data = null;
		try {
			data = databaseAdaptor.fetchUnsentData();
			if (data != null && data.isFirst()) {
				Log.i(TAG, "There is data to send. Forming contents");
				String deviceIdentifier = databaseAdaptor
						.findPreference(ConstantUtil.DEVICE_IDENT_KEY);
				if (deviceIdentifier == null) {
					deviceIdentifier = "unset";
				} else {
					if (deviceIdentifier.contains(",")) {
						// prevent commas from messing up the submission
						deviceIdentifier = deviceIdentifier
								.replaceAll(",", " ");
					}
				}
				do {

					String value = data.getString(data
							.getColumnIndexOrThrow(SurveyDbAdapter.ANSWER_COL));
					if (value != null) {
						value = value.trim();
						value = value.replaceAll("\\n", " ");
					}

					if (value == null || value.trim().length() == 0) {
						continue;
					}

					buf
							.append(
									data
											.getString(data
													.getColumnIndexOrThrow(SurveyDbAdapter.SURVEY_FK_COL)))
							.append(",");

					String type = data
							.getString(data
									.getColumnIndexOrThrow(SurveyDbAdapter.ANSWER_TYPE_COL));
					buf.append(data.getString(data
							.getColumnIndexOrThrow(SurveyDbAdapter.PK_ID_COL)));
					buf
							.append(",")
							.append(
									data
											.getString(data
													.getColumnIndexOrThrow(SurveyDbAdapter.QUESTION_FK_COL)));
					buf.append(",").append(type);
					buf.append(",").append(value);
					buf
							.append(",")
							.append(
									data
											.getString(data
													.getColumnIndexOrThrow(SurveyDbAdapter.DISP_NAME_COL)));
					buf
							.append(",")
							.append(
									data
											.getString(data
													.getColumnIndexOrThrow(SurveyDbAdapter.EMAIL_COL)));
					buf
							.append(",")
							.append(
									data
											.getString(data
													.getColumnIndexOrThrow(SurveyDbAdapter.SUBMITTED_DATE_COL)));
					buf.append(",").append(deviceIdentifier);
					String scoredVal = data
							.getString(data
									.getColumnIndexOrThrow(SurveyDbAdapter.SCORED_VAL_COL));
					buf.append(",").append(scoredVal != null ? scoredVal : "");
					String strength = data
							.getString(data
									.getColumnIndexOrThrow(SurveyDbAdapter.STRENGTH_COL));
					buf.append(",").append(strength != null ? strength : "");
					buf.append("\n");

					String respId = data.getString(data
							.getColumnIndexOrThrow(SurveyDbAdapter.PK_ID_COL));
					if (ConstantUtil.IMAGE_RESPONSE_TYPE.equals(type)
							|| ConstantUtil.VIDEO_RESPONSE_TYPE.equals(type)) {
						ArrayList<String> paths = imagePaths.get(respId);
						if (paths == null) {
							paths = new ArrayList<String>();
							imagePaths.put(respId, paths);
						}
						paths.add(value);
					}
					respondentIds.add(respId);
				} while (data.moveToNext());
			}
		} catch (Exception e) {
			Log.e(TAG, "Could not extract survey data from db", e);
			PersistentUncaughtExceptionHandler.recordException(e);
		} finally {
			if (data != null) {
				data.close();
			}
		}
	}

	/**
	 * sends the zip file containing data/images to the server via an http
	 * upload
	 * 
	 * @param fileAbsolutePath
	 */
	private boolean sendFile(String fileAbsolutePath, String dir,
			String policy, String sig, String contentType) {

		try {

			String fileName = fileAbsolutePath;
			if (fileName.contains(File.separator)) {
				fileName = fileName.substring(fileName
						.lastIndexOf(File.separator));
			}
			fireNotification(ConstantUtil.PROGRESS, fileName);

			HttpURLConnection conn = MultipartStream.createConnection(new URL(
					props.getProperty(ConstantUtil.DATA_UPLOAD_URL)));
			MultipartStream stream = new MultipartStream(conn.getOutputStream());
			stream.writeFormField("key", dir + "/${filename}");
			stream.writeFormField("AWSAccessKeyId", props
					.getProperty(ConstantUtil.S3_ID));
			stream.writeFormField("acl", "public-read");
			stream.writeFormField("success_action_redirect",
					"http://www.gallatinsystems.com/SuccessUpload.html");
			stream.writeFormField("policy", policy);
			stream.writeFormField("signature", sig);
			stream.writeFormField("Content-Type", contentType);
			stream.writeFile("file", fileAbsolutePath, null);
			stream.close();
			int code = conn.getResponseCode();
			if (code != REDIRECT_CODE && code != OK_CODE) {
				Log.e(TAG, "Server returned a bad code after upload: " + code);
				return false;
			} else {
				fireNotification(ConstantUtil.FILE_COMPLETE, fileName);
			}
		} catch (Exception e) {
			Log.e(TAG, "Could not send upload" + e.getMessage(), e);

			PersistentUncaughtExceptionHandler.recordException(e);
			return false;
		}
		return true;
	}

	/**
	 * constructs a filename for the data file
	 * 
	 * @return
	 */
	private String createFileName() {
		return Environment.getExternalStorageDirectory().getAbsolutePath()
				+ TEMP_FILE_NAME + System.nanoTime() + ".zip";
	}

	/**
	 * this method checks if the service can perform the requested operation. If
	 * the operation type is SEND and there is no connectivity, this will return
	 * false, otherwise it will return true
	 * 
	 * @param type
	 * @return
	 */
	private boolean isAbleToRun(String type, int uploadModeIndex) {
		boolean ok = false;
		// since a null type is treated like send, check !export
		if (!ConstantUtil.EXPORT.equals(type)) {
			if (uploadModeIndex > -1
					&& ConstantUtil.UPLOAD_NEVER_IDX == uploadModeIndex) {
				ok = StatusUtil.hasDataConnection(this, true);
			} else {
				ok = StatusUtil.hasDataConnection(this, false);
			}
		} else {
			// if we're exporting, we don't need to check the network
			ok = true;
		}
		return ok;
	}
}
