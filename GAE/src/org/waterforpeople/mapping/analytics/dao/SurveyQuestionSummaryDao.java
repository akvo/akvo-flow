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
	public static synchronized void incrementCount(QuestionAnswerStore answer) {
		PersistenceManager pm = PersistenceFilter.getManager();
		javax.jdo.Query query = pm.newQuery(SurveyQuestionSummary.class);
		query
				.setFilter("questionId == questionIdParam && response == answerParam");
		query.declareParameters("String questionIdParam, String answerParam");
		List results = (List) query.execute(answer.getQuestionID(), answer
				.getValue());
		SurveyQuestionSummary summary = null;
		if (results == null || results.size() == 0) {
			summary = new SurveyQuestionSummary();
			summary.setCount(new Long(1));
			summary.setQuestionId(answer.getQuestionID());
			summary.setResponse(answer.getValue());
		} else {
			summary = (SurveyQuestionSummary) results.get(0);
			summary.setCount(summary.getCount() + 1);
		}
		SurveyQuestionSummaryDao thisDao = new SurveyQuestionSummaryDao();
		thisDao.save(summary);
	}

}
