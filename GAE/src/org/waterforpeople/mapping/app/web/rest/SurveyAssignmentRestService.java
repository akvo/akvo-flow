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

import org.springframework.beans.BeanUtils;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import org.waterforpeople.mapping.app.util.DtoMarshaller;
import org.waterforpeople.mapping.app.web.rest.dto.SurveyAssignmentDto;
import org.waterforpeople.mapping.app.web.rest.dto.SurveyAssignmentPayload;
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
	public Map<String, List<SurveyAssignmentDto>> listAll() {
		final HashMap<String, List<SurveyAssignmentDto>> response = new HashMap<String, List<SurveyAssignmentDto>>();
		final List<SurveyAssignmentDto> results = new ArrayList<SurveyAssignmentDto>();

		for (SurveyAssignment sa : surveyAssignmentDao
				.list(Constants.ALL_RESULTS)) {
			results.add(marshallToDto(sa));
		}

		response.put("survey_assignments", results);
		return response;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
	@ResponseBody
	public Map<String, SurveyAssignmentDto> getById(@PathVariable("id") Long id) {
		final HashMap<String, SurveyAssignmentDto> response = new HashMap<String, SurveyAssignmentDto>();
		final SurveyAssignment sa = surveyAssignmentDao.getByKey(id);

		if (sa == null) {
			throw new HttpMessageNotReadableException(
					"Survey Assignment with id: " + id + " not found");
		}

		response.put("survey_assignment", marshallToDto(sa));
		return response;
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/{id}")
	@ResponseBody
	public Map<String, SurveyAssignmentDto> updateSurveyAssignment(
			@PathVariable("id") Long id,
			@RequestBody SurveyAssignmentPayload payload) {

		final SurveyAssignmentDto dto = payload.getSurvey_assignment();

		if (!id.equals(dto.getKeyId())) {
			throw new HttpMessageNotReadableException("Ids don't match: " + id
					+ " <> " + dto.getKeyId());
		}

		final SurveyAssignment sa = surveyAssignmentDao
				.getByKey(dto.getKeyId());

		final HashMap<String, SurveyAssignmentDto> response = new HashMap<String, SurveyAssignmentDto>();

		if (sa == null) {
			throw new HttpMessageNotReadableException(
					"Survey Assignment with id: " + dto.getKeyId()
							+ " not found");
		}

		BeanUtils.copyProperties(marshallToDomain(dto), sa);
		surveyAssignmentDao.save(sa);

		response.put("survey_assignment", marshallToDto(sa));

		return response;
	}

	private SurveyAssignmentDto marshallToDto(SurveyAssignment sa) {
		final SurveyAssignmentDto dto = new SurveyAssignmentDto();

		DtoMarshaller.copyToDto(sa, dto);
		dto.setDevices(sa.getDeviceIds());
		dto.setSurveys(sa.getSurveyIds());

		return dto;
	}

	private SurveyAssignment marshallToDomain(SurveyAssignmentDto dto) {
		final SurveyAssignment sa = new SurveyAssignment();

		DtoMarshaller.copyToCanonical(sa, dto);
		sa.setDeviceIds(dto.getDevices());
		sa.setSurveyIds(dto.getSurveys());

		return sa;
	}
}
