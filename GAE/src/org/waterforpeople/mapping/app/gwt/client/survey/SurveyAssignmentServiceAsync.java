package org.waterforpeople.mapping.app.gwt.client.survey;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SurveyAssignmentServiceAsync {

	void saveSurveyAssignment(SurveyAssignmentDto dto,
			AsyncCallback<SurveyAssignmentDto> callback);

	void listSurveyAssignments(AsyncCallback<SurveyAssignmentDto[]> callback);

	void deleteSurveyAssignment(SurveyAssignmentDto dto,
			AsyncCallback<Void> callback);

}
