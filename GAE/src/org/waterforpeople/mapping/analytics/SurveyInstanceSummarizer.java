package org.waterforpeople.mapping.analytics;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.waterforpeople.mapping.analytics.dao.SurveyInstanceSummaryDao;
import org.waterforpeople.mapping.dao.CommunityDao;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.Community;
import org.waterforpeople.mapping.domain.QuestionAnswerStore;
import org.waterforpeople.mapping.domain.SurveyInstance;

import com.gallatinsystems.framework.analytics.summarization.DataSummarizer;

/**
 * Populates SurveyInstanceSummary objects (a roll-up that aggregates survey
 * instances by country/region/day)
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyInstanceSummarizer implements DataSummarizer {

	private static Logger logger = Logger
			.getLogger(SurveyInstanceSummarizer.class.getName());
	// TODO: find a better way of identifying the community?
	private static final String COMMUNITY_QUESTION_ID = "q2";

	/**
	 * looks up a survey instance then finds it's corresponding community (by
	 * looking at the questionAnswerStore until it finds the question with the
	 * ID that matches the configured value. If it finds the community, it will
	 * increment the count for the summary record.
	 */
	@Override
	public boolean performSummarization(String key, String type) {
		if (key != null) {
			SurveyInstanceDAO instanceDao = new SurveyInstanceDAO();
			SurveyInstance instance = instanceDao.getByKey(new Long(key));
			if (instance != null) {
				String communityCode = null;
				if (instance.getQuestionAnswersStore() != null) {
					for (QuestionAnswerStore ans : instance
							.getQuestionAnswersStore()) {
						if (COMMUNITY_QUESTION_ID.equals(ans.getQuestionID())) {
							communityCode = ans.getValue();
							break;
						}
					}
					if (communityCode != null) {
						CommunityDao commDao = new CommunityDao();
						Community community = commDao
								.findCommunityByCode(communityCode);
						if (community != null) {
							SurveyInstanceSummaryDao.incrementCount(community
									.getCommunityCode(), community
									.getCountryCode(), instance.getCollectionDate());
						} else {
							logger
									.log(
											Level.SEVERE,
											"Couldn't find community for instance. Is the questionID set correctly on the summarizer?");
						}
					}
				}
			}
		}
		return true;
	}
}