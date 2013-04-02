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
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.gallatinsystems.survey.device.R;
import com.gallatinsystems.survey.device.dao.SurveyDbAdapter;
import com.gallatinsystems.survey.device.domain.Survey;
import com.gallatinsystems.survey.device.exception.PersistentUncaughtExceptionHandler;
import com.gallatinsystems.survey.device.service.ApkUpdateService;
import com.gallatinsystems.survey.device.service.BootstrapService;
import com.gallatinsystems.survey.device.service.DataSyncService;
import com.gallatinsystems.survey.device.service.ExceptionReportingService;
import com.gallatinsystems.survey.device.service.LocationService;
import com.gallatinsystems.survey.device.service.PrecacheService;
import com.gallatinsystems.survey.device.service.SurveyDownloadService;
import com.gallatinsystems.survey.device.util.ConstantUtil;
import com.gallatinsystems.survey.device.util.PropertyUtil;
import com.gallatinsystems.survey.device.util.ViewUtil;
import com.gallatinsystems.survey.device.view.adapter.HomeMenuViewAdapter;

/**
 * Activity to render the survey home screen. It will list all available
 * sub-activities and start them as needed.
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyHomeActivity extends Activity implements OnItemClickListener {

	private static final int DELETE_ID = Menu.FIRST + 1;
	private static final String TAG = "Survey Home Activity";
	public static final int SURVEY_ACTIVITY = 1;
	public static final int LIST_USER_ACTIVITY = 2;
	public static final int SETTINGS_ACTIVITY = 3;
	public static final int PLOTTING_ACTIVITY = 4;
	public static final int PLOT_LIST_ACTIVITY = 5;
	public static final int NEARBY_ACTIVITY = 6;
	public static final int REVIEW_ACTIVITY = 7;
	public static final int WF_CALC_ACTIVITY = 8;
	public static final int SURVEY_STS_HOME_ACTIVITY = 9;
	private String currentUserId;
	private String currentName;
	private TextView userField;
	private HomeMenuViewAdapter menuViewAdapter;
	private PropertyUtil props;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		props = new PropertyUtil(getResources());
		Thread.setDefaultUncaughtExceptionHandler(PersistentUncaughtExceptionHandler
				.getInstance());

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.home);

		boolean includeOptional = true;
		String useOpt = props.getProperty(ConstantUtil.INCLUDE_OPTIONAL_ICONS);
		if (useOpt != null) {
			try {
				includeOptional = Boolean.parseBoolean(useOpt.trim());
			} catch (Exception e) {
				Log.e(TAG, "include optional property is not a boolean: "
						+ useOpt);
			}
		}
		menuViewAdapter = new HomeMenuViewAdapter(this, includeOptional);
		userField = (TextView) findViewById(R.id.currentUserField);

		GridView grid = (GridView) findViewById(R.id.gridview);
		grid.setAdapter(menuViewAdapter);
		grid.setOnItemClickListener(this);
		registerForContextMenu(grid);

		currentUserId = savedInstanceState != null ? savedInstanceState
				.getString(ConstantUtil.ID_KEY) : null;
		currentName = savedInstanceState != null ? savedInstanceState
				.getString(ConstantUtil.DISPLAY_NAME_KEY) : null;

		if (currentUserId == null) {
			loadLastUser();
		}

		startSyncService();
		startService(SurveyDownloadService.class);
		startService(LocationService.class);
		startService(PrecacheService.class);
		startService(BootstrapService.class);
		startService(ApkUpdateService.class);
		startService(ExceptionReportingService.class);
	}

	/**
	 * checks if the user preference to persist logged-in users is set and, if
	 * so, loads the last logged-in user from the DB
	 * 
	 * @return
	 */
	private void loadLastUser() {

		SurveyDbAdapter database = new SurveyDbAdapter(this);
		database.open();
		// first check if they want to keep users logged in
		String val = database
				.findPreference(ConstantUtil.USER_SAVE_SETTING_KEY);
		if (val != null && Boolean.parseBoolean(val)) {
			val = database.findPreference(ConstantUtil.LAST_USER_SETTING_KEY);
			if (val != null && val.trim().length() > 0) {
				currentUserId = val;
				Cursor cur = database.findUser(new Long(val));
				if (cur != null) {
					currentName = cur
							.getString(cur
									.getColumnIndexOrThrow(SurveyDbAdapter.DISP_NAME_COL));
					cur.close();
				}
			}
		}
		database.close();
	}

	/**
	 * presents a single "edit" option when the user long-clicks a list item
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// we only allow delete of surveys so check the view tag
		if (ConstantUtil.SURVEY_OP
				.equals(((AdapterContextMenuInfo) menuInfo).targetView.getTag())) {
			super.onCreateContextMenu(menu, v, menuInfo);
			menu.add(0, DELETE_ID, 0, R.string.deletesurvey);
		}
	}

	/**
	 * spawns an activity (configured in initializeFields) in Edit mode
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case DELETE_ID:
			final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
					.getMenuInfo();
			ViewUtil.showAdminAuthDialog(SurveyHomeActivity.this,
					new ViewUtil.AdminAuthDialogListener() {
						@Override
						public void onAuthenticated() {
							menuViewAdapter.deleteItem(info.position,
									SurveyHomeActivity.this);

						}
					});

			return true;
		}
		return super.onContextItemSelected(item);
	}

	/**
	 * starts up the data sync service
	 */
	private void startSyncService() {
		Intent i = new Intent(this, DataSyncService.class);
		i.putExtra(ConstantUtil.OP_TYPE_KEY, ConstantUtil.SEND);
		getApplicationContext().startService(i);
	}

	/**
	 * starts up a service that takes no input
	 */
	private <T extends Service> void startService(Class<T> serviceClass) {
		getApplicationContext().startService(new Intent(this, serviceClass));
	}

	/**
	 * handles the button presses.
	 */
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		String selected = menuViewAdapter.getSelectedOperation(position);
		v.setSelected(false);
		if (selected.equals(ConstantUtil.USER_OP)) {
			Intent i = new Intent(v.getContext(), ListUserActivity.class);
			startActivityForResult(i, LIST_USER_ACTIVITY);
		} else if (selected.equals(ConstantUtil.PANIC_OP)) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.dontPanicMsg)
					.setCancelable(true)
					.setPositiveButton(R.string.okbutton,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
			builder.show();
		} else if (selected.equals(ConstantUtil.CONF_OP)) {
			Intent i = new Intent(v.getContext(), SettingsActivity.class);
			startActivityForResult(i, SETTINGS_ACTIVITY);
		} else if (selected.equals(ConstantUtil.PLOT_OP)) {
			LocationManager locMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			if (locMgr.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				Intent i = new Intent(v.getContext(), ListPlotActivity.class);
				startActivityForResult(i, PLOT_LIST_ACTIVITY);
			} else {
				ViewUtil.showGPSDialog(this);
			}
		} else if (selected.equals(ConstantUtil.NEARBY_OP)) {
			Intent i = new Intent(v.getContext(), NearbyItemActivity.class);
			startActivityForResult(i, NEARBY_ACTIVITY);
		} else if (selected.equals(ConstantUtil.REVIEW_OP)) {
			Intent i = new Intent(v.getContext(), SurveyStatusHomeActivity.class);
			startActivity(i); //No result
		} else if (selected.equals(ConstantUtil.WATERFLOW_CALC_OP)) {
			Intent i = new Intent(v.getContext(),
					WaterflowCalculatorActivity.class);
			startActivityForResult(i, WF_CALC_ACTIVITY);
		} else { //Implicit SURVEY_OP
			if (currentUserId != null) {
				if (!BootstrapService.isProcessing) {
					Survey survey = menuViewAdapter.getSelectedSurvey(position);
					if (survey != null) {
						Intent i = new Intent(v.getContext(),
								SurveyViewActivity.class);
						i.putExtra(ConstantUtil.USER_ID_KEY, currentUserId);
						i.putExtra(ConstantUtil.SURVEY_ID_KEY, survey.getId());
						startActivityForResult(i, SURVEY_ACTIVITY);
					} else {
						Log.e(TAG, "Survey for selection is null");
					}
				} else {
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setMessage(R.string.pleasewaitforbootstrap)
							.setCancelable(true)
							.setPositiveButton(R.string.okbutton,
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											dialog.cancel();
										}
									});
					builder.show();
				}
			} else {
				// if the current user is null, we can't enter survey mode
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(R.string.mustselectuser)
						.setCancelable(true)
						.setPositiveButton(R.string.okbutton,
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
					currentUserId = bundle.getString(ConstantUtil.ID_KEY);
					currentName = bundle
							.getString(ConstantUtil.DISPLAY_NAME_KEY);
					populateFields();
				}
			} else if (resultCode == RESULT_CANCELED && intent != null) {
				Bundle bundle = intent.getExtras();
				if (bundle != null
						&& bundle.getBoolean(ConstantUtil.DELETED_SAVED_USER)) {
					currentUserId = null;
					currentName = null;
					populateFields();
				}
			}
			break;
		case PLOT_LIST_ACTIVITY:
			if (resultCode == RESULT_OK) {
				Bundle bundle = intent.getExtras();
				if (bundle != null) {
					String plotId = bundle.getString(ConstantUtil.ID_KEY);
					String status = bundle.getString(ConstantUtil.STATUS_KEY);
					Intent i = new Intent(this, RegionPlotActivity.class);
					i.putExtra(ConstantUtil.PLOT_ID_KEY, plotId);
					i.putExtra(ConstantUtil.STATUS_KEY, status);
					startActivityForResult(i, PLOTTING_ACTIVITY);
				}
			}
			break;
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (outState != null) {
			if (currentUserId != null) {
				outState.putString(ConstantUtil.ID_KEY, currentUserId);
			}
			if (currentName != null) {
				outState.putString(ConstantUtil.DISPLAY_NAME_KEY, currentName);
			}
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

	private void populateFields() {
		userField.setText(currentName);
		menuViewAdapter.loadData(this);
	}

	private void saveState() {

	}
}
