/*
 *  Copyright (C) 2012,2017 Stichting Akvo (Akvo Foundation)
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

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.waterforpeople.mapping.analytics.dao.SurveyQuestionSummaryDao;
import org.waterforpeople.mapping.analytics.domain.SurveyQuestionSummary;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyQuestionSummaryDto;
import org.waterforpeople.mapping.app.util.DtoMarshaller;

import com.gallatinsystems.survey.dao.QuestionDao;
import com.gallatinsystems.survey.domain.Question;

@Controller
@RequestMapping("/survey_question_summarys")
public class SurveyQuestionSummaryRestService {

    private SurveyQuestionSummaryDao surveyQuestionSummaryDao = new SurveyQuestionSummaryDao();

    private QuestionDao questionDao = new QuestionDao();

    // list questionSummaries by question id (if the questionId parameter is
    // passed)
    // or, if the surveyId is passed and 'metricOnly=true', only for the
    // questions which have a metric.
    // if metricOnly = false, all SQS objects are returned for the survey.
    @RequestMapping(method = RequestMethod.GET, value = "")
    @ResponseBody
    public Map<String, List<SurveyQuestionSummaryDto>> listSurveyAnswerSummary(
            @RequestParam(value = "questionId", defaultValue = "")
            Long questionId,
            @RequestParam(value = "surveyId", defaultValue = "")
            Long surveyId,
            @RequestParam(value = "metricOnly", defaultValue = "")
            Boolean metricOnly) {
        final Map<String, List<SurveyQuestionSummaryDto>> response = new HashMap<String, List<SurveyQuestionSummaryDto>>();
        List<SurveyQuestionSummaryDto> results = new ArrayList<SurveyQuestionSummaryDto>();
        List<SurveyQuestionSummary> surveyQuestionSummaries = new ArrayList<SurveyQuestionSummary>();
        Boolean include;
        Boolean includeMetricOnly;
        if (questionId != null) {
            surveyQuestionSummaries = surveyQuestionSummaryDao
                    .listByQuestion(questionId.toString());
            if (surveyQuestionSummaries != null) {
                for (SurveyQuestionSummary s : surveyQuestionSummaries) {
                    SurveyQuestionSummaryDto dto = new SurveyQuestionSummaryDto();
                    DtoMarshaller.copyToDto(s, dto);
                    results.add(dto);
                }
            }
        } else {
            if (surveyId != null) {
                // get all questions for this survey
                List<Question> questions = new ArrayList<Question>();
                // TODO this can be improved: we are now getting all questions,
                // while
                // we only need those which have a metricId != null.
                questions = questionDao.listQuestionsBySurvey(surveyId);
                if (metricOnly != null && metricOnly) {
                    includeMetricOnly = true;
                } else {
                    includeMetricOnly = false;
                }
                if (questions != null && questions.size() > 0) {
                    for (Question question : questions) {
                        include = true;
                        if (includeMetricOnly && question.getMetricId() == null) {
                            include = false;
                        }
                        if (include) {
                            surveyQuestionSummaries = surveyQuestionSummaryDao
                                    .listByQuestion(String.valueOf(question
                                            .getKey().getId()));
                            if (surveyQuestionSummaries != null) {
                                for (SurveyQuestionSummary s : surveyQuestionSummaries) {
                                    SurveyQuestionSummaryDto dto = new SurveyQuestionSummaryDto();
                                    DtoMarshaller.copyToDto(s, dto);
                                    results.add(dto);
                                }
                            }
                        }
                    }
                }
            }
        }
        response.put("survey_question_summarys", results);
        return response;
    }
}
