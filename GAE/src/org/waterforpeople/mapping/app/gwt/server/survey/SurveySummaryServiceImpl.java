package org.waterforpeople.mapping.app.gwt.server.survey;

import java.util.List;
import java.util.logging.Logger;

import org.waterforpeople.mapping.analytics.dao.SurveyQuestionSummaryDao;
import org.waterforpeople.mapping.analytics.domain.SurveyQuestionSummary;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveySummaryDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveySummaryService;

import com.gallatinsystems.device.app.web.DeviceManagerServlet;
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
			.getLogger(DeviceManagerServlet.class.getName());

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

}