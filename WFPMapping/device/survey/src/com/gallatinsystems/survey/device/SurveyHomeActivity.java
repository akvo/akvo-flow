package com.gallatinsystems.survey.device;

import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.gallatinsystems.survey.device.dao.SurveyDbAdapter;
import com.gallatinsystems.survey.device.view.HomeMenuViewAdapter;

/**
 * Activity to render the survey home screen. It will list all available
 * sub-activities and start them as needed.
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyHomeActivity extends Activity implements OnItemClickListener {

	private static final String LABEL = "label";
	private static final String IMG = "img";

	public static final int SURVEY_ACTIVITY = 1;
	public static final int LIST_USER_ACTIVITY = 2;
	public static final int SETTINGS_ACTIVITY = 3;
	private String currentUserId;
	private String currentName;
	private TextView userField;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);

		userField = (TextView) findViewById(R.id.currentUserField);

		GridView grid = (GridView) findViewById(R.id.gridview);
		grid.setAdapter(new HomeMenuViewAdapter(this));
		grid.setOnItemClickListener(this);

		// TODO: store/fetch current user from DB?
		currentUserId = savedInstanceState != null ? savedInstanceState
				.getString(SurveyDbAdapter.USER_ID_COL) : null;
		currentName = savedInstanceState != null ? savedInstanceState
				.getString(SurveyDbAdapter.DISP_NAME_COL) : null;
		if (currentName != null) {
			populateFields();
		}
		startSyncService();
	}

	private HashMap<String, Object> createMap(String label, Drawable img) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put(LABEL, label);
		map.put(IMG, img);
		return map;
	}

	/**
	 * starts up the data sync service
	 */
	private void startSyncService() {
		Intent i = new Intent(this, DataSyncService.class);
		i.putExtra(DataSyncService.TYPE_KEY, DataSyncService.SEND);
		getApplicationContext().startService(i);
	}

	/**
	 * handles the button presses.
	 */
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		String selected = HomeMenuViewAdapter.operations[position];
		v.setSelected(false);
		if (selected.equals(HomeMenuViewAdapter.USER_OP)) {
			Intent i = new Intent(v.getContext(), ListUserActivity.class);
			startActivityForResult(i, LIST_USER_ACTIVITY);
		} else if (selected.equals(HomeMenuViewAdapter.CONF_OP)) {
			Intent i = new Intent(v.getContext(), SettingsActivity.class);
			startActivityForResult(i, SETTINGS_ACTIVITY);
		} else {
			if (currentUserId != null) {
				int resourceID = 0;
				// TODO: load survey ID from DB
				String surveyId = "1";
				if (selected.equals(HomeMenuViewAdapter.MAP_OP)) {
					resourceID = R.raw.mappingsurvey;
					surveyId = "1";
				} else if (selected.equals(HomeMenuViewAdapter.WPS_OP)) {
					resourceID = R.raw.testsurvey;
					surveyId = "2";
				} else if (selected.equals(HomeMenuViewAdapter.HHS_OP)) {
					resourceID = R.raw.testsurvey;
					surveyId = "3";
				} else if (selected.equals(HomeMenuViewAdapter.PUBS_OP)) {
					resourceID = R.raw.testsurvey;
					surveyId = "4";
				}
				Intent i = new Intent(v.getContext(), SurveyViewActivity.class);
				i.putExtra(SurveyViewActivity.SURVEY_RESOURCE_ID, resourceID);
				i.putExtra(SurveyViewActivity.USER_ID, currentUserId);
				i.putExtra(SurveyViewActivity.SURVEY_ID, surveyId);
				startActivityForResult(i, SURVEY_ACTIVITY);
			} else {
				// if the current user is null, we can't enter survey mode
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(R.string.mustselectuser).setCancelable(true)
						.setPositiveButton("Ok",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.cancel();
									}
								});
				builder.show();
			}
		}
	}

	/**
	 * handles the callbacks from the completed activities. If it was the
	 * user-select activity, we need to get the selected user from the bundle
	 * data and set it in the appropriate member variables.
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		switch (requestCode) {
		case LIST_USER_ACTIVITY:
			if (resultCode == RESULT_OK) {
				Bundle bundle = intent.getExtras();
				if (bundle != null) {
					currentUserId = bundle
							.getString(SurveyDbAdapter.USER_ID_COL);
					currentName = bundle
							.getString(SurveyDbAdapter.DISP_NAME_COL);
					populateFields();
				}
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(SurveyDbAdapter.USER_ID_COL, currentUserId);
		outState.putString(SurveyDbAdapter.DISP_NAME_COL, currentName);
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

	private void populateFields() {
		userField.setText(currentName);
	}

	private void saveState() {
		// TODO: persist current user?
	}
}
