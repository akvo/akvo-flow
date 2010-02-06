package com.gallatinsystems.survey.device.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.gallatinsystems.survey.device.R;
import com.gallatinsystems.survey.device.domain.Option;
import com.gallatinsystems.survey.device.domain.Question;
import com.gallatinsystems.survey.device.domain.QuestionResponse;

/**
 * Question type that supports the selection of a single option from a list of
 * choices (i.e. a radio button group).
 * 
 * 
 * @author Christopher Fagiani
 * 
 */
public class OptionQuestionView extends QuestionView {

	private static final String OTHER_TEXT = "Other...";
	private RadioGroup optionGroup;
	private Spinner spinner;
	private Map<Integer, String> idToValueMap;
	private boolean suppressListeners = false;

	public OptionQuestionView(Context context, Question q) {
		super(context, q);
		init();
	}

	private void init() {
		Context context = getContext();
		idToValueMap = new HashMap<Integer, String>();
		ArrayList<Option> options = question.getOptions();
		suppressListeners = true;
		if (options != null) {
			TableRow tr = new TableRow(context);
			if (Question.SPINNER_TYPE
					.equalsIgnoreCase(question.getRenderType())) {
				spinner = new Spinner(context);
				int extras = 1;
				if (question.isAllowOther()) {
					extras++;
				}
				String[] optionArray = new String[options.size() + extras];
				optionArray[0] = "";
				for (int i = 0; i < options.size(); i++) {
					optionArray[i + 1] = options.get(i).getText();
				}
				//put the "other" option in the last slot in the array
				if (question.isAllowOther()) {
					optionArray[optionArray.length - 1] = OTHER_TEXT;
				}
				ArrayAdapter<String> optionAdapter = new ArrayAdapter<String>(
						context, android.R.layout.simple_spinner_item,
						optionArray);
				optionAdapter
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinner.setAdapter(optionAdapter);
				//set the selection to the first element
				spinner.setSelection(0);

				spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
					public void onItemSelected(AdapterView parent, View view,
							int position, long id) {
						if (!suppressListeners) {
							// if position is greater than the size of the
							// array then OTHER is selected
							if (position > question.getOptions().size()) {
								// only display the dialog if OTHER isn't
								// already populated as the response. need this
								// to suppress the OTHER dialog
								if (getResponse() == null
										|| !getResponse().getType().equals(
												QuestionResponse.OTHER_TYPE)) {
									displayOtherDialog();
								}
							} else {
								setResponse(new QuestionResponse(question
										.getOptions()
										.get(position > 0 ? position - 1 : 0)
										.getText(),
										QuestionResponse.VALUE_TYPE, question
												.getId()));
							}
						}
					}

					public void onNothingSelected(AdapterView parent) {
						if (!suppressListeners) {
							setResponse(new QuestionResponse("",
									QuestionResponse.VALUE_TYPE, question
											.getId()));
						}
					}
				});
				tr.addView(spinner);

			} else {
				optionGroup = new RadioGroup(context);
				optionGroup
						.setOnCheckedChangeListener(new OnCheckedChangeListener() {
							public void onCheckedChanged(RadioGroup group,
									int checkedId) {
								if (OTHER_TEXT.equals(idToValueMap
										.get(checkedId))) {
									if (!suppressListeners) {
										// only display the dialog if OTHER
										// isn't
										// already populated as the response.
										// need this
										// to suppress the OTHER dialog
										if (getResponse() == null
												|| !getResponse()
														.getType()
														.equals(
																QuestionResponse.OTHER_TYPE)) {
											displayOtherDialog();
										}
									}
								} else {
									setResponse(new QuestionResponse(
											idToValueMap.get(checkedId),
											QuestionResponse.VALUE_TYPE,
											question.getId()));
								}
							}
						});
				int i = 0;
				for (int j = 0; j < options.size(); j++) {
					Option o = options.get(j);
					RadioButton rb = new RadioButton(context);
					rb.setText(o.getText());
					optionGroup.addView(rb, i++,
							new LayoutParams(LayoutParams.FILL_PARENT,
									LayoutParams.WRAP_CONTENT));
					idToValueMap.put(rb.getId(), o.getText());
				}
				if (question.isAllowOther()) {
					RadioButton rb = new RadioButton(context);
					rb.setText(OTHER_TEXT);
					optionGroup.addView(rb, i++,
							new LayoutParams(LayoutParams.FILL_PARENT,
									LayoutParams.WRAP_CONTENT));
					idToValueMap.put(rb.getId(), OTHER_TEXT);
				}
				tr.addView(optionGroup);
			}
			addView(tr);
		}
		suppressListeners = false;
	}

	/**
	 * displays a pop-up dialog where the user can enter in a specific value for
	 * the "OTHER" option in a freetext view.
	 */
	private void displayOtherDialog() {
		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		AlertDialog.Builder otherDialog = new AlertDialog.Builder(getContext());
		final View rootView = inflater.inflate(R.layout.otherdialog,
				(ViewGroup) findViewById(R.id.otherroot));
		otherDialog.setView(rootView);
		otherDialog.setPositiveButton("Ok",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						EditText val = (EditText) rootView
								.findViewById(R.id.otherField);
						String text = val.getText().toString();
						if (text == null) {
							text = "";
						} else {
							text = text.trim();
						}
						setResponse(new QuestionResponse(text,
								QuestionResponse.OTHER_TYPE, question.getId()));
						dialog.dismiss();
					}
				});
		otherDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						setResponse(new QuestionResponse("",
								QuestionResponse.OTHER_TYPE, question.getId()));
						dialog.dismiss();
					}
				});
		otherDialog.show();
	}

	/**
	 * checks off the correct option based on the response value
	 */
	public void rehydrate(QuestionResponse resp) {
		suppressListeners = true;
		super.rehydrate(resp);

		if (resp != null) {
			if (optionGroup != null) {
				// the enhanced for loop is ok here
				for (Integer key : idToValueMap.keySet()) {
					// if the response text matches the text stored for this
					// option ID OR if the response is the "OTHER" type and the
					// id matches the other option, select it
					if (idToValueMap.get(key).equals(resp.getValue())
							|| (QuestionResponse.OTHER_TYPE.equals(resp
									.getType()) && idToValueMap.get(key)
									.equals(OTHER_TEXT))) {
						optionGroup.check(key);
						break;
					}
				}
			}
			if (spinner != null && resp.getValue() != null) {
				ArrayList<Option> options = question.getOptions();
				if (QuestionResponse.OTHER_TYPE.equals(resp.getType())) {
					// since OTHER is the last option and the response is of
					// OTHER type, select the last option in the spinner which
					// is size +1 (accounting for the initial BLANK and the
					// OTHER options which both aren't in the options
					// collection)
					spinner.setSelection(options.size() + 1);
				} else {
					boolean found = false;
					for (int i = 0; i < options.size(); i++) {
						if (resp.getValue().equalsIgnoreCase(
								options.get(i).getText())) {
							// need to add 1 because of the initial blank option
							spinner.setSelection(i + 1);
							found = true;
							break;
						}
					}
					if (!found) {
						spinner.setSelection(0);
					}
				}
			}
		}
		suppressListeners = false;
	}

	/**
	 * clears the selected option
	 */
	public void resetQuestion() {
		super.resetQuestion();
		suppressListeners = true;
		if (optionGroup != null) {
			optionGroup.clearCheck();
		}
		if (spinner != null) {
			spinner.setSelection(0);
		}
		suppressListeners = false;
	}
}
