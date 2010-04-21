package com.gallatinsystems.survey.device.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
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
		LocationListener {

	private LocationManager locMgr;
	private Criteria locationCriteria;
	private TextView communityCodeField;
	private TextView distanceField;
	private ImageView arrowView;
	private AccessPointDto accessPoint;
	private Bitmap arrowBitmap;
	private Location apLocation;
	private float lastBearing;

	private static final float MIN_CHANGE = 2f;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		locMgr = (LocationManager) getSystemService(LOCATION_SERVICE);
		locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0,
				this);
		lastBearing = -999;
		locationCriteria = new Criteria();
		locationCriteria.setAccuracy(Criteria.NO_REQUIREMENT);
		setContentView(R.layout.nearbydetail);

		communityCodeField = (TextView) findViewById(R.id.communityCodeField);
		distanceField = (TextView) findViewById(R.id.distanceField);
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
	 * put loaded data into the views for display
	 */
	private void populateFields() {
		if (accessPoint != null) {
			communityCodeField.setText(accessPoint.getCommunityCode());
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
		// set the distance value
		distanceField.setText(apLocation.distanceTo(loc) + "");
		// rotate the compass arrow
		Matrix matrix = new Matrix();
		float newBearing = loc.bearingTo(apLocation);
		// only update the bearing and the corresponding image representation of
		// it if it changed more than MIN_CHANGE degrees
		// so we're not always manipulating the image
		if (Math.abs(newBearing - lastBearing) >= MIN_CHANGE) {
			matrix.postRotate(loc.bearingTo(apLocation));
			Bitmap resizedBitmap = Bitmap.createBitmap(arrowBitmap, 0, 0, 30,
					30, matrix, true);
			arrowView.setImageDrawable(new BitmapDrawable(resizedBitmap));
			lastBearing = newBearing;
		}

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
}