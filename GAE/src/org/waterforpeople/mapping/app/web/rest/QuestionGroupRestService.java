package org.waterforpeople.mapping.app.web.rest;

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
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.util.DtoMarshaller;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.framework.exceptions.IllegalDeletionException;
import com.gallatinsystems.survey.dao.QuestionGroupDao;
import com.gallatinsystems.survey.domain.QuestionGroup;
import com.gallatinsystems.survey.domain.Survey;

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
	@RequestMapping(method = RequestMethod.GET, value = "")
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
	
	// delete question group by id
	@RequestMapping(method = RequestMethod.DELETE, value = "/del/{id}")
	@ResponseBody
	public RestStatusDto deleteQuestionGroupById(@PathVariable("id") Long id){
		QuestionGroup qg = questionGroupDao.getByKey(id);		
		RestStatusDto dto = null;
		dto = new RestStatusDto();
		dto.setStatus("failed");
				  
		// check if questionGroup exists in the datastore
		if (qg != null){
			// delete question group
			questionGroupDao.delete(qg);
			dto.setStatus("ok");	
		}
		return dto;
	}
	
	// save a question group
	@RequestMapping(method = RequestMethod.POST, value="")
	@ResponseBody
	public QuestionGroupDto saveQuestionGroup(@RequestBody QuestionGroupDto questionGroupDto){
		QuestionGroupDto dto = null;
		
		// if the POST data contains a valid QuestionGroupDto, continue. Otherwise, server will respond with 400 Bad Request 
		if (questionGroupDto != null){
			Long keyId = questionGroupDto.getKeyId();
			QuestionGroup qg;
					
			// if the questionGroupDto has a key, try to get the surveyGroup.
			if (keyId != null) {
				qg = questionGroupDao.getByKey(keyId);
				// if the questionGroup doesn't exist, create a new questionGroup
				if (qg == null) {
					qg = new QuestionGroup();
				}
			} else {
				qg = new QuestionGroup();
			}
			
			// copy the properties, except the createdDateTime property, because it is set in the Dao.
			BeanUtils.copyProperties(questionGroupDto, qg, new String[] {"createdDateTime"});
			qg = questionGroupDao.save(qg);
					
			dto = new QuestionGroupDto();
			DtoMarshaller.copyToDto(qg, dto);
		}
		return dto;
	}

}
