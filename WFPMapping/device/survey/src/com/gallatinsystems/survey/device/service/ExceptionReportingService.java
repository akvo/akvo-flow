package com.gallatinsystems.survey.device.service;

import java.io.File;
import java.io.FilenameFilter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.gallatinsystems.survey.device.R;
import com.gallatinsystems.survey.device.dao.SurveyDbAdapter;
import com.gallatinsystems.survey.device.exception.PersistentUncaughtExceptionHandler;
import com.gallatinsystems.survey.device.util.ConstantUtil;
import com.gallatinsystems.survey.device.util.FileUtil;
import com.gallatinsystems.survey.device.util.HttpUtil;
import com.gallatinsystems.survey.device.util.PropertyUtil;
import com.gallatinsystems.survey.device.util.StatusUtil;

/**
 * service that periodically checks for stack trace files on the file system
 * and, if found, uploads them to the server. After a file is uploaded, the
 * trace file is deleted from the file system.
 * 
 * @author Christopher Fagiani
 * 
 */
public class ExceptionReportingService extends Service {

	private static final String TAG = "EXCEPTION_REPORTING_SERVICE";
	private static final String EXCEPTION_SERVICE_PATH = "/remoteexception";
	private static final String ACTION_PARAM = "action";
	private static final String ACTION_VALUE = "saveTrace";
	private static final String PHONE_PARAM = "phoneNumber";
	private static final String VER_PARAM = "version";
	private static final String DEV_ID_PARAM = "deviceIdentifier";
	private static final String DATE_PARAM = "date";
	private static final String TRACE_PARAM = "trace";
	private static final long INITIAL_DELAY = 60000;	
	private static final long INTERVAL = 300000;

	private static final DateFormat DATE_FMT = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	private static Timer timer;

	private String version;
	private String deviceId;
	private PropertyUtil props;
	private String phoneNumber;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * life cycle method for the service. This is called by the system when the
	 * service is started. It will schedule a timerTask that will periodically
	 * check the file system for trace files and, if found, will send them to
	 * the server via a REST call
	 */
	public int onStartCommand(final Intent intent, int flags, int startid) {

		SurveyDbAdapter database = new SurveyDbAdapter(this);
		database.open();
		deviceId = database.findPreference(ConstantUtil.DEVICE_IDENT_KEY);
		Resources resources = getResources();
		version = resources.getString(R.string.appversion);
		String serverBase = database
				.findPreference(ConstantUtil.SERVER_SETTING_KEY);
		if (serverBase != null && serverBase.trim().length() > 0) {
			serverBase = resources.getStringArray(R.array.servers)[Integer
					.parseInt(serverBase)];
		} else {
			serverBase = props.getProperty(ConstantUtil.SERVER_BASE);
		}
		final String server = serverBase;
		phoneNumber = StatusUtil.getPhoneNumber(this);
		database.close();
		if (timer == null) {
			timer = new Timer(true);
			timer.scheduleAtFixedRate(new TimerTask() {

				@Override
				public void run() {
					if (StatusUtil.hasDataConnection(
							ExceptionReportingService.this, false)) {
						submitStackTraces(server);
					}
				}
			}, INITIAL_DELAY, INTERVAL);
		}
		return Service.START_STICKY;
	}

	public void onCreate() {
		super.onCreate();
		Thread
				.setDefaultUncaughtExceptionHandler(PersistentUncaughtExceptionHandler
						.getInstance());
		props = new PropertyUtil(getResources());
	}

	/**
	 * Returns all stack trace files on the files system
	 * 
	 * @return
	 */
	private String[] getTraceFiles() {
		String dirString = null;
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			dirString  = FileUtil.getStorageDirectory(ConstantUtil.STACKTRACE_DIR, "false");
		}else{
			dirString = FileUtil.getStorageDirectory(ConstantUtil.STACKTRACE_DIR, "true");
		}
		File dir = FileUtil.findOrCreateDir(dirString);
		FilenameFilter traceFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(ConstantUtil.STACKTRACE_SUFFIX);
			}
		};
		return dir.list(traceFilter);
	}

	/**
	 * Look in the trace directory for any files. If any are found, upload to
	 * the server and, on success, delete the file.
	 */
	public void submitStackTraces(String server) {
		try {
			String dirString = null;
			if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
				dirString  = FileUtil.getStorageDirectory(ConstantUtil.STACKTRACE_DIR, "false");
			}else{
				dirString = FileUtil.getStorageDirectory(ConstantUtil.STACKTRACE_DIR, "true");
			}
			String[] list = getTraceFiles();
			if (list != null && list.length > 0) {
				for (int i = 0; i < list.length; i++) {
					String trace = FileUtil
							.readFileAsString(dirString
									+ list[i]);
					File f = new File(dirString + list[i]);

					Map<String, String> params = new HashMap<String, String>();
					params.put(ACTION_PARAM, ACTION_VALUE);
					params.put(PHONE_PARAM, phoneNumber);
					params.put(VER_PARAM, version);
					params.put(DATE_PARAM, DATE_FMT.format(new Date(f
							.lastModified())));
					params.put(DEV_ID_PARAM, deviceId);
					params.put(TRACE_PARAM, trace);

					String response = HttpUtil.httpPost(server
							+ EXCEPTION_SERVICE_PATH, params);
					if (response == null || response.trim().length() == 0) {
						f.delete();
					}
				}
			}
		} catch (Exception e) {
			Log.e(TAG, "Could not send exception", e);
		}
	}

}
