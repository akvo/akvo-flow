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

package com.gallatinsystems.survey.device.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.net.URLEncoder;

import org.apache.http.HttpException;

import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.os.IBinder;
import android.util.Log;

import com.gallatinsystems.survey.device.R;
import com.gallatinsystems.survey.device.dao.SurveyDao;
import com.gallatinsystems.survey.device.dao.SurveyDbAdapter;
import com.gallatinsystems.survey.device.domain.Question;
import com.gallatinsystems.survey.device.domain.QuestionHelp;
import com.gallatinsystems.survey.device.domain.Survey;
import com.gallatinsystems.survey.device.exception.PersistentUncaughtExceptionHandler;
import com.gallatinsystems.survey.device.exception.TransferException;
import com.gallatinsystems.survey.device.util.ConstantUtil;
import com.gallatinsystems.survey.device.util.FileUtil;
import com.gallatinsystems.survey.device.util.HttpUtil;
import com.gallatinsystems.survey.device.util.LangsPreferenceUtil;
import com.gallatinsystems.survey.device.util.PropertyUtil;
import com.gallatinsystems.survey.device.util.StatusUtil;
import com.gallatinsystems.survey.device.util.ViewUtil;

/**
 * this activity will check for new surveys on the device and install as needed
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyDownloadService extends Service {

	private static final String TAG = "SURVEY_DOWNLOAD_SERVICE";

	private static final String DEFAULT_TYPE = "Survey";
	private static final int COMPLETE_ID = 2;
	private static final int FAIL_ID = 3;

	@SuppressWarnings("unused")
	private static final String NO_SURVEY = "No Survey Found";
	private static final String SURVEY_LIST_SERVICE_PATH = "/surveymanager?action=getAvailableSurveysDevice&devicePhoneNumber=";
	private static final String SURVEY_HEADER_SERVICE_PATH = "/surveymanager?action=getSurveyHeader&surveyId=";
	private static final String DEV_ID_PARAM = "&devId=";
	private static final String IMEI_PARAM = "&imei=";
	@SuppressWarnings("unused")
	private static final String SURVEY_SERVICE_SERVICE_PATH = "/surveymanager?surveyId=";
	private static final String SD_LOC = "sdcard";

	private SurveyDbAdapter databaseAdaptor;
	private PropertyUtil props;
	private Thread thread;
	private ThreadPoolExecutor downloadExecutor;
	private static Semaphore lock = new Semaphore(1);

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
					String surveyId = null;
					if (intent.getExtras() != null) {
						surveyId = intent.getExtras().getString(
								ConstantUtil.SURVEY_ID_KEY);
					}
					checkAndDownload(surveyId);
				}
			}
		});
		thread.start();
		return Service.START_REDELIVER_INTENT;
	}

	public void onCreate() {
		super.onCreate();
		Thread.setDefaultUncaughtExceptionHandler(PersistentUncaughtExceptionHandler
				.getInstance());
		props = new PropertyUtil(getResources());
		downloadExecutor = new ThreadPoolExecutor(1, 3, 5000,
				TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
	}

	/**
	 * if no surveyId is passed in, this will check for new surveys and, if
	 * there are some new ones, downloads them to the DATA_DIR. If a surveyId is
	 * passed in, then that specific survey will be downloaded. If it's already
	 * on the device, the survey will be replaced with the new one.
	 */
	private void checkAndDownload(String surveyId) {
		if (isAbleToRun()) {
			try {
				lock.acquire();
				databaseAdaptor = new SurveyDbAdapter(this);
				databaseAdaptor.open();
				int precacheOption = Integer.parseInt(databaseAdaptor
						.findPreference(ConstantUtil.PRECACHE_SETTING_KEY));
				String serverBase = databaseAdaptor
						.findPreference(ConstantUtil.SERVER_SETTING_KEY);
				if (serverBase != null && serverBase.trim().length() > 0) {
					serverBase = getResources().getStringArray(R.array.servers)[Integer
							.parseInt(serverBase)];
				} else {
					serverBase = props.getProperty(ConstantUtil.SERVER_BASE);
				}

				int surveyCheckOption = Integer.parseInt(databaseAdaptor
						.findPreference(ConstantUtil.CHECK_FOR_SURVEYS));
				String deviceId = databaseAdaptor
						.findPreference(ConstantUtil.DEVICE_IDENT_KEY);
				ArrayList<Survey> surveys = null;

				if (surveyId != null && surveyId.trim().length() > 0) {
					surveys = getSurveyHeader(serverBase, surveyId, deviceId);
					if (surveys != null && surveys.size() > 0) {
						// if we already have the survey, delete it first
						databaseAdaptor.deleteSurvey(surveyId.trim(), true);
					}
				} else {
					if (canDownload(surveyCheckOption)) {
						surveys = checkForSurveys(serverBase, deviceId);
					}
				}
				if (surveys != null && surveys.size() > 0) {
					// if there are surveys for this device, see if we need
					// them
					surveys = databaseAdaptor.checkSurveyVersions(surveys);
					int updateCount = 0;
					if (surveys != null && surveys.size() > 0) {
						for (int i = 0; i < surveys.size(); i++) {
							Survey survey = surveys.get(i);
							try {
								if (downloadSurvey(serverBase, survey)) {
									databaseAdaptor.saveSurvey(survey);
									String[] langs = LangsPreferenceUtil.determineLanguages(this, survey);
									databaseAdaptor.addLanguages(langs);
									downloadHelp(survey, precacheOption);
									updateCount++;
								}
							} catch (Exception e) {
								Log.e(TAG, "Could not download survey", e);
								PersistentUncaughtExceptionHandler
										.recordException(e);
							}
						}
						if (updateCount > 0) {
							fireNotification(updateCount);
						}
					}
				}

				// now check if any previously downloaded surveys still need
				// don't have their help media pre-cached
				if (canDownload(precacheOption)) {
					surveys = databaseAdaptor.listSurveys(null);
					if (surveys != null) {
						for (int i = 0; i < surveys.size(); i++) {
							if (!surveys.get(i).isHelpDownloaded()) {
								downloadHelp(surveys.get(i), precacheOption);
							}
						}
					}
				}

			} catch (Exception e) {
				Log.e(TAG, "Could not update surveys", e);
				PersistentUncaughtExceptionHandler.recordException(e);
			} finally {
				databaseAdaptor.close();
				lock.release();
			}
		}
		try {
			downloadExecutor.shutdown();
			// wait up to 30 minutes to download the media
			downloadExecutor.awaitTermination(1800, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			Log.e(TAG,
					"Error while waiting for download executor to terminate", e);
		}
		stopSelf();
	}

	/**
	 * Downloads the survey based on the ID and then updates the survey object
	 * with the filename and location
	 */
	private boolean downloadSurvey(String serverBase, Survey survey) {
		boolean success = false;
		try {
			HttpUtil.httpDownload(
					props.getProperty(ConstantUtil.SURVEY_S3_URL)
							+ survey.getId() + ConstantUtil.ARCHIVE_SUFFIX,
					FileUtil.getFileOutputStream(
							survey.getId() + ConstantUtil.ARCHIVE_SUFFIX,
							ConstantUtil.DATA_DIR,
							props.getProperty(ConstantUtil.USE_INTERNAL_STORAGE),
							this));
			extractAndSave(FileUtil.getFileInputStream(survey.getId()
					+ ConstantUtil.ARCHIVE_SUFFIX, ConstantUtil.DATA_DIR,
					props.getProperty(ConstantUtil.USE_INTERNAL_STORAGE), this));

			survey.setFileName(survey.getId() + ConstantUtil.XML_SUFFIX);
			survey.setType(DEFAULT_TYPE);
			survey.setLocation(SD_LOC);
			success = true;
		} catch (IOException e) {
			Log.e(TAG, "Could write survey file " + survey.getFileName(), e);
			String text = getResources().getString(R.string.cannotupdate);
			ViewUtil.fireNotification(text, text, this, FAIL_ID, null);
			PersistentUncaughtExceptionHandler
					.recordException(new TransferException(survey.getId(),
							null, e));

		} catch (Exception e) {
			Log.e(TAG, "Could not download survey " + survey.getId(), e);

			String text = getResources().getString(R.string.cannotupdate);
			ViewUtil.fireNotification(text, text, this, FAIL_ID, null);

			PersistentUncaughtExceptionHandler
					.recordException(new TransferException(survey.getId(),
							null, e));

		}
		return success;
	}

	/**
	 * reads the byte array passed in using a zip input stream and extracts the
	 * entry to the file specified. This assumes ONE entry per zip
	 * 
	 * @param bytes
	 * @param f
	 * @throws IOException
	 */
	private void extractAndSave(FileInputStream zipFile) throws IOException {
		ZipInputStream zis = new ZipInputStream(zipFile);
		ZipEntry entry;
		while ((entry = zis.getNextEntry()) != null) {
			FileOutputStream fout = FileUtil.getFileOutputStream(
					entry.getName(), ConstantUtil.DATA_DIR,
					props.getProperty(ConstantUtil.USE_INTERNAL_STORAGE), this);
			byte[] buffer = new byte[2048];
			int size;
			while ((size = zis.read(buffer, 0, buffer.length)) != -1) {
				fout.write(buffer, 0, size);
			}
			fout.close();
			zis.closeEntry();
		}
		zis.close();
	}

	/**
	 * checks to see if we should pre-cache help media files (based on the
	 * property in the settings db) and, if we should, downloads the files
	 * 
	 * @param survey
	 */
	private void downloadHelp(Survey survey, int precacheOption) {
		// first, see if we should even bother trying to download
		if (canDownload(precacheOption)) {
			try {
				InputStream in = null;
				if (ConstantUtil.RESOURCE_LOCATION.equalsIgnoreCase(survey
						.getLocation())) {
					// load from resource
					Resources res = getResources();
					in = res.openRawResource(res.getIdentifier(
							survey.getFileName(), ConstantUtil.RAW_RESOURCE,
							ConstantUtil.RESOURCE_PACKAGE));
				} else {
					// load from file
					in = FileUtil
							.getFileInputStream(
									survey.getFileName(),
									ConstantUtil.DATA_DIR,
									props.getProperty(ConstantUtil.USE_INTERNAL_STORAGE),
									this);
				}
				Survey hydratedSurvey = SurveyDao.loadSurvey(survey, in);
				if (hydratedSurvey != null) {
					// collect files in a set just in case the same binary is
					// used in multiple questions
					// we only need to download once
					HashSet<String> fileSet = new HashSet<String>();
					if (hydratedSurvey.getQuestionGroups() != null) {
						for (int i = 0; i < hydratedSurvey.getQuestionGroups()
								.size(); i++) {
							ArrayList<Question> questions = hydratedSurvey
									.getQuestionGroups().get(i).getQuestions();
							if (questions != null) {
								for (int j = 0; j < questions.size(); j++) {
									if (questions
											.get(j)
											.getHelpByType(
													ConstantUtil.VIDEO_HELP_TYPE)
											.size() > 0) {
										fileSet.add(questions
												.get(j)
												.getHelpByType(
														ConstantUtil.VIDEO_HELP_TYPE)
												.get(0).getValue());
									}
									ArrayList<QuestionHelp> helpList = questions
											.get(j)
											.getHelpByType(
													ConstantUtil.IMAGE_HELP_TYPE);
									ArrayList<String> images = new ArrayList<String>();
									for (int x = 0; x < helpList.size(); x++) {
										images.add(helpList.get(x).getValue());
									}
									if (images != null) {
										for (int k = 0; k < images.size(); k++) {
											fileSet.add(images.get(k));
										}
									}
								}
							}
						}
					}
					for (String file : fileSet) {
						downloadBinary(file, survey.getId());
					}
					databaseAdaptor.markSurveyHelpDownloaded(survey.getId(),
							true);
				}
			} catch (FileNotFoundException e) {
				Log.e(TAG, "Could not parse survey survey file", e);
				PersistentUncaughtExceptionHandler.recordException(e);
			}
		}
	}

	/**
	 * uses the thread pool executor to download the remote file passed in via a
	 * background thread
	 * 
	 * @param remoteFile
	 * @param surveyId
	 */
	private void downloadBinary(final String remoteFile, final String surveyId) {
		try {
			final FileOutputStream out = FileUtil.getFileOutputStream(
					remoteFile.substring(remoteFile.lastIndexOf("/") + 1),
					ConstantUtil.DATA_DIR + surveyId + "/",
					props.getProperty(ConstantUtil.USE_INTERNAL_STORAGE), this);
			downloadExecutor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						HttpUtil.httpDownload(remoteFile, out);
					} catch (Exception e) {
						Log.e(TAG, "Could not download help media file", e);
					}
				}
			});
		} catch (FileNotFoundException e1) {
			Log.e(TAG, "Could not download binary file", e1);
			PersistentUncaughtExceptionHandler.recordException(e1);
		}
	}

	/**
	 * invokes a service call to get the header information for a single survey
	 * 
	 * @param serverBase
	 * @param surveyId
	 * @return
	 */
	private ArrayList<Survey> getSurveyHeader(String serverBase,
			String surveyId, String deviceId) {
		String response = null;
		ArrayList<Survey> surveys = new ArrayList<Survey>();
		try {
			response = HttpUtil.httpGet(serverBase + SURVEY_HEADER_SERVICE_PATH
					+ surveyId + "&devicePhoneNumber="
					+ StatusUtil.getPhoneNumber(this)
					+ (deviceId != null ? (DEV_ID_PARAM + URLEncoder.encode(deviceId, "UTF-8")) : ""));
			if (response != null) {
				StringTokenizer strTok = new StringTokenizer(response, "\n");
				while (strTok.hasMoreTokens()) {
					String currentLine = strTok.nextToken();
					String[] touple = currentLine.split(",");
					if (touple.length < 4) {
						Log.e(TAG,
								"Survey list response is in an unrecognized format");
					} else {
						Survey temp = new Survey();
						temp.setId(touple[0]);
						temp.setName(touple[1]);
						temp.setLanguage(touple[2]);
						temp.setVersion(Double.parseDouble(touple[3]));
						temp.setType(ConstantUtil.FILE_SURVEY_LOCATION_TYPE);
						surveys.add(temp);
					}
				}
			}
		} catch (HttpException e) {
			Log.e(TAG, "Server returned an unexpected response", e);
			PersistentUncaughtExceptionHandler.recordException(e);
		} catch (Exception e) {
			Log.e(TAG, "Could not get survey headers", e);
			PersistentUncaughtExceptionHandler.recordException(e);
		}
		return surveys;
	}

	/**
	 * invokes a service call to list all surveys that have been designated for
	 * this device (based on phone number).
	 * 
	 * @return - an arrayList of Survey objects with the id and version
	 *         populated
	 */
	private ArrayList<Survey> checkForSurveys(String serverBase, String deviceId) {
		String response = null;
		ArrayList<Survey> surveys = new ArrayList<Survey>();
		try {
			response = HttpUtil.httpGet(serverBase
					+ SURVEY_LIST_SERVICE_PATH + URLEncoder.encode(StatusUtil.getPhoneNumber(this), "UTF-8")
					+ IMEI_PARAM + URLEncoder.encode(StatusUtil.getImei(this), "UTF-8")
					+ (deviceId != null ? DEV_ID_PARAM + URLEncoder.encode(deviceId, "UTF-8") : ""));
			if (response != null) {
				StringTokenizer strTok = new StringTokenizer(response, "\n");
				while (strTok.hasMoreTokens()) {
					String currentLine = strTok.nextToken();
					String[] touple = currentLine.split(",");
					if (touple.length < 5) {
						Log.e(TAG,
								"Survey list response is in an unrecognized format");
					} else {
						Survey temp = new Survey();
						temp.setId(touple[1]);
						temp.setName(touple[2]);
						temp.setLanguage(touple[3]);
						temp.setVersion(Double.parseDouble(touple[4]));
						temp.setType(ConstantUtil.FILE_SURVEY_LOCATION_TYPE);
						surveys.add(temp);

					}
				}
			}
		} catch (HttpException e) {
			Log.e(TAG, "Server returned an unexpected response", e);
			PersistentUncaughtExceptionHandler.recordException(e);
		} catch (Exception e) {
			Log.e(TAG, "Could not download survey", e);
			PersistentUncaughtExceptionHandler.recordException(e);
		}
		return surveys;
	}

	/**
	 * displays a notification in the system status bar indicating the
	 * completion of the download operation
	 * 
	 * @param type
	 */
	private void fireNotification(int count) {
		String text = getResources().getText(R.string.surveysupdated)
				.toString();
		ViewUtil.fireNotification(text, text, this, COMPLETE_ID, null);
	}

	/**
	 * this method checks if the service can perform the requested operation. If
	 * there is no connectivity, this will return false, otherwise it will
	 * return true
	 * 
	 * 
	 * @param type
	 * @return
	 */
	private boolean isAbleToRun() {
		return StatusUtil.hasDataConnection(this, false);
	}

	/**
	 * this method checks if the service can precache media files based on the
	 * user preference and the type of network connection currently held
	 * 
	 * @param type
	 * @return
	 */
	private boolean canDownload(int precacheOptionIndex) {
		boolean ok = false;
		if (precacheOptionIndex > -1
				&& ConstantUtil.PRECACHE_WIFI_ONLY_IDX == precacheOptionIndex) {

			ok = StatusUtil.hasDataConnection(this, true);
		} else {
			ok = StatusUtil.hasDataConnection(this, false);
		}
		return ok;
	}

}
