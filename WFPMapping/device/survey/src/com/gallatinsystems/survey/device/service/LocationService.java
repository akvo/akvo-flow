package com.gallatinsystems.survey.device.service;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;

import com.gallatinsystems.survey.device.dao.SurveyDbAdapter;
import com.gallatinsystems.survey.device.util.ConstantUtil;

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
		// call endService if they change the preference to false after we're already
		// started
		SurveyDbAdapter database = new SurveyDbAdapter(this);
		database.open();
		String val = database
				.findPreference(ConstantUtil.LOCATION_BEACON_SETTING_KEY);
		if (val != null) {
			sendBeacon = Boolean.parseBoolean(val);
		}
		database.close();
		if (timer == null) {
			timer = new Timer(true);
			timer.scheduleAtFixedRate(new TimerTask() {

				@Override
				public void run() {
					if (sendBeacon) {
						String provider = locMgr.getBestProvider(
								locationCriteria, true);
						if (provider != null) {
							sendLocation(locMgr.getLastKnownLocation(provider));
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

	private void sendLocation(Location loc) {
		// TODO: get server API call for sending location beacons
		if(loc != null){
			
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
