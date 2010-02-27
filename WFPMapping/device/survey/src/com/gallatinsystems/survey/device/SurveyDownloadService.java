package com.gallatinsystems.survey.device;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.concurrent.Semaphore;

import org.apache.http.HttpException;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.gallatinsystems.survey.device.dao.SurveyDbAdapter;
import com.gallatinsystems.survey.device.domain.Survey;
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

	private static final String SURVEY_LIST_SERVICE_URL = "http://watermappingmonitoring.appspot.com/surveymanager?action=getAvailableSurveysDevice&devicePhoneNumber=";
	private static final String SURVEY_SERVICE_URL = "http://watermappingmonitoring.appspot.com/surveymanager?surveyId=";

	private SurveyDbAdapter databaseAdaptor;

	private static final String DATA_DIR = "/sdcard/fieldsurvey/data/";
	private static final String SD_LOC = "scdard";

	private Thread thread;

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
					checkAndDownload();
				}
			}
		});
		thread.start();
		return Service.START_REDELIVER_INTENT;
	}

	public void onCreate() {
		super.onCreate();
	}

	/**
	 * checks for new surveys and, if there are some new ones, downloads them to
	 * the DATA_DIR
	 */
	private void checkAndDownload() {
		if (isAbleToRun()) {
			try {
				lock.acquire();
				ArrayList<Survey> surveys = checkForSurveys();
				if (surveys != null && surveys.size() > 0) {
					// create directory if not there
					findOrCreateDataDir();
					// if there are surveys for this device, see if we need them
					databaseAdaptor = new SurveyDbAdapter(this);
					databaseAdaptor.open();
					surveys = databaseAdaptor.checkSurveyVersions(surveys);
					int updateCount = 0;
					if (surveys != null && surveys.size() > 0) {
						for (int i = 0; i < surveys.size(); i++) {
							Survey survey = surveys.get(i);
							try {
								if (downloadSurvey(survey)) {
									databaseAdaptor.saveSurvey(survey);
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
			} catch (Exception e) {
				Log.e(TAG, "Could not update surveys", e);
			} finally {
				lock.release();
			}
		}
	}

	/**
	 * Downloads the survey based on the ID and then updates the survey object
	 * with the filename and location
	 */
	private boolean downloadSurvey(Survey survey) {
		boolean success = false;
		try {
			String response = HttpUtil.httpGet(SURVEY_SERVICE_URL
					+ survey.getId());

			survey.setFileName(survey.getId() + SURVEY_FILE_SUFFIX);
			survey.setType(DEFAULT_TYPE);
			// TODO: get this from touple once service is updated
			survey.setName("Survey " + survey.getId());
			survey.setLocation(SD_LOC);
			survey.setVersion(survey.getVersion());
			File file = new File(DATA_DIR, survey.getFileName());
			PrintStream writer = new PrintStream(new FileOutputStream(file));
			writer.print(response);
			writer.close();
			success = true;
		} catch (IOException e) {
			Log.e(TAG, "Could write survey file " + survey.getFileName(), e);
		} catch (Exception e) {
			Log.e(TAG, "Could not download survey " + survey.getId(), e);
		}
		return success;
	}

	/**
	 * invokes a service call to list all surveys that have been designated for
	 * this device (based on phone number).
	 * 
	 * @return - an arrayList of Survey objects with the id and version
	 *         populated
	 */
	private ArrayList<Survey> checkForSurveys() {
		String response = null;
		ArrayList<Survey> surveys = new ArrayList<Survey>();
		try {
			response = HttpUtil.httpGet(SURVEY_LIST_SERVICE_URL
					+ getPhoneNumber());
			if (response != null) {
				StringTokenizer strTok = new StringTokenizer(response, "\n");
				while (strTok.hasMoreTokens()) {
					String currentLine = strTok.nextToken();
					String[] touple = currentLine.split(",");
					if (touple.length < 3) {
						Log
								.e(TAG,
										"Survey list response is in an unrecognized format");
					} else {
						Survey temp = new Survey();
						temp.setId(touple[1]);
						temp.setVersion(Double.parseDouble(touple[2]));
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
	 * gets the device's primary phone number
	 * 
	 * @return
	 */
	private String getPhoneNumber() {
		TelephonyManager teleMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		return teleMgr.getLine1Number();
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
		ViewUtil.fireNotification(text, text, this, COMPLETE_ID);
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
		return StatusUtil.hasDataConnection(this);
	}

	/**
	 * creates the data directory if it does not exist
	 */
	private void findOrCreateDataDir() {
		File dir = new File(DATA_DIR);
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}
}
