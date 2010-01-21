package com.gallatinsystems.survey.device.adapter;

import java.util.List;

import android.content.Context;
import android.sax.TextElementListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
public class QuestionAdapter extends ArrayAdapter<Question>  {

    private List<Question> questions;
    private LayoutInflater inflator;

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
            viewHolder.freetextEdit = (EditText) v
                    .findViewById(R.id.answerEditText);            
            v.setTag(viewHolder);
        } else {
            viewHolder = (QuestionViewHolder) v.getTag();            
        }

        Question q = questions.get(position);
        if (q != null && !viewHolder.isInitialized) {
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

   
}
