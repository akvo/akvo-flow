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
 * Activity for reviewing previously saved surveys. This activity will allow the
 * user to delete surveys from the device and to review submitted surveys in a
 * read-only mode.
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyReviewActivity extends ListActivity {

	private static final String TAG = "SurveyReviewActivity";
	private static final int MODE_SELECTOR = 1;
	private static final int DELETE_ALL = 3;
	private static final int DELETE_ONE = 4;
	private static final int RESEND_ALL = 5;
	private static final int VIEW_HISTORY = 5;
	private static final int RESEND_ONE = 6;
	private String currentStatusMode = ConstantUtil.SAVED_STATUS;
	private TextView viewTypeLabel;
	private Long selectedSurvey;
	private Cursor dataCursor;
	private SurveyDbAdapter databaseAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (savedInstanceState != null) {
			String state = savedInstanceState
					.getString(ConstantUtil.STATUS_KEY);
			if (state != null) {
				currentStatusMode = state;
			}
		}

		setContentView(R.layout.surveyreview);
		viewTypeLabel = (TextView) findViewById(R.id.viewtypelabel);
		databaseAdapter = new SurveyDbAdapter(this);

		registerForContextMenu(getListView());

	}

	/**
	 * loads the survey instances from the database. By default this will load
	 * only unsubmitted saved surveys, but if the user changes the mode to view
	 * submitted, it will list the submitted surveys.
	 */
	private void getData() {
		String label = null;
		if (ConstantUtil.SAVED_STATUS.equals(currentStatusMode)) {
			label = getString(R.string.savedsurveyslabel);
		} else {
			label = getString(R.string.submittedsurveyslabel);
		}
		try{
		if(dataCursor != null){
			dataCursor.close();
		}
		}catch(Exception e){
			Log.w(TAG, "Could not close old cursor before reloading list",e);
		}
		dataCursor = databaseAdapter
				.listSurveyRespondent(currentStatusMode);		

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
		if (!ConstantUtil.SAVED_STATUS.equals(currentStatusMode)) {
			menu.add(0, VIEW_HISTORY, 1, R.string.transmissionhist);
			menu.add(0, RESEND_ONE, 1, R.string.resendone);
		}

	}

	public void onResume() {
		super.onResume();
		databaseAdapter.open();
		getData();
	}

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
		case VIEW_HISTORY:
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
							SurveyReviewActivity.this.sendBroadcast(dataIntent);
							ViewUtil.showConfirmDialog(
									R.string.submitcompletetitle,
									R.string.submitcompletetext,
									SurveyReviewActivity.this);
						}
					});
			break;
		}
		return true;
	}

	/**
	 * presents the survey options menu when the user presses the menu key
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, MODE_SELECTOR, 0, R.string.submittedsurveysmenu);
		menu.add(0, DELETE_ALL, 1, R.string.deleteall);
		menu.add(0, RESEND_ALL, 2, R.string.resendall);
		menu.getItem(2).setVisible(false);
		return true;
	}

	@Override
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

	/**
	 * handles the menu actions. Use OptionsItemSelected instead of
	 * onMenuItemSelected or else we'll intercept the calls to the context menu.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case MODE_SELECTOR:
			if (!ConstantUtil.SAVED_STATUS.equals(currentStatusMode)) {
				currentStatusMode = ConstantUtil.SAVED_STATUS;
				getData();
			} else {
				currentStatusMode = ConstantUtil.SUBMITTED_STATUS;
				getData();
			}
			return true;
		case DELETE_ALL:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.deleteallwarning)
					.setCancelable(true)
					.setPositiveButton(R.string.okbutton,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									databaseAdapter.deleteAllResponses();
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
			return true;
		case RESEND_ALL:
			ViewUtil.showAdminAuthDialog(this,
					new ViewUtil.AdminAuthDialogListener() {
						@Override
						public void onAuthenticated() {
							databaseAdapter.markDataUnsent(null);
							Intent i = new Intent(
									ConstantUtil.DATA_AVAILABLE_INTENT);
							SurveyReviewActivity.this.sendBroadcast(i);
							ViewUtil.showConfirmDialog(
									R.string.submitcompletetitle,
									R.string.submitcompletetext,
									SurveyReviewActivity.this);

						}
					});

			return true;
		}
		return false;
	}

	/**
	 * when a list item is clicked, get the user id and name of the selected
	 * item and return it to the calling activity.
	 */
	@Override
	protected void onListItemClick(ListView list, View view, int position,
			long id) {
		super.onListItemClick(list, view, position, id);
		Intent intent = new Intent();

		Intent i = new Intent(view.getContext(), SurveyViewActivity.class);
		i.putExtra(ConstantUtil.USER_ID_KEY, ((Long) view
				.getTag(SurveyReviewCursorAdaptor.USER_ID_KEY)).toString());
		i.putExtra(ConstantUtil.SURVEY_ID_KEY, ((Long) view
				.getTag(SurveyReviewCursorAdaptor.SURVEY_ID_KEY)).toString());
		i.putExtra(ConstantUtil.RESPONDENT_ID_KEY,
				(Long) view.getTag(SurveyReviewCursorAdaptor.RESP_ID_KEY));
		if (ConstantUtil.SUBMITTED_STATUS.equals(currentStatusMode)) {
			i.putExtra(ConstantUtil.READONLY_KEY, true);
		}
		setResult(RESULT_OK, intent);
		finish();
		startActivity(i);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (outState != null) {
			outState.putString(ConstantUtil.STATUS_KEY, currentStatusMode);
		}
	}

}
