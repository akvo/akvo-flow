package com.gallatinsystems.survey.device;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

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

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		ImageButton mapButton = (ImageButton) findViewById(R.id.mapSurveyButton);
		ImageButton wpButton = (ImageButton) findViewById(R.id.wpSurveyButton);
		ImageButton hhButton = (ImageButton) findViewById(R.id.hhSurveyButton);
		ImageButton pubButton = (ImageButton) findViewById(R.id.pubSurveyButton);
		ImageButton userButotn = (ImageButton) findViewById(R.id.usersButton);
		TextView userField = (TextView) findViewById(R.id.currentUserField);
		// TODO: get current user from db
		userField.setText("Test User");

		mapButton.setOnClickListener(this);
		wpButton.setOnClickListener(this);
		hhButton.setOnClickListener(this);
		pubButton.setOnClickListener(this);
		userButotn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int clickedId = v.getId();
		if (clickedId == R.id.usersButton) {
			Intent i = new Intent(v.getContext(), ListUserActivity.class);
			startActivityForResult(i, LIST_USER_ACTIVITY);
		} else {
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
			startActivityForResult(i, SURVEY_ACTIVITY);
		}

	}
}
