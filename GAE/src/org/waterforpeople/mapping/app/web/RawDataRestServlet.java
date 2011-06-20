package org.waterforpeople.mapping.app.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto.QuestionType;
import org.waterforpeople.mapping.app.gwt.client.surveyinstance.QuestionAnswerStoreDto;
import org.waterforpeople.mapping.app.gwt.server.surveyinstance.SurveyInstanceServiceImpl;
import org.waterforpeople.mapping.app.web.dto.RawDataImportRequest;
import org.waterforpeople.mapping.dao.QuestionAnswerStoreDao;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.QuestionAnswerStore;
import org.waterforpeople.mapping.domain.SurveyInstance;

import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;
import com.gallatinsystems.survey.dao.QuestionDao;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.Question.Type;
import com.google.appengine.api.datastore.KeyFactory;

public class RawDataRestServlet extends AbstractRestApiServlet {

	private static final long serialVersionUID = 2409014651721639814L;

	private SurveyInstanceDAO instanceDao;

	public RawDataRestServlet() {
		instanceDao = new SurveyInstanceDAO();
	}

	@Override
	protected RestRequest convertRequest() throws Exception {
		HttpServletRequest req = getRequest();
		RestRequest restRequest = new RawDataImportRequest();
		restRequest.populateFromHttpRequest(req);
		return restRequest;
	}

	@Override
	protected RestResponse handleRequest(RestRequest req) throws Exception {
		SurveyInstanceServiceImpl sisi = new SurveyInstanceServiceImpl();
		RawDataImportRequest importReq = (RawDataImportRequest) req;
		if (RawDataImportRequest.SAVE_SURVEY_INSTANCE_ACTION.equals(importReq
				.getAction())) {		
			List<QuestionAnswerStoreDto> dtoList = new ArrayList<QuestionAnswerStoreDto>();
			if (importReq.getSurveyInstanceId() == null
					&& importReq.getSurveyId() != null) {
				// if the instanceID is null, we need to create one
				createInstance(importReq);
			}
			for (Map.Entry<Long, String[]> item : importReq
					.getQuestionAnswerMap().entrySet()) {
				QuestionAnswerStoreDto qasDto = new QuestionAnswerStoreDto();
				qasDto.setQuestionID(item.getKey().toString());
				qasDto.setSurveyInstanceId(importReq.getSurveyInstanceId());
				qasDto.setValue(item.getValue()[0]);
				qasDto.setType(item.getValue()[1]);
				qasDto.setSurveyId(importReq.getSurveyId());
				qasDto.setCollectionDate(importReq.getCollectionDate());
				dtoList.add(qasDto);
			}
			sisi.updateQuestions(dtoList, true);
		} else if (RawDataImportRequest.RESET_SURVEY_INSTANCE_ACTION
				.equals(importReq.getAction())) {
			SurveyInstance instance = instanceDao.getByKey(importReq
					.getSurveyInstanceId());
			List<QuestionAnswerStore> oldAnswers = instanceDao
					.listQuestionAnswerStore(importReq.getSurveyInstanceId(),
							null);
			if (oldAnswers != null && oldAnswers.size() > 0) {
				instanceDao.delete(oldAnswers);
				if (instance != null) {
					instance.setLastUpdateDateTime(new Date());
					instance.setSubmitterName(importReq.getSubmitter());
					instance.setSurveyId(importReq.getSurveyId());
					instanceDao.save(instance);
				}
			} else {
				if (instance == null) {
					instance = new SurveyInstance();
					instance.setKey(KeyFactory.createKey(
							SurveyInstance.class.getSimpleName(),
							importReq.getSurveyInstanceId()));
					instance.setSurveyId(importReq.getSurveyId());
					instance.setCollectionDate(importReq.getCollectionDate());
					instance.setSubmitterName(importReq.getSubmitter());
					instanceDao.save(instance);
				} else {
					instance.setLastUpdateDateTime(new Date());
					instance.setSubmitterName(importReq.getSubmitter());
					instance.setSurveyId(importReq.getSurveyId());
					instanceDao.save(instance);
				}
			}
		} else if (RawDataImportRequest.SAVE_FIXED_FIELD_SURVEY_INSTANCE_ACTION
				.equals(importReq.getAction())) {

			if (importReq.getFixedFieldValues() != null
					&& importReq.getFixedFieldValues().size() > 0) {
				// this method assumes we're always creating a new instance
				SurveyInstance inst = createInstance(importReq);
				QuestionDao questionDao = new QuestionDao();
				List<Question> questionList = questionDao
						.listQuestionsBySurvey(importReq.getSurveyId());

				if (questionList != null
						&& questionList.size() >= importReq
								.getFixedFieldValues().size()) {
					List<QuestionAnswerStore> answers = new ArrayList<QuestionAnswerStore>();
					for (int i = 0; i < importReq.getFixedFieldValues().size(); i++) {
						String val = importReq.getFixedFieldValues().get(i);
						if (val != null && val.trim().length() > 0) {
							QuestionAnswerStore ans = new QuestionAnswerStore();
							ans.setQuestionID(questionList.get(i).getKey()
									.getId()
									+ "");
							ans.setValue(val);
							Type type = questionList.get(i).getType();
							if (Type.GEO == type) {
								ans.setType(QuestionType.GEO.toString());
							} else if (Type.PHOTO == type) {
								ans.setType("IMAGE");
							} else {
								ans.setType("VALUE");
							}
							ans.setSurveyId(importReq.getSurveyId());
							ans.setSurveyInstanceId(importReq
									.getSurveyInstanceId());
							ans.setCollectionDate(importReq.getCollectionDate());
							answers.add(ans);
						}
					}
					if (answers.size() > 0) {
						QuestionAnswerStoreDao qasDao = new QuestionAnswerStoreDao();
						qasDao.save(answers);
						sisi.sendProcessingMessages(inst);
					}

				} else {
					log("No questions found for the survey id "
							+ importReq.getSurveyId());
				}
				// todo: send processing message
			}
		}
		return null;
	}

	/**
	 * constructs and persists a new surveyInstance using the data from the
	 * import request
	 * 
	 * @param importReq
	 * @return
	 */
	private SurveyInstance createInstance(RawDataImportRequest importReq) {
		SurveyInstance inst = new SurveyInstance();
		inst.setSurveyId(importReq.getSurveyId());
		inst.setCollectionDate(importReq.getCollectionDate() != null ? importReq
				.getCollectionDate() : new Date());
		inst.setApproximateLocationFlag("False");
		inst.setDeviceIdentifier("IMPORTER");
		inst.setSurveyedLocaleId(importReq.getSurveyedLocaleId());
		SurveyInstanceDAO instDao = new SurveyInstanceDAO();
		inst = instDao.save(inst);
		// set the key so the subsequent logic can populate it in the
		// QuestionAnswerStore objects
		importReq.setSurveyInstanceId(inst.getKey().getId());
		if (importReq.getCollectionDate() == null) {
			importReq.setCollectionDate(inst.getCollectionDate());
		}
		return inst;
	}

	@Override
	protected void writeOkResponse(RestResponse resp) throws Exception {
		// no-op

	}

}
