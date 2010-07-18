package org.waterforpeople.mapping.analytics;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.waterforpeople.mapping.analytics.dao.SurveyInstanceSummaryDao;
import org.waterforpeople.mapping.dao.CommunityDao;
import org.waterforpeople.mapping.dao.SurveyAttributeMappingDao;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.Community;
import org.waterforpeople.mapping.domain.QuestionAnswerStore;
import org.waterforpeople.mapping.domain.SurveyAttributeMapping;
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

	private static final String COMMUNITY_QUESTION_ATTRIBUTE = "communityCode";
	private SurveyInstanceDAO instanceDao;
	private SurveyAttributeMappingDao mappingDao;

	public SurveyInstanceSummarizer() {
		instanceDao = new SurveyInstanceDAO();
		mappingDao = new SurveyAttributeMappingDao();
	}

	/**
	 * looks up a survey instance then finds it's corresponding community (by
	 * looking at the questionAnswerStore until it finds the question with the
	 * ID that matches the configured value. If it finds the community, it will
	 * increment the count for the summary record.
	 */
	@Override
	public boolean performSummarization(String key, String type) {
		if (key != null) {

			SurveyInstance instance = instanceDao.getByKey(new Long(key));
			if (instance != null) {
				SurveyAttributeMapping mapping = mappingDao
						.findMappingForAttribute(instance.getSurveyId(),
								COMMUNITY_QUESTION_ATTRIBUTE);
				if (mapping != null) {
					// if the survey has the attribute mapped, find the
					// appropriate question
					QuestionAnswerStore qas = instanceDao
							.findQuestionAnswerStoreForQuestion(new Long(key),
									mapping.getSurveyQuestionId());
					if (qas != null && qas.getValue() != null) {
						CommunityDao commDao = new CommunityDao();
						Community community = commDao.findCommunityByCode(qas
								.getValue());
						if (community != null) {
							SurveyInstanceSummaryDao.incrementCount(community
									.getCommunityCode(), community
									.getCountryCode(), instance
									.getCollectionDate());
						} else {
							logger
									.log(Level.SEVERE,
											"Couldn't find community for instance. Was the community saved correctly?");
						}
					}
				}
			}
		}
		return true;
	}
}