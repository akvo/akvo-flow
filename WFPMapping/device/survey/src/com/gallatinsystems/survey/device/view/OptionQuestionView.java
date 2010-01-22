package com.gallatinsystems.survey.device.view;

import android.content.Context;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableRow;

import com.gallatinsystems.survey.device.domain.Option;
import com.gallatinsystems.survey.device.domain.Question;

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

    public OptionQuestionView(Context context, Question q) {
        super(context, q);
        init();
    }

    private void init() {
        Context context = getContext();
        if (question.getOptions() != null) {
            TableRow tr = new TableRow(context);
            optionGroup = new RadioGroup(context);
            int i = 0;
            for (Option o : question.getOptions()) {
                RadioButton rb = new RadioButton(context);
                rb.setText(o.getText());
                optionGroup.addView(rb, i++, new LayoutParams(
                        LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            }
            tr.addView(optionGroup);
            addView(tr);
        }
    }
}
