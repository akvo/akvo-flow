package com.gallatinsystems.survey.device;

import android.app.TabActivity;
import android.os.Bundle;
import android.widget.TabHost;

import com.gallatinsystems.survey.device.domain.QuestionGroup;
import com.gallatinsystems.survey.device.domain.Survey;
import com.gallatinsystems.survey.device.view.SurveyTabContentFactory;
import com.gallatinsystems.survey.device.xml.SaxSurveyParser;

public class SurveyViewActivity extends TabActivity {
	private static final String TAG = "SurveyViewActivity";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		SaxSurveyParser p = new SaxSurveyParser();
		// TODO: fetch the resource from the server
		Survey survey = p.parse(getResources()
				.openRawResource(R.raw.testsurvey));

		// TODO: iterate over the questions in the survey object and use them to
		if (survey != null) {
			TabHost tabHost = getTabHost();
			for (QuestionGroup group : survey.getQuestionGroups()) {
				tabHost.addTab(tabHost.newTabSpec(group.getHeading())
						.setIndicator(group.getHeading()).setContent(
								new SurveyTabContentFactory(this, group)));
			}

		}
		// create a layout/view
	}
}