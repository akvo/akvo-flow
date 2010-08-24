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
	public boolean performSummarization(String key, String type, Integer offset) {
		if (key != null) {
			SurveyInstanceDAO instanceDao = new SurveyInstanceDAO();
			List<QuestionAnswerStore> answers = instanceDao
					.listQuestionAnswerStore(new Long(key));
			if (answers != null) {
				int i = 0;
				if(offset != null){
					i = offset;
				}else{
					offset = 0;
				}
				//process BATCH_SIZE items
				while(i < answers.size() && i< offset+BATCH_SIZE){
					SurveyQuestionSummaryDao.incrementCount(answers.get(i));
					i++;
				}
				//if we still have more answers to process, return false
				if(i < answers.size()){
					return false;
				}
			}
		}
		return true;
	}

}
