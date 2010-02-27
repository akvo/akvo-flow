package com.gallatinsystems.survey.device.view;

import java.util.StringTokenizer;

import android.content.Context;
import android.text.InputFilter;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.widget.EditText;
import android.widget.TableRow;

import com.gallatinsystems.survey.device.domain.Question;
import com.gallatinsystems.survey.device.domain.QuestionResponse;
import com.gallatinsystems.survey.device.domain.ValidationRule;
import com.gallatinsystems.survey.device.util.ConstantUtil;

/**
 * Question that supports free-text input via the keyboard
 * 
 * 
 * @author Christopher Fagiani
 * 
 */
public class FreetextQuestionView extends QuestionView {
	private EditText freetextEdit;

	public FreetextQuestionView(Context context, Question q) {
		super(context, q);
		init();
	}

	protected void init() {
		Context context = getContext();
		TableRow tr = new TableRow(context);
		freetextEdit = new EditText(context);
		freetextEdit.setWidth(DEFAULT_WIDTH);
		ValidationRule rule = getQuestion().getValidationRule();
		if (rule != null) {
			// set the maximum length
			if (rule.getMaxLength() != null) {
				InputFilter[] FilterArray = new InputFilter[1];
				FilterArray[0] = new InputFilter.LengthFilter(getQuestion()
						.getValidationRule().getMaxLength());
				freetextEdit.setFilters(FilterArray);
			}
			// if the type is numeric, add numeric-specific rules
			if (ConstantUtil.NUMERIC_VALIDATION_TYPE.equalsIgnoreCase(rule
					.getValidationType())) {
				DigitsKeyListener MyDigitKeyListener = new DigitsKeyListener(
						rule.getAllowSigned(), rule.getAllowDecimal());
				freetextEdit.setKeyListener(MyDigitKeyListener);
			}

			// if we have NAME_VALIDATION_TYPE validation, we need to listen to loss of focus
			// and make sure each word is capitalized
			if (ConstantUtil.NAME_VALIDATION_TYPE.equalsIgnoreCase(rule.getValidationType())) {
				freetextEdit
						.setOnFocusChangeListener(new OnFocusChangeListener() {
							public void onFocusChange(View view,
									boolean hasFocus) {
								if (!hasFocus) {
									EditText textEdit = (EditText) view;
									if (textEdit.getText() != null) {
										String text = textEdit.getText()
												.toString();
										StringTokenizer strTok = new StringTokenizer(
												text, " ");
										StringBuilder builder = new StringBuilder();
										while (strTok.hasMoreTokens()) {
											String word = strTok.nextToken();
											builder.append(word.substring(0, 1)
													.toUpperCase());
											builder.append(word.substring(1));
											if (strTok.hasMoreTokens()) {
												builder.append(" ");
											}
										}
										textEdit.setText(builder.toString());
										// now capture the response
										captureResponse();
									}
								}
							}
						});
			}
		}
		tr.addView(freetextEdit);
		addView(tr);
	}

	/**
	 * pulls the data out of the fields and saves it as a response object
	 */
	public void captureResponse() {
		setResponse(new QuestionResponse(freetextEdit.getText().toString(),
				ConstantUtil.VALUE_RESPONSE_TYPE, getQuestion().getId()));
	}

	public void rehydrate(QuestionResponse resp) {
		super.rehydrate(resp);
		if (resp != null) {
			freetextEdit.setText(resp.getValue());
		}
	}

	public void resetQuestion() {
		super.resetQuestion();
		freetextEdit.setText("");
	}
}
