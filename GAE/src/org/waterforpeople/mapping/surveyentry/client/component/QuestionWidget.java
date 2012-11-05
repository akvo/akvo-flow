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

package org.waterforpeople.mapping.surveyentry.client.component;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.surveyinstance.QuestionAnswerStoreDto;
import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * widget for displaying question dtos as part of a survey entry screen
 * 
 * @author Christopher Fagiani
 * 
 */
public abstract class QuestionWidget extends Composite {
	protected static TextConstants TEXT_CONSTANTS = GWT
	.create(TextConstants.class);
	private static String TEXT_WIDTH = "300px";
	private static final String DEFAULT_ANS_TYPE = "VALUE";
	private Grid mainGrid;
	private Panel answerPanel;
	private QuestionDto question;
	private QuestionAnswerStoreDto answer;
	protected String currentLocale;

	protected QuestionWidget(QuestionDto question, QuestionAnswerStoreDto ans) {
		answerPanel = new VerticalPanel();
		answer = ans;
		currentLocale =  com.google.gwt.i18n.client.LocaleInfo.getCurrentLocale()
		.getLocaleName();
		if(currentLocale == null){
			currentLocale = "en";
		}
		if (answer == null) {
			answer = new QuestionAnswerStoreDto();
			answer.setType(DEFAULT_ANS_TYPE);
			answer.setQuestionID(question.getKeyId().toString());
			answer.setSurveyId(question.getSurveyId());
		}

		mainGrid = new Grid(1, 2);
		mainGrid.setWidget(0, 1, answerPanel);
		this.question = question;
		bindQuestion();
		initWidget(mainGrid);
	}

	/**
	 * configures the widget to display the correct controls based on the
	 * question type
	 * 
	 * TODO: handle unsupported question types (SCAN, TRACK, etc)
	 */
	protected void bindQuestion() {
		Label text = new Label(question.getLocalizedText(currentLocale)
				+ (question.getMandatoryFlag() ? "*" : ""));
		text.setWordWrap(true);
		text.setWidth(TEXT_WIDTH);
		mainGrid.setWidget(0, 0, text);
		constructResponseUi();
	}

	/**
	 * constructs the ui for the response section of the question
	 */
	protected abstract void constructResponseUi();

	/**
	 * populates the answer object with values from the ui
	 */
	protected abstract void captureAnswer();

	/**
	 * clears input from the ui widgets
	 */
	protected abstract void resetUi();

	public QuestionAnswerStoreDto getAnswer() {
		return answer;
	}


	protected Panel getPanel() {
		return answerPanel;
	}

	public QuestionDto getQuestion() {
		return question;
	}

	/**
	 * returns true if the question has a valid answer
	 * 
	 * @return
	 */
	public boolean isAnswered() {
		captureAnswer();
		if (answer != null && answer.getValue() != null
				&& answer.getValue().trim().length() > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * indicates whether the underlying quesiton is mandatory
	 * @return
	 */
	public boolean isMandatory(){
		return getQuestion().getMandatoryFlag();
	}

	/**
	 * resets the question to the unanswered state
	 */
	public void reset() {
		answer.setValue(null);
		answer.setKeyId(null);
		resetUi();
	}

}
