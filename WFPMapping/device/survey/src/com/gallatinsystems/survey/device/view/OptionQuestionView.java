package com.gallatinsystems.survey.device.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.gallatinsystems.survey.device.util.ConstantUtil;

/**
 * Question type that supports the selection of a single option from a list of
 * choices (i.e. a radio button group).
 * 
 * 
 * @author Christopher Fagiani
 * 
 */
public class OptionQuestionView extends QuestionView {

	private String OTHER_TEXT;
	private RadioGroup optionGroup;
	private ArrayList<CheckBox> checkBoxes;
	private Spinner spinner;
	private Map<Integer, String> idToValueMap;
	private boolean suppressListeners = false;

	public OptionQuestionView(Context context, Question q) {
		super(context, q);
		OTHER_TEXT = getResources().getString(R.string.othertext);
		init();
	}

	private void init() {
		Context context = getContext();
		idToValueMap = new HashMap<Integer, String>();
		ArrayList<Option> options = question.getOptions();
		suppressListeners = true;
		if (options != null) {
			TableRow tr = new TableRow(context);
			// spinners aren't compatible with multiple selection. if the survey
			// has both allowMultiple=true and renderMode=spinner, that is an
			// error but we'll just honor the allowMultiple
			if (!question.isAllowMultiple()
					&& ConstantUtil.SPINNER_RENDER_MODE
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
				// put the "other" option in the last slot in the array
				if (question.isAllowOther()) {
					optionArray[optionArray.length - 1] = OTHER_TEXT;
				}
				ArrayAdapter<String> optionAdapter = new ArrayAdapter<String>(
						context, android.R.layout.simple_spinner_item,
						optionArray);
				optionAdapter
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinner.setAdapter(optionAdapter);
				// set the selection to the first element
				spinner.setSelection(0);

				spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
					@SuppressWarnings("unchecked")
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
										|| !getResponse()
												.getType()
												.equals(
														ConstantUtil.OTHER_RESPONSE_TYPE)) {
									displayOtherDialog();
								}
							} else if (position == 0) {
								setResponse(new QuestionResponse("",
										ConstantUtil.VALUE_RESPONSE_TYPE,
										question.getId()));
							} else {
								setResponse(new QuestionResponse(question
										.getOptions()
										.get(position > 0 ? position - 1 : 0)
										.getText(),
										ConstantUtil.VALUE_RESPONSE_TYPE,
										question.getId()));
							}
						}
					}

					@SuppressWarnings("unchecked")
					public void onNothingSelected(AdapterView parent) {
						if (!suppressListeners) {
							setResponse(new QuestionResponse("",
									ConstantUtil.VALUE_RESPONSE_TYPE, question
											.getId()));
						}
					}
				});
				tr.addView(spinner);

			} else if (!question.isAllowMultiple()) {
				optionGroup = new RadioGroup(context);
				optionGroup
						.setOnCheckedChangeListener(new OnCheckedChangeListener() {
							public void onCheckedChanged(RadioGroup group,
									int checkedId) {
								handleSelection(checkedId, true);
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
			} else {
				checkBoxes = new ArrayList<CheckBox>();
				for (int i = 0; i < options.size(); i++) {
					TableRow boxRow = new TableRow(context);
					CheckBox box = new CheckBox(context);
					box.setId(i);
					checkBoxes.add(box);
					box.setText(options.get(i).getText());
					box
							.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

								@Override
								public void onCheckedChanged(
										CompoundButton buttonView,
										boolean isChecked) {
									handleSelection(buttonView.getId(),
											isChecked);
								}
							});
					idToValueMap.put(box.getId(), options.get(i).getText());
					boxRow.addView(box);
					addView(boxRow);
				}
				if (question.isAllowOther()) {
					TableRow boxRow = new TableRow(context);
					CheckBox box = new CheckBox(context);
					box.setId(options.size());
					box
							.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
								@Override
								public void onCheckedChanged(
										CompoundButton buttonView,
										boolean isChecked) {
									handleSelection(buttonView.getId(),
											isChecked);
								}
							});
					checkBoxes.add(box);
					box.setText(OTHER_TEXT);
					idToValueMap.put(box.getId(), OTHER_TEXT);
					boxRow.addView(box);
					addView(boxRow);
				}
			}
			if (tr.getChildCount() > 0) {
				addView(tr);
			}
		}
		suppressListeners = false;
	}

	/**
	 * populates the QuestionResponse object based on the current state of the
	 * selected option(s)
	 * 
	 * @param checkedId
	 * @param isChecked
	 */
	private void handleSelection(int checkedId, boolean isChecked) {
		if (OTHER_TEXT.equals(idToValueMap.get(checkedId))) {
			if (!suppressListeners) {
				// only display the dialog if OTHER isn't already populated as
				// the response need this to suppress the OTHER dialog
				if (isChecked
						&& (getResponse() == null || !getResponse().getType()
								.equals(ConstantUtil.OTHER_RESPONSE_TYPE))) {
					displayOtherDialog();
				} else if (!isChecked && getResponse() != null) {
					getResponse().setType(ConstantUtil.VALUE_RESPONSE_TYPE);
				}
			}
		} else {
			if (!question.isAllowMultiple()
					|| (question.isAllowMultiple() && (getResponse() == null
							|| getResponse().getValue() == null || getResponse()
							.getValue().trim().length() == 0))) {
				setResponse(new QuestionResponse(idToValueMap.get(checkedId),
						ConstantUtil.VALUE_RESPONSE_TYPE, question.getId()));
			} else {
				String newVal = idToValueMap.get(checkedId);
				// if there is already a response and we support multiple, we
				// have to combine
				QuestionResponse r = getResponse();
				String[] vals = r.getValue().split("\\|");
				boolean found = false;
				for (int i = 0; i < vals.length; i++) {
					if (vals[i].equals(newVal)) {
						found = true;
						break;
					}
				}
				if (found && !isChecked) {
					// if it's not longer selected, we need to remove the value
					// from the packed string
					StringBuilder buf = new StringBuilder();
					int count = 0;
					for (int j = 0; j < vals.length; j++) {
						if (!vals[j].equals(newVal)) {
							if (count > 0) {
								buf.append("|");
							}
							buf.append(vals[j]);
							count++;
						}
					}
					r.setValue(buf.toString());
				} else if (isChecked) {
					// add the new val to the packed values
					r.setValue(r.getValue() + "|" + newVal);
					r.setType(ConstantUtil.VALUE_RESPONSE_TYPE);
				}

			}
		}
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
								ConstantUtil.OTHER_RESPONSE_TYPE, question
										.getId()));
						dialog.dismiss();
					}
				});
		otherDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						setResponse(new QuestionResponse("",
								ConstantUtil.OTHER_RESPONSE_TYPE, question
										.getId()));
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
							|| (ConstantUtil.OTHER_RESPONSE_TYPE.equals(resp
									.getType()) && idToValueMap.get(key)
									.equals(OTHER_TEXT))) {
						optionGroup.check(key);
						break;
					}
				}
			} else if (checkBoxes != null) {
				if (resp.getValue() != null
						&& resp.getValue().trim().length() > 0) {
					List<String> valList = Arrays.asList(resp.getValue().split(
							"\\|"));
					for (Integer key : idToValueMap.keySet()) {
						if (valList.contains(idToValueMap.get(key))) {
							checkBoxes.get(key.intValue()).setChecked(true);
						}
					}
				}

				for (Integer key : idToValueMap.keySet()) {
					// if the response text matches the text stored for this
					// option ID OR if the response is the "OTHER" type and the
					// id matches the other option, select it
					if (idToValueMap.get(key).equals(resp.getValue())
							|| (ConstantUtil.OTHER_RESPONSE_TYPE.equals(resp
									.getType()) && idToValueMap.get(key)
									.equals(OTHER_TEXT))) {
						checkBoxes.get(key.intValue()).setChecked(true);
					}
				}
			}
			if (spinner != null && resp.getValue() != null) {
				ArrayList<Option> options = question.getOptions();
				if (ConstantUtil.OTHER_RESPONSE_TYPE.equals(resp.getType())) {
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
