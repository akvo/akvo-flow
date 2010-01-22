package com.gallatinsystems.survey.device.view;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TabHost.TabContentFactory;

import com.gallatinsystems.survey.device.R;
import com.gallatinsystems.survey.device.adapter.QuestionAdapter;
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
	private Context context;
	private ListView questionList;

	/**
	 * stores the context and questionGroup to member fields
	 * 
	 * @param c
	 * @param qg
	 */
	public SurveyTabContentFactory(Context c, QuestionGroup qg) {
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
		// TODO: add save/clear buttons to bottom of view. probably need a
		// container
	    questionList = new ListView(context);
		if (questionGroup != null && questionGroup.getQuestions() != null) {
		    questionList.setAdapter(new QuestionAdapter(context, R.id.QuestionLayout,
					questionGroup.getQuestions(), questionList));			
		    questionList.setOnItemClickListener(new OnItemClickListener(){                
                public void onItemClick(AdapterView<?> adapterView, View view,
                        int position, long id) {                    
                    questionList.setSelection(position);
                    
                }});
		}
		return questionList;
	}
}
