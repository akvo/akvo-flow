package com.gallatinsystems.survey.app.web.service;

import java.util.List;

import com.gallatinsystems.survey.app.web.client.dto.SurveyGroupDto;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SurveyGroupServiceAsync {

	
	void listSurveyGroup(String groupName,
			AsyncCallback<List<SurveyGroupDto>> callback);

	
	void saveSurveyGroup(SurveyGroupDto surveyGroup,
			AsyncCallback<SurveyGroupDto> callback);

	void listSurveyGroups(String orderBy,
			AsyncCallback<List<SurveyGroupDto>> callback);


	void deleteSurveyGroup(Long surveyGroupId,
			AsyncCallback<SurveyGroupDto> callback);


}
