package org.waterforpeople.mapping.app.web.controller;

import java.util.ArrayList;
import java.util.List;

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

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.domain.Survey;

@Controller
@RequestMapping("/survey")
public class SurveyRestService {

	@Inject
	private SurveyDAO surveyDao;

	@RequestMapping(method = RequestMethod.GET, value = "/all")
	@ResponseBody
	public List<SurveyDto> listSurveys() {
		List<SurveyDto> results = new ArrayList<SurveyDto>();
		List<Survey> surveys = surveyDao.list(Constants.ALL_RESULTS);
		if (surveys != null) {
			for (Survey s : surveys) {
				SurveyDto dto = new SurveyDto();

				dto.setName(s.getName());
				dto.setDescription(s.getDesc());
				dto.setSurveyGroupId(s.getSurveyGroupId());
				dto.setVersion(s.getVersion() != null ? s.getVersion().toString() : "");
				dto.setKeyId(s.getKey().getId());
				results.add(dto);
			}
		}
		return results;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/")
	@ResponseBody
	public List<SurveyDto> listSurveysByGroupId(@RequestParam("surveyGroupId") Long surveyGroupId) {
		List<SurveyDto> results = new ArrayList<SurveyDto>();
		List<Survey> surveys = surveyDao.listSurveysByGroup(surveyGroupId);
		if (surveys != null) {
			for (Survey s : surveys) {
				SurveyDto dto = new SurveyDto();

				dto.setName(s.getName());
				dto.setDescription(s.getDesc());
				dto.setSurveyGroupId(s.getSurveyGroupId());
				dto.setVersion(s.getVersion() != null ? s.getVersion().toString() : "");
				dto.setKeyId(s.getKey().getId());
				results.add(dto);
			}
		}
		return results;
	}
	
	
	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
	@ResponseBody
	public SurveyDto findSurvey(@PathVariable("id") Long id){
		Survey s =surveyDao.getByKey(id);		
		SurveyDto dto = null;
		if(s != null){
			dto = new SurveyDto();
			dto.setName(s.getName());
			dto.setVersion(s.getVersion() != null ? s.getVersion().toString() : "");
			dto.setKeyId(s.getKey().getId());
		}
		return dto;
		
	}
	
	@RequestMapping(method = RequestMethod.POST, value="/")
	@ResponseBody
	public SurveyDto saveSurvey(@RequestBody SurveyDto surveyDto){
		if(surveyDto != null){
			Survey s = new Survey();
			BeanUtils.copyProperties(surveyDto, s);
			s = surveyDao.save(s);
			surveyDto.setKeyId(s.getKey().getId());
		}
		return surveyDto;
	}

}
