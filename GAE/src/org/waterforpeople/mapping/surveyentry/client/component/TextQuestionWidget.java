package org.waterforpeople.mapping.surveyentry.client.component;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;

import com.gallatinsystems.framework.gwt.util.client.ViewUtil;
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
	protected void constructResponseUi() {
		textBox = new TextBox();
		getPanel().add(textBox);

	}

	@Override
	protected void captureAnswer() {
		if (ViewUtil.isTextPopulated(textBox)) {
			getAnswer().setValue(textBox.getText().trim());
		}
	}

	@Override
	protected void resetUi() {
		textBox.setText("");
	}

}
