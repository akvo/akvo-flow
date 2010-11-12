package com.gallatinsystems.survey.device.service;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

import com.gallatinsystems.survey.device.dao.SurveyDbAdapter;
import com.gallatinsystems.survey.device.exception.PersistentUncaughtExceptionHandler;
import com.gallatinsystems.survey.device.util.ConstantUtil;

/**
 * Background service used by the RegionPlotActivity to automatically record
 * plot points. This service acts as a location listener using the GPS provider.
 * One MUST invoke this with the PLOT_ID set as an extra in the intent. Since
 * the GPS uses a lot of power, one should refrain from setting an interval of <
 * 60 seconds. The default interval is currently 60000 milliseconds.
 * 
 * @author Christopher Fagiani
 * 
 */
public class RegionPlotService extends Service implements LocationListener {

	private static final int DEFAULT_INTERVAL = 60000;
	private SurveyDbAdapter database;
	private LocationManager locMgr;
	private int interval;
	private String plotId;
	private Location lastLocation;
	private Timer timer;

	/**
	 * if not already running (i.e. if timer is null) then open the database,
	 * register as a location listener and construct and start a new timer.
	 */
	public int onStartCommand(final Intent intent, int flags, int startid) {
		if (timer == null) {
			timer = new Timer();
			interval = intent.getIntExtra(ConstantUtil.INTERVAL_KEY, DEFAULT_INTERVAL);
			plotId = intent.getStringExtra(ConstantUtil.PLOT_ID_KEY);
			database = new SurveyDbAdapter(this);
			database.open();
			if (locMgr == null) {
				locMgr = (LocationManager) getSystemService(LOCATION_SERVICE);
			}
			locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER,
					interval, 0, this);
			timer.scheduleAtFixedRate(new TimerTask() {
				public void run() {
					if (lastLocation != null) {
						database.savePlotPoint(plotId, ""
								+ lastLocation.getLatitude(), ""
								+ lastLocation.getLongitude(), lastLocation
								.getAltitude());
					}
				}
			}, 0, interval);
		}
		return START_STICKY;
	}

	/**
	 * stops the timer from firing and then frees resources by closing the
	 * database and removing itself as a location listener.
	 */
	private void shutdown() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		if (locMgr != null) {
			locMgr.removeUpdates(this);
		}
		if (database != null) {
			database.close();
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * when the system is killing the service, clean up
	 */
	public void onDestroy() {
		shutdown();
	}

	@Override
	public void onLocationChanged(Location location) {
		lastLocation = location;
	}

	/**
	 * if the user turns off the GPS while this service is recoding location, we
	 * just shut down since we can't record if the GPS is off. We will NOT
	 * automatically resume recording if they turn it back on since we assume
	 * they shut it off to consume power and there is no guarantee that they're
	 * anywhere near where they want to be when they re-enable GPS.
	 */
	@Override
	public void onProviderDisabled(String provider) {
		shutdown();
	}

	@Override
	public void onProviderEnabled(String provider) {
		// no op

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// no op

	}
	
	@Override
	public void onCreate(){
		super.onCreate();
		Thread.setDefaultUncaughtExceptionHandler(PersistentUncaughtExceptionHandler.getInstance());
	}

}
