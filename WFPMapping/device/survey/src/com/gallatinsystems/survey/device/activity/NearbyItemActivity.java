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
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.gallatinsystems.survey.device.R;
import com.gallatinsystems.survey.device.dao.SurveyDbAdapter;
import com.gallatinsystems.survey.device.domain.PointOfInterest;
import com.gallatinsystems.survey.device.remote.PointOfInterestService;
import com.gallatinsystems.survey.device.util.ConstantUtil;

/**
 * Activity to list all "nearby" access points
 * 
 * @author Christopher Fagiani
 */
public class NearbyItemActivity extends ListActivity implements
		LocationListener {

	private static final int NEARBY_DETAIL_ACTIVITY = 1;
	private static final int NAVIGATE_OPT = 2;
	private static final int COUNTRY_OPT = 3;
	private static final int SERVER_OPT = 4;
	private LocationManager locMgr;
	private Criteria locationCriteria;
	private ProgressDialog progressDialog;
	private ArrayList<PointOfInterest> pointsOfInterest;
	private Handler dataHandler;
	private Runnable resultsUpdater;
	private String country;
	private String[] countries;
	private boolean useServer;
	private volatile boolean isRunning;
	private volatile boolean additive;
	private Thread dataThread;
	private SurveyDbAdapter databaseAdapter;
	private String serverBase;
	private PointOfInterest loadMorePlaceholder;
	private PointOfInterestService pointOfInterestService;
	private Double lastLat;
	private Double lastLon;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		pointOfInterestService = new PointOfInterestService();
		useServer = true;
		loadMorePlaceholder = new PointOfInterest();
		loadMorePlaceholder.setName(getString(R.string.loadmore));
		additive = false;
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
			setListAdapter(new ArrayAdapter<PointOfInterest>(this,
					R.layout.itemlistrow, pointsOfInterest));
		} else {
			setListAdapter(new ArrayAdapter<PointOfInterest>(this,
					R.layout.itemlistrow, new ArrayList<PointOfInterest>()));			
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
		lastLat = lat;
		lastLon = lon;
		progressDialog.show();

		// Fire off a thread to do some work that we shouldn't do directly in
		// the UI thread
		dataThread = new Thread() {
			public void run() {
				if (useServer) {
					ArrayList<PointOfInterest> newPoints = pointOfInterestService
							.getNearbyAccessPoints(lat, lon, country,
									serverBase, additive);
					savePoints(newPoints);

					if (additive && pointsOfInterest != null) {
						pointsOfInterest.remove(loadMorePlaceholder);
					}
					if (additive && newPoints != null) {
						pointsOfInterest.addAll(newPoints);
					} else {
						pointsOfInterest = newPoints;
					}
					if (pointOfInterestService.hasMore()) {
						pointsOfInterest.add(loadMorePlaceholder);
					}
				} else {
					pointsOfInterest = databaseAdapter
							.listPointsOfInterest(country);
				}
				dataHandler.post(resultsUpdater);
				additive = false;
			}
		};
		dataThread.start();
	}

	/**
	 * saves points of interest to the db
	 * 
	 * @param points
	 */
	private void savePoints(ArrayList<PointOfInterest> points) {
		if (points != null) {
			for (int i = 0; i < points.size(); i++) {
				if (points.get(i).getId() != null) {
					databaseAdapter.saveOrUpdatePointOfInterest(points.get(i));
				}
			}
		}
	}

	/**
	 * when a list item is clicked, launch the detail activity
	 */
	@Override
	protected void onListItemClick(ListView list, View view, int position,
			long id) {
		super.onListItemClick(list, view, position, id);
		Intent i = new Intent(this, NearbyItemDetailActivity.class);
		PointOfInterest dto = pointsOfInterest.get(position);
		if (dto == loadMorePlaceholder) {
			additive = true;
			loadData(lastLat, lastLon, country);
		} else {
			i.putExtra(ConstantUtil.AP_KEY, pointsOfInterest.get(position));
			startActivityForResult(i, NEARBY_DETAIL_ACTIVITY);
		}
	}

	/**
	 * presents a single view on map button when the user clicks the menu key
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, NAVIGATE_OPT, 0, R.string.navigate);
		menu.add(0, COUNTRY_OPT, 1, R.string.countryselection);
		menu.add(0, SERVER_OPT, 2, R.string.uselocal);
		return true;
	}

	/**
	 * handles the button press for the "add" button on the menu
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case NAVIGATE_OPT:
			launchNavigation();
			return true;
		case COUNTRY_OPT:
			displayCountrySelection();
			return true;
		case SERVER_OPT:
			useServer = !useServer;
			loadData(lastLat, lastLon, country);
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	/**
	 * disables the map option if no results are loaded
	 */
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		super.onMenuOpened(featureId, menu);
		if (pointsOfInterest == null || pointsOfInterest.size() == 0) {
			menu.getItem(0).setEnabled(false);
		} else {
			menu.getItem(0).setEnabled(true);
		}
		if (useServer) {
			menu.getItem(2).setTitle(R.string.uselocal);
		} else {
			menu.getItem(2).setTitle(R.string.useserver);
		}

		return true;
	}

	/**
	 * launches map view and zooms in on point of interest
	 */
	private void launchNavigation() {
		Intent mapsIntent = new Intent(this, PointOfInterestMapActivity.class);
		mapsIntent.putExtra(ConstantUtil.POINTS_KEY, pointsOfInterest);
		startActivity(mapsIntent);
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
						additive = false;
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