package com.gallatinsystems.survey.device.activity;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.gallatinsystems.survey.device.R;
import com.gallatinsystems.survey.device.dao.SurveyDbAdapter;
import com.gallatinsystems.survey.device.remote.PointOfInterestService;
import com.gallatinsystems.survey.device.remote.dto.PointOfInterestDto;
import com.gallatinsystems.survey.device.util.ConstantUtil;

/**
 * Activity to list all "nearby" access points
 * 
 * @author Christopher Fagiani
 */
public class NearbyItemActivity extends ListActivity implements
		LocationListener {

	private static final int NEARBY_DETAIL_ACTIVITY = 1;
	private LocationManager locMgr;
	private Criteria locationCriteria;
	private ProgressDialog progressDialog;
	private ArrayList<PointOfInterestDto> pointsOfInterest;
	private Handler dataHandler;
	private Runnable resultsUpdater;
	private String country;
	private String[] countries;
	private volatile boolean isRunning;
	private Thread dataThread;
	private SurveyDbAdapter databaseAdapter;
	private String serverBase;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		databaseAdapter = new SurveyDbAdapter(this);
		databaseAdapter.open();
		serverBase = databaseAdapter
				.findPreference(ConstantUtil.SERVER_SETTING_KEY);
		if (serverBase != null && serverBase.trim().length() > 0) {
			serverBase = getResources().getStringArray(R.array.servers)[Integer
					.parseInt(serverBase)];
		} else {
			serverBase = null;
		}
		setContentView(R.layout.nearbyitem);
		Resources resources = getResources();
		locMgr = (LocationManager) getSystemService(LOCATION_SERVICE);
		locationCriteria = new Criteria();
		locationCriteria.setAccuracy(Criteria.NO_REQUIREMENT);
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle(R.string.pleasewait);
		progressDialog.setMessage(resources.getString(R.string.loadingnearby));
		isRunning = false;
		progressDialog.setCancelable(true);
		progressDialog
				.setOnCancelListener(new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						if (isRunning && dataThread != null) {
							dataThread.interrupt();
						}
					}
				});

		countries = resources.getStringArray(R.array.countries);
		dataHandler = new Handler();
		resultsUpdater = new Runnable() {
			public void run() {
				updateUi();
			}
		};

		String provider = locMgr.getBestProvider(locationCriteria, true);
		if (provider != null) {
			Location loc = locMgr.getLastKnownLocation(provider);
			if (loc != null) {
				loadData(loc.getLatitude(), loc.getLongitude(), country);
			} else {
				locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER,
						1000, 0, this);
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (databaseAdapter != null) {
			databaseAdapter.close();
		}
	}

	/**
	 * updates the ui with the results of the service call that was running in
	 * the background thread.
	 */
	private void updateUi() {
		if (pointsOfInterest != null) {
			setListAdapter(new ArrayAdapter<PointOfInterestDto>(this,
					R.layout.itemlistrow, pointsOfInterest));
		}
		progressDialog.dismiss();
		isRunning = false;
	}

	/**
	 * loads the data from the server. TODO: make this work if offline too by
	 * loading from db
	 * 
	 * @param lat
	 * @param lon
	 */
	private void loadData(final Double lat, final Double lon,
			final String country) {
		isRunning = true;
		progressDialog.show();

		// Fire off a thread to do some work that we shouldn't do directly in
		// the UI thread
		dataThread = new Thread() {
			public void run() {
				pointsOfInterest = PointOfInterestService
						.getNearbyAccessPoints(lat, lon, country,serverBase);
				dataHandler.post(resultsUpdater);

			}
		};
		dataThread.start();
	}

	/**
	 * when a list item is clicked, launch the detail activity
	 */
	@Override
	protected void onListItemClick(ListView list, View view, int position,
			long id) {
		super.onListItemClick(list, view, position, id);
		Intent i = new Intent(this, NearbyItemDetailActivity.class);
		i.putExtra(ConstantUtil.AP_KEY, pointsOfInterest.get(position));
		startActivityForResult(i, NEARBY_DETAIL_ACTIVITY);
	}

	/**
	 * displays the country selection menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		displayCountrySelection();
		// return false so this method will be invoked on each press of the menu
		// button
		return false;
	}

	/**
	 * displays country selection dialog box
	 */
	private void displayCountrySelection() {
		AlertDialog dia = new AlertDialog.Builder(this).setTitle(

		R.string.countryselection).setItems(R.array.countries,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						country = countries[which];
						loadData(null, null, country);
					}
				}).create();
		dia.show();
	}

	@Override
	public void onLocationChanged(Location location) {
		locMgr.removeUpdates(this);
		if (!isRunning) {
			loadData(location.getLatitude(), location.getLongitude(), country);
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		// no-op

	}

	@Override
	public void onProviderEnabled(String provider) {
		// no-op

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// no-op
	}

}