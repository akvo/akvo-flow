package com.gallatinsystems.survey.device.view;

import java.util.ArrayList;
import java.util.HashMap;

import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TabHost.TabContentFactory;

import com.gallatinsystems.survey.device.R;
import com.gallatinsystems.survey.device.SurveyViewActivity;
import com.gallatinsystems.survey.device.dao.SurveyDbAdapter;
import com.gallatinsystems.survey.device.domain.Dependency;
import com.gallatinsystems.survey.device.domain.Question;
import com.gallatinsystems.survey.device.domain.QuestionGroup;
import com.gallatinsystems.survey.device.domain.QuestionResponse;

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
	private HashMap<String, QuestionView> questionMap;
	private SurveyDbAdapter databaseAdaptor;
	private ScrollView scrollView;

	/**
	 * stores the context and questionGroup to member fields
	 * 
	 * @param c
	 * @param qg
	 */
	public SurveyTabContentFactory(SurveyViewActivity c, QuestionGroup qg,
			SurveyDbAdapter dbAdaptor) {
		questionGroup = qg;
		context = c;
		databaseAdaptor = dbAdaptor;
	}

	/**
	 * Constructs a view using the question data from the stored questionGroup.
	 * This method makes use of a QuestionAdaptor to process individual
	 * questions.
	 */
	public View createTabContent(String tag) {
		scrollView = new ScrollView(context);
		TableLayout table = new TableLayout(context);

		scrollView.addView(table);
		questionMap = new HashMap<String, QuestionView>();

		ArrayList<Question> questions = questionGroup.getQuestions();
		for (int i = 0; i < questions.size(); i++) {
			QuestionView questionView = null;
			Question q = questions.get(i);
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
		// set up listeners for dependencies
		// we have to do this after all views are created so it can't be
		// combined with the loop above
		for (int i = 0; i < questions.size(); i++) {
			Question q = questions.get(i);
			ArrayList<Dependency> dependencies = q.getDependencies();
			if (dependencies != null) {
				for (int j = 0; j < dependencies.size(); j++) {
					Dependency dep = dependencies.get(j);
					QuestionView parentQ = questionMap.get(dep.getQuestion());
					QuestionView depQ = questionMap.get(q.getId());
					if (depQ != null && parentQ != null) {
						parentQ.addQuestionInteractionListener(depQ);
					}
				}
			}
		}

		// create save/clear buttons
		TableRow buttonRow = new TableRow(context);
		Button saveButton = new Button(context);
		saveButton.setText(R.string.savebutton);
		buttonRow.addView(saveButton);
		Button clearButton = new Button(context);
		clearButton.setText(R.string.clearbutton);
		buttonRow.addView(clearButton);
		table.addView(buttonRow);

		// set up actions on button press
		clearButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (questionMap != null) {
					for (QuestionView view : questionMap.values()) {
						view.resetQuestion();
					}
					scrollView.scrollTo(0, 0);
				}
			}
		});

		saveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (questionMap != null) {
					// while in general we avoid the enhanced for-loop in the
					// Android VM, we can use it here because we would still
					// need the iterator
					for (QuestionView view : questionMap.values()) {
						saveState(context.getRespondentId());
						view.resetQuestion();
					}
					// create a new response object
					// TODO: this probably needs to move.
					context.setRespondentId(databaseAdaptor
							.createSurveyRespondent(context.getSurveyId()
									.toString()));
					scrollView.scrollTo(0, 0);
				}
			}
		});
		loadState(context.getRespondentId());
		return scrollView;
	}

	/**
	 * loads the state from the database using the respondentId passed in. It
	 * will then use the loaded responses to update the status of the question
	 * views in this tab.
	 * 
	 * @param respondentId
	 */
	public void loadState(Long respondentId) {
		if (respondentId != null) {
			Cursor responseCursor = databaseAdaptor
					.fetchResponsesByRespondent(respondentId.toString());
			context.startManagingCursor(responseCursor);

			while (responseCursor.moveToNext()) {
				String[] cols = responseCursor.getColumnNames();
				QuestionResponse resp = new QuestionResponse();
				for (int i = 0; i < cols.length; i++) {
					if (cols[i].equals(SurveyDbAdapter.RESP_ID_COL)) {
						resp.setId(responseCursor.getLong(i));
					} else if (cols[i]
							.equals(SurveyDbAdapter.SURVEY_RESPONDENT_ID_COL)) {
						resp.setRespondentId(responseCursor.getLong(i));
					} else if (cols[i].equals(SurveyDbAdapter.ANSWER_COL)) {
						resp.setValue(responseCursor.getString(i));
					} else if (cols[i].equals(SurveyDbAdapter.ANSWER_TYPE_COL)) {
						resp.setType(responseCursor.getString(i));
					} else if (cols[i].equals(SurveyDbAdapter.QUESTION_COL)) {
						resp.setQuestionId(responseCursor.getString(i));
					}
				}
				if (questionMap != null) {
					// update the quesiton view to reflect the loaded data
					if (questionMap.get(resp.getQuestionId()) != null) {
						questionMap.get(resp.getQuestionId()).rehydrate(resp);

					}
				}
			}
		}
	}

	/**
	 * persists the current question responses in this tab to the database
	 * 
	 * @param respondentId
	 */
	public void saveState(Long respondentId) {
		if (questionMap != null) {
			for (QuestionView q : questionMap.values()) {
				if (q.getResponse() != null) {
					q.getResponse().setRespondentId(respondentId);
					databaseAdaptor.createOrUpdateSurveyResponse(q
							.getResponse());
				}
			}
		}
	}
}
