package org.waterforpeople.mapping.surveyentry.client.component;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.surveyinstance.QuestionAnswerStoreDto;

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
	private static String TEXT_WIDTH = "300px";
	private static final String DEFAULT_ANS_TYPE = "VALUE";
	private Grid mainGrid;
	private Panel answerPanel;
	private QuestionDto question;
	private QuestionAnswerStoreDto answer;

	protected QuestionWidget(QuestionDto question, QuestionAnswerStoreDto ans) {
		answerPanel = new VerticalPanel();
		answer = ans;
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
		Label text = new Label(question.getText()
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
	 * resets the question to the unanswered state
	 */
	public void reset() {
		answer.setValue(null);
		answer.setKeyId(null);
		resetUi();
	}

}
