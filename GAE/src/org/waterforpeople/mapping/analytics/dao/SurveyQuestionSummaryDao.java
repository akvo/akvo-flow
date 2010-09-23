package org.waterforpeople.mapping.analytics.dao;

import java.util.List;

import javax.jdo.PersistenceManager;

import org.waterforpeople.mapping.analytics.domain.SurveyQuestionSummary;
import org.waterforpeople.mapping.domain.QuestionAnswerStore;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;

/**
 * updates survey question objects
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyQuestionSummaryDao extends BaseDAO<SurveyQuestionSummary> {

	public SurveyQuestionSummaryDao() {
		super(SurveyQuestionSummary.class);
	}

	/**
	 * synchronized static method so that only 1 thread can be updating a
	 * summary at a time. This is inefficient but is the only way we can be sure
	 * we're keeping the count consistent since there is no "select for update"
	 * or sql dml-like construct
	 * 
	 * @param answer
	 */
	@SuppressWarnings("unchecked")
	public static synchronized void incrementCount(QuestionAnswerStore answer,
			int unit) {
		PersistenceManager pm = PersistenceFilter.getManager();
		String answerText = answer.getValue();
		String[] answers;
		if (answerText != null && answerText.contains("|")) {
			answers = answerText.split("\\|");
		} else {
			answers = new String[] { answerText };
		}
		for (int i = 0; i < answers.length; i++) {
			javax.jdo.Query query = pm.newQuery(SurveyQuestionSummary.class);
			query
					.setFilter("questionId == questionIdParam && response == answerParam");
			query
					.declareParameters("String questionIdParam, String answerParam");
			List results = (List) query.execute(answer.getQuestionID(),
					answers[i]);
			SurveyQuestionSummary summary = null;
			if ((results == null || results.size() == 0) && unit > 0) {
				summary = new SurveyQuestionSummary();
				summary.setCount(new Long(1));
				summary.setQuestionId(answer.getQuestionID());
				summary.setResponse(answers[i]);
			} else if (results != null && results.size() > 0) {
				summary = (SurveyQuestionSummary) results.get(0);
				summary.setCount(summary.getCount() + unit);
			}
			if (summary != null) {
				SurveyQuestionSummaryDao summaryDao = new SurveyQuestionSummaryDao();
				if (summary.getCount() > 0) {
					summaryDao.save(summary);
				} else if (summary.getKey() != null) {
					// if count has been decremented to 0 and the object is
					// already
					// persisted, delete it
					summaryDao.delete(summary);
				}
			}
		}
	}

	/**
	 * this method will list all the summary objects for a given question id
	 */
	public List<SurveyQuestionSummary> listByQuestion(String qId) {
		return listByProperty("questionId", qId, "String");
	}

}
