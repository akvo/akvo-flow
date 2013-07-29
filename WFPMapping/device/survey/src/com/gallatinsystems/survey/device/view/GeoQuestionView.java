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

package com.gallatinsystems.survey.device.view;

import java.text.DecimalFormat;
import java.util.StringTokenizer;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.gallatinsystems.survey.device.R;
import com.gallatinsystems.survey.device.domain.Question;
import com.gallatinsystems.survey.device.domain.QuestionResponse;
import com.gallatinsystems.survey.device.util.ConstantUtil;
import com.gallatinsystems.survey.device.util.StringUtil;
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
		LocationListener, OnFocusChangeListener {

	private static final int DEFAULT_WIDTH = 200;
	private static final float UNKNOWN_ACCURACY = 99999999f;
	private static final float ACCURACY_THRESHOLD = 25f;
	private static final String DELIM = "|";
	private Button geoButton;
	private TextView latLabel;
	private EditText latField;
	private TextView lonLabel;
	private EditText generatedCodeField;
	private TextView generatedCodeLabel;
	private EditText lonField;
	private TextView elevationLabel;
	private EditText elevationField;
	private TextView statusIndicator;
	private TextView searchingIndicator;
	private float lastAccuracy;
	private Resources res;
	private boolean needUpdate = false;
	private boolean searching = false;
	private boolean generateCode;

	public GeoQuestionView(Context context, Question q, String defaultLang,
			String[] langCodes, boolean readOnly) {
		super(context, q, defaultLang, langCodes, readOnly);
		// TODO: parameterize (add to Question ?)
		this.generateCode = true;
		init();
	}

	protected void init() {
		Context context = getContext();
		TableRow tr = new TableRow(context);

		TableLayout innerTable = new TableLayout(context);
		TableRow innerRow = new TableRow(context);

		DigitsKeyListener numericListener = new DigitsKeyListener(true, true);
		res = getResources();
		statusIndicator = new TextView(context);
		statusIndicator.setText(res.getString(R.string.accuracy) + ": ");
		statusIndicator.setTextColor(Color.WHITE);

		searchingIndicator = new TextView(context);
		searchingIndicator.setText("");
		searchingIndicator.setTextColor(Color.WHITE);
		
		latField = new EditText(context);
		latField.setWidth(screenWidth / 2);
		latField.setOnFocusChangeListener(this);
		latField.setKeyListener(numericListener);

		latLabel = new TextView(context);
		latLabel.setText(R.string.lat);

		innerRow.addView(latLabel);
		innerRow.addView(latField);

		innerTable.addView(innerRow);
		innerRow = new TableRow(context);

		lonField = new EditText(context);
		lonField.setWidth(screenWidth / 2);
		lonField.setKeyListener(numericListener);
		lonField.setOnFocusChangeListener(this);

		lonLabel = new TextView(context);
		lonLabel.setText(R.string.lon);

		innerRow.addView(lonLabel);
		innerRow.addView(lonField);
		innerRow.addView(searchingIndicator);

		innerTable.addView(innerRow);
		innerRow = new TableRow(context);

		elevationField = new EditText(context);
		elevationField.setWidth(screenWidth / 2);
		elevationField.setKeyListener(numericListener);
		elevationField.setOnFocusChangeListener(this);
		elevationLabel = new TextView(context);
		elevationLabel.setText(R.string.elevation);

		innerRow.addView(elevationLabel);
		innerRow.addView(elevationField);
		innerRow.addView(statusIndicator);

		innerTable.addView(innerRow);

		tr.addView(innerTable);
		addView(tr);
		tr = new TableRow(context);

		geoButton = new Button(context);
		geoButton.setText(R.string.getgeo);
		geoButton.setOnClickListener(this);
		geoButton.setWidth(screenWidth - 50);
		tr.addView(geoButton);

		addView(tr);

		if (generateCode) {
			generatedCodeLabel = new TextView(context);
			generatedCodeLabel.setText(R.string.generatedcode);
			generatedCodeField = new EditText(context);
			generatedCodeField.setWidth(DEFAULT_WIDTH);
			tr = new TableRow(context);
			tr.addView(generatedCodeLabel);
			addView(tr);
			tr = new TableRow(context);
			tr.addView(generatedCodeField);
			addView(tr);
		}

		if (readOnly) {
			latField.setFocusable(false);
			lonField.setFocusable(false);
			elevationField.setFocusable(false);
			if (generatedCodeField != null) {
				generatedCodeField.setFocusable(false);
			}
			geoButton.setEnabled(false);
		}
		if (question.isLocked()) {
			latField.setFocusable(false);
			lonField.setFocusable(false);
			elevationField.setFocusable(false);
			if (generatedCodeField != null) {
				generatedCodeField.setFocusable(false);
			}
		}
	}

	/**
	 * When the user clicks the "Populate Geo" button, start listening for
	 * location updates
	 */
	public void onClick(View v) {
		LocationManager locMgr = (LocationManager) getContext()
				.getSystemService(Context.LOCATION_SERVICE);
		if (locMgr.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//			Location loc = locMgr
//					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//			//if (loc != null) {
				// if the last location is accurate, then we can use it
				// TODO: also check if it is too old to be useful
				//if (loc.hasAccuracy() && loc.getAccuracy() < ACCURACY_THRESHOLD) {
				//	populateLocation(loc);
				//}
			//}
			statusIndicator.setText(res.getString(R.string.accuracy) + ": ");
			statusIndicator.setTextColor(Color.WHITE);
			
			latField.setText("");
			lonField.setText("");
			elevationField.setText("");
			if (generatedCodeField != null) {
				generatedCodeField.setText("");
			}
			needUpdate = true;
			searching = true;
			searchingIndicator.setText(R.string.searching);
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
		if (loc.hasAccuracy()) {
			statusIndicator.setText(res.getString(R.string.accuracy) + ": " + new DecimalFormat("#").format(loc.getAccuracy()) + "m");
			if (loc.getAccuracy() <= ACCURACY_THRESHOLD) {
				statusIndicator.setTextColor(Color.GREEN);
			} else {
				statusIndicator.setTextColor(Color.RED);
			}
		}
		latField.setText(loc.getLatitude() + "");
		lonField.setText(loc.getLongitude() + "");
		 //elevation is in meters, even one decimal is way more than GPS precision
		elevationField.setText(new DecimalFormat("#.#").format(loc.getAltitude()));
		if (generateCode) {
			generatedCodeField.setText(generateCode(loc.getLatitude(),
					loc.getLongitude()));
		}
		setResponse();
	}

	/**
	 * generates a unique code based on the lat/lon passed in. Current algorithm
	 * returns the concatenation of the integer portion of 1000 times absolute
	 * value of lat and lon in base 36
	 *
	 * @param lat
	 * @param lon
	 * @return
	 */
	private String generateCode(double lat, double lon) {
		Long code = Long.parseLong((int) ((Math.abs(lat) * 100000d)) + ""
				+ (int) ((Math.abs(lon) * 10000d)));
		return Long.toString(code, 36);
	}

	/**
	 * clears out the UI fields
	 */
	@Override
	public void resetQuestion(boolean fireEvent) {
		super.resetQuestion(fireEvent);
		latField.setText("");
		lonField.setText("");
		elevationField.setText("");
		if (generatedCodeField != null) {
			generatedCodeField.setText("");
		}
		searchingIndicator.setText("");
		statusIndicator.setText(res.getString(R.string.accuracy) + ": ");
		statusIndicator.setTextColor(Color.WHITE);
		
//		if (statusIndicator != null) {
//			statusIndicator.setVisibility(View.GONE);
//		}
	}

	/**
	 * restores the file path for the file and turns on the complete icon if the
	 * file exists
	 */
	@Override
	public void rehydrate(QuestionResponse resp) {
		super.rehydrate(resp);
		if (resp != null) {
			if (resp.getValue() != null) {
				StringTokenizer strTok = new StringTokenizer(resp.getValue(),
						DELIM);
				if (strTok.countTokens() >= 3) {
					latField.setText(strTok.nextToken());
					lonField.setText(strTok.nextToken());
					elevationField.setText(strTok.nextToken());
					if (generatedCodeField != null && strTok.hasMoreTokens()) {
						generatedCodeField.setText(strTok.nextToken());
					}
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

			// If we are below the accuracy treshold, stop listening for updates.
			// This means that after the geolocation is 'green', it stays the same, 
			// otherwise it keeps on listening
			if (currentAccuracy <= ACCURACY_THRESHOLD) {
				LocationManager locMgr = (LocationManager) getContext()
						.getSystemService(Context.LOCATION_SERVICE);
				locMgr.removeUpdates(this);
				searchingIndicator.setText(R.string.ready);
			}

			// if the location reading is more accurate than the last, update
			// the view
			if (lastAccuracy > currentAccuracy || needUpdate) {
				lastAccuracy = currentAccuracy;
				needUpdate = false;
				populateLocation(location);
			}
		} else if (needUpdate) {
			needUpdate = true;
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

	/**
	 * used to capture lat/lon/elevation if manually typed
	 */
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (!hasFocus) {
			if (generatedCodeField != null
					&& (!StringUtil
							.isNullOrEmpty(latField.getText().toString()) && !StringUtil
							.isNullOrEmpty(lonField.getText().toString()))) {
				generatedCodeField.setText(generateCode(
						Double.parseDouble(latField.getText().toString()),
						Double.parseDouble(lonField.getText().toString())));
			}
			setResponse();
		}
	}

	private void setResponse() {
		if (!generateCode) {
			setResponse(new QuestionResponse(latField.getText() + DELIM
					+ lonField.getText() + DELIM + elevationField.getText(),
					ConstantUtil.GEO_RESPONSE_TYPE, getQuestion().getId()));
		} else {
			setResponse(new QuestionResponse(latField.getText() + DELIM
					+ lonField.getText() + DELIM + elevationField.getText()
					+ DELIM + generatedCodeField.getText(),
					ConstantUtil.GEO_RESPONSE_TYPE, getQuestion().getId()));
		}
	}

}
