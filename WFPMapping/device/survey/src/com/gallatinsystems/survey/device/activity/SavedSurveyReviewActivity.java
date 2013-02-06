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

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.gallatinsystems.survey.device.R;
import com.gallatinsystems.survey.device.dao.SurveyDbAdapter;
import com.gallatinsystems.survey.device.util.ConstantUtil;
import com.gallatinsystems.survey.device.util.ViewUtil;
import com.gallatinsystems.survey.device.view.adapter.SurveyReviewCursorAdaptor;

/**
 * Activity for reviewing previously saved surveys. 
 * Click will resume filling out that survey.
 * Long press will give the option to delete the survey from the device.
 *   
 * @author Stellan Lagerström
 * 
 */
public class SavedSurveyReviewActivity extends ListActivity {

	private static final String TAG = "SavedSurveyReviewActivity";
	private static final int DELETE_ALL = 3;
	private static final int DELETE_ONE = 4;
	private TextView viewTypeLabel;
	private Long selectedSurvey;
	private Cursor dataCursor;
	private SurveyDbAdapter databaseAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.surveyreview);
		viewTypeLabel = (TextView) findViewById(R.id.viewtypelabel);
		databaseAdapter = new SurveyDbAdapter(this);

		registerForContextMenu(getListView());

	}

	
	/**
	 * loads the survey instances from the database. 
	 */
	private void getData() {
		String label = getString(R.string.savedsurveyslabel);

		try{
		if(dataCursor != null){
			dataCursor.close();
		}
		}catch(Exception e){
			Log.w(TAG, "Could not close old cursor before reloading list",e);
		}
		dataCursor = databaseAdapter
				.listSurveyRespondent(ConstantUtil.SAVED_STATUS);		

		SurveyReviewCursorAdaptor surveys = new SurveyReviewCursorAdaptor(this,
				dataCursor);
		setListAdapter(surveys);
		if (dataCursor != null) {
			viewTypeLabel.setText(label + " (" + dataCursor.getCount() + ")");
		} else {
			viewTypeLabel.setText(label + "(0)");
		}
	}

	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View view,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, view, menuInfo);
		selectedSurvey = getListAdapter().getItemId(
				((AdapterView.AdapterContextMenuInfo) menuInfo).position);
		menu.add(0, DELETE_ONE, 0, R.string.deletesurvey);
	}

	
	@Override
	public void onResume() {
		super.onResume();
		databaseAdapter.open();
		getData();
	}

	
	@Override
	protected void onDestroy() {
		if (dataCursor != null) {
			try {
				dataCursor.close();
			} catch (Exception e) {

			}
		}
		if (databaseAdapter != null) {
			databaseAdapter.close();
		}
		super.onDestroy();
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case DELETE_ONE:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.deleteonewarning)
					.setCancelable(true)
					.setPositiveButton(R.string.okbutton,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									databaseAdapter
											.deleteRespondent(selectedSurvey
													.toString());
									getData();
								}
							})
					.setNegativeButton(R.string.cancelbutton,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
			builder.show();
			break;
/*		case VIEW_HISTORY:
			Intent i = new Intent(this, TransmissionHistoryActivity.class);
			i.putExtra(ConstantUtil.RESPONDENT_ID_KEY, selectedSurvey);
			startActivity(i);
			break;
		case RESEND_ONE:
			ViewUtil.showAdminAuthDialog(this,
					new ViewUtil.AdminAuthDialogListener() {
						@Override
						public void onAuthenticated() {
							databaseAdapter.markDataUnsent(selectedSurvey);
							Intent dataIntent = new Intent(
									ConstantUtil.DATA_AVAILABLE_INTENT);
							SavedSurveyReviewActivity.this.sendBroadcast(dataIntent);
							ViewUtil.showConfirmDialog(
									R.string.submitcompletetitle,
									R.string.submitcompletetext,
									SavedSurveyReviewActivity.this);
						}
					});
			break;
			*/
		}
		return true;
	}

	/**
	 * presents the survey options menu when the user presses the menu key
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, DELETE_ALL, 0, R.string.deleteall);
		return true;
	}

/*	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		super.onMenuOpened(featureId, menu);
		if (ConstantUtil.SAVED_STATUS.equals(currentStatusMode)) {
			menu.getItem(0).setTitle(R.string.submittedsurveysmenu);
			menu.getItem(2).setVisible(false);
		} else {
			menu.getItem(0).setTitle(R.string.savedsurveyslabel);
			menu.getItem(2).setVisible(true);
		}
		return true;

	}
*/
	/**
	 * handles the menu actions. Use OptionsItemSelected instead of
	 * onMenuItemSelected or else we'll intercept the calls to the context menu.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case DELETE_ALL:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.deleteallwarning)
					.setCancelable(true)
					.setPositiveButton(R.string.okbutton,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									ViewUtil.showAdminAuthDialog(SavedSurveyReviewActivity.this, new ViewUtil.AdminAuthDialogListener() {										
										@Override
										public void onAuthenticated() {
											databaseAdapter.deleteAllResponses();
											getData();										
										}
									});									
								}
							})
					.setNegativeButton(R.string.cancelbutton,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
			builder.show();
			return true;
		}
		return false;
	}

	/**
	 * when a list item is clicked, get the user id and name of the selected
	 * item and return it to the calling activity.
	 */
	@Override
	protected void onListItemClick(ListView list, View view, int position, long id) {
		super.onListItemClick(list, view, position, id);
//		Intent intent = new Intent();

		Intent i = new Intent(view.getContext(), SurveyViewActivity.class);
		i.putExtra(ConstantUtil.USER_ID_KEY, ((Long) view
				.getTag(SurveyReviewCursorAdaptor.USER_ID_KEY)).toString());
		i.putExtra(ConstantUtil.SURVEY_ID_KEY, ((Long) view
				.getTag(SurveyReviewCursorAdaptor.SURVEY_ID_KEY)).toString());
		i.putExtra(ConstantUtil.RESPONDENT_ID_KEY,
				(Long) view.getTag(SurveyReviewCursorAdaptor.RESP_ID_KEY));
		//tell survey view to not chain to a new survey on submit
		i.putExtra(ConstantUtil.SINGLE_SURVEY_KEY, true);


		//No result, and do not close
//		setResult(RESULT_OK, intent);
//		finish();
		startActivity(i);
	}
/*
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (outState != null) {
			outState.putString(ConstantUtil.STATUS_KEY, currentStatusMode);
		}
	}
*/
}
