package com.gallatinsystems.survey.device.view;

import java.util.HashMap;
import java.util.Map;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TabHost.TabContentFactory;

import com.gallatinsystems.survey.device.SurveyViewActivity;
import com.gallatinsystems.survey.device.domain.Dependency;
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
    
        scrollView.addView(table);
        Map<String,QuestionView> questionMap = new HashMap<String,QuestionView>();
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
            questionMap.put(q.getId(), questionView);
            questionView
                    .addQuestionInteractionListener((SurveyViewActivity) context);
            tr.addView(questionView);
            table.addView(tr);
        }
        //set up listeners for dependencies
        for (Question q : questionGroup.getQuestions()) {
            if(q.getDependencies()!= null){
                for(Dependency dep: q.getDependencies()){
                    QuestionView parentQ = questionMap.get(dep.getQuestion());
                    QuestionView depQ = questionMap.get(q.getId());
                    if (depQ != null && parentQ != null){
                        parentQ.addQuestionInteractionListener(depQ);
                    }
                }
            }
        }
        return scrollView;
    }

}
