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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.waterforpeople.mapping.analytics.dao.SurveyInstanceSummaryDao;
import org.waterforpeople.mapping.analytics.domain.SurveyInstanceSummary;
import org.waterforpeople.mapping.app.web.dto.BootstrapGeneratorRequest;
import org.waterforpeople.mapping.app.web.rest.dto.RestStatusDto;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.app.gwt.server.survey.SurveyServiceImpl;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.domain.Survey;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

@Controller
@RequestMapping("/actions")
public class ActionRestService {

	@Inject
	private SurveyDAO surveyDao;

	@RequestMapping(method = RequestMethod.GET, value = "")
	@ResponseBody
	public Map<String, Object> doAction(
			@RequestParam(value = "action", defaultValue = "") String action,
			@RequestParam(value = "surveyId", defaultValue = "") Long surveyId,
			@RequestParam(value = "surveyIds[]", defaultValue = "") Long[] surveyIds,
			@RequestParam(value = "email", defaultValue = "") String email,
			@RequestParam(value = "dbInstructions", defaultValue = "") String dbInstructions) {
		String status = "failed";
		String message = "";
		final Map<String, Object> response = new HashMap<String, Object>();
		RestStatusDto statusDto = new RestStatusDto();

		// perform the required action
		if ("recomputeSurveyInstanceSummaries".equals(action)) {
			status = recomputeSurveyInstanceSummaries();
		} else if ("publishSurvey".equals(action) && surveyId != null) {
			status = publishSurvey(surveyId);
		} else if ("generateBootstrapFile".equals(action) && surveyIds != null
				&& email != null) {
			message = generateBootstrapFile(surveyIds, dbInstructions, email);
			status = "ok";
			statusDto.setMessage(message);
		}

		statusDto.setStatus(status);
		response.put("actions", "[]");
		response.put("meta", statusDto);
		return response;
	}

	@SuppressWarnings("unused")
	private String recomputeSurveyInstanceSummaries() {
		List<Survey> surveys = surveyDao.list(Constants.ALL_RESULTS);
		String status = "failed";
		if (surveys != null) {
			SurveyInstanceSummary sis = null;
			SurveyInstanceSummaryDao sisDao = new SurveyInstanceSummaryDao();
			for (Survey s : surveys) {

				// need to do it per page
				Iterable<Entity> siList = null;
				SurveyInstanceDAO dao = new SurveyInstanceDAO();
				siList = dao.listSurveyInstanceKeysBySurveyId(s.getKey()
						.getId());

				Long count = 0L;
				for (Entity si : siList) {
					count++;
				}

				sis = sisDao.findBySurveyId(s.getKey().getId());

				if (sis == null) {
					sis = new SurveyInstanceSummary();
					sis.setCount(count);
					sis.setSurveyId(s.getKey().getId());
				} else {
					sis.setCount(count);
				}
				sisDao.save(sis);
			}
			status = "success";
		}
		return status;
	}

	private String publishSurvey(Long surveyId) {
		SurveyServiceImpl surveyService = new SurveyServiceImpl();
		surveyService.publishSurveyAsync(surveyId);
		return "publishing requested";
	}

	private String generateBootstrapFile(Long[] surveyIdList,
			String dbInstructions, String notificationEmail) {

		StringBuilder buf = new StringBuilder();

		if (surveyIdList != null && surveyIdList[0] != null) {
			for (int i = 0; i < surveyIdList.length; i++) {
				if (i > 0) {
					buf.append(BootstrapGeneratorRequest.DELMITER);
				}
				buf.append(String.valueOf(surveyIdList[i]));
			}
		}

		Queue queue = QueueFactory.getQueue("background-processing");
		queue.add(TaskOptions.Builder
				.withUrl("/app_worker/bootstrapgen")
				.param(BootstrapGeneratorRequest.ACTION_PARAM,
						BootstrapGeneratorRequest.GEN_ACTION)
				.param(BootstrapGeneratorRequest.SURVEY_ID_LIST_PARAM,
						buf.toString())
				.param(BootstrapGeneratorRequest.EMAIL_PARAM, notificationEmail)
				.param(BootstrapGeneratorRequest.DB_PARAM,
						dbInstructions != null ? dbInstructions : ""));
		return "_request_submitted_email_will_be_sent";
	}
}