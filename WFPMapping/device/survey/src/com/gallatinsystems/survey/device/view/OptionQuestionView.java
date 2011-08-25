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
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.gallatinsystems.survey.device.R;
import com.gallatinsystems.survey.device.domain.AltText;
import com.gallatinsystems.survey.device.domain.Option;
import com.gallatinsystems.survey.device.domain.Question;
import com.gallatinsystems.survey.device.domain.QuestionResponse;
import com.gallatinsystems.survey.device.event.QuestionInteractionEvent;
import com.gallatinsystems.survey.device.util.ConstantUtil;
import com.gallatinsystems.survey.device.util.ViewUtil;

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
	private TextView otherText;
	private Map<Integer, String> idToValueMap;
	private volatile boolean suppressListeners = false;
	private String latestOtherText;
	public static boolean promptOnChange;

	public OptionQuestionView(Context context, Question q, String defaultLang,
			String[] langCodes, boolean readOnly) {
		super(context, q, defaultLang, langCodes, readOnly);
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

				initializeSpinnerOptions();
				// set the selection to the first element
				spinner.setSelection(0);

				spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
					@SuppressWarnings("rawtypes")
					public void onItemSelected(AdapterView parent, View view,
							int position, long id) {
						if (!suppressListeners) {
							spinner.requestFocus();
							// if position is greater than the size of the
							// array then OTHER is selected
							if (position > question.getOptions().size()) {
								// only display the dialog if OTHER isn't
								// already populated as the response. need this
								// to suppress the OTHER dialog
								if (getResponse() == null
										|| !getResponse()
												.getType()
												.equals(ConstantUtil.OTHER_RESPONSE_TYPE)) {
									displayOtherDialog();
								}
							} else if (position == 0) {
								if(otherText != null){
									otherText.setText("");
								}
								setResponse(new QuestionResponse("",
										ConstantUtil.VALUE_RESPONSE_TYPE,
										question.getId()));
							} else {
								if(otherText != null){
									otherText.setText("");
								}
								setResponse(new QuestionResponse(question
										.getOptions()
										.get(position > 0 ? position - 1 : 0)
										.getText(),
										ConstantUtil.VALUE_RESPONSE_TYPE,
										question.getId()));
							}
						}
					}

					@SuppressWarnings("rawtypes")
					public void onNothingSelected(AdapterView parent) {
						if (!suppressListeners) {
							setResponse(new QuestionResponse("",
									ConstantUtil.VALUE_RESPONSE_TYPE, question
											.getId()));
						}
					}
				});
				tr.addView(spinner);
				if (readOnly) {
					spinner.setEnabled(false);
				}
			} else if (!question.isAllowMultiple()) {
				optionGroup = new RadioGroup(context);
				optionGroup
						.setOnCheckedChangeListener(new OnCheckedChangeListener() {
							public void onCheckedChanged(RadioGroup group,
									int checkedId) {
								optionGroup.requestChildFocus(
										optionGroup.findViewById(checkedId),
										optionGroup);
								handleSelection(checkedId, true);
							}
						});
				int i = 0;
				for (int j = 0; j < options.size(); j++) {
					Option o = options.get(j);
					RadioButton rb = new RadioButton(context);
					rb.setLongClickable(true);
					rb.setOnLongClickListener(new OnLongClickListener() {
						@Override
						public boolean onLongClick(View v) {
							AlertDialog.Builder builder = new AlertDialog.Builder(
									getContext());
							TextView tipText = new TextView(getContext());
							tipText.setText(((RadioButton) (v)).getText(),
									BufferType.SPANNABLE);
							builder.setTitle(R.string.optiontext);
							builder.setView(tipText);
							builder.setPositiveButton(R.string.okbutton,
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											dialog.cancel();
										}
									});
							builder.show();
							return true;
						}
					});
					rb.setText(formOptionText(o), BufferType.SPANNABLE);
					rb.setWidth(getMaxTextWidth());
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
				if (readOnly) {
					for (int j = 0; j < optionGroup.getChildCount(); j++) {
						optionGroup.getChildAt(j).setEnabled(false);
					}
				}
			} else {
				checkBoxes = new ArrayList<CheckBox>();
				for (int i = 0; i < options.size(); i++) {
					TableRow boxRow = new TableRow(context);
					CheckBox box = new CheckBox(context);
					box.setId(i);
					box.setWidth(getMaxTextWidth());
					checkBoxes.add(box);
					box.setText(formOptionText(options.get(i)),
							BufferType.SPANNABLE);
					box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							handleSelection(buttonView.getId(), isChecked);
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
					box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							handleSelection(buttonView.getId(), isChecked);
						}
					});
					checkBoxes.add(box);
					box.setText(OTHER_TEXT);
					idToValueMap.put(box.getId(), OTHER_TEXT);
					boxRow.addView(box);
					addView(boxRow);
				}
				if (readOnly) {
					for (int i = 0; i < checkBoxes.size(); i++) {
						checkBoxes.get(i).setEnabled(false);
					}
				}
			}			
			if (tr.getChildCount() > 0) {
				addView(tr);
			}
			if(question.isAllowOther()){
				otherText = new TextView(context);
				otherText.setWidth(getMaxTextWidth());
				addView(otherText);
			}
		}
		suppressListeners = false;
	}

	/**
	 * updates the question's visible languages
	 * 
	 * @param languageCodes
	 */
	@Override
	public void updateSelectedLanguages(String[] languageCodes) {
		super.updateSelectedLanguages(languageCodes);
		if (ConstantUtil.SPINNER_RENDER_MODE.equalsIgnoreCase(question
				.getRenderType())) {
			initializeSpinnerOptions();
			rehydrate(getResponse(true));
		} else {
			ArrayList<Option> options = question.getOptions();
			if (question.isAllowMultiple()) {
				for (int i = 0; i < checkBoxes.size(); i++) {
					// make sure we have a corresponding option (i.e. not the
					// OTHER option)
					if (i < options.size()) {
						checkBoxes.get(i).setText(
								formOptionText(options.get(i)),
								BufferType.SPANNABLE);
					}
				}
			} else {

				for (int i = 0; i < optionGroup.getChildCount(); i++) {
					// make sure we have a corresponding option (i.e. not the
					// OTHER option)
					if (i < options.size()) {
						((RadioButton) (optionGroup.getChildAt(i)))
								.setText(formOptionText(options.get(i)));
					}
				}
			}
		}
	}

	/**
	 * sets the spinner content
	 */
	private void initializeSpinnerOptions() {
		int extras = 1;
		if (question.isAllowOther()) {
			extras++;
		}
		ArrayList<Option> options = question.getOptions();
		Spanned[] optionArray = new Spanned[options.size() + extras];
		optionArray[0] = Html.fromHtml("");
		for (int i = 0; i < options.size(); i++) {
			optionArray[i + 1] = formOptionText(options.get(i));
		}
		// put the "other" option in the last slot in the array
		if (question.isAllowOther()) {
			optionArray[optionArray.length - 1] = Html.fromHtml(OTHER_TEXT);
		}
		ArrayAdapter<CharSequence> optionAdapter = new ArrayAdapter<CharSequence>(
				getContext(), android.R.layout.simple_spinner_item, optionArray);
		optionAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(optionAdapter);
	}

	/**
	 * forms the text for an option based on the visible languages
	 * 
	 * @param opt
	 * @return
	 */
	private Spanned formOptionText(Option opt) {
		boolean isFirst = true;
		StringBuilder text = new StringBuilder();
		for (int i = 0; i < langs.length; i++) {
			if (getDefaultLang().equalsIgnoreCase(langs[i])) {
				if (!isFirst) {
					text.append(" / ");
				} else {
					isFirst = false;
				}
				text.append(TextUtils.htmlEncode(opt.getText()));

			} else {
				AltText txt = opt.getAltText(langs[i]);
				if (txt != null) {
					if (!isFirst) {
						text.append(" / ");
					} else {
						isFirst = false;
					}
					text.append("<font color='");
					// spinners have black backgrounds so if the text color is
					// white, make it black so it shows up
					if (ConstantUtil.WHITE_COLOR.equalsIgnoreCase(colors[i])
							&& ConstantUtil.SPINNER_RENDER_MODE
									.equalsIgnoreCase(question.getRenderType())) {
						text.append(ConstantUtil.BLACK_COLOR);
					} else {
						text.append(colors[i]);
					}
					text.append("'>")
							.append(TextUtils.htmlEncode(txt.getText()))
							.append("</font>");
				}
			}
		}
		return Html.fromHtml(text.toString());
	}

	/**
	 * prompts the user to confirm they want to change the value and, if so,
	 * populates the QuestionResponse.
	 * 
	 * @param checkedId
	 * @param isChecked
	 */
	private void handleSelection(final int checkedId, final boolean isChecked) {
		if (!suppressListeners) {
			QuestionResponse r = getResponse(true);
			if (r != null && r.getValue() != null
					&& r.getValue().trim().length() > 0 && promptOnChange) {
				ViewUtil.showConfirmDialog(R.string.confirmchangetitle,
						R.string.confirmchangetext, getContext(), true,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								handleSelectionInternal(checkedId, isChecked);
							}
						}, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// if they select "Cancel", then undo the change
								rehydrate(getResponse(true));
							}
						});
			} else {
				handleSelectionInternal(checkedId, isChecked);
			}
		}
	}

	/**
	 * populates the QuestionResponse object based on the current state of the
	 * selected option(s)
	 * 
	 * @param checkedId
	 * @param isChecked
	 */
	private void handleSelectionInternal(int checkedId, boolean isChecked) {
		if (OTHER_TEXT.equals(idToValueMap.get(checkedId))) {
			// only display the dialog if OTHER isn't already populated as
			// the response need this to suppress the OTHER dialog
			if (isChecked
					&& (getResponse() == null || !getResponse().getType()
							.equals(ConstantUtil.OTHER_RESPONSE_TYPE))) {
				displayOtherDialog();
			} else if (!isChecked && getResponse() != null) {
				//since they unchecked "Other", clear the display
				otherText.setText("");				
				getResponse().setType(ConstantUtil.VALUE_RESPONSE_TYPE);
			}

		} else {
			if (!question.isAllowMultiple()
					|| (question.isAllowMultiple() && (getResponse() == null
							|| getResponse().getValue() == null || getResponse()
							.getValue().trim().length() == 0))) {
				//if we don't allow multiple and they didn't select other, we can clear the otherText
				otherText.setText("");
				setResponse(new QuestionResponse(idToValueMap.get(checkedId),
						ConstantUtil.VALUE_RESPONSE_TYPE, question.getId()));
			} else {
				// if there is already a response and we support multiple,
				// we have to combine
				QuestionResponse r = getResponse();
				String newResponse = getMultipleSelections(r);
				r.setValue(newResponse);
				r.setType(ConstantUtil.VALUE_RESPONSE_TYPE);
				notifyQuestionListeners(QuestionInteractionEvent.QUESTION_ANSWER_EVENT);
			}
		}
	}

	/**
	 * forms a delimited string containing all selected options not including
	 * OTHER
	 * 
	 * @param r
	 * @return
	 */
	private String getMultipleSelections(QuestionResponse r) {
		StringBuffer newResponse = new StringBuffer();
		int count = 0;
		if (checkBoxes != null) {
			for (int i = 0; i < checkBoxes.size(); i++) {
				if (checkBoxes.get(i).isChecked()) {
					if (count > 0) {
						newResponse.append("|");
					}
					if (!OTHER_TEXT.equals(idToValueMap.get(checkBoxes.get(i)
							.getId()))) {
						newResponse.append(idToValueMap.get(checkBoxes.get(i)
								.getId()));
					} else {
						// if OTHER is selected
						newResponse.append(latestOtherText);

					}
					count++;
				}
			}
		}
		return newResponse.toString();
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
		otherDialog.setPositiveButton(R.string.okbutton,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						EditText val = (EditText) rootView
								.findViewById(R.id.otherField);
						latestOtherText = val.getText().toString();
						if (latestOtherText == null) {
							latestOtherText = "";
						} else {
							latestOtherText = latestOtherText.trim();
						}
						if (getQuestion().isAllowMultiple()
								&& getResponse() != null
								&& getResponse().getValue() != null) {
							// if we support multiple, we need to append the
							// answer
							QuestionResponse r = getResponse();
							String responseText = getMultipleSelections(r);
							if (responseText.trim().length() > 0) {
								responseText = responseText + "|"
										+ latestOtherText;
							} else {
								responseText = latestOtherText;
							}
							setResponse(new QuestionResponse(responseText,
									ConstantUtil.OTHER_RESPONSE_TYPE, question
											.getId()));
						} else {
							// if we aren't supporting multiple or we don't
							// already have a value, just set it
							setResponse(new QuestionResponse(latestOtherText,
									ConstantUtil.OTHER_RESPONSE_TYPE, question
											.getId()));
						}
						//update the UI with the other text
						
						otherText.setText(latestOtherText);
						
						dialog.dismiss();
					}
				});
		otherDialog.setNegativeButton(R.string.cancelbutton,
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
	@Override
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
						if(idToValueMap.get(key)
									.equals(OTHER_TEXT)){
							String txt = resp.getValue();
							if(txt != null){
								otherText.setText(txt);
							}
						}
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
						} else if (ConstantUtil.OTHER_RESPONSE_TYPE.equals(resp
								.getType())
								&& OTHER_TEXT.equals(idToValueMap.get(key))) {
							checkBoxes.get(key.intValue()).setChecked(true);
							// the last token is always the Other text (even if
							// it's blank)
							latestOtherText = valList.get(valList.size() - 1);
							otherText.setText(latestOtherText);
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
						if(idToValueMap.get(key)
								.equals(OTHER_TEXT)){
						String txt = resp.getValue();
						if(txt != null){
							otherText.setText(txt);
						}
					}
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
					if(resp.getValue() != null){
					otherText.setText(resp.getValue());
					}
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
		// this is just to trigger listeners now that the view is updated
		setResponse(resp);
	}

	/**
	 * clears the selected option
	 */
	@Override
	public void resetQuestion(boolean fireEvent) {
		super.resetQuestion(fireEvent);
		suppressListeners = true;
		if (optionGroup != null) {
			optionGroup.clearCheck();
		}
		if (spinner != null) {
			spinner.setSelection(0);
		}
		if (checkBoxes != null) {
			for (int i = 0; i < checkBoxes.size(); i++) {
				checkBoxes.get(i).setChecked(false);
			}
		}
		suppressListeners = false;
	}

	@Override
	public void setTextSize(float size) {
		super.setTextSize(size);
		if (optionGroup != null && optionGroup.getChildCount() > 0) {
			for (int i = 0; i < optionGroup.getChildCount(); i++) {
				((RadioButton) (optionGroup.getChildAt(i))).setTextSize(size);
			}
		} else if (checkBoxes != null && checkBoxes.size() > 0) {
			for (int i = 0; i < checkBoxes.size(); i++) {
				checkBoxes.get(i).setTextSize(size);
			}
		}
	}
}
