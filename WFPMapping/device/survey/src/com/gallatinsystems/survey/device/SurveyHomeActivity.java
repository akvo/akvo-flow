package com.gallatinsystems.survey.device;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

import com.gallatinsystems.survey.device.dao.SurveyDbAdapter;
import com.gallatinsystems.survey.device.domain.Survey;
import com.gallatinsystems.survey.device.util.ViewUtil;
import com.gallatinsystems.survey.device.view.HomeMenuViewAdapter;

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
	private String currentUserId;
	private String currentName;
	private TextView userField;
	private HomeMenuViewAdapter menuViewAdapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);

		menuViewAdapter = new HomeMenuViewAdapter(this);
		userField = (TextView) findViewById(R.id.currentUserField);

		GridView grid = (GridView) findViewById(R.id.gridview);
		grid.setAdapter(menuViewAdapter);
		grid.setOnItemClickListener(this);
		registerForContextMenu(grid);

		// TODO: store/fetch current user from DB?
		currentUserId = savedInstanceState != null ? savedInstanceState
				.getString(SurveyDbAdapter.PK_ID_COL) : null;
		currentName = savedInstanceState != null ? savedInstanceState
				.getString(SurveyDbAdapter.DISP_NAME_COL) : null;
		if (currentName != null) {
			populateFields();
		}

		startSyncService();
		startDownloadService();
	}

	/**
	 * presents a single "edit" option when the user long-clicks a list item
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// we only allow delete of surveys so check the view tag
		if (HomeMenuViewAdapter.SURVEY_OP
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
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
					.getMenuInfo();
			menuViewAdapter.deleteItem(info.position, this);

			return true;
		}
		return super.onContextItemSelected(item);
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
	 * starts up the download service
	 */
	private void startDownloadService() {
		getApplicationContext().startService(
				new Intent(this, SurveyDownloadService.class));
	}

	/**
	 * handles the button presses.
	 */
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		String selected = menuViewAdapter.getSelectedOperation(position);
		v.setSelected(false);

		if (selected.equals(HomeMenuViewAdapter.USER_OP)) {
			Intent i = new Intent(v.getContext(), ListUserActivity.class);
			startActivityForResult(i, LIST_USER_ACTIVITY);
		} else if (selected.equals(HomeMenuViewAdapter.CONF_OP)) {
			Intent i = new Intent(v.getContext(), SettingsActivity.class);
			startActivityForResult(i, SETTINGS_ACTIVITY);
		} else if (selected.equals(HomeMenuViewAdapter.PLOT_OP)) {
			LocationManager locMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			if (locMgr.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				Intent i = new Intent(v.getContext(), ListPlotActivity.class);
				startActivityForResult(i, PLOT_LIST_ACTIVITY);
			} else {
				ViewUtil.showGPSDialog(this);
			}
		} else {
			if (currentUserId != null) {
				int resourceID = 0;
				Survey survey = menuViewAdapter.getSelectedSurvey(position);
				if (survey != null) {
					Intent i = new Intent(v.getContext(),
							SurveyViewActivity.class);
					i.putExtra(SurveyViewActivity.SURVEY_RESOURCE_ID,
							resourceID);
					i.putExtra(SurveyViewActivity.USER_ID, currentUserId);
					i.putExtra(SurveyViewActivity.SURVEY_ID, survey.getId());
					startActivityForResult(i, SURVEY_ACTIVITY);
				} else {
					Log.e(TAG, "Survey for selection is null");
				}
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
					currentUserId = bundle.getString(SurveyDbAdapter.PK_ID_COL);
					currentName = bundle
							.getString(SurveyDbAdapter.DISP_NAME_COL);
					populateFields();
				}
			}
			break;
		case PLOT_LIST_ACTIVITY:
			if (resultCode == RESULT_OK) {
				Bundle bundle = intent.getExtras();
				if (bundle != null) {
					String plotId = bundle.getString(SurveyDbAdapter.PK_ID_COL);
					String status = bundle
							.getString(SurveyDbAdapter.STATUS_COL);
					Intent i = new Intent(this, RegionPlotActivity.class);
					i.putExtra(RegionPlotActivity.PLOT_ID, plotId);
					i.putExtra(RegionPlotActivity.STATUS, status);
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
				outState.putString(SurveyDbAdapter.PK_ID_COL, currentUserId);
			}
			if (currentName != null) {
				outState.putString(SurveyDbAdapter.DISP_NAME_COL, currentName);
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
		// TODO: persist current user?
	}
}
