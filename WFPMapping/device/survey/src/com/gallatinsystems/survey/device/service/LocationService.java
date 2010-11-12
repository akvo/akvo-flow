package com.gallatinsystems.survey.device.service;

import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;

import com.gallatinsystems.survey.device.R;
import com.gallatinsystems.survey.device.dao.SurveyDbAdapter;
import com.gallatinsystems.survey.device.exception.PersistentUncaughtExceptionHandler;
import com.gallatinsystems.survey.device.util.ConstantUtil;
import com.gallatinsystems.survey.device.util.HttpUtil;
import com.gallatinsystems.survey.device.util.PropertyUtil;
import com.gallatinsystems.survey.device.util.StatusUtil;

/**
 * service for sending location beacons on a set interval to the server. This
 * can be disabled via the properties menu
 * 
 * @author Christopher Fagiani
 * 
 */
public class LocationService extends Service {
	private static Timer timer;
	private LocationManager locMgr;
	private Criteria locationCriteria;
	private static final long INITIAL_DELAY = 60000;
	private static final long INTERVAL = 300000;
	private static boolean sendBeacon = true;
	private static final String BEACON_SERVICE_PATH = "/locationBeacon?action=beacon&phoneNumber=";
	private static final String VER = "&ver=";
	private static final String LAT = "&lat=";
	private static final String LON = "&lon=";
	private static final String ACC = "&acc=";
	private static final String DEV_ID = "&devId=";
	private static final String TAG = "LocationService";
	private String version;
	private String deviceId;
	private Properties props;

	public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * life cycle method for the service. This is called by the system when the
	 * service is started. It will schedule a timerTask that will periodically
	 * check the current location and send it to the server
	 */
	public int onStartCommand(final Intent intent, int flags, int startid) {
		// we only need to check this on command start since we'll explicitly
		// call endService if they change the preference to false after we're
		// already
		// started
		SurveyDbAdapter database = new SurveyDbAdapter(this);
		database.open();
		String val = database
				.findPreference(ConstantUtil.LOCATION_BEACON_SETTING_KEY);
		deviceId = database.findPreference(ConstantUtil.DEVICE_IDENT_KEY);
		if (val != null) {
			sendBeacon = Boolean.parseBoolean(val);
		}
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

		database.close();
		if (timer == null && sendBeacon) {
			timer = new Timer(true);
			timer.scheduleAtFixedRate(new TimerTask() {

				@Override
				public void run() {
					if (sendBeacon) {
						String provider = locMgr.getBestProvider(
								locationCriteria, true);
						if (provider != null) {
							sendLocation(server, locMgr
									.getLastKnownLocation(provider));
						}
					}
				}
			}, INITIAL_DELAY, INTERVAL);
		}
		return Service.START_STICKY;
	}

	public void onCreate() {
		super.onCreate();
		Thread.setDefaultUncaughtExceptionHandler(PersistentUncaughtExceptionHandler.getInstance());
		locMgr = (LocationManager) getSystemService(LOCATION_SERVICE);
		locationCriteria = new Criteria();
		locationCriteria.setAccuracy(Criteria.NO_REQUIREMENT);
		props = PropertyUtil.loadProperties(getResources());
	}

	/**
	 * sends the location beacon to the server
	 * 
	 * @param loc
	 */
	private void sendLocation(String serverBase, Location loc) {
		try {
			if (loc != null) {
				String url = serverBase + BEACON_SERVICE_PATH
						+ StatusUtil.getPhoneNumber(this) + LAT
						+ loc.getLatitude() + LON + loc.getLongitude() + ACC
						+ loc.getAccuracy() + VER + version;
				if (deviceId != null) {
					url += DEV_ID + deviceId;
				}
				HttpUtil.httpGet(url);
			} else {
				// if location is null, send an update anyway, just without
				// lat/lon
				HttpUtil.httpGet(serverBase + BEACON_SERVICE_PATH
						+ StatusUtil.getPhoneNumber(this) + VER + version);

			}
		} catch (Exception e) {
			Log.e(TAG, "Could not send location beacon", e);
		}
	}

	public void onDestroy() {
		super.onDestroy();
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}
}
