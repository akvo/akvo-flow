package com.gallatinsystems.survey.device.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;

import com.gallatinsystems.survey.device.R;
import com.gallatinsystems.survey.device.domain.Question;
import com.gallatinsystems.survey.device.domain.QuestionResponse;
import com.gallatinsystems.survey.device.domain.ValidationRule;
import com.gallatinsystems.survey.device.exception.ValidationException;
import com.gallatinsystems.survey.device.util.ConstantUtil;

/**
 * Question that supports free-text input via the keyboard
 * 
 * 
 * @author Christopher Fagiani
 * 
 */
public class FreetextQuestionView extends QuestionView implements
		OnFocusChangeListener {
	private EditText freetextEdit;

	public FreetextQuestionView(Context context, Question q,
			String defaultLang, String[] langCodes, boolean readOnly) {
		super(context, q, defaultLang, langCodes, readOnly);
		init();
	}

	protected void init() {
		Context context = getContext();
		TableRow tr = new TableRow(context);
		freetextEdit = new EditText(context);
		freetextEdit.setWidth(DEFAULT_WIDTH);
		freetextEdit.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
		if (readOnly) {
			freetextEdit.setFocusable(false);
		}
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
		}
		freetextEdit.setOnFocusChangeListener(this);
		freetextEdit.setWidth(screenWidth - 50);

		tr.addView(freetextEdit);
		addView(tr);
	}

	/**
	 * pulls the data out of the fields and saves it as a response object
	 */
	@Override
	public void captureResponse() {
		captureResponse(false);
	}

	@Override
	public void setResponse(QuestionResponse resp) {
		if (resp != null && freetextEdit != null) {
			freetextEdit.setText(resp.getValue());
		}
		super.setResponse(resp);
	}

	/**
	 * pulls the data out of the fields and saves it as a response object,
	 * possibly suppressing listeners
	 */
	public void captureResponse(boolean suppressListeners) {
		setResponse(new QuestionResponse(freetextEdit.getText().toString(),
				ConstantUtil.VALUE_RESPONSE_TYPE, getQuestion().getId()),
				suppressListeners);
	}

	@Override
	public void rehydrate(QuestionResponse resp) {
		super.rehydrate(resp);
		if (resp != null) {
			freetextEdit.setText(resp.getValue());
		}
	}

	@Override
	public void resetQuestion(boolean fireEvent) {
		super.resetQuestion(fireEvent);
		freetextEdit.setText("");
	}

	/**
	 * captures the response and runs validation on loss of focus
	 */
	@Override
	public void onFocusChange(View view, boolean hasFocus) {
		// we need to listen to loss of focus
		// and make sure input is valid
		if (!hasFocus) {
			ValidationRule currentRule = getQuestion().getValidationRule();
			EditText textEdit = (EditText) view;
			if (textEdit.getText() != null
					&& textEdit.getText().toString().trim().length() > 0) {
				if (currentRule != null) {
					try {
						String validatedText = currentRule
								.performValidation(textEdit.getText()
										.toString());
						textEdit.setText(validatedText);
						// now capture the response
						captureResponse();
					} catch (ValidationException e) {
						// if we failed validation, display
						// a message to the user
						AlertDialog.Builder builder = new AlertDialog.Builder(
								getContext());
						builder.setTitle(R.string.validationerrtitle);
						TextView tipText = new TextView(getContext());
						if (ValidationException.TOO_LARGE.equals(e.getType())) {
							String baseText = getResources().getString(
									R.string.toolargeerr);
							tipText.setText(baseText
									+ currentRule.getMaxValString());
						} else if (ValidationException.TOO_SMALL.equals(e
								.getType())) {
							String baseText = getResources().getString(
									R.string.toosmallerr);
							tipText.setText(baseText
									+ currentRule.getMinValString());
						} else {
							tipText.setText(R.string.baddatatypeerr);
						}
						builder.setView(tipText);
						builder.setPositiveButton(R.string.okbutton,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.dismiss();
									}
								});
						builder.show();
					}
				} else {
					captureResponse();
				}
			}
		}
	}
}
