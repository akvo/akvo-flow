package org.waterforpeople.mapping.app.gwt.client.survey;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SurveyServiceAsync {

	public void listSurvey(AsyncCallback<SurveyDto[]> callback);


	void listSurveyQuestionByType(String typeCode,
			AsyncCallback<SurveyQuestionDto[]> callback);


	void getSurveyGroup(String surveyGroupCode,
			AsyncCallback<ArrayList<SurveyDto>> callback);

	void listSurveysByGroup(String selectedGroupId,
			AsyncCallback<ArrayList<SurveyDto>> callback);

	void save(SurveyGroupDto value, AsyncCallback<SurveyGroupDto> callback);

	void loadFullSurvey(Long surveyId, AsyncCallback<SurveyDto> callback);


	void listSurveyGroups(String cursorString, Boolean loadSurveyFlag,
			Boolean loadQuestionGroupFlag, Boolean loadQuestionFlag,
			AsyncCallback<ArrayList<SurveyGroupDto>> callback);
}
