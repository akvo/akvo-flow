package com.gallatinsystems.survey.device;

import android.app.TabActivity;
import android.os.Bundle;
import android.widget.TabHost;

import com.gallatinsystems.survey.device.domain.QuestionGroup;
import com.gallatinsystems.survey.device.domain.Survey;
import com.gallatinsystems.survey.device.view.SurveyTabContentFactory;
import com.gallatinsystems.survey.device.xml.SaxSurveyParser;

/**
 * main activity for the Field Survey application. It will read in the current
 * survey definition and render the survey UI based on the questions defined.
 * 
 * TODO: add logic for starting the background activity to check for updated
 * surveys
 * 
 * TODO: add logic for starting background activity for sending collected data
 * to server
 * 
 * TODO: add logic for starting background data replication activity
 * 
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyViewActivity extends TabActivity {
    
    private static final String ACTIVITY_NAME = "SurveyViewActivity";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        SaxSurveyParser p = new SaxSurveyParser();

        // TODO: fetch the resource from the server
        Survey survey = p.parse(getResources()
                .openRawResource(R.raw.testsurvey));

        if (survey != null) {
            // if the device has an active survey, create a tab for each
            // question group
            TabHost tabHost = getTabHost();
            for (QuestionGroup group : survey.getQuestionGroups()) {
                tabHost.addTab(tabHost.newTabSpec(group.getHeading())
                        .setIndicator(group.getHeading()).setContent(
                                new SurveyTabContentFactory(this, group)));
            }
        }
    }
}