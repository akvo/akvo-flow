package com.gallatinsystems.survey.device.view;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost.TabContentFactory;

import com.gallatinsystems.survey.device.R;
import com.gallatinsystems.survey.device.domain.Question;
import com.gallatinsystems.survey.device.domain.QuestionGroup;

public class SurveyTabContentFactory implements TabContentFactory {

	private QuestionGroup questionGroup;
	private Context context;

	public SurveyTabContentFactory(Context c, QuestionGroup qg) {
		questionGroup = qg;
		context = c;
	}

	@Override
	public View createTabContent(String tag) {
		ListView listView = new ListView(context);
		if (questionGroup != null && questionGroup.getQuestions() != null)
			listView.setAdapter(new ArrayAdapter<Question>(context,
					R.layout.question, R.id.questionText, questionGroup.getQuestions()));
		/*
		 * for (Question q : questionGroup.getQuestions()) {
		 * listView.addView(new OptionQuestionView(context, q)); }
		 */
		return listView;
	}
}
