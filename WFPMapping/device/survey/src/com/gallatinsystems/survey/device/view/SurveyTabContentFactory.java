package com.gallatinsystems.survey.device.view;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TabHost.TabContentFactory;

import com.gallatinsystems.survey.device.R;
import com.gallatinsystems.survey.device.SurveyViewActivity;
import com.gallatinsystems.survey.device.domain.Option;
import com.gallatinsystems.survey.device.domain.Question;
import com.gallatinsystems.survey.device.domain.QuestionGroup;

/**
 * Creates the content for a single tab in the survey (corresponds to a
 * QuestionGroup). The tab will lay out all the questions in the QuestionGroup
 * (passed in at construction) in a List view and will append save/clear buttons
 * to the bottom of the list.
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyTabContentFactory implements TabContentFactory {

    private QuestionGroup questionGroup;
    private SurveyViewActivity context;
    

    /**
     * stores the context and questionGroup to member fields
     * 
     * @param c
     * @param qg
     */
    public SurveyTabContentFactory(SurveyViewActivity c, QuestionGroup qg) {
        questionGroup = qg;
        context = c;
    }

    /**
     * Constructs a view using the question data from the stored questionGroup.
     * This method makes use of a QuestionAdaptor to process individual
     * questions.
     */
    @Override
    public View createTabContent(String tag) {
        // TODO: implement dependent questions
        // TODO: add save/clear buttons to bottom of view. probably need a
        ScrollView scrollView = new ScrollView(context);
        TableLayout table = new TableLayout(context);
        LayoutInflater inflator = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        scrollView.addView(table);

        for (Question q : questionGroup.getQuestions()) {
            View questionView = inflator.inflate(R.layout.question, null);
            TableRow tr = new TableRow(context);
            tr.setLayoutParams(new ViewGroup.LayoutParams(
                    LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

            QuestionViewHolder viewHolder = new QuestionViewHolder();
            viewHolder.text = (TextView) questionView
                    .findViewById(R.id.questionText);
            viewHolder.optionGroup = (RadioGroup) questionView
                    .findViewById(R.id.answerRadioGroup);
            // viewHolder.optionGroup.setOnCheckedChangeListener(this);
            viewHolder.freetextEdit = (EditText) questionView
                    .findViewById(R.id.answerEditText);
            viewHolder.photoButton = (Button) questionView
                    .findViewById(R.id.photoButton);
            viewHolder.okIcon = (ImageView)questionView.findViewById(R.id.okIcon);
            viewHolder.photoButton.setOnClickListener(context);
            viewHolder.text.setText(q.getText());
            if (Question.OPTION_TYPE.equalsIgnoreCase(q.getType())) {
                if (q.getOptions() != null) {
                    viewHolder.optionGroup.setVisibility(View.VISIBLE);

                    int i = 0;
                    for (Option o : q.getOptions()) {
                        RadioButton rb = new RadioButton(context);
                        rb.setText(o.getText());

                        viewHolder.optionGroup.addView(rb, i++,
                                new LayoutParams(LayoutParams.FILL_PARENT,
                                        LayoutParams.WRAP_CONTENT));
                    }
                    // TODO: handle the "other" text box
                }
            } else if (Question.FREE_TYPE.equalsIgnoreCase(q.getType())) {
                viewHolder.freetextEdit.setVisibility(View.VISIBLE);
            } else if (Question.PHOTO_TYPE.equalsIgnoreCase(q.getType())) {
                viewHolder.photoButton.setVisibility(View.VISIBLE);
            }
            viewHolder.isInitialized = true;
            tr.addView(questionView);
            table.addView(tr);
        }
        return scrollView;
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
        Button photoButton;
        boolean isInitialized;
        ImageView okIcon;

    }
   
}
