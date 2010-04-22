package com.gallatinsystems.survey.device.activity;

import android.app.Activity;
import android.content.Context;
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
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.gallatinsystems.survey.device.R;
import com.gallatinsystems.survey.device.remote.dto.AccessPointDto;
import com.gallatinsystems.survey.device.util.ConstantUtil;

/**
 * displays the detail information for a single nearby item
 *
 * @author Christopher Fagiani
 */
public class NearbyItemDetailActivity extends Activity implements
		LocationListener, SensorEventListener {

	private LocationManager locMgr;
	private Criteria locationCriteria;
	private TextView communityCodeField;
	private TextView distanceField;
	private TextView techTypeField;
	private TextView statusField;
	private ImageView arrowView;
	private AccessPointDto accessPoint;
	private Bitmap arrowBitmap;
	private Location apLocation;
	private float lastBearing;
	private Location lastLocation;
	private float lastOrientation;
	private Sensor orientSensor;


	private static final float MIN_CHANGE = 2f;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		lastBearing = -999f;
		lastOrientation = 0f;
		locMgr = (LocationManager) getSystemService(LOCATION_SERVICE);
		locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0,
				this);
		SensorManager sensorMgr = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

		orientSensor = sensorMgr.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		sensorMgr.registerListener(this,orientSensor,SensorManager.SENSOR_DELAY_NORMAL);


		locationCriteria = new Criteria();
		locationCriteria.setAccuracy(Criteria.NO_REQUIREMENT);
		setContentView(R.layout.nearbydetail);

		communityCodeField = (TextView) findViewById(R.id.communityCodeField);
		distanceField = (TextView) findViewById(R.id.distanceField);
		techTypeField = (TextView) findViewById(R.id.techTypeField);
		statusField = (TextView) findViewById(R.id.statusField);
		arrowView = (ImageView) findViewById(R.id.arrowView);
		arrowBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.uparrow);


		accessPoint = savedInstanceState != null ? (AccessPointDto) savedInstanceState
				.getSerializable(ConstantUtil.AP_KEY)
				: null;
		if (accessPoint == null) {
			Bundle extras = getIntent().getExtras();
			accessPoint = extras != null ? (AccessPointDto) extras
					.getSerializable(ConstantUtil.AP_KEY) : null;
		}
		apLocation = new Location(LocationManager.GPS_PROVIDER);
		apLocation.setLatitude(accessPoint.getLat());
		apLocation.setLongitude(accessPoint.getLon());
		populateFields();
	}

/**
* when this activity is done, stop listening for location updates
*/
	@Override
	public void onDestroy(){
		super.onDestroy();
		if(locMgr != null){
			locMgr.removeUpdates(this);
		}
	}

	/**
	 * put loaded data into the views for display
	 */
	private void populateFields() {
		if (accessPoint != null) {
			communityCodeField.setText(accessPoint.getCommunityCode());
			techTypeField.setText(accessPoint.getTechType());
			statusField.setText(accessPoint.getStatus());
			// TODO: add other fields
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (outState != null && accessPoint != null) {
			outState.putSerializable(ConstantUtil.AP_KEY, accessPoint);
		}
	}

	@Override
	public void onLocationChanged(Location loc) {
		lastLocation = loc;
		// set the distance value
		distanceField.setText(apLocation.distanceTo(loc) + "");
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
*  Rotates the direction arrow so it always points at the access point
*/
	private void updateArrow(){
		Matrix matrix = new Matrix();
		matrix.postRotate(lastBearing + lastOrientation);
		Bitmap resizedBitmap = Bitmap.createBitmap(arrowBitmap, 0, 0, 30,
				30, matrix, true);
		arrowView.setImageDrawable(new BitmapDrawable(resizedBitmap));
	}

	@Override
	public void onProviderDisabled(String arg0) {
		//no-op

	}

	@Override
	public void onProviderEnabled(String provider) {
		//no-op
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		//no-op

	}

	   @Override
	        public void onAccuracyChanged(Sensor sensor, int accuracy) {
	            // no-op
	        }

	        @Override
	        public void onSensorChanged(SensorEvent event) {
	            if (event.sensor == orientSensor) {
	                lastOrientation  = event.values[0];
	                updateArrow();

	            }
	        }
}