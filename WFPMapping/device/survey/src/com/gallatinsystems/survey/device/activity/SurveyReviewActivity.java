package com.gallatinsystems.survey.device.activity;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ListView;

import com.gallatinsystems.survey.device.R;
import com.gallatinsystems.survey.device.dao.SurveyDbAdapter;
import com.gallatinsystems.survey.device.util.ConstantUtil;
import com.gallatinsystems.survey.device.view.adapter.SurveyReviewCursorAdaptor;

/**
 * Activity for reviewing previously saved surveys.
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyReviewActivity extends ListActivity {

	private SurveyDbAdapter databaseAdaptor;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.surveyreview);
		databaseAdaptor = new SurveyDbAdapter(this);
		databaseAdaptor.open();
		getData();
	}

	private void getData() {
		Cursor dataCursor = databaseAdaptor
				.listSurveyRespondent(ConstantUtil.SAVED_STATUS);
		startManagingCursor(dataCursor);

		SurveyReviewCursorAdaptor surveys = new SurveyReviewCursorAdaptor(this,
				dataCursor);
		setListAdapter(surveys);
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
		databaseAdaptor.close();
		setResult(RESULT_OK, intent);
		finish();
		startActivity(i);
	}
}
