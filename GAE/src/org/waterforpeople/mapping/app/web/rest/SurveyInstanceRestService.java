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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.waterforpeople.mapping.app.gwt.client.surveyinstance.SurveyInstanceDto;
import org.waterforpeople.mapping.app.util.DtoMarshaller;
import org.waterforpeople.mapping.app.web.rest.dto.SurveyInstancePayload;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.survey.dao.SurveyDAO;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import com.gallatinsystems.survey.domain.Survey;
import org.waterforpeople.mapping.domain.SurveyInstance;

@Controller
@RequestMapping("/survey_instances")
public class SurveyInstanceRestService {

	@Inject
	private SurveyInstanceDAO surveyInstanceDao;

	@Inject
	private SurveyDAO surveyDao;

	//TODO put in meta information?
	// list all survey instances
	@RequestMapping(method = RequestMethod.GET, value = "")
	@ResponseBody
	public Map<String, List<SurveyInstanceDto>> listSurveyInstances() {
		final Map<String, List<SurveyInstanceDto>> response = new HashMap<String, List<SurveyInstanceDto>>();
		List<SurveyInstanceDto> results = new ArrayList<SurveyInstanceDto>();
		List<SurveyInstance> surveys = surveyInstanceDao.list(Constants.ALL_RESULTS);
		if (surveys != null) {
			for (SurveyInstance s : surveys) {
				SurveyInstanceDto dto = new SurveyInstanceDto();
				DtoMarshaller.copyToDto(s, dto);
				results.add(dto);
			}
		}
		response.put("survey_instances", results);
		return response;
	}

	// find survey instance by id
	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
	@ResponseBody
	public Map<String, SurveyInstanceDto> findSurveyInstanceById(
			@PathVariable("id") Long id) {
		final Map<String, SurveyInstanceDto> response = new HashMap<String, SurveyInstanceDto>();
		SurveyInstance s = surveyInstanceDao.getByKey(id);
		SurveyInstanceDto dto = null;
		if (s != null) {
			dto = new SurveyInstanceDto();
			DtoMarshaller.copyToDto(s, dto);
		}
		response.put("survey_instance", dto);
		return response;
	}

	// delete survey instance by id
	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
	@ResponseBody
	public Map<String, RestStatusDto> deleteSurveyInstanceById(
			@PathVariable("id") Long id) {
		final Map<String, RestStatusDto> response = new HashMap<String, RestStatusDto>();
		SurveyInstance s = surveyInstanceDao.getByKey(id);
		RestStatusDto statusDto = new RestStatusDto();
		statusDto.setStatus("failed");

		// check if surveyInstance exists in the datastore
		if (s != null) {
			surveyInstanceDao.delete(s);
			statusDto.setStatus("ok");
		}
		response.put("meta", statusDto);
		return response;
	}

	// Update survey instance
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}")
	@ResponseBody
	public Map<String, Object> saveExistingSurveyInstance(
			@RequestBody SurveyInstancePayload payLoad) {

		final SurveyInstanceDto surveyInstanceDto = payLoad.getSurvey_instance();
		final Map<String, Object> response = new HashMap<String, Object>();
		SurveyInstanceDto dto = null;

		RestStatusDto statusDto = new RestStatusDto();
		statusDto.setStatus("failed");
		
		// if the POST data contains a valid surveyInstanceDto, continue.
		// Otherwise, server 400 Bad Request
		if (surveyInstanceDto != null) {
			Long keyId = surveyInstanceDto.getKeyId();
			SurveyInstance s;

			// if the surveyInstanceDto has a key, try to get the surveyInstance.
			if (keyId != null) {
				s = surveyInstanceDao.getByKey(keyId);
				// if we find the surveyInstance, update it's properties
				if (s != null) {
					// copy the properties, except the properties that are set
					// or provided by the Dao.
					BeanUtils.copyProperties(surveyInstanceDto, s, new String[] {
							"createdDateTime", "lastUpdateDateTime",
							"displayName", "questionInstanceList" });
					s = surveyInstanceDao.save(s);

					dto = new SurveyInstanceDto();
					DtoMarshaller.copyToDto(s, dto);
					statusDto.setStatus("ok");
				}
			}
		}
		response.put("meta", statusDto);
		response.put("survey_instance", dto);
		return response;
	}

	// Create new survey instance
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> saveNewSurveyInstance(
			@RequestBody SurveyInstancePayload payLoad) {

		final SurveyInstanceDto surveyInstanceDto = payLoad.getSurvey_instance();
		final Map<String, Object> response = new HashMap<String, Object>();
		SurveyInstanceDto dto = null;
		
		RestStatusDto statusDto = new RestStatusDto();
		statusDto.setStatus("failed");

		// if the POST data contains a valid surveyInstanceDto, continue.
		// Otherwise, server 400 Bad Request
		if (surveyInstanceDto != null) {
			SurveyInstance s = new SurveyInstance();

			// copy the properties, except the properties that are set or
			// provided by the Dao.
			BeanUtils.copyProperties(surveyInstanceDto, s, new String[] {
					"createdDateTime", "lastUpdateDateTime", "displayName",
					"questionInstanceList" });
			s = surveyInstanceDao.save(s);

			dto = new SurveyInstanceDto();
			DtoMarshaller.copyToDto(s, dto);
			statusDto.setStatus("ok");
		}
		response.put("meta", statusDto);
		response.put("survey_instance", dto);
		return response;
	}

}
