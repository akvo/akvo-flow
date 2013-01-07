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
import org.waterforpeople.mapping.analytics.dao.SurveyInstanceSummaryDao;
import org.waterforpeople.mapping.analytics.domain.SurveyInstanceSummary;
import org.waterforpeople.mapping.app.web.rest.dto.RestStatusDto;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.domain.Survey;
import com.google.appengine.api.datastore.Entity;

@Controller
@RequestMapping("/actions")
public class ActionRestService {
	
	@Inject
	private SurveyDAO surveyDao;
	
	@RequestMapping(method = RequestMethod.GET, value = "")
	@ResponseBody
	public Map<String, Object> doAction(
			@RequestParam(value = "action", defaultValue = "") String action) {
		final Map<String, Object> response = new HashMap<String, Object>();
		RestStatusDto statusDto = new RestStatusDto();
		statusDto.setStatus("failed");

		if ("recomputeSurveyInstanceSummaries".equals(action)) {
			List<Survey> surveys = surveyDao.list(Constants.ALL_RESULTS);
			if (surveys != null) {
				SurveyInstanceSummary sis = null;
				SurveyInstanceSummaryDao sisDao = new SurveyInstanceSummaryDao();
				for (Survey s : surveys) {
					
					// need to do it per page
					Iterable<Entity> siList = null;
					SurveyInstanceDAO dao = new SurveyInstanceDAO();
					siList = dao.listSurveyInstanceKeysBySurveyId(s.getKey().getId());
					
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
			}
			statusDto.setStatus("success");
		}

		response.put("meta", statusDto);
		return response;
	}
}
