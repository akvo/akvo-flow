package com.gallatinsystems.survey.device;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import android.app.TabActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.TabHost;

import com.gallatinsystems.survey.device.domain.QuestionGroup;
import com.gallatinsystems.survey.device.domain.Survey;
import com.gallatinsystems.survey.device.event.QuestionInteractionEvent;
import com.gallatinsystems.survey.device.event.QuestionInteractionListener;
import com.gallatinsystems.survey.device.view.QuestionView;
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
public class SurveyViewActivity extends TabActivity implements
        QuestionInteractionListener {

    private static final String ACTIVITY_NAME = "SurveyViewActivity";
    private static final int PHOTO_ACTIVITY_REQUEST = 1;
    private static final int GEO_ACTIVITY_REQUEST = 2;
    private static final String TEMP_PHOTO_NAME = "/mappingphototemp.jpg";
    private List<SurveyTabContentFactory> tabContentFactories;
    private QuestionView photoSource;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        SaxSurveyParser p = new SaxSurveyParser();

        // TODO: fetch the resource from the server
        Survey survey = p.parse(getResources().openRawResource(
                R.raw.testsurvey));

        if (survey != null) {
            tabContentFactories = new ArrayList<SurveyTabContentFactory>();
            // if the device has an active survey, create a tab for each
            // question group
            TabHost tabHost = getTabHost();
            for (QuestionGroup group : survey.getQuestionGroups()) {
                SurveyTabContentFactory factory = new SurveyTabContentFactory(
                        this, group);
                tabHost.addTab(tabHost.newTabSpec(group.getHeading())
                        .setIndicator(group.getHeading()).setContent(factory));
                tabContentFactories.add(factory);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // on activity return
        if (requestCode == PHOTO_ACTIVITY_REQUEST) {
            if (resultCode == RESULT_OK) {
                File f = new File(Environment.getExternalStorageDirectory()
                        .getAbsolutePath()
                        + TEMP_PHOTO_NAME);
                try {
                    Uri u = Uri.parse(android.provider.MediaStore.Images.Media
                            .insertImage(getContentResolver(), f
                                    .getAbsolutePath(), null, null));
                    if (photoSource != null) {  
                        photoSource.questionComplete();
                    }
                    f.delete();
                } catch (FileNotFoundException e) {
                    Log.e(ACTIVITY_NAME, e.getMessage());
                } finally {
                    photoSource = null;
                }
            }
        }

    }

    public void onQuestionInteraction(QuestionInteractionEvent event) {
        if (QuestionInteractionEvent.TAKE_PHOTO_EVENT.equals(event
                .getEventType())) {
            // fire off the intent
            Intent i = new Intent(
                    android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            i.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri
                    .fromFile(new File(Environment
                            .getExternalStorageDirectory().getAbsolutePath()
                            + TEMP_PHOTO_NAME)));
            photoSource = event.getSource();
            startActivityForResult(i, PHOTO_ACTIVITY_REQUEST);
        }

    }
}