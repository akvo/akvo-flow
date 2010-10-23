package org.waterforpeople.mapping.app.gwt.client.survey;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SurveyGroupServiceAsync {

	
	void saveSurveyGroup(SurveyGroupDto surveyGroup,
			AsyncCallback<SurveyGroupDto> callback);


	void deleteSurveyGroup(Long surveyGroupId,
			AsyncCallback<SurveyGroupDto> callback);


}
