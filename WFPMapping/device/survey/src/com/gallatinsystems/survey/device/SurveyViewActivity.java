package com.gallatinsystems.survey.device;

import android.app.Activity;
import android.os.Bundle;

import com.gallatinsystems.survey.device.domain.Survey;
import com.gallatinsystems.survey.device.xml.SaxSurveyParser;

public class SurveyViewActivity extends Activity {
	private static final String TAG = "SurveyViewActivity";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		SaxSurveyParser p = new SaxSurveyParser();
		// TODO: fetch the resource from the server
		Survey survey = p.parse(getResources().openRawResource(R.raw.testsurvey));

		// TODO: iterate over the questions in the survey object and use them to
		// create a layout/view
	}
}