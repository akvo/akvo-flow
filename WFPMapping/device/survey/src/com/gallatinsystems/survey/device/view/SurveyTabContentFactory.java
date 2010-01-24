package com.gallatinsystems.survey.device.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TabHost.TabContentFactory;

import com.gallatinsystems.survey.device.SurveyViewActivity;
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
    public View createTabContent(String tag) {
        // TODO: implement dependent questions
        // TODO: add save/clear buttons to bottom of view. probably need a
        ScrollView scrollView = new ScrollView(context);
        TableLayout table = new TableLayout(context);
        LayoutInflater inflator = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        scrollView.addView(table);

        for (Question q : questionGroup.getQuestions()) {
            QuestionView questionView = null;

            TableRow tr = new TableRow(context);
            tr.setLayoutParams(new ViewGroup.LayoutParams(
                    LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            if (Question.OPTION_TYPE.equalsIgnoreCase(q.getType())) {
                questionView = new OptionQuestionView(context, q);

            } else if (Question.FREE_TYPE.equalsIgnoreCase(q.getType())) {
                questionView = new FreetextQuestionView(context, q);
            } else if (Question.PHOTO_TYPE.equalsIgnoreCase(q.getType())) {
                questionView = new PhotoQuestionView(context, q);
            } else if (Question.GEO_TYPE.equalsIgnoreCase(q.getType())) {
                questionView = new GeoQuestionView(context, q);
            } else {
                questionView = new QuestionView(context, q);
            }

            questionView
                    .addQuestionInteractionListener((SurveyViewActivity) context);
            tr.addView(questionView);
            table.addView(tr);
        }
        return scrollView;
    }

}
