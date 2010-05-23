package com.gallatinsystems.survey.device.service;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;

import com.gallatinsystems.survey.device.R;
import com.gallatinsystems.survey.device.dao.SurveyDbAdapter;
import com.gallatinsystems.survey.device.util.ConstantUtil;
import com.gallatinsystems.survey.device.util.HttpUtil;
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
	private static final String BEACON_SERVICE_BASE = "http://watermapmonitordev.appspot.com";
	private static final String LAT = "&lat=";
	private static final String LON = "&lon=";
	private static final String ACC = "&acc=";

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
		if (val != null) {
			sendBeacon = Boolean.parseBoolean(val);
		}
		String serverBase = database
				.findPreference(ConstantUtil.SERVER_SETTING_KEY);
		if (serverBase == null || serverBase.trim().length() > 0) {
			serverBase = getResources().getStringArray(R.array.servers)[Integer
					.parseInt(serverBase)];
		} else {
			serverBase = BEACON_SERVICE_BASE;
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
		locMgr = (LocationManager) getSystemService(LOCATION_SERVICE);
		locationCriteria = new Criteria();
		locationCriteria.setAccuracy(Criteria.NO_REQUIREMENT);
	}

	/**
	 * sends the location beacon to the server
	 * 
	 * @param loc
	 */
	private void sendLocation(String serverBase, Location loc) {
		if (loc != null) {
			try {
				HttpUtil.httpGet(serverBase + BEACON_SERVICE_PATH
						+ StatusUtil.getPhoneNumber(this) + LAT
						+ loc.getLatitude() + LON + loc.getLongitude() + ACC
						+ loc.getAccuracy());
			} catch (Exception e) {
				// TODO: log error
			}
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
