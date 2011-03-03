package org.waterforpeople.mapping.app.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.waterforpeople.mapping.app.gwt.client.surveyinstance.QuestionAnswerStoreDto;
import org.waterforpeople.mapping.app.gwt.server.surveyinstance.SurveyInstanceServiceImpl;
import org.waterforpeople.mapping.app.web.dto.RawDataImportRequest;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.QuestionAnswerStore;
import org.waterforpeople.mapping.domain.SurveyInstance;

import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;
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
		RawDataImportRequest importReq = (RawDataImportRequest) req;
		if (RawDataImportRequest.SAVE_SURVEY_INSTANCE_ACTION.equals(importReq
				.getAction())) {
			SurveyInstanceServiceImpl sisi = new SurveyInstanceServiceImpl();
			List<QuestionAnswerStoreDto> dtoList = new ArrayList<QuestionAnswerStoreDto>();
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
			sisi.updateQuestions(dtoList,true);
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
		}
		return null;
	}

	@Override
	protected void writeOkResponse(RestResponse resp) throws Exception {
		// no-op

	}

}
