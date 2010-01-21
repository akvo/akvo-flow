package com.gallatinsystems.survey.device.view;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.gallatinsystems.survey.device.domain.Question;

public class OptionQuestionView extends FrameLayout {

	public OptionQuestionView(Context context, Question q) {
		super(context);
		TextView textView = new TextView(context);
		textView.setText(q.getText());		
		addView(textView);

	}

}
