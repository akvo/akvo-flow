package com.gallatinsystems.survey.app.web.service;

import java.util.List;

import com.gallatinsystems.survey.app.web.client.dto.SurveyGroup;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SurveyGroupServiceAsync {

	
	void listSurveyGroup(String groupName,
			AsyncCallback<List<SurveyGroup>> callback);

	
	void saveSurveyGroup(SurveyGroup surveyGroup,
			AsyncCallback<SurveyGroup> callback);

	void listSurveyGroups(String orderBy,
			AsyncCallback<List<SurveyGroup>> callback);


	void deleteSurveyGroup(Long surveyGroupId,
			AsyncCallback<SurveyGroup> callback);


}
