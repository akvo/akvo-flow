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

import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.gallatinsystems.framework.gwt.util.client.ViewUtil;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;

/**
 * handles free text questions
 * 
 * @author Christopher Fagiani
 * 
 */
public class TextQuestionWidget extends QuestionWidget implements
		KeyUpHandler {
	private static final String CHAR_REMAIN_STYLE = "char-remaining";
	private static final String MAX_WIDTH = "300px";
	private static final String TEXT_HEIGHT = "300px";
	private static final int MAX_TXT_LENGTH = 500;

	private TextArea textBox;
	private Label remainingCharLabel;
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
		Panel p = new HorizontalPanel();
		textBox = new TextArea();
		textBox.setHeight(TEXT_HEIGHT);
		textBox.setWidth(MAX_WIDTH);
		textBox.addKeyUpHandler(this);
		p.add(textBox);
		remainingCharLabel = new Label(MAX_TXT_LENGTH+" "+TEXT_CONSTANTS.remaining());
		remainingCharLabel.setStylePrimaryName(CHAR_REMAIN_STYLE);
		p.add(remainingCharLabel);
		getPanel().add(p);
		if (getAnswer().getKeyId() != null) {
			textBox.setText(getAnswer().getValue());
			if(getAnswer().getValue()!= null){
				remainingCharLabel.setText((MAX_TXT_LENGTH-getAnswer().getValue().length())+TEXT_CONSTANTS.remaining());
			}
		}
	}

	@Override
	protected void captureAnswer() {
		if (ViewUtil.isTextPopulated(textBox)) {
			String answerTextValue = textBox.getText().trim();
			getAnswer().setValue(answerTextValue);
		}
	}

	@Override
	protected void resetUi() {
		textBox.setText("");
	}

	@Override
	public void onKeyUp(KeyUpEvent event) {				
		if (textBox != null && textBox.getText() != null
				&& textBox.getText().length() > MAX_TXT_LENGTH) {			
			MessageDialog dia = new MessageDialog(TEXT_CONSTANTS.inputError(),
					TEXT_CONSTANTS.textMustBeLessThan500Chars());
			dia.showCentered();
			textBox.setText(textBox.getText().substring(0, MAX_TXT_LENGTH - 1));
		}
		
		if (textBox != null && textBox.getText()!= null){
			
			remainingCharLabel.setText((MAX_TXT_LENGTH - textBox.getText().length())+TEXT_CONSTANTS.remaining());
		}
	}
}
