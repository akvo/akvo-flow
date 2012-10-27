package org.waterforpeople.mapping.app.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyGroupDto;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.survey.dao.SurveyGroupDAO;
import com.gallatinsystems.survey.domain.SurveyGroup;

@Controller
@RequestMapping("/survey-group")
public class SurveyGroupRestService {

	@Inject
	private SurveyGroupDAO surveyGroupDao;

	@RequestMapping(method = RequestMethod.GET, value = "/")
	@ResponseBody
	public List<SurveyGroupDto> listSurveyGroups() {
		List<SurveyGroupDto> results = new ArrayList<SurveyGroupDto>();
		List<SurveyGroup> surveys = surveyGroupDao.list(Constants.ALL_RESULTS);
		if (surveys != null) {
			for (SurveyGroup s : surveys) {
				SurveyGroupDto dto = new SurveyGroupDto();

				dto.setName(s.getName());
				dto.setCode(s.getCode());
				dto.setDescription(s.getDescription());
				dto.setKeyId(s.getKey().getId());
				results.add(dto);
			}
		}
		return results;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
	@ResponseBody
	public SurveyGroupDto findSurveyGroup(@PathVariable("id") Long id){
		SurveyGroup s =surveyGroupDao.getByKey(id);		
		SurveyGroupDto dto = null;
		if(s != null){
			dto = new SurveyGroupDto();
			dto.setName(s.getName());
			dto.setKeyId(s.getKey().getId());
		}
		return dto;
		
	}
	
	@RequestMapping(method = RequestMethod.POST, value="/")
	@ResponseBody
	public SurveyGroupDto saveSurveyGroup(@RequestBody SurveyGroupDto surveyGroupDto){
		if(surveyGroupDto != null){
			SurveyGroup s = new SurveyGroup();
			BeanUtils.copyProperties(surveyGroupDto, s);
			s = surveyGroupDao.save(s);
			surveyGroupDto.setKeyId(s.getKey().getId());
		}
		return surveyGroupDto;
	}

}
