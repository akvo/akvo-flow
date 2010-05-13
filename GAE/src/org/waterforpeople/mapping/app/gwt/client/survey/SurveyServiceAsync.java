package org.waterforpeople.mapping.app.gwt.client.survey;

import java.util.ArrayList;
import java.util.Date;

import com.gallatinsystems.survey.app.web.client.dto.SurveyGroupDto;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SurveyServiceAsync {

	public void listSurvey(AsyncCallback<SurveyDto[]> callback);

	void listSurveyActivityByDate(Date startDate, Date endDate,
			String rollUpType, AsyncCallback<SurveyActivityDto[]> callback);

	void listSurveyQuestionByType(String typeCode,
			AsyncCallback<SurveyQuestionDto[]> callback);

	void listSurveyGroups(String cursorString,
			AsyncCallback<ArrayList<SurveyGroupDto>> callback);

	void getSurveyGroup(String surveyGroupCode,
			AsyncCallback<ArrayList<SurveyDto>> callback);

}
