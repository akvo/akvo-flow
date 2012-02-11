package org.waterforpeople.mapping.app.gwt.server.survey;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.waterforpeople.mapping.analytics.dao.SurveyInstanceSummaryDao;
import org.waterforpeople.mapping.analytics.dao.SurveyQuestionSummaryDao;
import org.waterforpeople.mapping.analytics.domain.SurveyInstanceSummary;
import org.waterforpeople.mapping.analytics.domain.SurveyQuestionSummary;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveySummaryDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveySummaryService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * This service is responsible for returning survey summarization objects. The
 * fields within the summary DTO may be partially populated based on the type of
 * summarization being returned.
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveySummaryServiceImpl extends RemoteServiceServlet implements
		SurveySummaryService {

	private static final long serialVersionUID = -5722103696712574220L;
	@SuppressWarnings("unused")
	private static final Logger log = Logger
			.getLogger(SurveySummaryServiceImpl.class.getName());

	/**
	 * returns an array of SurveySummaryDto objects that match the questionId
	 * passed in. The summary objects will have the response text and response
	 * count populated.
	 */
	public SurveySummaryDto[] listResponses(String questionId) {
		SurveyQuestionSummaryDao summaryDao = new SurveyQuestionSummaryDao();
		List<SurveyQuestionSummary> summaries = summaryDao
				.listByQuestion(questionId);
		SurveySummaryDto[] dtoList = null;
		if (summaries != null) {
			dtoList = new SurveySummaryDto[summaries.size()];
			for (int i = 0; i < summaries.size(); i++) {
				SurveySummaryDto dto = new SurveySummaryDto();
				dto.setQuestionId(questionId);
				dto.setResponseText(summaries.get(i).getResponse());
				dto.setCount(summaries.get(i).getCount());
				dtoList[i] = dto;
			}
		}
		return dtoList;
	}

	/**
	 * returns a list of SurveySummaryDto objects rolled up by date to represent
	 * the number of survey instances collected on a given date,possibly
	 * filtered by country and/or community.
	 * 
	 * @param countryCode
	 * @param communityCode
	 * @return
	 */
	public SurveySummaryDto[] listInstanceSummaryByLocation(String countryCode,
			String communityCode) {
		SurveySummaryDto[] dtoList = null;
		SurveyInstanceSummaryDao siDao = new SurveyInstanceSummaryDao();
		List<SurveyInstanceSummary> summaries = siDao.listByLocation(
				countryCode, communityCode);
		if (summaries != null) {
			// now do the rollup.
			dtoList = new SurveySummaryDto[summaries.size()];
			Map<Date, Long> countMap = new HashMap<Date, Long>();
			for (int i = 0; i < summaries.size(); i++) {
				Long val = countMap.get(summaries.get(i).getCollectionDate());
				if (val == null) {
					val = new Long(summaries.get(i).getCount());
				} else {
					val += summaries.get(i).getCount();
				}
				countMap.put(summaries.get(i).getCollectionDate(), val);
			}
			int i = 0;
			for (Date d : countMap.keySet()) {
				SurveySummaryDto dto = new SurveySummaryDto();
				dto.setCommunityCode(communityCode);
				dto.setCountryCode(countryCode);
				dto.setDate(d);
				dto.setCount(countMap.get(d));
				dtoList[i++] = dto;
			}
		}
		return dtoList;
	}
}