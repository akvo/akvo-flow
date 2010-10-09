package com.gallatinsystems.survey.device.service;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.Semaphore;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.gallatinsystems.survey.device.R;
import com.gallatinsystems.survey.device.dao.SurveyDbAdapter;
import com.gallatinsystems.survey.device.domain.Survey;
import com.gallatinsystems.survey.device.util.ConstantUtil;
import com.gallatinsystems.survey.device.util.FileUtil;
import com.gallatinsystems.survey.device.util.ViewUtil;

/**
 * Service that will check a well-known location on the device's SD card for a
 * zip file that contains data that should be loaded on the device. The root of
 * the zip file can contain a file called dbinstructions.sql. If it does, the
 * sql statements contained therein will be executed in the order they appear.
 * The zip file can also contain any number of directories which can each
 * contain ONE survey (the survey xml and any help media). The name of the
 * directory must be the surveyID and the name of the survey file will be used
 * for the survey name. The system will iterate through each directory and
 * install the survey and help media contained therein. If the survey is already
 * present on the device, the survey in the ZIP file will overwrite the data
 * already on the device.
 * 
 * If there are multiple zip files in the directory, this utility will process
 * them in lexicographical order by file name;
 * 
 * Any files with a name starting with . will be skipped (to prevent inadvertent
 * processing of MAC OSX metadata files).
 * 
 * @author Christopher Fagiani
 * 
 */
public class BootstrapService extends Service {
	private static final String TAG = "BOOTSTRAP_SERVICE";
	private static Semaphore lock = new Semaphore(1);
	private Thread workerThread;
	private SurveyDbAdapter databaseAdapter;
	private static final Integer NOTIFICATION_ID = new Integer(123);

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	/**
	 * life cycle method for the service. This is called by the system when the
	 * service is started
	 */
	public int onStartCommand(final Intent intent, int flags, int startid) {
		workerThread = new Thread(new Runnable() {
			public void run() {
				checkAndInstall();
				stopSelf();
			}
		});
		workerThread.start();
		return Service.START_STICKY;
	}

	/**
	 * Checks the bootstrap directory for unprocessed zip files. If they are
	 * found, they're processed one at a time. If an error occurs, all
	 * processing stops (subsequent zips won't be processed if there are
	 * multiple zips in the directory) just in case data in a later zip depends
	 * on the previous one being there.
	 */
	private void checkAndInstall() {
		try {
			lock.acquire();
			ArrayList<File> zipFiles = getZipFiles();
			if (zipFiles != null && zipFiles.size() > 0) {
				String startMessage = getString(R.string.bootstrapstart);
				ViewUtil.fireNotification(startMessage, startMessage, this,
						NOTIFICATION_ID, android.R.drawable.ic_dialog_info);
				databaseAdapter = new SurveyDbAdapter(this);
				databaseAdapter.open();
				try {
					for (int i = 0; i < zipFiles.size(); i++) {
						try {
							processFile(zipFiles.get(i));
						} catch (Exception e) {
							String newFilename = zipFiles.get(i)
									.getAbsolutePath();
							zipFiles
									.get(i)
									.renameTo(
											new File(
													newFilename
															+ ConstantUtil.PROCESSED_ERROR_SUFFIX));
							throw (e);
						}
					}
					String endMessage = getString(R.string.bootstrapcomplete);
					ViewUtil.fireNotification(endMessage, endMessage, this,
							NOTIFICATION_ID, android.R.drawable.ic_dialog_info);
				} finally {
					if (databaseAdapter != null) {
						databaseAdapter.close();
					}
				}
			}
		} catch (Exception e) {
			String errorMessage = getString(R.string.bootstraperror);
			ViewUtil.fireNotification(errorMessage, errorMessage, this,
					NOTIFICATION_ID, android.R.drawable.ic_dialog_alert);
			Log.e(TAG, "Bootstrap error", e);
		} finally {
			lock.release();
		}
	}

	/**
	 * processes a bootstrap zip file
	 * 
	 * @param zipFile
	 * @throws Exception
	 */
	private void processFile(File zipFile) throws Exception {
		ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
		ZipEntry entry = null;
		HashSet<String> surveysWithImages = new HashSet<String>();
		while ((entry = zis.getNextEntry()) != null) {
			String parts[] = entry.getName().split("/");
			String fileName = parts[parts.length - 1];
			// make sure we're not processing a hidden file
			if (!fileName.startsWith(".")) {
				if (entry.getName().toLowerCase().endsWith(
						ConstantUtil.BOOTSTRAP_DB_FILE.toLowerCase())) {
					processDbInstructions(FileUtil.readTextFromZip(zis));

				} else if (!entry.isDirectory()) {
					String id = parts[parts.length - 2];
					if (entry.getName().toLowerCase().endsWith(
							ConstantUtil.XML_SUFFIX.toLowerCase())) {
						String surveyName = fileName;
						if (surveyName.contains(".")) {
							surveyName = surveyName.substring(0, surveyName
									.indexOf("."));
						}
						Survey survey = databaseAdapter.findSurvey(id);
						if (survey == null) {
							survey = new Survey();
							survey.setId(id);
							survey.setVersion(1d);
							survey.setName(surveyName);
							survey.setHelpDownloaded(false);
							survey
									.setLanguage(ConstantUtil.SURVEY_DEFAULT_LANG);
							survey.setType(ConstantUtil.SURVEY_TYPE);
						} else {
							survey.setVersion(survey.getVersion() + 1d);
						}
						survey.setLocation(ConstantUtil.FILE_LOCATION);
						survey.setFileName(fileName);

						// in both cases (new survey and existing), we need to
						// update the xml
						FileUtil.extractAndSaveFile(zis, ConstantUtil.DATA_DIR
								+ fileName);
						// now save the survey
						databaseAdapter.saveSurvey(survey);
					} else {
						// if it's not a sql file and its not a survey, it must
						// be help media
						FileUtil.extractAndSaveFile(zis, ConstantUtil.DATA_DIR
								+ id + File.separator + fileName);

						// record the fact that this survey had media
						surveysWithImages.add(id);
					}
				}
			}
		}
		// update survey record so the system knows not to bother trying to
		// re-download media (since it was in the file)
		for (String sid : surveysWithImages) {
			databaseAdapter.markSurveyHelpDownloaded(sid, true);
		}

		// now rename the zip file so we don't process it again
		String newFilename = zipFile.getAbsolutePath();
		zipFile.renameTo(new File(newFilename
				+ ConstantUtil.PROCESSED_OK_SUFFIX));
	}

	/**
	 * tokenizes instructions using the newline character as a delimiter and
	 * executes each line as a separate SQL command;
	 * 
	 * @param instructions
	 */
	private void processDbInstructions(String instructions) {
		if (instructions != null && instructions.trim().length() > 0) {
			String[] instructionList = instructions.split("\n");
			for (int i = 0; i < instructionList.length; i++) {
				String command = instructionList[i].trim();
				if (!command.endsWith(";")) {
					command = command + ";";
				}
				databaseAdapter.executeSql(command);
			}
		}
	}

	/**
	 * returns an ordered list of zip files that exist in the device's bootstrap
	 * directory
	 * 
	 * @return
	 */
	private ArrayList<File> getZipFiles() {
		ArrayList<File> zipFiles = new ArrayList<File>();
		File dir = FileUtil.findOrCreateDir(ConstantUtil.BOOTSTRAP_DIR);
		if (dir != null) {
			File[] fileList = dir.listFiles();
			if (fileList != null) {
				for (int i = 0; i < fileList.length; i++) {
					if (fileList[i].isFile()
							&& fileList[i].getName().toLowerCase().endsWith(
									ConstantUtil.ARCHIVE_SUFFIX.toLowerCase())) {
						zipFiles.add(fileList[i]);
					}
				}
			}
			Collections.sort(zipFiles);
		}
		return zipFiles;
	}

	public void onCreate() {
		super.onCreate();
	}

}
