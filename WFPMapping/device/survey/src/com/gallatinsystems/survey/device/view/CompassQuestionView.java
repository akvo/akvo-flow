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

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.gallatinsystems.survey.device.R;
import com.gallatinsystems.survey.device.domain.Question;

/**
 * Question that supports recording the current device heading using the compass
 * sensor. Obviously, use of this question type depends on the device having the
 * proper sensor.
 * 
 * @author Christopher Fagiani
 * 
 */
public class CompassQuestionView extends QuestionView implements
		OnClickListener {

	private EditText headingEdit;
	private Button captureButton;
	private Sensor orientationSensor;
	private SensorManager sensorMgr;

	public CompassQuestionView(Context context, Question q, String defaultLang,
			String[] langs, boolean readOnly) {
		super(context, q, defaultLang, langs, readOnly);
		sensorMgr = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);
		if (sensorMgr != null) {
			orientationSensor = sensorMgr
					.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		}
		init();
	}

	protected void init() {
		Context context = getContext();
		headingEdit = new EditText(context);
		headingEdit.setWidth(screenWidth / 2);
		addView(headingEdit);
		if (sensorMgr != null) {
			captureButton = new Button(context);
			captureButton.setText(R.string.captureheading);
			captureButton.setOnClickListener(this);
			captureButton.setWidth(screenWidth - 50);
			addView(captureButton);
		}
	}

	@Override
	public void onClick(View v) {
		if (v == captureButton) {
			if (sensorMgr != null) {
				sensorMgr.registerListener(new SensorEventListener() {
					@Override
					public void onSensorChanged(SensorEvent event) {
						if (event.sensor == orientationSensor) {
							headingEdit.setText("" + event.values[0]);
							sensorMgr.unregisterListener(this);
						}
					}

					@Override
					public void onAccuracyChanged(Sensor sensor, int accuracy) {
						// no-op
					}
				}, orientationSensor, SensorManager.SENSOR_DELAY_NORMAL);
			}
		}
	}

}
