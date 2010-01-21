package com.gallatinsystems.survey.device.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.gallatinsystems.survey.device.R;
import com.gallatinsystems.survey.device.domain.Option;
import com.gallatinsystems.survey.device.domain.Question;

/**
 * Handles the rendering of a custom view for each question. The view will
 * depend on the type of the question as well as the values defined in the
 * installed Question objects. Since this class extends ArrayAdapter it can used
 * with a ListView to render the question in List form
 * 
 * @author Christopher Fagiani
 * 
 */
public class QuestionAdapter extends ArrayAdapter<Question> {

    private List<Question> questions;

    /**
     * install the list of questions that should be bound to the view
     * 
     * @param context
     * @param textViewResourceId
     * @param qList
     */
    public QuestionAdapter(Context context, int textViewResourceId,
            List<Question> qList) {
        super(context, textViewResourceId, qList);
        this.questions = qList;
    }

    /**
     * called by the system in order to render the questions to the screen.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        Context currentContext = getContext();
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) currentContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.question, null);
        }
        // TODO: change the layout for the questions to have place holder for
        // each type
        // TODO: hide irrelevant place holders (based on type
        Question q = questions.get(position);
        if (q != null) {
            TextView tt = (TextView) v.findViewById(R.id.questionText);
            if (tt != null) {
                tt.setText(q.getText());
            }
            // TODO: only do this if type is "OPTION"
            if (q.getOptions() != null) {
                RadioGroup rg = (RadioGroup) v
                        .findViewById(R.id.answerRadioGroup);
                int i = 0;
                for (Option o : q.getOptions()) {
                    RadioButton rb = new RadioButton(currentContext);
                    rb.setText(o.getText());
                    rg.addView(rb, i++,
                            new LayoutParams(LayoutParams.FILL_PARENT,
                                    LayoutParams.WRAP_CONTENT));
                }
            }
        }
        return v;
    }
}
