package com.gallatinsystems.survey.device.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.gallatinsystems.survey.device.domain.Option;
import com.gallatinsystems.survey.device.domain.Question;
import com.gallatinsystems.survey.device.domain.QuestionResponse;

/**
 * Question type that supports the selection of a single option from a list of
 * choices (i.e. a radio button group).
 * 
 * TODO: implement the "other" pop-up
 * 
 * @author Christopher Fagiani
 * 
 */
public class OptionQuestionView extends QuestionView {

	private RadioGroup optionGroup;
	private Spinner spinner;
	private Map<Integer, String> idToValueMap;

	public OptionQuestionView(Context context, Question q) {
		super(context, q);
		init();
	}

	private void init() {
		Context context = getContext();
		idToValueMap = new HashMap<Integer, String>();
		ArrayList<Option> options = question.getOptions();
		if (options != null) {
			TableRow tr = new TableRow(context);
			if (Question.SPINNER_TYPE
					.equalsIgnoreCase(question.getRenderType())) {
				spinner = new Spinner(context);
				String[] optionArray = new String[options.size() + 1];
				optionArray[0] = "";
				for (int i = 0; i < options.size(); i++) {
					optionArray[i + 1] = options.get(i).getText();
				}
				ArrayAdapter<String> optionAdapter = new ArrayAdapter<String>(
						context, android.R.layout.simple_spinner_item,
						optionArray);
				optionAdapter
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinner.setAdapter(optionAdapter);
				spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
					public void onItemSelected(AdapterView parent, View view,
							int position, long id) {						
						setResponse(new QuestionResponse(question.getOptions().get(position>0? position-1:0).getText(),
								QuestionResponse.VALUE_TYPE, question
										.getId()));						
					}
					
					public void onNothingSelected(AdapterView parent) {
						setResponse(new QuestionResponse("",
								QuestionResponse.VALUE_TYPE, question
										.getId()));						
					}
				});
				tr.addView(spinner);

			} else {
				optionGroup = new RadioGroup(context);
				optionGroup
						.setOnCheckedChangeListener(new OnCheckedChangeListener() {
							public void onCheckedChanged(RadioGroup group,
									int checkedId) {
								setResponse(new QuestionResponse(idToValueMap
										.get(checkedId),
										QuestionResponse.VALUE_TYPE, question
												.getId()));
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
				tr.addView(optionGroup);
			}
			addView(tr);
		}
	}

	/**
	 * checks off the correct option based on the response value
	 */
	public void rehydrate(QuestionResponse resp) {
		super.rehydrate(resp);
		if (resp != null) {
			if (optionGroup != null) {
				// the enhanced for loop is ok here
				for (Integer key : idToValueMap.keySet()) {
					if (idToValueMap.get(key).equals(resp.getValue())) {
						optionGroup.check(key);
						break;
					}
				}
			}
			if (spinner != null && resp.getValue() != null) {
				ArrayList<Option> options = question.getOptions();
				boolean found = false;
				for (int i = 0; i < options.size(); i++) {
					if (resp.getValue().equalsIgnoreCase(
							options.get(i).getText())) {
						//need to add 1 because of the initial blank option
						spinner.setSelection(i+1);
						found = true;
						break;
					}
				}
				if(!found){
					spinner.setSelection(0);
				}
			}
		}
	}

	/**
	 * clears the selected option
	 */
	public void resetQuestion() {
		super.resetQuestion();
		if (optionGroup != null) {
			optionGroup.clearCheck();
		}
		if (spinner != null) {
			spinner.setSelection(0);
		}
	}
}
