package org.waterforpeople.mapping.app.gwt.server.surveyinstance;

import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.url;

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

import com.gallatinsystems.framework.analytics.summarization.DataSummarizationRequest;
import com.gallatinsystems.framework.domain.DataChangeRecord;
import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.gallatinsystems.survey.dao.QuestionDao;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.Survey;
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class SurveyInstanceServiceImpl extends RemoteServiceServlet implements
		SurveyInstanceService {

	private static final long serialVersionUID = -9175237700587455358L;
	private static final Logger log = Logger
			.getLogger(SurveyInstanceServiceImpl.class);

	@Override
	public ResponseDto<ArrayList<SurveyInstanceDto>> listSurveyInstance(
			Date beginDate, boolean unapprovedOnlyFlag, String cursorString) {
		SurveyInstanceDAO dao = new SurveyInstanceDAO();
		SurveyDAO surveyDao = new SurveyDAO();
		List<Survey> surveyList = surveyDao.list("all");
		HashMap<Long, String> surveyMap = new HashMap<Long, String>();
		for (Survey s : surveyList) {
			surveyMap.put(s.getKey().getId(), s.getPath() + "/" + s.getCode());
		}
		List<SurveyInstance> siList = null;
		if (beginDate == null) {
			Calendar c = Calendar.getInstance();
			c.add(Calendar.DAY_OF_MONTH, -90);
			beginDate = c.getTime();
		}
		siList = dao.listByDateRange(beginDate, null, unapprovedOnlyFlag,
				cursorString);
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

		if (questions != null && questions.size()>0) {
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
			questionDtos = new ArrayList<QuestionAnswerStoreDto>(orderedResults.values());
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
		List<QuestionAnswerStore> domainList = new ArrayList<QuestionAnswerStore>();
		for (QuestionAnswerStoreDto dto : dtoList) {
			QuestionAnswerStore answer = new QuestionAnswerStore();
			DtoMarshaller.copyToCanonical(answer, dto);
			domainList.add(answer);
		}
		SurveyInstanceDAO dao = new SurveyInstanceDAO();
		SurveyAttributeMappingDao mappingDao = new SurveyAttributeMappingDao();
		dao.save(domainList);
		if (isApproved) {
			// now send a change message for each item
			Queue queue = QueueFactory.getQueue("dataUpdate");
			for (QuestionAnswerStoreDto item : dtoList) {
				DataChangeRecord value = new DataChangeRecord(
						QuestionAnswerStore.class.getName(), item
								.getQuestionID(), item.getOldValue(), item
								.getValue());
				queue
						.add(url("/app_worker/dataupdate").param(
								DataSummarizationRequest.OBJECT_KEY,
								item.getQuestionID()).param(
								DataSummarizationRequest.OBJECT_TYPE,
								"QuestionDataChange").param(
								DataSummarizationRequest.VALUE_KEY,
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
									+ mapping.getKey().getId(), item
									.getOldValue(), item.getValue());
					queue.add(url("/app_worker/dataupdate").param(
							DataSummarizationRequest.OBJECT_KEY,
							item.getQuestionID()).param(
							DataSummarizationRequest.OBJECT_TYPE,
							"AccessPointChange").param(
							DataSummarizationRequest.VALUE_KEY,
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
							QuestionAnswerStore.class.getName(), ans
									.getQuestionID(), ans.getValue(), "");
					queue.add(url("/app_worker/dataupdate").param(
							DataSummarizationRequest.OBJECT_KEY,
							ans.getQuestionID()).param(
							DataSummarizationRequest.OBJECT_TYPE,
							"QuestionDataChange").param(
							DataSummarizationRequest.VALUE_KEY,
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
		domain = dao.save(domain);
		instance.setKeyId(domain.getKey().getId());
		if (instance.getQuestionAnswersStore() != null) {
			List<QuestionAnswerStore> answerList = new ArrayList<QuestionAnswerStore>();
			for (QuestionAnswerStoreDto ans : instance
					.getQuestionAnswersStore()) {
				QuestionAnswerStore store = new QuestionAnswerStore();
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

		return instance;
	}

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
			}
		}
	}

	private void sendProcessingMessages(SurveyInstance domain) {
		// send async request to populate the AccessPoint using the mapping
		QueueFactory.getDefaultQueue().add(
				url("/app_worker/task").param("action", "addAccessPoint")
						.param("surveyId", domain.getKey().getId() + ""));
		// send asyn crequest to summarize the instance
		QueueFactory.getQueue("dataSummarization").add(
				url("/app_worker/datasummarization").param("objectKey",
						domain.getKey().getId() + "").param("type",
						"SurveyInstance"));
	}

}
