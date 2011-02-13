package org.waterforpeople.mapping.surveyentry.client.component;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;

import com.gallatinsystems.framework.gwt.util.client.ViewUtil;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.TextBox;

/**
 * handles free text questions
 * 
 * @author Christopher Fagiani
 * 
 */
public class TextQuestionWidget extends QuestionWidget {
	private TextBox textBox;
	private boolean isNumeric;

	public TextQuestionWidget(QuestionDto q, boolean numericOnly) {
		super(q);
		isNumeric = numericOnly;
		if (textBox != null && isNumeric) {
			// prevent non-numeric entry in the text box but allow navigational
			// keys
			textBox.addKeyPressHandler(new KeyPressHandler() {

				@Override
				public void onKeyPress(KeyPressEvent event) {
					char keyCode = event.getCharCode();
					if ((!Character.isDigit(keyCode))
							&& (keyCode != KeyCodes.KEY_TAB)
							&& (keyCode != KeyCodes.KEY_BACKSPACE)
							&& (keyCode != KeyCodes.KEY_DELETE)
							&& (keyCode != KeyCodes.KEY_ENTER)
							&& (keyCode != KeyCodes.KEY_HOME)
							&& (keyCode != KeyCodes.KEY_END)
							&& (keyCode != (KeyCodes.KEY_LEFT)
									&& (keyCode != KeyCodes.KEY_UP)
									&& (keyCode != KeyCodes.KEY_RIGHT) && (keyCode != KeyCodes.KEY_DOWN))) {
						((TextBox) event.getSource()).cancelKey();
					}
				}
			});
		}
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
