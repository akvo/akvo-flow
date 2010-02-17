package com.gallatinsystems.survey.device.view;

import java.util.StringTokenizer;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.gallatinsystems.survey.device.R;
import com.gallatinsystems.survey.device.domain.Question;
import com.gallatinsystems.survey.device.domain.QuestionResponse;
import com.gallatinsystems.survey.device.util.ViewUtil;

/**
 * Question that can handle geographic location input. This question can also
 * listen to location updates from the GPS sensor on the device.
 * 
 * 
 * @author Christopher Fagiani
 * 
 */
public class GeoQuestionView extends QuestionView implements OnClickListener,
		LocationListener {

	private static final int DEFAULT_WIDTH = 200;
	private static final float UNKNOWN_ACCURACY = 99999999f;
	private static final float ACCURACY_THRESHOLD = 100f;
	private static final String DELIM = "|";
	private Button geoButton;
	private TextView latLabel;
	private EditText latField;
	private TextView lonLabel;
	private EditText lonField;
	private TextView elevationLabel;
	private EditText elevationField;
	private float lastAccuracy;
	private boolean needUpdate = false;

	public GeoQuestionView(Context context, Question q) {
		super(context, q);
		init();
	}

	protected void init() {
		Context context = getContext();
		TableRow tr = new TableRow(context);

		TableLayout innerTable = new TableLayout(context);
		TableRow innerRow = new TableRow(context);

		latField = new EditText(context);
		latField.setWidth(DEFAULT_WIDTH);
		latLabel = new TextView(context);
		latLabel.setText(R.string.lat);

		innerRow.addView(latLabel);
		innerRow.addView(latField);

		innerTable.addView(innerRow);
		innerRow = new TableRow(context);

		lonField = new EditText(context);
		lonField.setWidth(DEFAULT_WIDTH);
		lonLabel = new TextView(context);
		lonLabel.setText(R.string.lon);

		innerRow.addView(lonLabel);
		innerRow.addView(lonField);

		innerTable.addView(innerRow);
		innerRow = new TableRow(context);

		elevationField = new EditText(context);
		elevationField.setWidth(DEFAULT_WIDTH);
		elevationLabel = new TextView(context);
		elevationLabel.setText(R.string.elevation);

		innerRow.addView(elevationLabel);
		innerRow.addView(elevationField);

		innerTable.addView(innerRow);

		tr.addView(innerTable);
		addView(tr);
		tr = new TableRow(context);

		geoButton = new Button(context);
		geoButton.setText(R.string.getgeo);
		geoButton.setOnClickListener(this);
		tr.addView(geoButton);
		addView(tr);

	}

	/**
	 * When the user clicks the "Populate Geo" button, start listening for
	 * location updates
	 */
	public void onClick(View v) {
		LocationManager locMgr = (LocationManager) getContext()
				.getSystemService(Context.LOCATION_SERVICE);
		if (locMgr.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			Location loc = locMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if(loc!= null){
				//if the last location is accurate, then we can use it
				if(loc.hasAccuracy() && loc.getAccuracy()< ACCURACY_THRESHOLD){
					populateLocation(loc);
				}
			}
			needUpdate = true;
			lastAccuracy = UNKNOWN_ACCURACY;
			locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
					this);
		} else {
			// we can't turn GPS on directly, the best we can do is launch the
			// settings page
			ViewUtil.showGPSDialog(getContext());
		}

	}

	/**
	 * populates the fields on the UI with the location info from the event
	 * 
	 * @param loc
	 */
	private void populateLocation(Location loc) {
		latField.setText(loc.getLatitude() + "");
		lonField.setText(loc.getLongitude() + "");
		elevationField.setText(loc.getAltitude() + "");
		setResponse(new QuestionResponse(loc.getLatitude() + DELIM
				+ loc.getLongitude() + DELIM + loc.getAltitude(),
				QuestionResponse.GEO_TYPE, getQuestion().getId()));
	}

	/**
	 * clears out the UI fields
	 */
	public void resetQuestion() {
		super.resetQuestion();
		latField.setText("");
		lonField.setText("");
		elevationField.setText("");
	}

	/**
	 * restores the file path for the file and turns on the complete icon if the
	 * file exists
	 */
	public void rehydrate(QuestionResponse resp) {
		super.rehydrate(resp);
		if (resp != null) {
			if (resp.getValue() != null) {
				StringTokenizer strTok = new StringTokenizer(resp.getValue(),
						DELIM);
				if (strTok.countTokens() == 3) {
					latField.setText(strTok.nextToken());
					lonField.setText(strTok.nextToken());
					elevationField.setText(strTok.nextToken());
				}

			}

		}
	}

	@Override
	public void questionComplete(Bundle data) {
		// completeIcon.setVisibility(View.VISIBLE);
	}

	/**
	 * called by the system when it gets location updates.
	 */
	public void onLocationChanged(Location location) {
		float currentAccuracy = location.getAccuracy();
		// if accuracy is 0 then the gps has no idea where we're at
		if (currentAccuracy > 0) {

			// if we're decreasing in accuracy or staying the same, or if we're
			// below the accuracy threshold, stop listening for updates
			if (currentAccuracy >= lastAccuracy
					|| currentAccuracy <= ACCURACY_THRESHOLD) {
				LocationManager locMgr = (LocationManager) getContext()
						.getSystemService(Context.LOCATION_SERVICE);
				locMgr.removeUpdates(this);
			}

			// if the location reading is more accurate than the last, update
			// the view
			if (lastAccuracy > currentAccuracy || needUpdate) {
				lastAccuracy = currentAccuracy;
				needUpdate = false;
				populateLocation(location);
			}
		}else if (needUpdate){
			needUpdate = false;
			populateLocation(location);
		}
	}

	public void onProviderDisabled(String provider) {
		// no op. needed for LocationListener interface

	}

	public void onProviderEnabled(String provider) {
		// no op. needed for LocationListener interface

	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// no op. needed for LocationListener interface
	}
}
