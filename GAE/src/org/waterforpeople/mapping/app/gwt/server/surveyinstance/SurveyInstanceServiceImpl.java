/*
 *  Copyright (C) 2010-2012 Stichting Akvo (Akvo Foundation)
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

package org.waterforpeople.mapping.app.gwt.server.surveyinstance;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.waterforpeople.mapping.app.gwt.client.surveyinstance.QuestionAnswerStoreDto;
import org.waterforpeople.mapping.app.gwt.client.surveyinstance.SurveyInstanceDto;
import org.waterforpeople.mapping.app.gwt.client.surveyinstance.SurveyInstanceService;
import org.waterforpeople.mapping.app.util.DtoMarshaller;
import org.waterforpeople.mapping.dao.SurveyAttributeMappingDao;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.QuestionAnswerStore;
import org.waterforpeople.mapping.domain.SurveyAttributeMapping;
import org.waterforpeople.mapping.domain.SurveyInstance;
import org.waterforpeople.mapping.helper.SurveyEventHelper;

import com.gallatinsystems.framework.analytics.summarization.DataSummarizationRequest;
import com.gallatinsystems.framework.domain.DataChangeRecord;
import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.gallatinsystems.survey.dao.QuestionDao;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.surveyal.app.web.SurveyalRestRequest;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class SurveyInstanceServiceImpl extends RemoteServiceServlet implements
		SurveyInstanceService {

	private static final long serialVersionUID = -9175237700587455358L;
	private static final Logger log = Logger
			.getLogger(SurveyInstanceServiceImpl.class);

	@Override
	public ResponseDto<ArrayList<SurveyInstanceDto>> listSurveyInstance(
			Date beginDate, Date toDate, boolean unapprovedOnlyFlag,
			Long surveyId, String source, String cursorString) {
		SurveyInstanceDAO dao = new SurveyInstanceDAO();
		SurveyDAO surveyDao = new SurveyDAO();
		List<Survey> surveyList = surveyDao.list("all");
		HashMap<Long, String> surveyMap = new HashMap<Long, String>();
		for (Survey s : surveyList) {
			surveyMap.put(s.getKey().getId(), s.getPath() + "/" + s.getCode());
		}
		List<SurveyInstance> siList = null;
		if (beginDate == null && toDate == null) {
			Calendar c = Calendar.getInstance();
			c.add(Calendar.YEAR, -90);
			beginDate = c.getTime();
		}
		siList = dao.listByDateRange(beginDate, toDate, unapprovedOnlyFlag,
				surveyId, source, cursorString);
		String newCursor = SurveyInstanceDAO.getCursor(siList);

		ArrayList<SurveyInstanceDto> siDtoList = new ArrayList<SurveyInstanceDto>();
		for (SurveyInstance siItem : siList) {
			String code = surveyMap.get(siItem.getSurveyId());
			SurveyInstanceDto siDto = marshalToDto(siItem);
			if (code != null)
				siDto.setSurveyCode(code);
			siDtoList.add(siDto);

		}
		ResponseDto<ArrayList<SurveyInstanceDto>> response = new ResponseDto<ArrayList<SurveyInstanceDto>>();
		response.setCursorString(newCursor);
		response.setPayload(siDtoList);
		return response;
	}

	public List<QuestionAnswerStoreDto> listQuestionsByInstance(Long instanceId) {
		List<QuestionAnswerStoreDto> questionDtos = new ArrayList<QuestionAnswerStoreDto>();
		SurveyInstanceDAO dao = new SurveyInstanceDAO();
		List<QuestionAnswerStore> questions = dao.listQuestionAnswerStore(
				instanceId, null);
		QuestionDao qDao = new QuestionDao();

		if (questions != null && questions.size() > 0) {
			List<Question> qList = qDao.listQuestionInOrder(questions.get(0)
					.getSurveyId());
			Map<Integer, QuestionAnswerStoreDto> orderedResults = new TreeMap<Integer, QuestionAnswerStoreDto>();
			int notFoundCount = 0;
			if (qList != null) {
				for (QuestionAnswerStore qas : questions) {
					QuestionAnswerStoreDto qasDto = new QuestionAnswerStoreDto();
					DtoMarshaller.copyToDto(qas, qasDto);
					int idx = -1 - notFoundCount;
					for (int i = 0; i < qList.size(); i++) {
						if (Long.parseLong(qas.getQuestionID()) == qList.get(i)
								.getKey().getId()) {
							qasDto.setQuestionText(qList.get(i).getText());
							idx = i;
							break;
						}
					}
					if (idx < 0) {
						// do this to prevent collisions on the -1 key if there
						// is more than one questionAnswerStore item that isn't
						// in the question list
						notFoundCount++;
					}
					orderedResults.put(idx, qasDto);
				}
			}
			questionDtos = new ArrayList<QuestionAnswerStoreDto>(
					orderedResults.values());
		}
		return questionDtos;
	}

	private SurveyInstanceDto marshalToDto(SurveyInstance si) {
		SurveyInstanceDto siDto = new SurveyInstanceDto();
		DtoMarshaller.copyToDto(si, siDto);
		siDto.setQuestionAnswersStore(null);
		if (si.getQuestionAnswersStore() != null) {
			for (QuestionAnswerStore qas : si.getQuestionAnswersStore()) {
				siDto.addQuestionAnswerStore(marshalToDto(qas));
			}
		}
		return siDto;
	}

	private QuestionAnswerStoreDto marshalToDto(QuestionAnswerStore qas) {
		QuestionAnswerStoreDto qasDto = new QuestionAnswerStoreDto();
		DtoMarshaller.copyToDto(qas, qasDto);
		return qasDto;
	}

	/**
	 * updates the list of QAS dto objects passed in and fires summarization
	 * messages to the async queues
	 */
	@Override
	public List<QuestionAnswerStoreDto> updateQuestions(
			List<QuestionAnswerStoreDto> dtoList, boolean isApproved) {
		return updateQuestions(dtoList, isApproved, true);
	}

	public List<QuestionAnswerStoreDto> updateQuestions(
			List<QuestionAnswerStoreDto> dtoList, boolean isApproved,
			boolean processSummaries) {
		List<QuestionAnswerStore> domainList = new ArrayList<QuestionAnswerStore>();
		for (QuestionAnswerStoreDto dto : dtoList) {
			if ("".equals(dto.getValue()) && dto.getOldValue() == null) {
				continue; // empty string as value, skipping it
			}
			QuestionAnswerStore answer = new QuestionAnswerStore();
			DtoMarshaller.copyToCanonical(answer, dto);
			if (answer.getValue() != null) {
				answer.setValue(answer.getValue().replaceAll("\t", ""));
			}
			domainList.add(answer);
		}
		SurveyInstanceDAO dao = new SurveyInstanceDAO();
		dao.save(domainList);
		
		// this is not active - questionAnswerSummary objects are updated in bulk 
		// after the import  in RawDataSpreadsheetImporter
		if (isApproved && processSummaries) {
			SurveyAttributeMappingDao mappingDao = new SurveyAttributeMappingDao();
			// now send a change message for each item
			Queue queue = QueueFactory.getQueue("dataUpdate");
			for (QuestionAnswerStoreDto item : dtoList) {
				DataChangeRecord value = new DataChangeRecord(
						QuestionAnswerStore.class.getName(),
						item.getQuestionID(), item.getOldValue(),
						item.getValue());
				queue.add(TaskOptions.Builder
						.withUrl("/app_worker/dataupdate")
						.param(DataSummarizationRequest.OBJECT_KEY,
								item.getQuestionID())
						.param(DataSummarizationRequest.OBJECT_TYPE,
								"QuestionDataChange")
						.param(DataSummarizationRequest.VALUE_KEY,
								value.packString()));
				// see if the question is mapped. And if it is, send an Access
				// Point
				// change message
				SurveyAttributeMapping mapping = mappingDao
						.findMappingForQuestion(item.getQuestionID());
				if (mapping != null) {
					DataChangeRecord apValue = new DataChangeRecord(
							"AcessPointUpdate", mapping.getSurveyId() + "|"
									+ mapping.getSurveyQuestionId() + "|"
									+ item.getSurveyInstanceId() + "|"
									+ mapping.getKey().getId(),
							item.getOldValue(), item.getValue());
					queue.add(TaskOptions.Builder
							.withUrl("/app_worker/dataupdate")
							.param(DataSummarizationRequest.OBJECT_KEY,
									item.getQuestionID())
							.param(DataSummarizationRequest.OBJECT_TYPE,
									"AccessPointChange")
							.param(DataSummarizationRequest.VALUE_KEY,
									apValue.packString()));
				}
			}
		}

		return dtoList;
	}

	/**
	 * lists all responses for a single question
	 */
	@Override
	public ResponseDto<ArrayList<QuestionAnswerStoreDto>> listResponsesByQuestion(
			Long questionId, String cursorString) {
		SurveyInstanceDAO dao = new SurveyInstanceDAO();
		List<QuestionAnswerStore> qasList = dao
				.listQuestionAnswerStoreForQuestion(questionId.toString(),
						cursorString);
		String newCursor = SurveyInstanceDAO.getCursor(qasList);
		ArrayList<QuestionAnswerStoreDto> qasDtoList = new ArrayList<QuestionAnswerStoreDto>();
		for (QuestionAnswerStore item : qasList) {
			qasDtoList.add(marshalToDto(item));
		}
		ResponseDto<ArrayList<QuestionAnswerStoreDto>> response = new ResponseDto<ArrayList<QuestionAnswerStoreDto>>();
		response.setCursorString(newCursor);
		response.setPayload(qasDtoList);
		return response;
	}

	/**
	 * deletes a survey instance. This will only back out Question summaries. To
	 * back out the access point, the AP needs to be deleted manually since it
	 * may have come from multiple instances.
	 */
	@Override
	public void deleteSurveyInstance(Long instanceId) {
		if (instanceId != null) {
			SurveyInstanceDAO dao = new SurveyInstanceDAO();
			List<QuestionAnswerStore> answers = dao.listQuestionAnswerStore(
					instanceId, null);
			if (answers != null) {
				// back out summaries
				Queue queue = QueueFactory.getQueue("dataUpdate");
				for (QuestionAnswerStore ans : answers) {
					DataChangeRecord value = new DataChangeRecord(
							QuestionAnswerStore.class.getName(),
							ans.getQuestionID(), ans.getValue(), "");
					queue.add(TaskOptions.Builder
							.withUrl("/app_worker/dataupdate")
							.param(DataSummarizationRequest.OBJECT_KEY,
									ans.getQuestionID())
							.param(DataSummarizationRequest.OBJECT_TYPE,
									"QuestionDataChange")
							.param(DataSummarizationRequest.VALUE_KEY,
									value.packString()));
				}
				dao.delete(answers);
			}
			SurveyInstance instance = dao.getByKey(instanceId);
			if (instance != null) {
				dao.delete(instance);
				log.log(Level.INFO, "Deleted: " + instanceId);
			}
		}
	}

	/**
	 * saves a new survey instance and triggers processing
	 * 
	 * @param instance
	 * @return
	 */
	public SurveyInstanceDto submitSurveyInstance(SurveyInstanceDto instance) {
		SurveyInstanceDAO dao = new SurveyInstanceDAO();
		SurveyInstance domain = new SurveyInstance();
		DtoMarshaller.copyToCanonical(domain, instance);
		if (domain.getCollectionDate() == null) {
			domain.setCollectionDate(new Date());
		}
		domain = dao.save(domain);
		instance.setKeyId(domain.getKey().getId());
		if (instance.getQuestionAnswersStore() != null) {
			List<QuestionAnswerStore> answerList = new ArrayList<QuestionAnswerStore>();
			for (QuestionAnswerStoreDto ans : instance
					.getQuestionAnswersStore()) {
				QuestionAnswerStore store = new QuestionAnswerStore();
				if (ans.getCollectionDate() == null) {
					ans.setCollectionDate(domain.getCollectionDate());
				}
				if (ans.getValue() != null) {
					ans.setValue(ans.getValue().replaceAll("\t", ""));
					if (ans.getValue().length() > 500) {
						ans.setValue(ans.getValue().substring(0, 499));

					}
				}
				DtoMarshaller.copyToCanonical(store, ans);
				store.setSurveyInstanceId(domain.getKey().getId());
				answerList.add(store);
			}
			dao.save(answerList);
			if (instance.getApprovedFlag() == null
					|| !"False".equalsIgnoreCase(instance.getApprovedFlag())) {
				sendProcessingMessages(domain);
			}
		}
		SurveyEventHelper.fireEvent(SurveyEventHelper.SUBMISSION_EVENT,
				instance.getSurveyId(), instance.getKeyId());
		return instance;
	}

	/**
	 * marks the survey instance as approved, updating any changed answers as it
	 * does so and then sends a processing message to the task queue so the
	 * instance can be summarized.
	 */
	public void approveSurveyInstance(Long surveyInstanceId,
			List<QuestionAnswerStoreDto> changedAnswers) {
		SurveyInstanceDAO dao = new SurveyInstanceDAO();
		SurveyInstance instance = dao.getByKey(surveyInstanceId);
		if (changedAnswers != null && changedAnswers.size() > 0) {
			updateQuestions(changedAnswers, false);
		}
		if (instance != null) {
			if (instance.getApprovedFlag() == null
					|| !"True".equalsIgnoreCase(instance.getApprovedFlag())) {
				instance.setApprovedFlag("True");
				sendProcessingMessages(instance);
				// fire a survey event
				SurveyEventHelper.fireEvent(SurveyEventHelper.APPROVAL_EVENT,
						instance.getSurveyId(), instance.getKey().getId());
			}
		}
	}

	/**
	 * lists all surveyInstances associated with the surveyedLocaleId passed in.
	 * 
	 * @param localeId
	 * @return
	 */
	public ResponseDto<ArrayList<SurveyInstanceDto>> listInstancesByLocale(
			Long localeId, Date dateFrom, Date dateTo, String cursor) {
		SurveyInstanceDAO dao = new SurveyInstanceDAO();
		ResponseDto<ArrayList<SurveyInstanceDto>> response = new ResponseDto<ArrayList<SurveyInstanceDto>>();
		ArrayList<SurveyInstanceDto> dtoList = new ArrayList<SurveyInstanceDto>();
		List<SurveyInstance> instances = dao.listInstancesByLocale(localeId,
				dateFrom, dateTo, cursor);
		if (instances != null) {
			for (SurveyInstance inst : instances) {
				SurveyInstanceDto dto = new SurveyInstanceDto();
				DtoMarshaller.copyToDto(inst, dto);
				dtoList.add(dto);
			}
			response.setPayload(dtoList);
			if (dtoList.size() == SurveyInstanceDAO.DEFAULT_RESULT_COUNT) {
				response.setCursorString(SurveyInstanceDAO.getCursor(instances));
			}
		}
		return response;
	}

	public void sendProcessingMessages(SurveyInstance domain) {
		// send async request to populate the AccessPoint using the mapping
		QueueFactory.getDefaultQueue().add(
				TaskOptions.Builder.withUrl("/app_worker/task")
						.param("action", "addAccessPoint")
						.param("surveyId", domain.getKey().getId() + ""));
		// send asyn crequest to summarize the instance
		QueueFactory.getQueue("dataSummarization").add(
				TaskOptions.Builder.withUrl("/app_worker/datasummarization")
						.param("objectKey", domain.getKey().getId() + "")
						.param("type", "SurveyInstance"));
		QueueFactory.getDefaultQueue().add(
				TaskOptions.Builder
						.withUrl("/app_worker/surveyalservlet")
						.param(SurveyalRestRequest.ACTION_PARAM,
								SurveyalRestRequest.INGEST_INSTANCE_ACTION)
						.param(SurveyalRestRequest.SURVEY_INSTANCE_PARAM,
								domain.getKey().getId() + ""));
	}

}
