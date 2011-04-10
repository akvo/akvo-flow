package com.gallatinsystems.survey.device.view;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

import com.gallatinsystems.survey.device.R;
import com.gallatinsystems.survey.device.domain.Question;
import com.gallatinsystems.survey.device.domain.QuestionResponse;
import com.gallatinsystems.survey.device.util.ConstantUtil;

/**
 * Similar to Option question type except this also includes a spinner where the
 * user can select the "strength" of their preference for the answer selected.
 * 
 * @author Christopher Fagiani
 * 
 */
public class StrengthQuestionView extends OptionQuestionView {

	private String STRENGTH_TEXT;
	private Spinner strengthSpinner;
	private volatile boolean suppressListeners = false;

	public StrengthQuestionView(Context context, Question q,
			String[] langCodes, boolean readOnly) {
		super(context, q, langCodes, readOnly);
		STRENGTH_TEXT = getResources().getString(R.string.strengthtext);
		suppressListeners = true;
		init();
		suppressListeners = false;
	}

	protected void init() {

		Context context = getContext();
		strengthSpinner = new Spinner(context);
		String[] optionArray = new String[(getQuestion().getStrengthMax() - getQuestion()
				.getStrengthMin()) + 2];
		optionArray[0] = "";
		int count = 1;
		for (int val = getQuestion().getStrengthMin(); val <= getQuestion()
				.getStrengthMax(); val++) {
			optionArray[count++] = "" + val;
		}
		ArrayAdapter<CharSequence> optionAdapter = new ArrayAdapter<CharSequence>(
				getContext(), android.R.layout.simple_spinner_item, optionArray);
		optionAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		strengthSpinner.setAdapter(optionAdapter);		

		strengthSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			@SuppressWarnings("rawtypes")
			public void onItemSelected(AdapterView parent, View view,
					int position, long id) {
				if (!suppressListeners) {
					strengthSpinner.requestFocus();
					if (position <= 0) {
						setResponseStrength(null);
					} else {
						setResponseStrength(""
								+ (getQuestion().getStrengthMin() - 1 + position));
					}
				}
			}

			@Override
			@SuppressWarnings("rawtypes")
			public void onNothingSelected(AdapterView parent) {
				// no-op
			}
		});
		TableRow tr = new TableRow(context);
		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);
		TextView labelView = new TextView(context);
		labelView.setText(STRENGTH_TEXT);
		layout.addView(labelView);
		layout.addView(strengthSpinner);
		tr.addView(layout);
		addView(tr);

	}

	private void setResponseStrength(String val) {
		QuestionResponse resp = getResponse(true);
		if (resp != null) {
			resp.setStrength(val);
		} else {
			setResponse(new QuestionResponse(null, null, getQuestion().getId(),
					"", ConstantUtil.VALUE_RESPONSE_TYPE, "true", val));
		}
	}

	@Override
	public void rehydrate(QuestionResponse resp) {
		suppressListeners = true;
		super.rehydrate(resp);
		if (resp.getStrength() != null
				&& resp.getStrength().trim().length() > 0) {
			int val = Integer.parseInt(resp.getStrength());
			strengthSpinner
					.setSelection((val - getQuestion().getStrengthMin()) + 1);
		}
		suppressListeners = false;
	}
}
