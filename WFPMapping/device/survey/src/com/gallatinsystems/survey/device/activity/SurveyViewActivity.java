package com.gallatinsystems.survey.device.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.TabHost;

import com.gallatinsystems.survey.device.R;
import com.gallatinsystems.survey.device.dao.SurveyDao;
import com.gallatinsystems.survey.device.dao.SurveyDbAdapter;
import com.gallatinsystems.survey.device.domain.Question;
import com.gallatinsystems.survey.device.domain.QuestionGroup;
import com.gallatinsystems.survey.device.domain.Survey;
import com.gallatinsystems.survey.device.event.QuestionInteractionEvent;
import com.gallatinsystems.survey.device.event.QuestionInteractionListener;
import com.gallatinsystems.survey.device.util.ConstantUtil;
import com.gallatinsystems.survey.device.util.FileUtil;
import com.gallatinsystems.survey.device.view.QuestionView;
import com.gallatinsystems.survey.device.view.SurveyTabContentFactory;

/**
 * main activity for the Field Survey application. It will read in the current
 * survey definition and render the survey UI based on the questions defined.
 * 
 * 
 * 
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyViewActivity extends TabActivity implements
		QuestionInteractionListener {

	private static final String TAG = "Survey View Activity";

	private static final String ACTIVITY_NAME = "SurveyViewActivity";
	private static final int PHOTO_ACTIVITY_REQUEST = 1;
	private static final int VIDEO_ACTIVITY_REQUEST = 2;
	private static final int SCAN_ACTIVITY_REQUEST = 3;
	private static final String TEMP_PHOTO_NAME_PREFIX = "/wfpPhoto";
	private static final String TEMP_VIDEO_NAME_PREFIX = "/wfpVideo";
	private static final String VIDEO_PREFIX = "file:////";
	private static final String HTTP_PREFIX = "http://";
	private static final String VIDEO_TYPE = "video/*";
	private static final String IMAGE_SUFFIX = ".jpg";
	private static final String VIDEO_SUFFIX = ".mp4";
	private ArrayList<SurveyTabContentFactory> tabContentFactories;
	private QuestionView eventQuestionSource;
	private SurveyDbAdapter databaseAdapter;
	private String surveyId;
	private Long respondentId;
	private String userId;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		databaseAdapter = new SurveyDbAdapter(this);
		databaseAdapter.open();

		setContentView(R.layout.main);

		Bundle extras = getIntent().getExtras();
		userId = extras != null ? extras.getString(ConstantUtil.USER_ID_KEY)
				: null;
		if (userId == null) {
			userId = savedInstanceState != null ? savedInstanceState
					.getString(ConstantUtil.USER_ID_KEY) : null;
		}

		surveyId = extras != null ? extras
				.getString(ConstantUtil.SURVEY_ID_KEY) : null;
		if (surveyId == null) {
			surveyId = savedInstanceState != null ? savedInstanceState
					.getString(ConstantUtil.SURVEY_ID_KEY) : "1";
		}

		Survey survey = null;
		try {
			survey = SurveyDao.loadSurvey(databaseAdapter.findSurvey(surveyId),
					getResources());
		} catch (FileNotFoundException e) {
			Log.e(TAG, "Could not load survey xml file");
		}

		respondentId = savedInstanceState != null ? savedInstanceState
				.getLong(ConstantUtil.RESPONDENT_ID_KEY) : null;

		if (respondentId == null) {
			respondentId = databaseAdapter.createOrLoadSurveyRespondent(
					surveyId.toString(), userId.toString());
		}

		if (survey != null) {
			tabContentFactories = new ArrayList<SurveyTabContentFactory>();
			// if the device has an active survey, create a tab for each
			// question group
			TabHost tabHost = getTabHost();
			for (QuestionGroup group : survey.getQuestionGroups()) {
				SurveyTabContentFactory factory = new SurveyTabContentFactory(
						this, group, databaseAdapter);
				tabHost.addTab(tabHost.newTabSpec(group.getHeading())
						.setIndicator(group.getHeading()).setContent(factory));
				tabContentFactories.add(factory);
			}
		}
	}

	/**
	 * this is called when external activities launched by this activity return
	 * and we need to do something. Right now the only activity we care about is
	 * the media (photo/video/audio) activity (when the user is done recording
	 * the media). When we get control back from the camera, we just need to
	 * capture the details about the file that was just stored and stuff it into
	 * the question response.
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// on activity return
		if (requestCode == PHOTO_ACTIVITY_REQUEST
				|| requestCode == VIDEO_ACTIVITY_REQUEST) {
			if (resultCode == RESULT_OK) {
				String filePrefix = null;
				String fileSuffix = null;
				if (requestCode == PHOTO_ACTIVITY_REQUEST) {
					filePrefix = TEMP_PHOTO_NAME_PREFIX;
					fileSuffix = IMAGE_SUFFIX;
				} else {
					filePrefix = TEMP_VIDEO_NAME_PREFIX;
					fileSuffix = VIDEO_SUFFIX;
				}

				File f = new File(Environment.getExternalStorageDirectory()
						.getAbsolutePath()
						+ filePrefix + fileSuffix);
				String newName = Environment.getExternalStorageDirectory()
						.getAbsolutePath()
						+ filePrefix + System.nanoTime() + fileSuffix;
				f.renameTo(new File(newName));

				try {
					if (eventQuestionSource != null) {
						Bundle photoData = new Bundle();
						photoData.putString(ConstantUtil.MEDIA_FILE_KEY,
								newName);
						eventQuestionSource.questionComplete(photoData);
					}
				} catch (Exception e) {
					Log.e(ACTIVITY_NAME, e.getMessage());
				} finally {
					eventQuestionSource = null;
				}
			}
		} else if (requestCode == SCAN_ACTIVITY_REQUEST) {
			if (resultCode == RESULT_OK) {
				eventQuestionSource.questionComplete(data.getExtras());
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
			eventQuestionSource = event.getSource();
			startActivityForResult(i, PHOTO_ACTIVITY_REQUEST);
		} else if (QuestionInteractionEvent.VIDEO_TIP_VIEW.equals(event
				.getEventType())) {
			Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
			Uri uri = null;
			String src = event.getSource().getQuestion().getVideo().trim();
			if (src.toLowerCase().startsWith(HTTP_PREFIX)) {
				// first see if we have a precached copy of the file
				String localFile = FileUtil.convertRemoteToLocalFile(src,
						surveyId);
				if (FileUtil.doesFileExist(localFile)) {
					uri = Uri.parse(VIDEO_PREFIX + localFile);
				} else {
					uri = Uri.parse(src);
				}
			} else {
				// if the source doesn't start with http, assume it's
				// referencing device storage
				uri = Uri.parse(VIDEO_PREFIX + src);
			}
			intent.setDataAndType(uri, VIDEO_TYPE);
			startActivity(intent);
		} else if (QuestionInteractionEvent.PHOTO_TIP_VIEW.equals(event
				.getEventType())) {
			Intent intent = new Intent(this, ImageBrowserActivity.class);

			intent.putExtra(ConstantUtil.IMAGE_URL_LIST_KEY, event.getSource()
					.getQuestion().getImages());
			intent.putExtra(ConstantUtil.IMAGE_CAPTION_LIST_KEY, event
					.getSource().getQuestion().getImageCaptions());
			startActivity(intent);
		} else if (QuestionInteractionEvent.TAKE_VIDEO_EVENT.equals(event
				.getEventType())) {
			// fire off the intent
			Intent i = new Intent(
					android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
			i.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri
					.fromFile(new File(Environment
							.getExternalStorageDirectory().getAbsolutePath()
							+ TEMP_VIDEO_NAME_PREFIX + VIDEO_SUFFIX)));
			eventQuestionSource = event.getSource();
			startActivityForResult(i, VIDEO_ACTIVITY_REQUEST);
		} else if (QuestionInteractionEvent.SCAN_BARCODE_EVENT.equals(event
				.getEventType())) {
			Intent intent = new Intent(ConstantUtil.BARCODE_SCAN_INTENT);
			try {
				startActivityForResult(intent, SCAN_ACTIVITY_REQUEST);
				eventQuestionSource = event.getSource();
			} catch (ActivityNotFoundException ex) {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(R.string.barcodeerror);
				builder.setPositiveButton(R.string.okbutton,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
				builder.show();
			}
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
				outState.putString(ConstantUtil.SURVEY_ID_KEY, surveyId);
			}
			if (respondentId != null) {
				outState.putLong(ConstantUtil.RESPONDENT_ID_KEY, respondentId);
			}
			if (userId != null) {
				outState.putString(ConstantUtil.USER_ID_KEY, userId);
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
		if (databaseAdapter != null) {
			databaseAdapter.close();
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