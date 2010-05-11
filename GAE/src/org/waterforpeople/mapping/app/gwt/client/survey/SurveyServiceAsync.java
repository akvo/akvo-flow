package org.waterforpeople.mapping.app.gwt.client.survey;

import java.util.Date;
import java.util.List;

import com.gallatinsystems.survey.app.web.client.dto.SurveyGroupDto;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SurveyServiceAsync {

	public void listSurvey(AsyncCallback<SurveyDto[]> callback);

	void listSurveyActivityByDate(Date startDate, Date endDate,
			String rollUpType, AsyncCallback<SurveyActivityDto[]> callback);

	void listSurveyQuestionByType(String typeCode,
			AsyncCallback<SurveyQuestionDto[]> callback);

	void listSurveyGroups(String cursorString,
			AsyncCallback<List<SurveyGroupDto>> callback);

	void getSurveyGroup(String surveyGroupCode,
			AsyncCallback<List<SurveyDto>> callback);

}
