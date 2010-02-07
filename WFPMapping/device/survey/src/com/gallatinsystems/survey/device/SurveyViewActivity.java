package com.gallatinsystems.survey.device;

import java.io.File;
import java.util.ArrayList;

import android.app.TabActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.TabHost;

import com.gallatinsystems.survey.device.dao.SurveyDbAdapter;
import com.gallatinsystems.survey.device.domain.Question;
import com.gallatinsystems.survey.device.domain.QuestionGroup;
import com.gallatinsystems.survey.device.domain.Survey;
import com.gallatinsystems.survey.device.event.QuestionInteractionEvent;
import com.gallatinsystems.survey.device.event.QuestionInteractionListener;
import com.gallatinsystems.survey.device.view.PhotoQuestionView;
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
 * 
 * TODO: refactor to obey suggestions here:
 * http://developer.android.com/guide/practices/design/performance.html
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyViewActivity extends TabActivity implements
		QuestionInteractionListener {

	public static final String SURVEY_RESOURCE_ID = "RESID";
	public static final String USER_ID = "UID";
	private static final String ACTIVITY_NAME = "SurveyViewActivity";
	private static final int PHOTO_ACTIVITY_REQUEST = 1;
	private static final int GEO_ACTIVITY_REQUEST = 2;
	private static final String TEMP_PHOTO_NAME_PREFIX = "/wfpPhoto";
	private ArrayList<SurveyTabContentFactory> tabContentFactories;
	private QuestionView photoSource;
	private SurveyDbAdapter databaseAdaptor;
	private Long surveyId;
	private Long respondentId;
	private String userId;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		databaseAdaptor = new SurveyDbAdapter(this);
		databaseAdaptor.open();

		setContentView(R.layout.main);
		SaxSurveyParser p = new SaxSurveyParser();

		Bundle extras = getIntent().getExtras();
		int resourceID = extras != null ? extras.getInt(SURVEY_RESOURCE_ID) : 0;
		userId = extras != null ? extras.getString(USER_ID) : null;
		if (userId == null) {
			userId = savedInstanceState != null ? savedInstanceState
					.getString(SurveyDbAdapter.USER_FK_COL) : null;
		}

		// TODO: fetch the resource from the server
		Survey survey = null;
		if (resourceID > 0) {
			survey = p.parse(getResources().openRawResource(resourceID));
		} else {
			survey = p.parse(getResources().openRawResource(R.raw.testsurvey));
		}

		// TODO: load survey ID from DB
		surveyId = savedInstanceState != null ? savedInstanceState
				.getLong(SurveyDbAdapter.SURVEY_ID_COL) : new Long(1);
		respondentId = savedInstanceState != null ? savedInstanceState
				.getLong(SurveyDbAdapter.RESP_ID_COL) : null;

		if (respondentId == null) {
			respondentId = databaseAdaptor.createOrLoadSurveyRespondent(
					surveyId.toString(), userId.toString());
		}

		if (survey != null) {
			tabContentFactories = new ArrayList<SurveyTabContentFactory>();
			// if the device has an active survey, create a tab for each
			// question group
			TabHost tabHost = getTabHost();
			for (QuestionGroup group : survey.getQuestionGroups()) {
				SurveyTabContentFactory factory = new SurveyTabContentFactory(
						this, group, databaseAdaptor);
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
						+ TEMP_PHOTO_NAME_PREFIX + ".jpg");
				String newName = Environment.getExternalStorageDirectory()
						.getAbsolutePath()
						+ TEMP_PHOTO_NAME_PREFIX + System.nanoTime() + ".jpg";
				f.renameTo(new File(newName));

				try {
					/*
					 * //this will resolve put the image in the media browser
					 * Uri u =
					 * Uri.parse(android.provider.MediaStore.Images.Media
					 * .insertImage(getContentResolver(), f .getAbsolutePath(),
					 * null, null));
					 */
					if (photoSource != null) {
						Bundle photoData = new Bundle();
						photoData.putString(PhotoQuestionView.PHOTO_FILE_KEY,
								newName);
						photoSource.questionComplete(photoData);
					}
					// f.delete();
				} catch (Exception e) {
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
							+ TEMP_PHOTO_NAME_PREFIX + ".jpg")));
			photoSource = event.getSource();
			startActivityForResult(i, PHOTO_ACTIVITY_REQUEST);
		}
	}

	/**
	 * checks if all the mandatory questions (on all tabs) have responses
	 * 
	 * @return
	 */
	public ArrayList<Question> checkMandatory() {
		ArrayList<Question> missingQuestions = new ArrayList<Question>();
		if (tabContentFactories != null) {
			for (int i = 0; i < tabContentFactories.size(); i++) {
				missingQuestions.addAll(tabContentFactories.get(i).checkMandatoryQuestions());				
			}
		}
		return missingQuestions;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(SurveyDbAdapter.SURVEY_ID_COL, surveyId);
		outState.putLong(SurveyDbAdapter.RESP_ID_COL, respondentId);
		outState.putString(SurveyDbAdapter.USER_FK_COL, userId);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (tabContentFactories != null) {
			for (SurveyTabContentFactory tab : tabContentFactories) {
				tab.saveState(respondentId);
			}
		}		
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (tabContentFactories != null) {
			for (SurveyTabContentFactory tab : tabContentFactories) {
				tab.loadState(respondentId);
			}
		}
	}

	protected void onDestroy(){
		super.onDestroy();
		if(databaseAdaptor != null){
			databaseAdaptor.close();
		}
	}
	
	public Long getSurveyId() {
		return surveyId;
	}

	public void setRespondentId(Long respondentId) {
		this.respondentId = respondentId;
	}

	public Long getRespondentId() {
		return respondentId;
	}

	public String getUserId() {
		return userId;
	}

}