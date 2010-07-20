package com.gallatinsystems.survey.device.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpException;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.gallatinsystems.survey.device.R;
import com.gallatinsystems.survey.device.dao.SurveyDao;
import com.gallatinsystems.survey.device.dao.SurveyDbAdapter;
import com.gallatinsystems.survey.device.domain.Question;
import com.gallatinsystems.survey.device.domain.Survey;
import com.gallatinsystems.survey.device.util.ConstantUtil;
import com.gallatinsystems.survey.device.util.FileUtil;
import com.gallatinsystems.survey.device.util.HttpUtil;
import com.gallatinsystems.survey.device.util.StatusUtil;
import com.gallatinsystems.survey.device.util.ViewUtil;

/**
 * this activity will check for new surveys on the device and install as needed
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyDownloadService extends Service {

	private static final String TAG = "SURVEY_DOWNLOAD_ACTIVITY";

	private static final String SURVEY_FILE_SUFFIX = ".xml";
	private static final String DEFAULT_TYPE = "Survey";
	private static final int COMPLETE_ID = 2;

	private static final String NO_SURVEY = "No Survey Found";

	private static final String SERVER_BASE = "http://watermapmonitordev.appspot.com";
	private static final String SURVEY_LIST_SERVICE_PATH = "/surveymanager?action=getAvailableSurveysDevice&devicePhoneNumber=";
	private static final String SURVEY_HEADER_SERVICE_PATH = "/surveymanager?action=getSurveyHeader&surveyId=";
	private static final String SURVEY_SERVICE_SERVICE_PATH = "/surveymanager?surveyId=";
	private static final String SURVEY_S3_URL = "http://waterforpeople.s3.amazonaws.com/surveys/";

	private SurveyDbAdapter databaseAdaptor;

	private static final String SD_LOC = "sdcard";

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
				int precacheOption = Integer
						.parseInt(databaseAdaptor
								.findPreference(ConstantUtil.PRECACHE_HELP_SETTING_KEY));
				String serverBase = databaseAdaptor
						.findPreference(ConstantUtil.SERVER_SETTING_KEY);
				if (serverBase == null || serverBase.trim().length() > 0) {
					serverBase = getResources().getStringArray(R.array.servers)[Integer
							.parseInt(serverBase)];
				} else {
					serverBase = SERVER_BASE;
				}
				ArrayList<Survey> surveys = null;

				if (surveyId != null && surveyId.trim().length() > 0) {
					surveys = getSurveyHeader(serverBase, surveyId);
					if (surveys != null && surveys.size() > 0) {
						// if we already have the survey, delete it first
						databaseAdaptor.deleteSurvey(surveyId.trim(), true);
					}
				} else {
					surveys = checkForSurveys(serverBase);
				}
				if (surveys != null && surveys.size() > 0) {
					// create directory if not there
					FileUtil.findOrCreateDir(ConstantUtil.DATA_DIR);
					// if there are surveys for this device, see if we need them

					surveys = databaseAdaptor.checkSurveyVersions(surveys);
					int updateCount = 0;
					if (surveys != null && surveys.size() > 0) {
						for (int i = 0; i < surveys.size(); i++) {
							Survey survey = surveys.get(i);
							try {
								if (downloadSurvey(serverBase, survey)) {
									databaseAdaptor.saveSurvey(survey);
									downloadHelp(survey, precacheOption);
									updateCount++;
								}
							} catch (Exception e) {
								Log.e(TAG, "Could not download survey", e);
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
				databaseAdaptor.close();
			} catch (Exception e) {
				Log.e(TAG, "Could not update surveys", e);
			} finally {
				lock.release();
			}
		}
		try {
			downloadExecutor.shutdown();
			// wait up to 30 minutes to download the media
			downloadExecutor.awaitTermination(1800, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			Log
					.e(
							TAG,
							"Error while waiting for download executor to terminate",
							e);
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
			String response = HttpUtil.httpGet(SURVEY_S3_URL + survey.getId()+".xml");
			if (response != null
					&& !response.trim().equalsIgnoreCase(NO_SURVEY)) {
				survey.setFileName(survey.getId() + SURVEY_FILE_SUFFIX);
				survey.setType(DEFAULT_TYPE);
				survey.setLocation(SD_LOC);
				File file = new File(ConstantUtil.DATA_DIR, survey
						.getFileName());
				PrintStream writer = new PrintStream(new FileOutputStream(file));
				writer.print(response);
				writer.close();
				success = true;
			}
		} catch (IOException e) {
			Log.e(TAG, "Could write survey file " + survey.getFileName(), e);
		} catch (Exception e) {
			Log.e(TAG, "Could not download survey " + survey.getId(), e);
		}
		return success;
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
				Survey hydratedSurvey = SurveyDao.loadSurvey(survey,
						getResources());
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
									if (questions.get(j).getVideo() != null) {
										fileSet
												.add(questions.get(j)
														.getVideo());
									}
									ArrayList<String> images = questions.get(j)
											.getImages();
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

		final String localFile = FileUtil.convertRemoteToLocalFile(remoteFile,
				surveyId);
		downloadExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					HttpUtil.httpDownload(remoteFile, localFile);
				} catch (Exception e) {
					Log.e(TAG, "Could not download help media file", e);
				}
			}
		});
	}

	/**
	 * invokes a service call to get the header information for a single survey
	 * 
	 * @param serverBase
	 * @param surveyId
	 * @return
	 */
	private ArrayList<Survey> getSurveyHeader(String serverBase, String surveyId) {
		String response = null;
		ArrayList<Survey> surveys = new ArrayList<Survey>();
		try {
			response = HttpUtil.httpGet(serverBase + SURVEY_HEADER_SERVICE_PATH
					+ surveyId);
			if (response != null) {
				StringTokenizer strTok = new StringTokenizer(response, "\n");
				while (strTok.hasMoreTokens()) {
					String currentLine = strTok.nextToken();
					String[] touple = currentLine.split(",");
					if (touple.length < 4) {
						Log
								.e(TAG,
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
		} catch (Exception e) {
			Log.e(TAG, "Could not send processing call", e);
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
	private ArrayList<Survey> checkForSurveys(String serverBase) {
		String response = null;
		ArrayList<Survey> surveys = new ArrayList<Survey>();
		try {
			response = HttpUtil.httpGet(serverBase + SURVEY_LIST_SERVICE_PATH
					+ StatusUtil.getPhoneNumber(this));
			if (response != null) {
				StringTokenizer strTok = new StringTokenizer(response, "\n");
				while (strTok.hasMoreTokens()) {
					String currentLine = strTok.nextToken();
					String[] touple = currentLine.split(",");
					if (touple.length < 5) {
						Log
								.e(TAG,
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
		} catch (Exception e) {
			Log.e(TAG, "Could not send processing call", e);
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
		ViewUtil.fireNotification(text, text, this, COMPLETE_ID,null);
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
				&& ConstantUtil.PRECACHE_HELP_WIFI_ONLY_IDX == precacheOptionIndex) {
			ok = StatusUtil.hasDataConnection(this, true);
		} else {
			ok = StatusUtil.hasDataConnection(this, false);
		}
		return ok;
	}
}
