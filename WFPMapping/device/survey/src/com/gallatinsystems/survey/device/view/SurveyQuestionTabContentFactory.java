/*
 *  Copyright (C) 2010-2012 Stichting Akvo (Akvo Foundation)
 *
 *  This file is part of Akvo FLOW.
 *
 *  Akvo FLOW is free software: you can redistribute it and modify it under the terms of
 *  the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 *  either version 3 of the License or any later version.
 *
 *  Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Affero General Public License included below for more details.
 *
 *  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */

package com.gallatinsystems.survey.device.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import android.database.Cursor;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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
	private HashMap<String, QuestionResponse> responseMap;

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
			String defaultLang, String[] languageCodes, boolean readOnly) {
		super(c, dbAdaptor, textSize, defaultLang, languageCodes);
		responseMap = null;
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
						getDefaultLang(), languageCodes, readOnly);

			} else if (ConstantUtil.FREE_QUESTION_TYPE.equalsIgnoreCase(q
					.getType())) {
				questionView = new FreetextQuestionView(context, q,
						getDefaultLang(), languageCodes, readOnly);
			} else if (ConstantUtil.PHOTO_QUESTION_TYPE.equalsIgnoreCase(q
					.getType())) {
				questionView = new MediaQuestionView(context, q,
						ConstantUtil.PHOTO_QUESTION_TYPE, getDefaultLang(),
						languageCodes, readOnly);
			} else if (ConstantUtil.VIDEO_QUESTION_TYPE.equalsIgnoreCase(q
					.getType())) {
				questionView = new MediaQuestionView(context, q,
						ConstantUtil.VIDEO_QUESTION_TYPE, getDefaultLang(),
						languageCodes, readOnly);
			} else if (ConstantUtil.GEO_QUESTION_TYPE.equalsIgnoreCase(q
					.getType())) {
				questionView = new GeoQuestionView(context, q,
						getDefaultLang(), languageCodes, readOnly);
			} else if (ConstantUtil.SCAN_QUESTION_TYPE.equalsIgnoreCase(q
					.getType())) {
				questionView = new BarcodeQuestionView(context, q,
						getDefaultLang(), languageCodes, readOnly);
			} else if (ConstantUtil.TRACK_QUESTION_TYPE.equalsIgnoreCase(q
					.getType())) {
				questionView = new GeoTrackQuestionView(context, q,
						getDefaultLang(), languageCodes, readOnly);
			} else if (ConstantUtil.STRENGTH_QUESTION_TYPE.equalsIgnoreCase(q
					.getType())) {
				questionView = new StrengthQuestionView(context, q,
						getDefaultLang(), languageCodes, readOnly);
			} else if (ConstantUtil.HEADING_QUESTION_TYPE.equalsIgnoreCase(q
					.getType())) {
				questionView = new CompassQuestionView(context, q,
						getDefaultLang(), languageCodes, readOnly);
			} else if (ConstantUtil.DATE_QUESTION_TYPE.equalsIgnoreCase(q
					.getType())) {
				questionView = new DateQuestionView(context, q,
						getDefaultLang(), languageCodes, readOnly);
			} else {
				questionView = new QuestionView(context, q, getDefaultLang(),
						languageCodes, readOnly);
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
				view.resetQuestion(false);
			}
			resetView();
		}
		if(responseMap != null){
			responseMap.clear();
		}
	}
	
	/**
	 * sets the mandatory highlight on each question that is in the set of question ids passed in
	 * @param questions
	 */
	public void highlightMissingQuestions(HashSet<String> questions){
		if(questionMap != null && questions != null){
			for (QuestionView view : questionMap.values()) {
				if(questions.contains(view.getQuestion().getId())){
					view.highlight(true);
				}else{				
					view.highlight(false);
				}
			}
		}
	}

	/**
	 * updates the visible languages for all questions in the tab
	 * 
	 * @param langCodes
	 */
	public void updateQuestionLanguages(String[] langCodes) {
		updateSelectedLanguages(langCodes);
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
		if (responseMap == null) {
			loadState(context.getRespondentId());
		}
		ArrayList<Question> missingQuestions = new ArrayList<Question>();
		// we have to check if the map is null or empty since the views aren't
		// created until the tab is clicked the first time
		if (questionMap == null || questionMap.size() == 0) {
			// add all the mandatory questions
			ArrayList<Question> uninitializedQuesitons = questionGroup
					.getQuestions();
			for (int i = 0; i < uninitializedQuesitons.size(); i++) {
				if (uninitializedQuesitons.get(i).isMandatory()) {
					QuestionResponse resp = responseMap
							.get(uninitializedQuesitons.get(i).getId());
					if (resp == null || !resp.isValid()) {
						missingQuestions.add(uninitializedQuesitons.get(i));
					}
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
	public HashMap<String, QuestionResponse> loadState(Long respondentId) {
		if (responseMap == null) {
			responseMap = new HashMap<String, QuestionResponse>();
		}
		if (respondentId != null) {
			Cursor responseCursor = databaseAdaptor
					.fetchResponsesByRespondent(respondentId.toString());			

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
					} else if (cols[i].equals(SurveyDbAdapter.INCLUDE_FLAG_COL)) {
						resp.setIncludeFlag(responseCursor.getString(i));
					} else if (cols[i].equals(SurveyDbAdapter.SCORED_VAL_COL)) {
						resp.setScoredValue(responseCursor.getString(i));
					} else if (cols[i].equals(SurveyDbAdapter.STRENGTH_COL)) {
						resp.setStrength(responseCursor.getString(i));
					}
				}
				responseMap.put(resp.getQuestionId(), resp);
				if (questionMap != null) {
					// update the question view to reflect the loaded data
					if (questionMap.get(resp.getQuestionId()) != null) {
						questionMap.get(resp.getQuestionId()).rehydrate(resp);
					}
				}
			}
			responseCursor.close();
		}
		return responseMap;
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
		if (responseMap == null) {
			responseMap = new HashMap<String, QuestionResponse>();
		}
		if (questionMap != null) {
			for (QuestionView q : questionMap.values()) {
				QuestionResponse curResponse = q.getResponse(true);
				if (curResponse != null
						&& curResponse.hasValue()) {
					curResponse.setRespondentId(respondentId);
					databaseAdaptor.createOrUpdateSurveyResponse(curResponse);
					responseMap.put(curResponse.getQuestionId(),
							curResponse);
				} else if (curResponse != null
						&& curResponse.getId() != null
						&& curResponse.getId() > 0) {
					// if we don't have a value BUT there is an ID, we need to
					// remove it since the user blanked out their response
					databaseAdaptor.deleteResponse(respondentId.toString(), q
							.getQuestion().getId());
					responseMap.remove(curResponse.getQuestionId());
				} else if (curResponse != null) {
					//if we're here, the response is blank but hasn't been saved yet (has no ID)
					//so we can just discard it
					responseMap.remove(curResponse.getQuestionId());
				}
			}
		}
	}
}
