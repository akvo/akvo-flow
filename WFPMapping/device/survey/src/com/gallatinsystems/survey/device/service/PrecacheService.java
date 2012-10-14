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
import java.util.concurrent.Semaphore;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.gallatinsystems.survey.device.R;
import com.gallatinsystems.survey.device.dao.SurveyDbAdapter;
import com.gallatinsystems.survey.device.domain.PointOfInterest;
import com.gallatinsystems.survey.device.exception.PersistentUncaughtExceptionHandler;
import com.gallatinsystems.survey.device.remote.PointOfInterestService;
import com.gallatinsystems.survey.device.util.ConstantUtil;
import com.gallatinsystems.survey.device.util.PropertyUtil;
import com.gallatinsystems.survey.device.util.StatusUtil;

/**
 * This service is used to pre cache data from the server on the device. The
 * data to be cached is driven by the preference database. This class can cache
 * the following data types:
 * 
 * <ul>
 * <li>Points of Interest - configured by country</li>
 * 
 * @author Christopher Fagiani
 * 
 */
public class PrecacheService extends Service {

	private static final String TAG = "PRECACHE_SERVICE";
	private static final int DEFAULT_LIMIT = 200;

	private SurveyDbAdapter databaseAdapter;
	private String serverBase;

	private Thread thread;
	private static Semaphore lock = new Semaphore(1);
	private PropertyUtil props;
	private String[] countryArray;

	public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * life cycle method for the service. This is called by the system when the
	 * service is started
	 */
	public int onStartCommand(final Intent intent, int flags, int startid) {
		countryArray = getResources().getStringArray(R.array.countries);
		thread = new Thread(new Runnable() {
			public void run() {
				if (intent != null) {
					try {
						lock.acquire();
						databaseAdapter = new SurveyDbAdapter(
								PrecacheService.this);
						databaseAdapter.open();
						int precacheOption = Integer.parseInt(databaseAdapter
								.findPreference(ConstantUtil.PRECACHE_SETTING_KEY));
						serverBase = databaseAdapter
								.findPreference(ConstantUtil.SERVER_SETTING_KEY);
						if (serverBase != null
								&& serverBase.trim().length() > 0) {
							serverBase = getResources().getStringArray(
									R.array.servers)[Integer
									.parseInt(serverBase)];
						} else {
							serverBase = props
									.getProperty(ConstantUtil.SERVER_BASE);
						}
						String pointCountries = databaseAdapter
								.findPreference(ConstantUtil.PRECACHE_POINT_COUNTRY_KEY);
						if (pointCountries != null
								&& pointCountries.trim().length() > 0
								&& canDownload(precacheOption)) {
							String limit = databaseAdapter
									.findPreference(ConstantUtil.PRECACHE_POINT_LIMIT_KEY);
							int limitVal = DEFAULT_LIMIT;
							if (limit != null && limit.trim().length() > 0) {
								try {
									limitVal = Integer.parseInt(limit);
								} catch (Exception e) {
									Log.e(TAG,
											"Precache limit is not an integer",
											e);
								}
							}
							cachePoints(pointCountries, limitVal);
						}
					} catch (Exception e) {
						Log.e(TAG, "Could not precache data", e);
						PersistentUncaughtExceptionHandler.recordException(e);
					} finally {
						lock.release();
						if (databaseAdapter != null) {
							databaseAdapter.close();
						}
					}
				}
				stopSelf();
			}
		});
		thread.start();
		return Service.START_REDELIVER_INTENT;
	}

	/**
	 * attempts to download all of the
	 * 
	 * @param countries
	 */
	private void cachePoints(String countries, int limit) {
		String[] countryList = countries.split(",");
		for (int i = 0; i < countryList.length; i++) {
			String countryName = countryList[i];
			try {
				int countryIdx = Integer.parseInt(countryName);
				countryName = countryArray[countryIdx];
			} catch (Exception e) {
			}
			PointOfInterestService pointOfInterestService = new PointOfInterestService(
					serverBase);
			int count = 0;
			do {
				ArrayList<PointOfInterest> points = pointOfInterestService
						.getNearbyAccessPoints(null, null, countryName,
								serverBase, true);
				if (points != null) {
					for (int j = 0; j < points.size(); j++) {
						if (points.get(i).getId() != null) {
							databaseAdapter.saveOrUpdatePointOfInterest(points
									.get(j));
						}
					}
				}
			} while (count < limit && pointOfInterestService.hasMore());
		}
	}

	public void onCreate() {
		super.onCreate();
		Thread.setDefaultUncaughtExceptionHandler(PersistentUncaughtExceptionHandler
				.getInstance());
		props = new PropertyUtil(getResources());
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
