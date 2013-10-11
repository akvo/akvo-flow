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
import java.util.TreeMap;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.waterforpeople.mapping.analytics.dao.SurveyQuestionSummaryDao;
import org.waterforpeople.mapping.app.gwt.client.surveyinstance.QuestionAnswerStoreDto;
import org.waterforpeople.mapping.app.util.DtoMarshaller;
import org.waterforpeople.mapping.app.web.rest.dto.QuestionAnswerStorePayload;
import org.waterforpeople.mapping.app.web.rest.dto.RestStatusDto;
import org.waterforpeople.mapping.dao.QuestionAnswerStoreDao;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.QuestionAnswerStore;

import com.gallatinsystems.survey.dao.QuestionDao;
import com.gallatinsystems.survey.dao.SurveyUtils;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.surveyal.domain.SurveyalValue;
import com.gallatinsystems.surveyal.dao.SurveyedLocaleDao;


@Controller
@RequestMapping("/question_answers")
public class QuestionAnswerRestService {

	// list questionAnswerStores by id
	@RequestMapping(method = RequestMethod.GET, value = "")
	@ResponseBody
	public Map<String, List<QuestionAnswerStoreDto>> listQABySurveyInstanceId(
			@RequestParam(value = "surveyInstanceId", defaultValue = "") Long surveyInstanceId) {
		final Map<String, List<QuestionAnswerStoreDto>> response = new HashMap<String, List<QuestionAnswerStoreDto>>();
		List<QuestionAnswerStoreDto> results = new ArrayList<QuestionAnswerStoreDto>();
		List<QuestionAnswerStore> questionAnswerStores = null;
		SurveyInstanceDAO siDao = new SurveyInstanceDAO();
		QuestionDao qDao = new QuestionDao();

		// get list of question-answers
		if (surveyInstanceId != null) {
			questionAnswerStores = siDao.listQuestionAnswerStore(
					surveyInstanceId, null);

			if (questionAnswerStores != null && questionAnswerStores.size() > 0) {

				// get the list of questions belonging to this surveyInstance in
				// the right order
				List<Question> qList = qDao
						.listQuestionsInOrder(questionAnswerStores.get(0)
								.getSurveyId(), null);

				Map<Integer, QuestionAnswerStoreDto> orderedResults = new TreeMap<Integer, QuestionAnswerStoreDto>();

				// sort the questionAnswers in the order of the questions
				int notFoundCount = 0;
				if (qList != null) {
					for (QuestionAnswerStore qas : questionAnswerStores) {
						QuestionAnswerStoreDto qasDto = new QuestionAnswerStoreDto();
						DtoMarshaller.copyToDto(qas, qasDto);
						int idx = -1 - notFoundCount;
						for (int i = 0; i < qList.size(); i++) {
							if (Long.parseLong(qas.getQuestionID()) == qList
									.get(i).getKey().getId()) {
								qasDto.setQuestionText(qList.get(i).getText());
								idx = i;
								break;
							}
						}
						// do this to prevent collisions on the -1 key if there
						// is more than one questionAnswerStore item that isn't
						// in the question list. QuestionAnswerStores that don't
						// have a corresponding question are put at the front
						// of the map.
						if (idx < 0) {
							notFoundCount++;
						}
						orderedResults.put(idx, qasDto);
					}
				}
				results = new ArrayList<QuestionAnswerStoreDto>(
						orderedResults.values());
			}
		}

		response.put("question_answers", results);
		return response;
	}

	// find a single questionAnswerStore by the questionAnswerStoreId
	// TODO include question text in dto
	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
	@ResponseBody
	public Map<String, QuestionAnswerStoreDto> findQuestionAnswerStore(
			@PathVariable("id") Long id) {
		final Map<String, QuestionAnswerStoreDto> response = new HashMap<String, QuestionAnswerStoreDto>();
		QuestionAnswerStoreDao qaDao = new QuestionAnswerStoreDao();
		QuestionAnswerStore s = qaDao.getByKey(id);
		QuestionAnswerStoreDto dto = null;
		if (s != null) {
			dto = new QuestionAnswerStoreDto();
			DtoMarshaller.copyToDto(s, dto);
		}
		response.put("question_answer", dto);
		return response;

	}

	// update existing questionAnswerStore
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}")
	@ResponseBody
	public Map<String, Object> saveExistingQuestionAnswerStore(
			@RequestBody QuestionAnswerStorePayload payLoad) {
		final QuestionAnswerStoreDto questionAnswerStoreDto = payLoad
				.getQuestion_answer();
		final Map<String, Object> response = new HashMap<String, Object>();
		QuestionAnswerStoreDto dto = null;
		QuestionAnswerStoreDao qaDao = new QuestionAnswerStoreDao();

		RestStatusDto statusDto = new RestStatusDto();
		statusDto.setStatus("failed");

		// if the POST data contains a valid questionAnswerStoreDto, continue.
		// Otherwise,
		// server will respond with 400 Bad Request
		if (questionAnswerStoreDto != null) {
			Long keyId = questionAnswerStoreDto.getKeyId();
			QuestionAnswerStore qa;

			// if the questionAnswerStoreDto has a key, try to get the
			// questionAnswerStore.
			if (keyId != null) {
				qa = qaDao.getByKey(keyId);
				// if we find the questionAnswerStore, update it's properties
				if (qa != null) {
					//Before updating the properties, fix the questionAnswerSummary counts if it is an OPTION question
					QuestionDao qDao = new QuestionDao();
					Question q = qDao.getByKey(Long.parseLong(qa.getQuestionID()));
					if (q != null && Question.Type.OPTION.equals(q.getType())) {
						// decrease count of current item
						SurveyQuestionSummaryDao.incrementCount(qa, -1);
						
						// increase count of new item
						String newVal = questionAnswerStoreDto.getValue();
						if (newVal != null && newVal.trim().length() > 0){
							SurveyQuestionSummaryDao.incrementCount(constructQAS(qa.getQuestionID(),newVal),1);
						}		
					}					
					// copy the properties, except the createdDateTime property,
					// because it is set in the Dao.
					BeanUtils.copyProperties(questionAnswerStoreDto, qa,
							new String[] { "createdDateTime", "status",
									"version", "lastUpdateDateTime",
									"displayName", "questionGroupList", "questionText" });
					qa = qaDao.save(qa);
					
					// next, update the corresponding surveyalValue object
					// find surveyalValue based on surveyInstanceId and questionId
					Long surveyInstanceId = qa.getSurveyInstanceId();
					String questionId = qa.getQuestionID();
					SurveyedLocaleDao slDao = new SurveyedLocaleDao();
					List<SurveyalValue> svals = slDao.listSVByQuestionAndSurveyInstance(surveyInstanceId,Long.parseLong(questionId)); 
					if (svals != null && svals.size() > 0){
						SurveyalValue sval = svals.get(0);
						sval.setStringValue(qa.getValue());
						slDao.save(sval);
					}
					
					// return result to the Dashboard
					dto = new QuestionAnswerStoreDto();
					DtoMarshaller.copyToDto(qa, dto);
					// give back the question text as we received it
					dto.setQuestionText(questionAnswerStoreDto.getQuestionText());
					statusDto.setStatus("ok");

					try {
						// A PUT is done when editing a QuestionAnswerStore, we
						// need to invalidate a cached report
						List<Long> surveyIds = new ArrayList<Long>();
						surveyIds.add(questionAnswerStoreDto.getSurveyId());
						SurveyUtils
								.notifyReportService(surveyIds, "invalidate");
					} catch (Exception e) {
						// no-op
					}
				}
			}
		}

		response.put("meta", statusDto);
		response.put("question_answer", dto);
		return response;
	}

	
	/**
	 * helper method to create a new QuestionAnswerStore object using the values
	 * passed in.
	 * Same method as in SurveyQuestionSummaryUpdater
	 * @param id
	 * @param value
	 * @return
	 */
	private QuestionAnswerStore constructQAS(String id, String value) {
		QuestionAnswerStore qas = new QuestionAnswerStore();
		qas.setQuestionID(id);
		qas.setValue(value);
		return qas;
	}
	
}
