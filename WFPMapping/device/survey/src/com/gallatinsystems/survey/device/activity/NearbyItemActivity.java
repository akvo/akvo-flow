package com.gallatinsystems.survey.device.activity;

import java.util.ArrayList;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.gallatinsystems.survey.device.R;
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

	private static int NEARBY_DETAIL_ACTIVITY = 1;
	private LocationManager locMgr;
	private Criteria locationCriteria;
	private ProgressDialog progressDialog;
	private ArrayList<PointOfInterestDto> pointsOfInterest;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nearbyitem);
		locMgr = (LocationManager) getSystemService(LOCATION_SERVICE);
		locationCriteria = new Criteria();
		locationCriteria.setAccuracy(Criteria.NO_REQUIREMENT);
		progressDialog = ProgressDialog.show(this, "Please wait...",
				"Loading nearby items...", true);

		String provider = locMgr.getBestProvider(locationCriteria, true);
		if (provider != null) {
			Location loc = locMgr.getLastKnownLocation(provider);
			if (loc != null) {
				loadData(loc.getLatitude(), loc.getLongitude());
			} else {
				locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER,
						1000, 0, this);
			}
		}

	}

	/**
	 * loads the data from the server.
	 * TODO: make this work if offline too by loading from db
	 * @param lat
	 * @param lon
	 */
	private void loadData(Double lat, Double lon) {
		pointsOfInterest = PointOfInterestService.getNearbyAccessPoints(lat,
				lon);
		if (pointsOfInterest != null) {
			setListAdapter(new ArrayAdapter<PointOfInterestDto>(this,
					R.layout.itemlistrow, pointsOfInterest));
		}
		progressDialog.dismiss();
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

	@Override
	public void onLocationChanged(Location location) {
		locMgr.removeUpdates(this);
		loadData(location.getLatitude(), location.getLongitude());
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