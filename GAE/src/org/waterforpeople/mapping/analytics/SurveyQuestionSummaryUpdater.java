package org.waterforpeople.mapping.analytics;

import org.waterforpeople.mapping.analytics.dao.SurveyQuestionSummaryDao;
import org.waterforpeople.mapping.domain.QuestionAnswerStore;

import com.gallatinsystems.framework.analytics.summarization.DataSummarizer;
import com.gallatinsystems.framework.domain.DataChangeRecord;

/**
 * handles updates to questionSummary objects
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyQuestionSummaryUpdater implements DataSummarizer {

	public SurveyQuestionSummaryUpdater() {
	}

	@Override
	public String getCursor() {
		return null;
	}

	/**
	 * handles changes to question responses by using the DataChangeRecord to
	 * decrement the question response counts for the oldValue and increment the
	 * counts for the newValue in the SurveyQuestionSummary.
	 */
	@Override
	public boolean performSummarization(String key, String type, String value,
			Integer offset, String cursor) {

		DataChangeRecord changeRecord = new DataChangeRecord(value);
		SurveyQuestionSummaryDao.incrementCount(
				constructQAS(changeRecord.getId(), changeRecord.getOldVal()),
				-1);
		if (changeRecord.getNewVal() != null
				&& changeRecord.getNewVal().trim().length() > 0) {
			SurveyQuestionSummaryDao
					.incrementCount(
							constructQAS(changeRecord.getId(),
									changeRecord.getNewVal()), 1);
		}

		return true;
	}

	/**
	 * helper method to create a new QuestionAnswerStore object using the values
	 * passed in.
	 * 
	 * @param id
	 * @param value
	 * @return
	 */
	private QuestionAnswerStore constructQAS(String id, String value) {
		QuestionAnswerStore qas = new QuestionAnswerStore();
		qas.setQuestionID(id);
		qas.setValue(value);
		return qas;
	}
}
