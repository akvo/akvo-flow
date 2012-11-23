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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyGroupDto;
import org.waterforpeople.mapping.app.util.DtoMarshaller;
import org.waterforpeople.mapping.app.web.rest.dto.SurveyPayload;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.framework.exceptions.IllegalDeletionException;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.domain.Survey;

@Controller
@RequestMapping("/surveys")
public class SurveyRestService {

	@Inject
	private SurveyDAO surveyDao;

	//TODO put in meta information?
	// list all surveys
	@RequestMapping(method = RequestMethod.GET, value = "/all")
	@ResponseBody
	public Map<String, List<SurveyDto>> listSurveys() {
		final Map<String, List<SurveyDto>> response = new HashMap<String, List<SurveyDto>>();
		List<SurveyDto> results = new ArrayList<SurveyDto>();
		List<Survey> surveys = surveyDao.list(Constants.ALL_RESULTS);
		if (surveys != null) {
			for (Survey s : surveys) {
				SurveyDto dto = new SurveyDto();
				DtoMarshaller.copyToDto(s, dto);

				// needed because of different names for description in survey
				// and surveyDto
				dto.setDescription(s.getDesc());
				results.add(dto);
			}
		}
		response.put("surveys", results);
		return response;
	}
	
	//TODO put in meta information?
	// list surveys by surveyGroup id
	@RequestMapping(method = RequestMethod.GET, value = "")
	@ResponseBody
	public Map<String, List<SurveyDto>> listSurveysByGroupId(
			@RequestParam("surveyGroupId") Long surveyGroupId) {
		final Map<String, List<SurveyDto>> response = new HashMap<String, List<SurveyDto>>();
		List<SurveyDto> results = new ArrayList<SurveyDto>();
		List<Survey> surveys = surveyDao.listSurveysByGroup(surveyGroupId);
		if (surveys != null) {
			for (Survey s : surveys) {
				SurveyDto dto = new SurveyDto();
				DtoMarshaller.copyToDto(s, dto);

				// needed because of different names for description in survey
				// and surveyDto
				dto.setDescription(s.getDesc());
				results.add(dto);
			}
		}
		response.put("surveys", results);
		return response;
	}

	// find a single survey by the surveyId
	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
	@ResponseBody
	public Map<String, SurveyDto> findSurvey(@PathVariable("id") Long id) {
		final Map<String, SurveyDto> response = new HashMap<String, SurveyDto>();
		Survey s = surveyDao.getByKey(id);
		SurveyDto dto = null;
		if (s != null) {
			dto = new SurveyDto();
			DtoMarshaller.copyToDto(s, dto);
			// needed because of different names for description in survey and
			// surveyDto
			dto.setDescription(s.getDesc());
		}
		response.put("survey", dto);
		return response;

	}

	// delete survey by id
	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
	@ResponseBody
	public Map<String, RestStatusDto> deleteSurveyById(
			@PathVariable("id") Long id) {
		final Map<String, RestStatusDto> response = new HashMap<String, RestStatusDto>();
		Survey s = surveyDao.getByKey(id);
		RestStatusDto statusDto = null;
		statusDto = new RestStatusDto();
		statusDto.setStatus("failed");

		// check if survey exists in the datastore
		if (s != null) {
			// delete survey group
			try {
				surveyDao.delete(s);
				statusDto.setStatus("ok");
			} catch (IllegalDeletionException e) {
				statusDto.setStatus("failed");
				statusDto.setMessage(e.getMessage());
			}
		}
		response.put("meta", statusDto);
		return response;
	}

	// update existing survey
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}")
	@ResponseBody
	public Map<String, Object> saveExistingSurvey(
			@RequestBody SurveyPayload payLoad) {
		final SurveyDto surveyDto = payLoad.getSurvey();
		final Map<String, Object> response = new HashMap<String, Object>();
		SurveyDto dto = null;

		RestStatusDto statusDto = new RestStatusDto();
		statusDto.setStatus("failed");

		// if the POST data contains a valid surveyDto, continue. Otherwise,
		// server will respond with 400 Bad Request
		if (surveyDto != null) {
			Long keyId = surveyDto.getKeyId();
			Survey s;

			// if the surveyDto has a key, try to get the survey.
			if (keyId != null) {
				s = surveyDao.getByKey(keyId);
				// if we find the survey, update it's properties
				if (s != null) {
					// copy the properties, except the createdDateTime property,
					// because it is set in the Dao.
					BeanUtils.copyProperties(surveyDto, s, new String[] {
							"createdDateTime", "status", "version",
							"lastUpdateDateTime", "displayName",
							"questionGroupList" });
					s = surveyDao.save(s);
					dto = new SurveyDto();
					DtoMarshaller.copyToDto(s, dto);
					statusDto.setStatus("ok");
				}
			}
		}
		response.put("meta", statusDto);
		response.put("survey", dto);
		return response;
	}

	// create new survey
	@RequestMapping(method = RequestMethod.POST, value = "")
	@ResponseBody
	public Map<String, Object> saveNewSurvey(@RequestBody SurveyPayload payLoad) {
		final SurveyDto surveyDto = payLoad.getSurvey();
		final Map<String, Object> response = new HashMap<String, Object>();
		SurveyDto dto = null;

		RestStatusDto statusDto = new RestStatusDto();
		statusDto.setStatus("failed");

		// if the POST data contains a valid surveyDto, continue. Otherwise,
		// server will respond with 400 Bad Request
		if (surveyDto != null) {
			Survey s = new Survey();

			// copy the properties, except the createdDateTime property, because
			// it is set in the Dao.
			BeanUtils.copyProperties(surveyDto, s, new String[] {
					"createdDateTime", "status", "version",
					"lastUpdateDateTime", "displayName", "questionGroupList" });
			s = surveyDao.save(s);

			dto = new SurveyDto();
			DtoMarshaller.copyToDto(s, dto);
			statusDto.setStatus("ok");
		}

		response.put("meta", statusDto);
		response.put("survey", dto);
		return response;
	}

}
