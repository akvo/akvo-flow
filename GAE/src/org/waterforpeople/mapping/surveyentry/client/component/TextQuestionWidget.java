package org.waterforpeople.mapping.surveyentry.client.component;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.surveyinstance.QuestionAnswerStoreDto;

import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.gallatinsystems.framework.gwt.util.client.ViewUtil;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;

/**
 * handles free text questions
 * 
 * @author Christopher Fagiani
 * 
 */
public class TextQuestionWidget extends QuestionWidget implements
		KeyPressHandler {
	private static final String MAX_WIDTH = "300px";
	private static final String TEXT_HEIGHT = "300px";
	private static final int MAX_TXT_LENGTH = 500;

	private TextArea textBox;
	private boolean isNumeric;

	public TextQuestionWidget(QuestionDto q, QuestionAnswerStoreDto a,
			boolean numericOnly) {
		super(q, a);
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
		textBox = new TextArea();
		textBox.setHeight(TEXT_HEIGHT);
		textBox.setWidth(MAX_WIDTH);
		textBox.addKeyPressHandler(this);
		getPanel().add(textBox);
		if (getAnswer().getKeyId() != null) {
			textBox.setText(getAnswer().getValue());
		}
	}

	@Override
	protected void captureAnswer() {
		if (ViewUtil.isTextPopulated(textBox)) {
			String answerTextValue = textBox.getText().trim();
			if (answerTextValue.contains(",")) {

			}
			getAnswer().setValue(answerTextValue);
		}
	}

	@Override
	protected void resetUi() {
		textBox.setText("");
	}

	@Override
	public void onKeyPress(KeyPressEvent event) {
		if (textBox != null && textBox.getText() != null
				&& textBox.getText().length() > MAX_TXT_LENGTH) {
			MessageDialog dia = new MessageDialog(TEXT_CONSTANTS.inputError(),
					TEXT_CONSTANTS.textMustBeLessThan500Chars());
			dia.showCentered();
			textBox.setText(textBox.getText().substring(0, MAX_TXT_LENGTH - 1));
		}
	}
}
