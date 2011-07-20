package com.gallatinsystems.survey.device.activity;

import java.text.NumberFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.gallatinsystems.survey.device.R;
import com.gallatinsystems.survey.device.domain.PointOfInterest;
import com.gallatinsystems.survey.device.util.ConstantUtil;

/**
 * displays the detail information for a single nearby item
 * 
 * @author Christopher Fagiani
 */
public class NearbyItemDetailActivity extends Activity implements
		LocationListener, SensorEventListener {

	private static final int NAVIGATE_ID = Menu.FIRST;
	private LocationManager locMgr;
	private Criteria locationCriteria;
	private TextView nameField;
	private TextView distanceField;
	private TextView typeField;
	private ScrollView scrollView;
	private ImageView arrowView;
	private PointOfInterest pointOfInterest;
	private Bitmap arrowBitmap;
	private Location apLocation;
	private float lastBearing;
	private float lastOrientation;
	private Sensor orientSensor;
	private NumberFormat distanceFormat;
	private String[] units;
	private Resources res;

	private static final float MIN_CHANGE = 2f;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		lastBearing = -999f;
		lastOrientation = 0f;
		distanceFormat = NumberFormat.getInstance();
		locMgr = (LocationManager) getSystemService(LOCATION_SERVICE);
		locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0,
				this);
		SensorManager sensorMgr = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

		orientSensor = sensorMgr.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		sensorMgr.registerListener(this, orientSensor,
				SensorManager.SENSOR_DELAY_NORMAL);
		res = getResources();
		units = res.getStringArray(R.array.distancemesures);
		locationCriteria = new Criteria();
		locationCriteria.setAccuracy(Criteria.NO_REQUIREMENT);
		setContentView(R.layout.nearbydetail);

		nameField = (TextView) findViewById(R.id.pointNameField);
		distanceField = (TextView) findViewById(R.id.distanceField);
		typeField = (TextView) findViewById(R.id.pointTypeField);
		scrollView = (ScrollView) findViewById(R.id.pointscroll);
		arrowView = (ImageView) findViewById(R.id.arrowView);
		arrowBitmap = BitmapFactory.decodeResource(res, R.drawable.uparrow);

		pointOfInterest = savedInstanceState != null ? (PointOfInterest) savedInstanceState
				.getSerializable(ConstantUtil.AP_KEY) : null;
		if (pointOfInterest == null) {
			Bundle extras = getIntent().getExtras();
			pointOfInterest = extras != null ? (PointOfInterest) extras
					.getSerializable(ConstantUtil.AP_KEY) : null;
		}
		apLocation = new Location(LocationManager.GPS_PROVIDER);
		apLocation.setLatitude(pointOfInterest.getLatitude());
		apLocation.setLongitude(pointOfInterest.getLongitude());
		populateFields();
	}

	/**
	 * when this activity is done, stop listening for location updates
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (locMgr != null) {
			locMgr.removeUpdates(this);
		}
	}

	/**
	 * put loaded data into the views for display
	 */
	private void populateFields() {
		if (pointOfInterest != null) {
			nameField.setText(" " + pointOfInterest.getName());
			typeField.setText(" " + pointOfInterest.getType());
			if (pointOfInterest.getPropertyNames() != null) {
				for (int i = 0; i < pointOfInterest.getPropertyNames().size(); i++) {
					if (pointOfInterest.getPropertyValues().size() > i) {
						String val = pointOfInterest.getPropertyValues().get(i);
						if (val != null && val.trim().length() > 0
								&& !"null".equalsIgnoreCase(val.trim())) {
							LinearLayout l = new LinearLayout(this);
							l.setOrientation(LinearLayout.HORIZONTAL);
							TextView labelView = new TextView(this);
							labelView.setText(pointOfInterest
									.getPropertyNames().get(i) + ": ");
							l.addView(labelView);
							TextView valView = new TextView(this);
							valView.setText(val);
							l.addView(valView);
							scrollView.addView(l);
						}
					}
				}
			}
		}
	}

	/**
	 * presents a single view on map button when the user clicks the menu key
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, NAVIGATE_ID, 0, R.string.navigate);
		return true;
	}

	/**
	 * handles the button press for the "add" button on the menu
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case NAVIGATE_ID:
			launchNavigation();
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	/**
	 * sets the selected point (if there is one) in the Bundle
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (outState != null && pointOfInterest != null) {
			outState.putSerializable(ConstantUtil.AP_KEY, pointOfInterest);
		}
	}

	/**
	 * when the location is updated, update the distance field and change the
	 * rotation of the directional arrow. The units of the distance may change
	 * based on the distance (i.e. the point is more than 1 KM away, the
	 * distance will be shown in kilometers. Otherwise, meters are used.
	 */
	@Override
	public void onLocationChanged(Location loc) {
		// set the distance value
		float distance = apLocation.distanceTo(loc);
		int unitsIdx = 0;
		if (distance > 1000) {
			distance /= 1000;
			unitsIdx = 1;
		}
		distanceField.setText(" " + distanceFormat.format(distance) + " "
				+ units[unitsIdx]);
		// only update the bearing and the corresponding image representation of
		// it if it changed more than MIN_CHANGE degrees
		// so we're not always manipulating the image
		float newBearing = loc.bearingTo(apLocation);
		if (Math.abs(newBearing - lastBearing) >= MIN_CHANGE) {
			lastBearing = newBearing;
			updateArrow();
		}
	}

	/**
	 * Rotates the direction arrow so it always points at the access point
	 */
	private void updateArrow() {
		Matrix matrix = new Matrix();
		matrix.postRotate(lastBearing + lastOrientation);
		Bitmap resizedBitmap = Bitmap.createBitmap(arrowBitmap, 0, 0, 30, 30,
				matrix, true);
		arrowView.setImageDrawable(new BitmapDrawable(res, resizedBitmap));
	}

	/**
	 * launches map view and zooms in on point of interest
	 */
	private void launchNavigation() {
		Intent mapsIntent = new Intent(this, PointOfInterestMapActivity.class);

		ArrayList<PointOfInterest> dtoList = new ArrayList<PointOfInterest>();
		dtoList.add(pointOfInterest);
		mapsIntent.putExtra(ConstantUtil.POINTS_KEY, dtoList);
		startActivity(mapsIntent);
	}

	@Override
	public void onProviderDisabled(String arg0) {
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

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// no-op
	}

	/**
	 * update the directional arrow since we detected a change in orientation
	 */
	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor == orientSensor) {
			lastOrientation = event.values[0];
			updateArrow();

		}
	}

}