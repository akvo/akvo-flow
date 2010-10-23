package org.waterforpeople.mapping.app.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.waterforpeople.mapping.app.gwt.client.surveyinstance.QuestionAnswerStoreDto;
import org.waterforpeople.mapping.app.gwt.server.surveyinstance.SurveyInstanceServiceImpl;
import org.waterforpeople.mapping.app.web.dto.RawDataImportRequest;

import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;

public class RawDataRestServlet extends AbstractRestApiServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2409014651721639814L;

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
		if (RawDataImportRequest.ACTION_PARAM.equals(importReq.getAction())) {
			SurveyInstanceServiceImpl sisi = new SurveyInstanceServiceImpl();
			List<QuestionAnswerStoreDto> dtoList = new ArrayList<QuestionAnswerStoreDto>();
			for (Map.Entry<Long, String> item : importReq
					.getQuestionAnswerMap().entrySet()) {
				QuestionAnswerStoreDto qasDto = new QuestionAnswerStoreDto();
				qasDto.setQuestionID(item.getKey().toString());
				qasDto.setSurveyInstanceId(importReq.getSurveyInstanceId());
				qasDto.setValue(item.getValue());
				dtoList.add(qasDto);
			}
			sisi.updateQuestions(dtoList);
		}

		return null;
	}

	@Override
	protected void writeOkResponse(RestResponse resp) throws Exception {
		// no-op

	}

}
