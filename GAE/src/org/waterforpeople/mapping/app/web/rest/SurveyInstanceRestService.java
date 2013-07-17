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
import java.util.Date;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.waterforpeople.mapping.app.gwt.client.surveyinstance.SurveyInstanceDto;
import org.waterforpeople.mapping.app.util.DtoMarshaller;
import org.waterforpeople.mapping.app.web.rest.dto.RestStatusDto;
import org.waterforpeople.mapping.app.web.rest.dto.SurveyInstancePayload;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.SurveyInstance;

import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.dao.SurveyUtils;
import com.gallatinsystems.survey.domain.Survey;

@Controller
@RequestMapping("/survey_instances")
public class SurveyInstanceRestService {

	@Inject
	private SurveyInstanceDAO surveyInstanceDao;

	// list survey instances
	@RequestMapping(method = RequestMethod.GET, value = "")
	@ResponseBody
	public Map<String, Object> listSurveyInstances(
			@RequestParam(value = "beginDate", defaultValue = "") Long bDate,
			@RequestParam(value = "endDate", defaultValue = "") Long eDate,
			@RequestParam(value = "surveyId", defaultValue = "") Long surveyId,
			@RequestParam(value = "since", defaultValue = "") String since,
			@RequestParam(value = "unapprovedOnlyFlag",defaultValue = "") Boolean unapprovedOnlyFlag,
			@RequestParam(value = "deviceId", defaultValue = "") String deviceId,
			@RequestParam(value = "submitterName", defaultValue = "") String submitterName,
			@RequestParam(value = "countryCode", defaultValue = "") String countryCode,
			@RequestParam(value = "level1", defaultValue = "") String level1,
			@RequestParam(value = "level2", defaultValue = "") String level2) {
		
		// we don't want to search for empty fields
		if ("".equals(deviceId)) {
			deviceId = null;
		}
		if ("".equals(submitterName)) {
			submitterName = null;
		}
		if ("".equals(countryCode)) {
			countryCode = null;
		}
		if ("".equals(level1)) {
			level1 = null;
		}
		if ("".equals(level2)) {
			level2 = null;
		}
		
		final Map<String, Object> response = new HashMap<String, Object>();
		RestStatusDto statusDto = new RestStatusDto();
		
		// create list of surveygroup / survey
		SurveyDAO surveyDao = new SurveyDAO();
		List<Survey> surveyList = surveyDao.list("all");
		HashMap<Long, String> surveyMap = new HashMap<Long, String>();
		for (Survey s : surveyList) {
			surveyMap.put(s.getKey().getId(), s.getPath() + "/" + s.getCode());
		}
		
		// turn params into dates
		Date beginDate = null;
		Date endDate = null;
		
		if (bDate != null) {
			beginDate = new Date(bDate);
		}
		
		if (eDate != null) {
			endDate = new Date(eDate);
		}
		
		// if no begin and end date, choose begin date 1-1-1970
		if (beginDate == null && endDate == null) {
			//Calendar c = Calendar.getInstance();
			//c.add(Calendar.YEAR, -90);
			beginDate = new Date (0); //c.getTime();
		}
		
		// get survey Instances
		List<SurveyInstance> siList = null;
		SurveyInstanceDAO dao = new SurveyInstanceDAO();
		siList = dao.listByDateRangeAndSubmitter(beginDate, endDate, false,
				surveyId, deviceId, submitterName, countryCode, level1, level2, since);
		Integer num = siList.size();
		String newSince = SurveyInstanceDAO.getCursor(siList);
		
		// put in survey group/survey names
		ArrayList<SurveyInstanceDto> siDtoList = new ArrayList<SurveyInstanceDto>();
		for (SurveyInstance siItem : siList) {
			String code = surveyMap.get(siItem.getSurveyId());
			SurveyInstanceDto dto = new SurveyInstanceDto();
			DtoMarshaller.copyToDto(siItem, dto);
			if (code != null) dto.setSurveyCode(code);
			siDtoList.add(dto);
		}
		
		statusDto.setSince(newSince);
		statusDto.setNum(num);
		response.put("meta", statusDto);
		response.put("survey_instances", siDtoList);
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
	// TODO update counts
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
			surveyInstanceDao.deleteSurveyInstance(s);
			statusDto.setStatus("ok");
		}
		response.put("meta", statusDto);

		List<Long> ids = new ArrayList<Long>();
		ids.add(id);
		SurveyUtils.notifyReportService(ids, "invalidate");

		return response;
	}

	// Update survey instance
	// TODO: question - when is this used?
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
