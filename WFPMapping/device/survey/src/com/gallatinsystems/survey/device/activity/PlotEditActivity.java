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
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.gallatinsystems.survey.device.R;
import com.gallatinsystems.survey.device.dao.SurveyDbAdapter;
import com.gallatinsystems.survey.device.util.ConstantUtil;

/**
 * create or edit a new Plot record. This activity just allows editing of the
 * plot metadata fields (i.e. name & description), not the points within that plot.
 * 
 * TODO: include current userID?
 * 
 * @author Christopher Fagiani
 * 
 */
public class PlotEditActivity extends Activity {
	private EditText displayName;
	private EditText description;
	private Long plotId;
	private SurveyDbAdapter databaseAdaptor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.plotedit);

		displayName = (EditText) findViewById(R.id.displayNameField);
		description = (EditText) findViewById(R.id.descField);

		databaseAdaptor = new SurveyDbAdapter(this);
		

		Button saveButton = (Button) findViewById(R.id.confirm);

		plotId = savedInstanceState != null ? savedInstanceState
				.getLong(ConstantUtil.ID_KEY) : null;
		if (plotId == null || plotId == 0L) {
			Bundle extras = getIntent().getExtras();
			plotId = extras != null ? new Long(
					extras.getString(ConstantUtil.ID_KEY)) : null;
		}
		

		saveButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				setResult(RESULT_OK);
				finish();
			}
		});
	}

	/**
	 * put loaded data into the views for display
	 */
	private void populateFields() {
		if (plotId != null) {
			Cursor plot = databaseAdaptor.findPlot(plotId);
			startManagingCursor(plot);
			displayName.setText(plot.getString(plot
					.getColumnIndexOrThrow(SurveyDbAdapter.DISP_NAME_COL)));
			description.setText(plot.getString(plot
					.getColumnIndexOrThrow(SurveyDbAdapter.DESC_COL)));
		}
	}

	/**
	 * sets the id of the selected plot (if there is one) in the bundle
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (outState != null && plotId != null) {
			outState.putLong(ConstantUtil.ID_KEY, plotId);
		}
	}

	@Override
	protected void onPause() {		
		saveState();
		if (databaseAdaptor != null) {
			databaseAdaptor.close();
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		databaseAdaptor.open();
		populateFields();
	}

	protected void onDestroy() {
		super.onDestroy();
		
	}

	/**
	 * save the name and description to the db
	 */
	private void saveState() {
		String name = displayName.getText().toString();
		String desc = description.getText().toString();
		databaseAdaptor.createOrUpdatePlot(plotId, name, desc, null);
	}
}
