package com.gallatinsystems.survey.device.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
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
public class QuestionAdapter extends ArrayAdapter<Question> implements
        RadioGroup.OnCheckedChangeListener {

    private List<Question> questions;
    private LayoutInflater inflator;
    private String[] answers;
    private ListView listView;

    /**
     * install the list of questions that should be bound to the view
     * 
     * @param context
     * @param textViewResourceId
     * @param qList
     */
    public QuestionAdapter(Context context, int textViewResourceId,
            List<Question> qList, ListView lv) {
        super(context, textViewResourceId, qList);
        listView = lv;
        answers = new String[qList.size()];

        inflator = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.questions = qList;
    }

    /**
     * called by the system in order to render the questions to the screen.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        Context currentContext = getContext();

        QuestionViewHolder viewHolder;
        if (v == null) {
            viewHolder = new QuestionViewHolder();
            v = inflator.inflate(R.layout.question, null);
            viewHolder.text = (TextView) v.findViewById(R.id.questionText);
            viewHolder.optionGroup = (RadioGroup) v
                    .findViewById(R.id.answerRadioGroup);
            viewHolder.optionGroup.setOnCheckedChangeListener(this);
            viewHolder.freetextEdit = (EditText) v
                    .findViewById(R.id.answerEditText);
            v.setTag(viewHolder);
        } else {
            viewHolder = (QuestionViewHolder) v.getTag();
        }

        Question q = questions.get(position);
        if (q != null) {// && !viewHolder.isInitialized) {
            // not sure if this is correct
            resetViewHolder(viewHolder);

            viewHolder.text.setText(q.getText());
            if (Question.OPTION_TYPE.equalsIgnoreCase(q.getType())) {
                if (q.getOptions() != null) {
                    viewHolder.optionGroup.setVisibility(View.VISIBLE);

                    int i = 0;
                    for (Option o : q.getOptions()) {
                        RadioButton rb = new RadioButton(currentContext);
                        rb.setText(o.getText());

                        viewHolder.optionGroup.addView(rb, i++,
                                new LayoutParams(LayoutParams.FILL_PARENT,
                                        LayoutParams.WRAP_CONTENT));
                        if (answers.length > position
                                && answers[position] != null) {
                            if (o.getText().equals(answers[position])) {
                                rb.setChecked(true);
                            }
                        }
                    }
                    // TODO: handle the "other" text box
                }
            } else if (Question.FREE_TYPE.equalsIgnoreCase(q.getType())) {
                viewHolder.freetextEdit.setVisibility(View.VISIBLE);
            }
            viewHolder.isInitialized = true;
        }
        return v;
    }

    private void resetViewHolder(QuestionViewHolder viewHolder) {
        viewHolder.optionGroup.setVisibility(View.GONE);
        viewHolder.optionGroup.removeAllViews();
        viewHolder.freetextEdit.setVisibility(View.GONE);
    }

    public int getViewTypeCount() {
        return 1;
    }

    /**
     * private class used to minimize lookups for views by IDs since that is a
     * very expensive operation
     * 
     * @author Christopher Fagiani
     * 
     */
    private class QuestionViewHolder {
        TextView text;
        RadioGroup optionGroup;
        EditText freetextEdit;
        boolean isInitialized;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {   
        ViewParent parent = group.getParent();
        while(parent != null && !(parent instanceof ListView)){
            parent = parent.getParent();
        }
        if(parent!= null){
            answers[((ListView)parent).getPositionForView(group)]=(((RadioButton)group.findViewById(checkedId)).getText()).toString();
        }
    }

}
