package org.waterforpeople.mapping.app.gwt.client.surveyinstance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SurveyInstanceServiceAsync {

	void listSurveyInstance(Date beginDate, String cursorString,
			AsyncCallback< ResponseDto<ArrayList<SurveyInstanceDto>>> callback);

	void saveSurveyInstance(SurveyInstanceDto item,
			AsyncCallback<SurveyInstanceDto> callback);

	void listQuestionsByInstance(Long instanceId,
			AsyncCallback<List<QuestionAnswerStoreDto>> callback);

}
