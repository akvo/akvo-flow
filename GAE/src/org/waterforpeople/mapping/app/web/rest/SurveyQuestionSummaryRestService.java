/*
 *  Copyright (C) 2012 Stichting Akvo (Akvo Foundation)
 *
 *  This file is part of Akvo FLOW.
 *
 *  Akvo FLOW is free software: you can redistribute it and modify it under the terms of
 *  the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 *  either version 3 of the License or any later version.
 *
 *  Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Affero General Public License included below for more details.
 *
 *  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */
package org.waterforpeople.mapping.app.web.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.waterforpeople.mapping.analytics.dao.SurveyQuestionSummaryDao;
import org.waterforpeople.mapping.analytics.domain.SurveyQuestionSummary;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyQuestionSummaryDto;
import org.waterforpeople.mapping.app.util.DtoMarshaller;

@Controller
@RequestMapping("/survey_question_summarys")
public class SurveyQuestionSummaryRestService {

	@Inject
	private SurveyQuestionSummaryDao surveyQuestionSummaryDao;

	// list surveys by surveyGroup id
	@RequestMapping(method = RequestMethod.GET, value = "")
	@ResponseBody
	public Map<String, List<SurveyQuestionSummaryDto>> listSurveyAnswerSummaryByQuestionId(
			@RequestParam(value = "questionId", defaultValue = "") Long questionId) {
		final Map<String, List<SurveyQuestionSummaryDto>> response = new HashMap<String, List<SurveyQuestionSummaryDto>>();
		List<SurveyQuestionSummaryDto> results = new ArrayList<SurveyQuestionSummaryDto>();
		List<SurveyQuestionSummary> surveyQuestionSummaries = null;

		if (questionId != null) {
			surveyQuestionSummaries = surveyQuestionSummaryDao.listByQuestion(questionId.toString());
		} 

		if (surveyQuestionSummaries != null) {
			for (SurveyQuestionSummary s : surveyQuestionSummaries) {
				SurveyQuestionSummaryDto dto = new SurveyQuestionSummaryDto();
				DtoMarshaller.copyToDto(s, dto);

				results.add(dto);
			}
		}
		response.put("survey_question_summarys", results);
		return response;
	}
}
