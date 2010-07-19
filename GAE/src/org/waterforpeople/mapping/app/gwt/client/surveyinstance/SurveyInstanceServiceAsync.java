package org.waterforpeople.mapping.app.gwt.client.surveyinstance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SurveyInstanceServiceAsync {

	void listSurveyInstance(Date beginDate,
			AsyncCallback<ArrayList<SurveyInstanceDto>> callback);

	void saveSurveyInstance(SurveyInstanceDto item,
			AsyncCallback<SurveyInstanceDto> callback);

	void deleteSurveyInstance(Long id, AsyncCallback<Void> callback);

	void listQuestionsByInstance(Long instanceId,
			AsyncCallback<List<QuestionAnswerStoreDto>> callback);

}
