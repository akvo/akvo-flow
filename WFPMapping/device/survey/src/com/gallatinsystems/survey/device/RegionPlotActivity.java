package com.gallatinsystems.survey.device;

import java.util.List;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.gallatinsystems.survey.device.dao.SurveyDbAdapter;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

public class RegionPlotActivity extends MapActivity implements OnClickListener,
		LocationListener {

	public static final String PLOT_ID = "plotid";

	private MapController mapController;
	private MapView mapView;
	private MyLocationOverlay myLocation;
	private LocationManager locMgr;
	private String plotId;
	private SurveyDbAdapter dbAdaptor;

	private static final float MINIMUM_ACCURACY = 1000f;
	private static final int INITIAL_ZOOM_LEVEL = 16;
	private static final int LOCATION_UPDATE_FREQ = 5000;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.regionplotview);
		mapView = (MapView) findViewById(R.id.mapview);
		mapController = mapView.getController();
		mapController.setZoom(INITIAL_ZOOM_LEVEL);
		Button addPointButton = (Button) findViewById(R.id.addpoint_button);
		addPointButton.setOnClickListener(this);
		// set up my location overlay
		myLocation = new MyLocationOverlay(this, mapView);
		List<Overlay> overlays = mapView.getOverlays();
		overlays.add(myLocation);

		locMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		// handle instance state
		plotId = savedInstanceState != null ? savedInstanceState
				.getString(SurveyDbAdapter.PK_ID_COL) : null;
		if (plotId == null) {
			Bundle extras = getIntent().getExtras();
			plotId = extras != null ? extras.getString(PLOT_ID) : null;
		}
		dbAdaptor = new SurveyDbAdapter(this);
		dbAdaptor.open();

	}

	protected boolean isRouteDisplayed() {
		return false;
	}

	public void onDestroy() {
		super.onDestroy();
		dbAdaptor.close();
	}

	public void onPause() {
		super.onPause();
		myLocation.disableMyLocation();
		locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				LOCATION_UPDATE_FREQ, 0, this);
	}

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
	 * TODO: add handler for onComplete click
	 */
	@Override
	public void onClick(View v) {
		GeoPoint point = myLocation.getMyLocation();
		if (point != null) {
			mapController.animateTo(point);
			mapView.invalidate();
			dbAdaptor.savePlotPoint(plotId, decodeLocation(point
					.getLatitudeE6()), decodeLocation(point.getLongitudeE6()));
		}
	}

	private GeoPoint convertToPoint(Location loc) {
		Double latitude = loc.getLatitude() * 1E6;
		Double longitude = loc.getLongitude() * 1E6;
		return new GeoPoint(latitude.intValue(), longitude.intValue());
	}

	private String decodeLocation(int val) {
		return "" + ((double) val / (double) 1E6);
	}

	/**
	 * called by the system when it gets location updates.
	 */
	public void onLocationChanged(Location loc) {
		if (loc != null) {
			// if (loc.getAccuracy() < MINIMUM_ACCURACY) {
			mapController.animateTo(convertToPoint(loc));
			mapView.invalidate();
			// }

		}
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
}
