package com.gallatinsystems.survey.device.view;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableRow;
import android.widget.RadioGroup.OnCheckedChangeListener;

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
    private Map<Integer, String> idToValueMap;

    public OptionQuestionView(Context context, Question q) {
        super(context, q);
        init();
    }

    private void init() {
        Context context = getContext();
        idToValueMap = new HashMap<Integer, String>();
        if (question.getOptions() != null) {
            TableRow tr = new TableRow(context);
            optionGroup = new RadioGroup(context);
            optionGroup
                    .setOnCheckedChangeListener(new OnCheckedChangeListener() {
                        public void onCheckedChanged(RadioGroup group,
                                int checkedId) {
                            setCurrentValue(idToValueMap.get(checkedId));
                        }
                    });
            int i = 0;
            for (Option o : question.getOptions()) {
                RadioButton rb = new RadioButton(context);
                rb.setText(o.getText());
                optionGroup.addView(rb, i++, new LayoutParams(
                        LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
                idToValueMap.put(rb.getId(), o.getText());
            }
            tr.addView(optionGroup);
            addView(tr);
        }
    }
}
