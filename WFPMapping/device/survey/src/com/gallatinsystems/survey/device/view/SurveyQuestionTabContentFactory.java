package com.gallatinsystems.survey.device.view;

import java.util.ArrayList;
import java.util.HashMap;

import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.gallatinsystems.survey.device.R;
import com.gallatinsystems.survey.device.activity.SurveyViewActivity;
import com.gallatinsystems.survey.device.dao.SurveyDbAdapter;
import com.gallatinsystems.survey.device.domain.Question;
import com.gallatinsystems.survey.device.domain.QuestionGroup;
import com.gallatinsystems.survey.device.domain.QuestionResponse;
import com.gallatinsystems.survey.device.util.ConstantUtil;

/**
 * Creates the content for a single tab in the survey (corresponds to a
 * QuestionGroup). The tab will lay out all the questions in the QuestionGroup
 * (passed in at construction) in a List view and will append save/clear buttons
 * to the bottom of the list.
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyQuestionTabContentFactory extends SurveyTabContentFactory {

	private QuestionGroup questionGroup;
	private HashMap<String, QuestionView> questionMap;

	private boolean readOnly;

	public HashMap<String, QuestionView> getQuestionMap() {
		return questionMap;
	}

	/**
	 * stores the context and questionGroup to member fields
	 * 
	 * @param c
	 * @param qg
	 */
	public SurveyQuestionTabContentFactory(SurveyViewActivity c,
			QuestionGroup qg, SurveyDbAdapter dbAdaptor, float textSize,
			String[] languageCodes, boolean readOnly) {
		super(c, dbAdaptor, textSize, languageCodes);
		questionGroup = qg;
		questionMap = new HashMap<String, QuestionView>();
		this.readOnly = readOnly;
	}

	/**
	 * Constructs a view using the question data from the stored questionGroup.
	 * This method makes use of a QuestionAdaptor to process individual
	 * questions.
	 */
	public View createTabContent(String tag) {
		ScrollView scrollView = createSurveyTabContent();
	
		TableLayout table = new TableLayout(context);
		table.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));

		scrollView.addView(table);

		ArrayList<Question> questions = questionGroup.getQuestions();

		for (int i = 0; i < questions.size(); i++) {
			QuestionView questionView = null;
			Question q = questions.get(i);
			TableRow tr = new TableRow(context);
			tr.setLayoutParams(new ViewGroup.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

			if (ConstantUtil.OPTION_QUESTION_TYPE.equalsIgnoreCase(q.getType())) {
				questionView = new OptionQuestionView(context, q,
						languageCodes, readOnly);

			} else if (ConstantUtil.FREE_QUESTION_TYPE.equalsIgnoreCase(q
					.getType())) {
				questionView = new FreetextQuestionView(context, q,
						languageCodes, readOnly);
			} else if (ConstantUtil.PHOTO_QUESTION_TYPE.equalsIgnoreCase(q
					.getType())) {
				questionView = new MediaQuestionView(context, q,
						ConstantUtil.PHOTO_QUESTION_TYPE, languageCodes,
						readOnly);
			} else if (ConstantUtil.VIDEO_QUESTION_TYPE.equalsIgnoreCase(q
					.getType())) {
				questionView = new MediaQuestionView(context, q,
						ConstantUtil.VIDEO_QUESTION_TYPE, languageCodes,
						readOnly);
			} else if (ConstantUtil.GEO_QUESTION_TYPE.equalsIgnoreCase(q
					.getType())) {
				questionView = new GeoQuestionView(context, q, languageCodes,
						readOnly);
			} else if (ConstantUtil.SCAN_QUESTION_TYPE.equalsIgnoreCase(q
					.getType())) {
				questionView = new BarcodeQuestionView(context, q,
						languageCodes, readOnly);
			} else if (ConstantUtil.TRACK_QUESTION_TYPE.equalsIgnoreCase(q
					.getType())) {
				questionView = new GeoTrackQuestionView(context, q,
						languageCodes, readOnly);
			} else {
				questionView = new QuestionView(context, q, languageCodes,
						readOnly);
			}
			questionView.setTextSize(defaultTextSize);
			questionMap.put(q.getId(), questionView);
			questionView
					.addQuestionInteractionListener((SurveyViewActivity) context);
			tr.addView(questionView);
			if (i < questions.size() - 1) {
				View ruler = new View(context);
				ruler.setBackgroundColor(0xFFFFFFFF);
				questionView.addView(ruler, new ViewGroup.LayoutParams(
						ViewGroup.LayoutParams.FILL_PARENT, 2));
			}
			table.addView(tr);
		}
		// set up listeners for dependencies. Since the dependencies can span
		// groups, the parent needs to do this
		context.establishDependencies(questionGroup);

		// create save/clear buttons
		TableRow buttonRow = new TableRow(context);
		LinearLayout group = new LinearLayout(context);

		group.addView(configureActionButton(R.string.nextbutton,
				new OnClickListener() {
					public void onClick(View v) {
						saveState(context.getRespondentId());
						context.advanceTab();
					}
				}));
		if (readOnly) {
			toggleButtons(false);
		}

		buttonRow.addView(group);
		table.addView(buttonRow);

		// clicking submit will check to see if all mandatory questions are
		// answered and, if so, will fire a broadcast indicating that data is
		// available for transfer
		/*	nextButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
			
				 * if (questionMap != null) { // make sure we don't lose
				 * anything that was already // written
				 * saveState(context.getRespondentId()); // get the list (across
				 * all tabs) of missing mandatory // responses
				 * ArrayList<Question> missingQuestions = context
				 * .checkMandatory(); if (missingQuestions.size() == 0) { // if
				 * we have no missing responses, submit the // survey
				 * databaseAdaptor.submitResponses(context
				 * .getRespondentId().toString());
				 * 
				 * // send a broadcast message indicating new data is //
				 * available Intent i = new Intent(
				 * ConstantUtil.DATA_AVAILABLE_INTENT);
				 * context.sendBroadcast(i); ViewUtil.showConfirmDialog(
				 * R.string.submitcompletetitle, R.string.submitcompletetext,
				 * context); startNewSurvey(); } else { // if we do have missing
				 * responses, tell the user
				 * ViewUtil.showConfirmDialog(R.string.cannotsave,
				 * R.string.mandatorywarning, context); } }
				 

				context.advanceTab();

			}
		});*/
		loadState(context.getRespondentId());
		return scrollView;
	}

	/**
	 * resets all the questions on this tab
	 */
	public void resetTabQuestions() {
		// while in general we avoid the enhanced for-loop in
		// the Android VM, we can use it here because we
		// would still need the iterator
		if (questionMap != null) {
			for (QuestionView view : questionMap.values()) {
				view.resetQuestion();
			}
			resetView();
		}
	}

	/**
	 * updates the visible languages for all questions in the tab
	 * 
	 * @param langCodes
	 */
	public void updateQuestionLanguages(String[] langCodes) {
		if (questionMap != null) {
			for (QuestionView view : questionMap.values()) {
				view.updateSelectedLanguages(langCodes);
			}
		}
	}

	/**
	 * checks to make sure the mandatory questions in this tab have a response
	 * 
	 * @return
	 */
	public ArrayList<Question> checkMandatoryQuestions() {
		ArrayList<Question> missingQuestions = new ArrayList<Question>();
		// we have to check if the map is null or empty since the views aren't
		// created until the tab is clicked the first time
		if (questionMap == null || questionMap.size() == 0) {
			// add all the mandatory questions
			ArrayList<Question> uninitializedQuesitons = questionGroup
					.getQuestions();
			for (int i = 0; i < uninitializedQuesitons.size(); i++) {
				if (uninitializedQuesitons.get(i).isMandatory()) {
					missingQuestions.add(uninitializedQuesitons.get(i));
				}
			}
		} else {
			for (QuestionView view : questionMap.values()) {
				if (view.getQuestion().isMandatory()) {
					QuestionResponse resp = view.getResponse();
					if (resp == null || !resp.isValid()) {
						missingQuestions.add(view.getQuestion());
					}
				}
			}
		}
		return missingQuestions;
	}

	/**
	 * loads the state from the database using the respondentId passed in. It
	 * will then use the loaded responses to update the status of the question
	 * views in this tab.
	 * 
	 * @param respondentId
	 */
	public void loadState(Long respondentId) {
		if (respondentId != null) {
			Cursor responseCursor = databaseAdaptor
					.fetchResponsesByRespondent(respondentId.toString());
			context.startManagingCursor(responseCursor);

			while (responseCursor.moveToNext()) {
				String[] cols = responseCursor.getColumnNames();
				QuestionResponse resp = new QuestionResponse();
				for (int i = 0; i < cols.length; i++) {
					if (cols[i].equals(SurveyDbAdapter.RESP_ID_COL)) {
						resp.setId(responseCursor.getLong(i));
					} else if (cols[i]
							.equals(SurveyDbAdapter.SURVEY_RESPONDENT_ID_COL)) {
						resp.setRespondentId(responseCursor.getLong(i));
					} else if (cols[i].equals(SurveyDbAdapter.ANSWER_COL)) {
						resp.setValue(responseCursor.getString(i));
					} else if (cols[i].equals(SurveyDbAdapter.ANSWER_TYPE_COL)) {
						resp.setType(responseCursor.getString(i));
					} else if (cols[i].equals(SurveyDbAdapter.QUESTION_FK_COL)) {
						resp.setQuestionId(responseCursor.getString(i));
					}
				}
				if (questionMap != null) {
					// update the question view to reflect the loaded data
					if (questionMap.get(resp.getQuestionId()) != null) {
						questionMap.get(resp.getQuestionId()).rehydrate(resp);

					}
				}
			}
		}
	}

	/**
	 * updates text size of all questions in this tab
	 * 
	 * @param size
	 */
	public void updateTextSize(float size) {
		defaultTextSize = size;
		if (questionMap != null) {
			for (QuestionView qv : questionMap.values()) {
				qv.setTextSize(size);
			}
		}
	}

	/**
	 * persists the current question responses in this tab to the database
	 * 
	 * @param respondentId
	 */
	public void saveState(Long respondentId) {
		if (questionMap != null) {
			for (QuestionView q : questionMap.values()) {
				if (q.getResponse(true) != null
						&& q.getResponse(true).hasValue()) {
					q.getResponse(true).setRespondentId(respondentId);
					databaseAdaptor.createOrUpdateSurveyResponse(q
							.getResponse(true));
				}
			}
		}
	}

}
