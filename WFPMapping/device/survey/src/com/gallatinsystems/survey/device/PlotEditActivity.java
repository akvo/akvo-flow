package com.gallatinsystems.survey.device;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.gallatinsystems.survey.device.dao.SurveyDbAdapter;
import com.gallatinsystems.survey.device.util.ConstantUtil;

/**
 * create or edit a new Plot record
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
		setContentView(R.layout.plotedit);

		displayName = (EditText) findViewById(R.id.displayNameField);
		description = (EditText) findViewById(R.id.descField);

		databaseAdaptor = new SurveyDbAdapter(this);
		databaseAdaptor.open();

		Button saveButton = (Button) findViewById(R.id.confirm);

		plotId = savedInstanceState != null ? savedInstanceState
				.getLong(ConstantUtil.ID_KEY) : null;
		if (plotId == null) {
			Bundle extras = getIntent().getExtras();
			plotId = extras != null ? new Long(extras
					.getString(ConstantUtil.ID_KEY)) : null;
		}
		populateFields();

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

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (outState != null && plotId != null) {
			outState.putLong(ConstantUtil.ID_KEY, plotId);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		saveState();
	}

	@Override
	protected void onResume() {
		super.onResume();
		populateFields();
	}

	protected void onDestroy() {
		super.onDestroy();
		if (databaseAdaptor != null) {
			databaseAdaptor.close();
		}
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
