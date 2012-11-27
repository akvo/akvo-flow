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

package com.gallatinsystems.survey.device.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

import com.gallatinsystems.survey.device.R;
import com.gallatinsystems.survey.device.util.ConstantUtil;
import com.gallatinsystems.survey.device.util.ViewUtil;

/**
 * this activity is used to calculate water flow. It presents 3 fields (pipe
 * diameter, water velocity and flow rate). The user can fill in any 2 fields
 * and press the "Solve" button to get the value for the third.
 * 
 * @author Christopher Fagiani
 * 
 */
public class WaterflowCalculatorActivity extends Activity implements
		OnClickListener, OnItemSelectedListener {

	private Button solveButton;
	private Button clearButton;
	private Button doneButton;
	private EditText diameterText;
	private EditText velocityText;
	private EditText flowRateText;
	private Spinner diaMetricSpinner;
	private Spinner velocityMetricSpinner;
	private Spinner flowRateMetricSpinner;
	private String[] lengthConversion;
	private String[] velocityConversion;
	private String[] flowConversion;
	private int selectedLengthMetric;
	private int selectedVelMetric;
	private EditText lastSolvedText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.waterflowcalc);
		doneButton = (Button) findViewById(R.id.donebutton);
		solveButton = (Button) findViewById(R.id.solvebutton);
		clearButton = (Button) findViewById(R.id.clearbutton);
		diameterText = (EditText) findViewById(R.id.diaedit);
		velocityText = (EditText) findViewById(R.id.velocityedit);
		flowRateText = (EditText) findViewById(R.id.flowrateedit);
		diaMetricSpinner = (Spinner) findViewById(R.id.diametricspinner);
		velocityMetricSpinner = (Spinner) findViewById(R.id.velocitymetricspinner);
		flowRateMetricSpinner = (Spinner) findViewById(R.id.flowratemetricspinner);
		Resources res = getResources();
		lengthConversion = res.getStringArray(R.array.lengthconversion);
		velocityConversion = res.getStringArray(R.array.velocityconversion);
		flowConversion = res.getStringArray(R.array.flowconversion);
		initializeViews(getIntent().getStringExtra(ConstantUtil.MODE_KEY));
	}

	/**
	 * loads options into the spinners
	 */
	private void initializeViews(String mode) {
		lastSolvedText = null;
		DigitsKeyListener digitKeyListener = new DigitsKeyListener(false, true);
		diameterText.setKeyListener(digitKeyListener);
		velocityText.setKeyListener(digitKeyListener);
		flowRateText.setKeyListener(digitKeyListener);
		diaMetricSpinner.setAdapter(ArrayAdapter.createFromResource(this,
				R.array.lengthmetric, android.R.layout.simple_spinner_item));
		diaMetricSpinner.setOnItemSelectedListener(this);
		velocityMetricSpinner.setAdapter(ArrayAdapter.createFromResource(this,
				R.array.velocitymetric, android.R.layout.simple_spinner_item));
		velocityMetricSpinner.setOnItemSelectedListener(this);
		flowRateMetricSpinner.setAdapter(ArrayAdapter.createFromResource(this,
				R.array.flowratemetric, android.R.layout.simple_spinner_item));
		solveButton.setOnClickListener(this);
		clearButton.setOnClickListener(this);
		doneButton.setOnClickListener(this);
		selectedLengthMetric = 0;
		selectedVelMetric = 0;
		if (mode != null
				&& ConstantUtil.SURVEY_RESULT_MODE.equalsIgnoreCase(mode)) {
			doneButton.setVisibility(View.VISIBLE);
		} else {
			doneButton.setVisibility(View.GONE);
		}

	}

	/**
	 * extracts the values from the input controls and calls solve
	 */
	@Override
	public void onClick(View v) {
		if (v == clearButton) {
			diameterText.setText("");
			velocityText.setText("");
			flowRateText.setText("");
			diaMetricSpinner.setSelection(0);
			velocityMetricSpinner.setSelection(0);
			flowRateMetricSpinner.setSelection(0);
			selectedLengthMetric = 0;
			selectedVelMetric = 0;
		} else if (v == solveButton) {
			Double dia = extractDouble(diameterText, diaMetricSpinner
					.getSelectedItemPosition(), lengthConversion);
			Double vel = extractDouble(velocityText, velocityMetricSpinner
					.getSelectedItemPosition(), velocityConversion);
			Double flow = extractDouble(flowRateText, flowRateMetricSpinner
					.getSelectedItemPosition(), flowConversion);

			try {
				Double result = solve(dia, vel, flow);
				if (dia == null) {
					setText(result, diameterText, diaMetricSpinner
							.getSelectedItemPosition(), lengthConversion);
				} else if (vel == null) {
					setText(result, velocityText, velocityMetricSpinner
							.getSelectedItemPosition(), velocityConversion);
				} else {
					setText(result, flowRateText, flowRateMetricSpinner
							.getSelectedItemPosition(), flowConversion);
				}
			} catch (Exception e) {
				// if we get a NPE, then more than 1 entry was null
				ViewUtil.showConfirmDialog(R.string.invalidinput,
						R.string.onlyonecanbeblank, this);
			}
		} else if (v == doneButton) {
			Intent resultData = new Intent();
			if (lastSolvedText != null) {
				resultData.putExtra(ConstantUtil.CALC_RESULT_KEY,
						lastSolvedText.getText().toString());
			}
			setResult(RESULT_OK, resultData);
			finish();
		}
	}

	/**
	 * sets the value passed in in the text box passed in, converting the value
	 * to the metric indicated
	 * 
	 * @param txt
	 * @param metricArrayIdx
	 * @param conversionArray
	 */
	private void setText(Double val, EditText txt, int metricArrayIdx,
			String[] conversionArray) {
		Double conversionRate = new Double(conversionArray[metricArrayIdx]
				.trim());
		if (conversionRate != 0) {
			txt.setText((val / conversionRate) + "");
		} else {
			txt.setText("");
		}
		lastSolvedText = txt;
	}

	/**
	 * returns the double value contained in the text box or null if blank.
	 * Units will be converted into meter-based measures (m, m/s, cubic m /s)
	 * 
	 * @param txt
	 * @return
	 */
	private Double extractDouble(EditText txt, int metricArrayIdx,
			String[] conversionArray) {
		String text = null;
		if (txt.getText() != null) {
			text = txt.getText().toString();
			if (text != null && text.trim().length() > 0) {
				return (new Double(text.trim()) * new Double(
						conversionArray[metricArrayIdx].trim()));
			}
		}
		return null;
	}

	/**
	 * solves the equation: flowRate = 1/4 * PI * (diameter^2) * velocity *.001
	 * 
	 * 
	 * @param diameter
	 *            - pipe diameter in m
	 * @param velocity
	 *            - water velocity in m/s
	 * @param flowRate
	 *            - flow rate in l/s
	 * @return - whichever input value was null, that is the value returned
	 */
	public Double solve(Double diameter, Double velocity, Double flowRate) {
		if (diameter == null && flowRate != null && velocity != null) {
			return Math.sqrt((4 * flowRate * 0.001) / (Math.PI * velocity));
		} else if (velocity == null && flowRate != null && diameter != null) {
			return (4 * flowRate * 0.001) / (Math.PI * Math.pow(diameter, 2d));
		} else {
			return (diameter * diameter * Math.PI * .25d * velocity) / 0.001;
		}
	}

	/**
	 * converts values based on the metric selected
	 */
	@Override
	public void onItemSelected(AdapterView<?> spinner, View selection,
			int position, long id) {
		if (spinner == diaMetricSpinner) {
			Double val = extractDouble(diameterText, selectedLengthMetric,
					lengthConversion);
			if (val != null) {
				// now val is in m/s
				Double newConversion = new Double(lengthConversion[position]
						.trim());
				diameterText.setText((val / newConversion) + "");
			}
			selectedLengthMetric = position;
		} else if (spinner == velocityMetricSpinner) {
			Double val = extractDouble(velocityText, selectedVelMetric,
					velocityConversion);
			if (val != null) {
				// now val is in m/s
				Double newConversion = new Double(velocityConversion[position]
						.trim());
				velocityText.setText((val / newConversion) + "");
			}
			selectedVelMetric = position;
		}

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// no-op

	}
}
