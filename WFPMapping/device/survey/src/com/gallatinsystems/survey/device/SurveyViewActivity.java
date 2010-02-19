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
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyViewActivity extends TabActivity implements
		QuestionInteractionListener {

	public static final String SURVEY_RESOURCE_ID = "RESID";
	public static final String USER_ID = "UID";
	public static final String SURVEY_ID = "SID";
	private static final String ACTIVITY_NAME = "SurveyViewActivity";
	private static final int PHOTO_ACTIVITY_REQUEST = 1;
	private static final String TEMP_PHOTO_NAME_PREFIX = "/wfpPhoto";
	private static final String VIDEO_PREFIX = "file:////";
	private static final String HTTP_PREFIX = "http://";
	private static final String VIDEO_TYPE = "video/*";
	private static final String IMAGE_SUFFIX = ".jpg";
	private ArrayList<SurveyTabContentFactory> tabContentFactories;
	private QuestionView photoSource;
	private SurveyDbAdapter databaseAdaptor;
	private String surveyId;
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

		surveyId = extras != null ? extras.getString(SURVEY_ID) : null;
		if (surveyId == null) {
			surveyId = savedInstanceState != null ? savedInstanceState
					.getString(SurveyDbAdapter.SURVEY_ID_COL) : "1";
		}

		// TODO: fetch the resource from the server
		Survey survey = null;
		if (resourceID > 0) {
			survey = p.parse(getResources().openRawResource(resourceID));
		} else {
			survey = p.parse(getResources().openRawResource(R.raw.testsurvey));
		}

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

	/**
	 * this is called when external activities launched by this activity return
	 * and we need to do something. Right now the only activity we care about is
	 * the Photo activity (when the user is done taking a picture). When we get
	 * control back from the camera, we just need to capture the details about
	 * the picture that was just stored and stuff it into the question response.
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// on activity return
		if (requestCode == PHOTO_ACTIVITY_REQUEST) {
			if (resultCode == RESULT_OK) {
				File f = new File(Environment.getExternalStorageDirectory()
						.getAbsolutePath()
						+ TEMP_PHOTO_NAME_PREFIX + IMAGE_SUFFIX);
				String newName = Environment.getExternalStorageDirectory()
						.getAbsolutePath()
						+ TEMP_PHOTO_NAME_PREFIX
						+ System.nanoTime()
						+ IMAGE_SUFFIX;
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

	/**
	 * iterates over all tabs and calls their RESET method to blank out the
	 * questions
	 */
	public void resetAllQuestions() {
		for (int i = 0; i < tabContentFactories.size(); i++) {
			tabContentFactories.get(i).resetTabQuestions();
		}
	}

	/**
	 * event handler that can be used to handle events fired by individual
	 * questions at the Activity level. Because we can't launch the photo
	 * activity from a view (we need to launch it from the activity), the photo
	 * question view fires a QuestionInteractionEvent (to which this activity
	 * listens). When we get the event, we can then spawn the camera activity.
	 * 
	 * Currently, this method supports handing TAKE_PHOTO_EVENT and
	 * VIDEO_TIP_EVENT types
	 */
	public void onQuestionInteraction(QuestionInteractionEvent event) {
		if (QuestionInteractionEvent.TAKE_PHOTO_EVENT.equals(event
				.getEventType())) {
			// fire off the intent
			Intent i = new Intent(
					android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			i.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri
					.fromFile(new File(Environment
							.getExternalStorageDirectory().getAbsolutePath()
							+ TEMP_PHOTO_NAME_PREFIX + IMAGE_SUFFIX)));
			photoSource = event.getSource();
			startActivityForResult(i, PHOTO_ACTIVITY_REQUEST);
		} else if (QuestionInteractionEvent.VIDEO_TIP_VIEW.equals(event
				.getEventType())) {
			Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
			Uri uri = null;
			String src = event.getSource().getQuestion().getVideo().trim();
			if (src.toLowerCase().startsWith(HTTP_PREFIX)) {
				uri = Uri.parse(src);
			} else {
				// if the source doesn't start with http, assume it's
				// referencing device storage
				uri = Uri.parse(VIDEO_PREFIX + src);
			}
			intent.setDataAndType(uri, VIDEO_TYPE);
			startActivity(intent);
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
				missingQuestions.addAll(tabContentFactories.get(i)
						.checkMandatoryQuestions());
			}
		}
		return missingQuestions;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (outState != null) {
			if (surveyId != null) {
				outState.putString(SurveyDbAdapter.SURVEY_ID_COL, surveyId);
			}
			if (respondentId != null) {
				outState.putLong(SurveyDbAdapter.RESP_ID_COL, respondentId);
			}
			if (userId != null) {
				outState.putString(SurveyDbAdapter.USER_FK_COL, userId);
			}
		}
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

	protected void onDestroy() {
		super.onDestroy();
		if (databaseAdaptor != null) {
			databaseAdaptor.close();
		}
	}

	public String getSurveyId() {
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