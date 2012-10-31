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
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;
import org.waterforpeople.mapping.app.util.DtoMarshaller;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.survey.dao.QuestionGroupDao;
import com.gallatinsystems.survey.domain.QuestionGroup;

@Controller
@RequestMapping("/question-group")
public class QuestionGroupRestService {

	@Inject
	private QuestionGroupDao questionGroupDao;

	// list all question groups
	@RequestMapping(method = RequestMethod.GET, value = "/all")
	@ResponseBody
	public List<QuestionGroupDto> listQuestionGroups() {
		List<QuestionGroupDto> results = new ArrayList<QuestionGroupDto>();
		List<QuestionGroup> questionGroups = questionGroupDao.list(Constants.ALL_RESULTS);
		if (questionGroups != null) {
			for (QuestionGroup sg : questionGroups) {
				QuestionGroupDto dto = new QuestionGroupDto();
				DtoMarshaller.copyToDto(sg, dto);
				results.add(dto);
			}
		}
		return results;
	}
	
	// list question groups by their survey group id
	@RequestMapping(method = RequestMethod.GET, value = "/")
	@ResponseBody
	public List<QuestionGroupDto> listQuestionGroupsBySurveyGroupId(@RequestParam("surveyId") Long surveyId) {
		List<QuestionGroupDto> results = new ArrayList<QuestionGroupDto>();
		List<QuestionGroup> questionGroups = questionGroupDao.listQuestionGroupBySurvey(surveyId);
		if (questionGroups != null) {
			for (QuestionGroup sg : questionGroups) {
				QuestionGroupDto dto = new QuestionGroupDto();
				DtoMarshaller.copyToDto(sg, dto);
				results.add(dto);
			}
		}
		return results;
	}
	
	// find a single question group by its id
	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
	@ResponseBody
	public QuestionGroupDto findQuestionGroup(@PathVariable("id") Long id){
		QuestionGroup sg =questionGroupDao.getByKey(id);		
		QuestionGroupDto dto = null;
		if(sg != null){
			dto = new QuestionGroupDto();
			DtoMarshaller.copyToDto(sg, dto);
		}
		return dto;
		
	}
	
	// save a question group
	@RequestMapping(method = RequestMethod.POST, value="/")
	@ResponseBody
	public QuestionGroupDto saveQuestionGroup(@RequestBody QuestionGroupDto questionGroupDto){
		QuestionGroupDto dto = null;
		
		// TODO
		return questionGroupDto;
	}

}
