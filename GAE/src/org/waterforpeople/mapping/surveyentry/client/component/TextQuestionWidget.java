package org.waterforpeople.mapping.surveyentry.client.component;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;

import com.google.gwt.user.client.ui.TextBox;

/**
 * handles free text questions
 * 
 * @author Christopher Fagiani
 * 
 */
public class TextQuestionWidget extends QuestionWidget {
	private TextBox textBox;

	public TextQuestionWidget(QuestionDto q) {
		super(q);
	}

	@Override
	protected void bindResponseSection() {
		textBox = new TextBox();
		getPanel().add(textBox);

	}

}
