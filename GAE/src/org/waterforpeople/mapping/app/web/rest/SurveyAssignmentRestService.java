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
import org.springframework.web.bind.annotation.ResponseBody;

import org.waterforpeople.mapping.app.util.DtoMarshaller;
import org.waterforpeople.mapping.app.web.rest.dto.SurveyAssignmentDto;
import org.waterforpeople.mapping.domain.SurveyAssignment;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.survey.dao.SurveyAssignmentDAO;

@Controller
@RequestMapping("/survey_assignments")
public class SurveyAssignmentRestService {

	@Inject
	SurveyAssignmentDAO surveyAssignmentDao;

	@RequestMapping(method = RequestMethod.GET, value = "")
	@ResponseBody
	public Map<String, List<SurveyAssignmentDto>> listSurveyGroups() {
		final HashMap<String, List<SurveyAssignmentDto>> response = new HashMap<String, List<SurveyAssignmentDto>>();

		final List<SurveyAssignmentDto> results = new ArrayList<SurveyAssignmentDto>();
		for (SurveyAssignment sa : surveyAssignmentDao
				.list(Constants.ALL_RESULTS)) {
			results.add(marshallToDto(sa));
		}
		response.put("survey_assignments", results);
		return response;
	}

	private SurveyAssignmentDto marshallToDto(SurveyAssignment sa) {
		final SurveyAssignmentDto dto = new SurveyAssignmentDto();

		DtoMarshaller.copyToDto(sa, dto);
		dto.setDevices(sa.getDeviceIds());
		dto.setSurveys(sa.getSurveyIds());

		return dto;
	}
}
