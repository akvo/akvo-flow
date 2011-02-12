package org.waterforpeople.mapping.surveyentry.client.component;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.surveyinstance.QuestionAnswerStoreDto;

import com.google.gwt.user.client.ui.Composite;
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
	private Panel panel;
	private QuestionDto question;
	private QuestionAnswerStoreDto answer;

	protected QuestionWidget(QuestionDto question) {
		panel = new VerticalPanel();
		answer = new QuestionAnswerStoreDto();
		this.question = question;
		bindQuestion();
		initWidget(panel);
	}

	/**
	 * configures the widget to display the correct controls based on the
	 * question type
	 * 
	 * TODO: handle unsupported question types (SCAN, TRACK, etc)
	 */
	protected void bindQuestion() {
		Label text = new Label(question.getText());
		panel.add(text);
		bindResponseSection();
	}

	protected abstract void bindResponseSection();

	protected QuestionAnswerStoreDto getAnswer() {
		return answer;
	}

	protected Panel getPanel() {
		return panel;
	}

	protected QuestionDto getQuestion() {
		return question;
	}
}
