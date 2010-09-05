package org.waterforpeople.mapping.app.gwt.server.surveyinstance;

import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.url;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class SurveyInstanceServiceImpl extends RemoteServiceServlet implements
		SurveyInstanceService {

	private static final long serialVersionUID = -9175237700587455358L;

	@Override
	public ResponseDto<ArrayList<SurveyInstanceDto>> listSurveyInstance(
			Date beginDate, String cursorString) {
		SurveyInstanceDAO dao = new SurveyInstanceDAO();
		List<SurveyInstance> siList = null;
		if (beginDate == null) {
			Calendar c = Calendar.getInstance();
			c.add(Calendar.DAY_OF_MONTH, -90);
			beginDate = c.getTime();
		}
		siList = dao.listByDateRange(beginDate, null, cursorString);
		String newCursor = SurveyInstanceDAO.getCursor(siList);

		ArrayList<SurveyInstanceDto> siDtoList = new ArrayList<SurveyInstanceDto>();
		for (SurveyInstance siItem : siList) {
			siDtoList.add(marshalToDto(siItem));
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
		if (questions != null) {
			for (QuestionAnswerStore qas : questions) {
				QuestionAnswerStoreDto qasDto = new QuestionAnswerStoreDto();
				DtoMarshaller.copyToDto(qas, qasDto);
				questionDtos.add(qasDto);
			}
		}
		return questionDtos;
	}

	private SurveyInstanceDto marshalToDto(SurveyInstance si) {
		SurveyInstanceDto siDto = new SurveyInstanceDto();
		DtoMarshaller.copyToDto(si, siDto);
		siDto.setQuestionAnswersStore(null);
		if (si.getQuestionAnswersStore() != null) {
			for (QuestionAnswerStore qas : si.getQuestionAnswersStore()) {
				QuestionAnswerStoreDto qasDto = new QuestionAnswerStoreDto();
				DtoMarshaller.copyToDto(qas, qasDto);
				siDto.addQuestionAnswerStore(qasDto);
			}
		}
		return siDto;
	}

	@Override
	public SurveyInstanceDto saveSurveyInstance(SurveyInstanceDto item) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * updates the list of QAS dto objects passed in and fires summarization
	 * messages to the async queues
	 */
	@Override
	public List<QuestionAnswerStoreDto> updateQuestions(
			List<QuestionAnswerStoreDto> dtoList) {
		List<QuestionAnswerStore> domainList = new ArrayList<QuestionAnswerStore>();
		for (QuestionAnswerStoreDto dto : dtoList) {
			QuestionAnswerStore answer = new QuestionAnswerStore();
			DtoMarshaller.copyToCanonical(answer, dto);
			domainList.add(answer);
		}
		SurveyInstanceDAO dao = new SurveyInstanceDAO();
		SurveyAttributeMappingDao mappingDao = new SurveyAttributeMappingDao();
		dao.save(domainList);
		// now send a change message for each item
		Queue queue = QueueFactory.getQueue("dataUpdate");
		for (QuestionAnswerStoreDto item : dtoList) {
			DataChangeRecord value = new DataChangeRecord(
					QuestionAnswerStore.class.getName(), item.getQuestionID(),
					item.getOldValue(), item.getValue());
			queue.add(url("/app_worker/dataupdate").param(
					DataSummarizationRequest.OBJECT_KEY, item.getQuestionID())
					.param(DataSummarizationRequest.OBJECT_TYPE,
							"QuestionDataChange").param(
							DataSummarizationRequest.VALUE_KEY,
							value.packString()));
			// see if the question is mapped. And if it is, send an Access Point
			// change message
			SurveyAttributeMapping mapping = mappingDao
					.findMappingForQuestion(item.getQuestionID());
			if (mapping != null) {
				DataChangeRecord apValue = new DataChangeRecord(
						"AcessPointUpdate", mapping.getSurveyId() + "|"
								+ mapping.getSurveyQuestionId() + "|"
								+ item.getSurveyInstanceId() + "|"
								+ mapping.getKey().getId(), item.getOldValue(),
						item.getValue());
				queue.add(url("/app_worker/dataupdate").param(
						DataSummarizationRequest.OBJECT_KEY,
						item.getQuestionID()).param(
						DataSummarizationRequest.OBJECT_TYPE,
						"AccessPointChange").param(
						DataSummarizationRequest.VALUE_KEY,
						apValue.packString()));
			}
		}

		return dtoList;
	}
}
