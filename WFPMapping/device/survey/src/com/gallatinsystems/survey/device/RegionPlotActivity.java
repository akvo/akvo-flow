package com.gallatinsystems.survey.device;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.gallatinsystems.survey.device.dao.SurveyDbAdapter;
import com.gallatinsystems.survey.device.view.GeoPlotOverlay;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

/**
 * Activity to handle display of a mapview that allows the user to map a region
 * by recording GPS way-points.
 * 
 * @author Christopher Fagiani
 * 
 */
public class RegionPlotActivity extends MapActivity implements OnClickListener,
		LocationListener {

	public static final String PLOT_ID = "plotid";
	public static final String STATUS = "status";

	private static final int TOGGLE_ID = Menu.FIRST;
	private static final String AUTO_MODE = "auto";
	private static final String MANUAL_MODE = "manual";
	private static final float MINIMUM_ACCURACY = 1000f;
	private static final int INITIAL_ZOOM_LEVEL = 16;
	private static final int LOCATION_UPDATE_FREQ = 5000;

	private MapController mapController;
	private MapView mapView;
	private MyLocationOverlay myLocation;
	private GeoPlotOverlay regionPlot;
	private LocationManager locMgr;
	private String plotId;
	private SurveyDbAdapter dbAdaptor;
	private ArrayList<String> idList;
	private double currentElevation;
	private String currentMode;
	private String currentStatus;
	private Button actionButton;
	private String lastDrawTime;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.regionplotview);
		mapView = (MapView) findViewById(R.id.mapview);
		idList = new ArrayList<String>();
		lastDrawTime = null;
		mapController = mapView.getController();
		mapController.setZoom(INITIAL_ZOOM_LEVEL);
		// turn on zoom controls
		mapView.setBuiltInZoomControls(true);

		actionButton = (Button) findViewById(R.id.plotaction_button);
		actionButton.setOnClickListener(this);

		Button button = (Button) findViewById(R.id.completeplot_button);
		button.setOnClickListener(this);

		// set up my location and area rendering overlays
		myLocation = new MyLocationOverlay(this, mapView);
		regionPlot = new GeoPlotOverlay(this);
		List<Overlay> overlays = mapView.getOverlays();
		overlays.add(myLocation);
		overlays.add(regionPlot);

		locMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		// handle instance state
		plotId = savedInstanceState != null ? savedInstanceState
				.getString(SurveyDbAdapter.PK_ID_COL) : null;
		currentStatus = savedInstanceState != null ? savedInstanceState
				.getString(SurveyDbAdapter.STATUS_COL)
				: SurveyDbAdapter.IN_PROGRESS_STATUS;
		if (plotId == null) {
			Bundle extras = getIntent().getExtras();
			plotId = extras != null ? extras.getString(PLOT_ID) : null;
			currentStatus = extras != null ? extras.getString(STATUS)
					: SurveyDbAdapter.IN_PROGRESS_STATUS;
		}
		if (SurveyDbAdapter.RUNNING_STATUS.equals(currentStatus)) {
			currentMode = AUTO_MODE;
		} else {
			currentMode = MANUAL_MODE;
		}
		updateLabels();
		dbAdaptor = new SurveyDbAdapter(this);
		dbAdaptor.open();
		fillPlot();
		registerForContextMenu(mapView);
	}

	/**
	 * iterates over the points from the database and adds them to the region
	 * overlay.
	 */
	private void fillPlot() {
		Cursor data = dbAdaptor.listPlotPoints(plotId, lastDrawTime);
		startManagingCursor(data);
		while (!data.isAfterLast()) {
			regionPlot.addLocation(convertToPoint(data.getString(data
					.getColumnIndexOrThrow(SurveyDbAdapter.LAT_COL)), data
					.getString(data
							.getColumnIndexOrThrow(SurveyDbAdapter.LON_COL))));
			idList.add(data.getString(data
					.getColumnIndexOrThrow(SurveyDbAdapter.PK_ID_COL)));
			data.moveToNext();
		}
		data.close();
		lastDrawTime = "" + System.currentTimeMillis();
	}

	private void updateLabels() {
		if (MANUAL_MODE.equals(currentMode)) {
			actionButton.setText(R.string.addpoint);
		} else {
			if (SurveyDbAdapter.RUNNING_STATUS.equals(currentStatus)) {
				actionButton.setText(R.string.stopplotting);
			} else {
				actionButton.setText(R.string.startplotting);
			}
		}
	}

	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (outState != null && plotId != null) {
			outState.putString(SurveyDbAdapter.PK_ID_COL, plotId);
			outState.putString(SurveyDbAdapter.STATUS_COL, currentStatus);
		}
	}

	protected boolean isRouteDisplayed() {
		return false;
	}

	public void onDestroy() {
		super.onDestroy();
		dbAdaptor.close();
	}

	/**
	 * stop getting location updates when we're paused (to conserve battery
	 * life).
	 */
	public void onPause() {
		super.onPause();
		myLocation.disableMyLocation();
		locMgr.removeUpdates(this);
	}

	/**
	 * starts listening for location updates
	 */
	public void onResume() {
		super.onResume();
		myLocation.enableMyLocation();
		GeoPoint point = myLocation.getMyLocation();
		if (point != null) {
			mapController.animateTo(point);
		}
		locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				LOCATION_UPDATE_FREQ, 0, this);
	}

	/**
	 * deletes a plot point from the database
	 * 
	 * @param index
	 */
	public void deletePoint(int index) {
		if (index < idList.size()) {
			String id = idList.get(index);
			if (id != null) {
				dbAdaptor.deletePlotPoint(id);
				idList.remove(id);
			}
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.plotaction_button) {
			if (MANUAL_MODE.equals(currentMode)) {
				GeoPoint point = myLocation.getMyLocation();
				if (point != null) {
					mapController.animateTo(point);
					regionPlot.addLocation(point);
					mapView.invalidate();
					dbAdaptor.savePlotPoint(plotId, decodeLocation(point
							.getLatitudeE6()), decodeLocation(point
							.getLongitudeE6()), currentElevation);
				}
			} else {
				// if we're in AUTO mode, then we are going to either start or
				// stop the plotting service
				if (SurveyDbAdapter.RUNNING_STATUS.equals(currentStatus)) {
					// if we're running, then stop the service
					stopPlotService();
					// set the status to In progress
					changePlotStatus(SurveyDbAdapter.IN_PROGRESS_STATUS);
				} else {
					// if we're not running, then we should start
					startPlotService();
					changePlotStatus(SurveyDbAdapter.RUNNING_STATUS);
				}
			}
		} else {
			// if we're in AUTO mode, we have to stop the service
			if (AUTO_MODE.equals(currentMode)) {
				stopPlotService();
			}
			changePlotStatus(SurveyDbAdapter.COMPLETE_STATUS);
			// send a broadcast message indicating new data is available
			sendBroadcast(new Intent(BroadcastDispatcher.DATA_AVAILABLE_INTENT));
			finish();
		}
	}

	private GeoPoint convertToPoint(String lat, String lon) {
		Double latitude = Double.parseDouble(lat) * 1E6;
		Double longitude = Double.parseDouble(lon) * 1E6;
		return new GeoPoint(latitude.intValue(), longitude.intValue());
	}

	private GeoPoint convertToPoint(Location loc) {
		Double latitude = loc.getLatitude() * 1E6;
		Double longitude = loc.getLongitude() * 1E6;
		return new GeoPoint(latitude.intValue(), longitude.intValue());
	}

	private String decodeLocation(int val) {
		return "" + ((double) val / (double) 1E6);
	}

	private void changePlotStatus(String status) {
		currentStatus = status;
		dbAdaptor.updatePlotStatus(plotId, currentStatus);
		updateLabels();
	}

	/**
	 * termniates the RegionPlotService
	 */
	private void stopPlotService() {
		Intent i = new Intent(this, RegionPlotService.class);
		stopService(i);
	}

	/**
	 * starts the plot service
	 */
	private void startPlotService() {
		Intent i = new Intent(this, RegionPlotService.class);
		i.putExtra(RegionPlotService.PLOT_ID, plotId);
		startService(i);
	}

	/**
	 * presents a single button ("Toggle Mode") when the user clicks the menu
	 * key
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, TOGGLE_ID, 0, R.string.toggleplotmode);
		return true;
	}

	/**
	 * handles the button press for the options menu
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case TOGGLE_ID:
			if (AUTO_MODE.equals(currentMode)) {
				currentMode = MANUAL_MODE;
				actionButton.setText(R.string.addpoint);
			} else {
				currentMode = AUTO_MODE;
				actionButton.setText(R.string.startplotting);
			}
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	/**
	 * called by the system when it gets location updates.
	 */
	public void onLocationChanged(Location loc) {
		if (loc != null) {
			// TODO: put this back in?
			// if (loc.getAccuracy() < MINIMUM_ACCURACY) {
			mapController.animateTo(convertToPoint(loc));
			currentElevation = loc.getAltitude();
			fillPlot();
			mapView.invalidate();
			// }

		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		// no op. needed to satisfy location interface

	}

	@Override
	public void onProviderEnabled(String provider) {
		// no op. needed to satisfy location interface

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// no op. needed to satisfy location interface

	}
}
