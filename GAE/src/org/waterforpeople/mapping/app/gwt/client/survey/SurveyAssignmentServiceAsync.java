package org.waterforpeople.mapping.app.gwt.client.survey;

import java.util.ArrayList;

import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SurveyAssignmentServiceAsync {

	void saveSurveyAssignment(SurveyAssignmentDto dto,
			AsyncCallback<SurveyAssignmentDto> callback);

	void listSurveyAssignments(AsyncCallback<SurveyAssignmentDto[]> callback);

	void deleteSurveyAssignment(SurveyAssignmentDto dto,
			AsyncCallback<Void> callback);

	void listSurveyAssignments(String cursor,
			AsyncCallback<ResponseDto<ArrayList<SurveyAssignmentDto>>> callback);

}
