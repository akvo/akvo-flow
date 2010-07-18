package org.waterforpeople.mapping.analytics;

import java.util.List;

import org.waterforpeople.mapping.analytics.dao.SurveyQuestionSummaryDao;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.QuestionAnswerStore;

import com.gallatinsystems.framework.analytics.summarization.DataSummarizer;

/**
 * This class will summarize survey information by updating counts by response
 * type by survey. The key should be a key of a survey instance object
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyQuestionSummarizer implements DataSummarizer {

	@Override
	public boolean performSummarization(String key, String type) {
		if (key != null) {
			SurveyInstanceDAO instanceDao = new SurveyInstanceDAO();
			List<QuestionAnswerStore> answers = instanceDao
					.listQuestionAnswerStore(new Long(key));
			if (answers != null) {
				for (QuestionAnswerStore answer : answers) {
					SurveyQuestionSummaryDao.incrementCount(answer);
				}
			}
		}
		return true;
	}

}
