package org.waterforpeople.mapping.app.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import javax.inject.Inject;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.util.DtoMarshaller;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.survey.dao.QuestionDao;
import com.gallatinsystems.survey.domain.Question;

@Controller
@RequestMapping("/question")
public class QuestionRestService {

	@Inject
	private QuestionDao questionDao;
	
	// list questions by their question group id
	@RequestMapping(method = RequestMethod.GET, value = "/")
	@ResponseBody
	public List<QuestionDto> listQuestionsByQuestionGroupId(@RequestParam("questionGroupId") Long questionGroupId) {
		TreeMap<Integer, Question> questions = questionDao.listQuestionsByQuestionGroup(questionGroupId, false);
		List<QuestionDto> results = new ArrayList<QuestionDto>();

		if (questions != null) {
			for (Question q : questions.values()) {
				QuestionDto dto = new QuestionDto();
				DtoMarshaller.copyToDto(q, dto);
				results.add(dto);
			}
		}
		return results;
	}

	//TODO
	// find a single question group by its id
	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
	@ResponseBody
	public QuestionDto findQuestion(@PathVariable("id") Long id){
		Question sg =questionDao.getByKey(id);		
		QuestionDto dto = null;
		if(sg != null){
			dto = new QuestionDto();
			DtoMarshaller.copyToDto(sg, dto);
		}
		return dto;	
	}
	
	// TODO
	// delete question group by id
	@RequestMapping(method = RequestMethod.DELETE, value = "/del/{id}")
	@ResponseBody
	public RestStatusDto deleteQuestionById(@PathVariable("id") Long id){
		Question qg = questionDao.getByKey(id);		
		RestStatusDto dto = null;
		dto = new RestStatusDto();
		dto.setStatus("failed");
				  
		// check if question exists in the datastore
		if (qg != null){
			// delete question group
			//questionDao.delete(qg);
			dto.setStatus("ok");	
		}
		return dto;
	}
	
	// save a question
	@RequestMapping(method = RequestMethod.POST, value="/")
	@ResponseBody
	public QuestionDto saveQuestion(@RequestBody QuestionDto questionDto){
		QuestionDto dto = null;
		
		// if the POST data contains a valid QuestionDto, continue. Otherwise, server will respond with 400 Bad Request 
		if (questionDto != null){
			Long keyId = questionDto.getKeyId();
			Question q;
					
			// if the questionDto has a key, try to get the question.
			if (keyId != null) {
				q = questionDao.getByKey(keyId);
				// if the question doesn't exist, create a new question
				if (q == null) {
					q = new Question();
				}
			} else {
				q = new Question();
			}
			
			// copy the properties, except the createdDateTime property, because it is set in the Dao.
			BeanUtils.copyProperties(questionDto, q, new String[] {"createdDateTime"});
			q = questionDao.save(q);
			
			// TODO 
			//saveSurveyUpdateMessage(q.getSurveyId());
			// code lives in SurveyServiceImpl
			
			dto = new QuestionDto();
			DtoMarshaller.copyToDto(q, dto);
		}
		return dto;
	}
}
