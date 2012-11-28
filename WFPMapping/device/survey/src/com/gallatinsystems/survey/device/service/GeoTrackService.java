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

import java.util.ArrayList;

import android.app.Service;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.gallatinsystems.survey.device.R;
import com.gallatinsystems.survey.device.exception.PersistentUncaughtExceptionHandler;
import com.gallatinsystems.survey.device.util.ViewUtil;

/**
 * This service is used to record geo tracks (a series of geo coordinates)
 * launched by the GeoTrack question type. It will display a notification when
 * it start/stops.
 * 
 * @author Christopher Fagiani
 * 
 */
public class GeoTrackService extends Service implements LocationListener {

	private static long INTERVAL = 60000;
	private static float MIN_DIST = 10;
	private ArrayList<String> points;
	private LocationManager locMgr;
	private Criteria locationCriteria;
	private final GeoTrackBinder binder = new GeoTrackBinder();

	@Override
	public void onCreate() {
		super.onCreate();
		Thread.setDefaultUncaughtExceptionHandler(PersistentUncaughtExceptionHandler.getInstance());
	}

	private void startRecording() {
		locMgr = (LocationManager) getSystemService(LOCATION_SERVICE);
		locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, INTERVAL,
				MIN_DIST, this);
		locationCriteria = new Criteria();
		locationCriteria.setAccuracy(Criteria.NO_REQUIREMENT);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		points = new ArrayList<String>();
		sendNotification();
		startRecording();
		return START_STICKY;
	}

	/**
	 * cancels the notification and terminates the recording of points. This
	 * will also unsubscribe from location updates to conserve battery
	 */
	@Override
	public void onDestroy() {
		if (locMgr != null) {
			locMgr.removeUpdates(this);
			ViewUtil.cancelNotification(R.string.trackstartnotification, this);
			Toast.makeText(this, R.string.trackendnotification,
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	private void sendNotification() {
		CharSequence text = getText(R.string.trackstartnotification);
		ViewUtil.fireNotification(text.toString(), text.toString(), this,
				R.string.trackstartnotification,
				android.R.drawable.stat_notify_sync);
	}

	@Override
	public void onLocationChanged(Location location) {
		points.add(new String(location.getLatitude() + " "
				+ location.getLongitude()));
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	public ArrayList<String> getPoints() {
		return points;
	}

	/**
	 * Binder class so clients can call methods on the service instance
	 * 
	 * @author Christopher Fagiani
	 * 
	 */
	public class GeoTrackBinder extends Binder {
		public GeoTrackService getService() {
			return GeoTrackService.this;
		}
	}

}
