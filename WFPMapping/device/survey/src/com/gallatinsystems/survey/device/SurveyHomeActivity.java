package com.gallatinsystems.survey.device;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.gallatinsystems.survey.device.dao.SurveyDbAdapter;

/**
 * Activity to render the survey home screen. It will list all available
 * sub-activities and start them as needed.
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyHomeActivity extends Activity implements OnClickListener {

	public static final int SURVEY_ACTIVITY = 1;
	public static final int LIST_USER_ACTIVITY = 2;
	public static final int SETTINGS_ACTIVITY = 3;
	private String currentUserId;
	private String currentName;
	private TextView userField;
	private TextView syncField;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		ImageButton mapButton = (ImageButton) findViewById(R.id.mapSurveyButton);
		ImageButton wpButton = (ImageButton) findViewById(R.id.wpSurveyButton);
		ImageButton hhButton = (ImageButton) findViewById(R.id.hhSurveyButton);
		ImageButton pubButton = (ImageButton) findViewById(R.id.pubSurveyButton);
		ImageButton userButton = (ImageButton) findViewById(R.id.usersButton);
		ImageButton settingsButton = (ImageButton) findViewById(R.id.settingsButton);
		userField = (TextView) findViewById(R.id.currentUserField);
		syncField = (TextView) findViewById(R.id.lastSyncField);

		// TODO: store/fetch current user from DB?
		currentUserId = savedInstanceState != null ? savedInstanceState
				.getString(SurveyDbAdapter.USER_ID_COL) : null;
		currentName = savedInstanceState != null ? savedInstanceState
				.getString(SurveyDbAdapter.DISP_NAME_COL) : null;
		if (currentName != null) {
			populateFields();
		}

		startSyncService();

		mapButton.setOnClickListener(this);
		wpButton.setOnClickListener(this);
		hhButton.setOnClickListener(this);
		pubButton.setOnClickListener(this);
		userButton.setOnClickListener(this);
		settingsButton.setOnClickListener(this);
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
	public void onClick(View v) {
		int clickedId = v.getId();
		if (clickedId == R.id.usersButton) {
			Intent i = new Intent(v.getContext(), ListUserActivity.class);
			startActivityForResult(i, LIST_USER_ACTIVITY);
		} else if (clickedId == R.id.settingsButton) {
			// TODO do we want this to be StartActivityForResult so we can
			// update last sync time?
			// Intent i = new Intent(v.getContext(), DataSyncActivity.class);
			// startActivity(i);

			Intent i = new Intent(v.getContext(), SettingsActivity.class);
			startActivityForResult(i, SETTINGS_ACTIVITY);
			// synchField.setText(R.string.syncinprogress);
		} else {
			if (currentUserId != null) {
				int resourceID = 0;
				switch (clickedId) {
				case R.id.mapSurveyButton:
					resourceID = R.raw.mappingsurvey;
					break;
				case R.id.wpSurveyButton:
					resourceID = R.raw.testsurvey;
					break;
				default:
					resourceID = R.raw.testsurvey;
				}
				Intent i = new Intent(v.getContext(), SurveyViewActivity.class);
				i.putExtra(SurveyViewActivity.SURVEY_RESOURCE_ID, resourceID);
				i.putExtra(SurveyViewActivity.USER_ID, currentUserId);
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
		// databaseAdaptor.createOrUpdateUser(userId, name, email);
	}
}
