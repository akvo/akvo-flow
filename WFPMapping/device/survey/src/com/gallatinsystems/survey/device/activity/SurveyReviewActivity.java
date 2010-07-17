package com.gallatinsystems.survey.device.activity;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

import com.gallatinsystems.survey.device.R;
import com.gallatinsystems.survey.device.dao.SurveyDbAdapter;
import com.gallatinsystems.survey.device.util.ConstantUtil;
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

	private SurveyDbAdapter databaseAdaptor;
	private static final int SAVED_SURVEYS = 1;
	private static final int SUBMITTED_SURVEYS = 2;
	private static final int DELETE_ALL = 3;	
	private String currentStatusMode = ConstantUtil.SAVED_STATUS;
	private TextView viewTypeLabel;	

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
		databaseAdaptor = new SurveyDbAdapter(this);
		databaseAdaptor.open();
		getData();
	}

	private void getData() {
		if (ConstantUtil.SAVED_STATUS.equals(currentStatusMode)) {
			viewTypeLabel.setText(R.string.savedsurveyslabel);
		} else {
			viewTypeLabel.setText(R.string.submittedsurveyslabel);
		}
		Cursor dataCursor = databaseAdaptor
				.listSurveyRespondent(currentStatusMode);
		startManagingCursor(dataCursor);

		SurveyReviewCursorAdaptor surveys = new SurveyReviewCursorAdaptor(this,
				dataCursor);
		setListAdapter(surveys);
	}

	/**
	 * presents the survey options menu when the user presses the menu key
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, SAVED_SURVEYS, 0, R.string.savedsurveyslabel);
		menu.add(0, SUBMITTED_SURVEYS, 1, R.string.submittedsurveysmenu);
		menu.add(0, DELETE_ALL, 2, R.string.deleteall);
		return true;
	}

	/**
	 * handles the menu actions
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case SAVED_SURVEYS:
			if (!ConstantUtil.SAVED_STATUS.equals(currentStatusMode)) {
				currentStatusMode = ConstantUtil.SAVED_STATUS;
				getData();
			}
			return true;
		case SUBMITTED_SURVEYS:
			if (!ConstantUtil.SUBMITTED_STATUS.equals(currentStatusMode)) {
				currentStatusMode = ConstantUtil.SUBMITTED_STATUS;
				getData();
			}
			return true;
		case DELETE_ALL:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.deleteallwarning).setCancelable(true)
					.setPositiveButton(R.string.okbutton,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									databaseAdaptor.deleteAllResponses();
									getData();
								}
							}).setNegativeButton(R.string.cancelbutton,
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
	protected void onListItemClick(ListView list, View view, int position,
			long id) {
		super.onListItemClick(list, view, position, id);
		Intent intent = new Intent();

		Intent i = new Intent(view.getContext(), SurveyViewActivity.class);
		i.putExtra(ConstantUtil.USER_ID_KEY, ((Long) view
				.getTag(SurveyReviewCursorAdaptor.USER_ID_KEY)).toString());
		i.putExtra(ConstantUtil.SURVEY_ID_KEY, ((Long) view
				.getTag(SurveyReviewCursorAdaptor.SURVEY_ID_KEY)).toString());
		i.putExtra(ConstantUtil.RESPONDENT_ID_KEY, (Long) view
				.getTag(SurveyReviewCursorAdaptor.RESP_ID_KEY));
		if(ConstantUtil.SUBMITTED_STATUS.equals(currentStatusMode)){
			i.putExtra(ConstantUtil.READONLY_KEY,true);
		}
		databaseAdaptor.close();
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
