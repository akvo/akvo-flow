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
	private Grid mainGrid;
	private Panel answerPanel;
	private QuestionDto question;
	private QuestionAnswerStoreDto answer;

	protected QuestionWidget(QuestionDto question) {
		answerPanel = new VerticalPanel();
		answer = new QuestionAnswerStoreDto();
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
		Label text = new Label(question.getText()+(question.getMandatoryFlag()?"*":""));
		text.setWordWrap(true);
		text.setWidth(TEXT_WIDTH);
		mainGrid.setWidget(0, 0, text);
		bindResponseSection();
	}

	protected abstract void bindResponseSection();

	protected QuestionAnswerStoreDto getAnswer() {
		return answer;
	}

	protected Panel getPanel() {
		return answerPanel;
	}

	protected QuestionDto getQuestion() {
		return question;
	}
}
