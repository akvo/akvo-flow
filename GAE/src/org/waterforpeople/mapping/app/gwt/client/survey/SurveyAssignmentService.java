package org.waterforpeople.mapping.app.gwt.client.survey;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("surveyassnrpcservice")
public interface SurveyAssignmentService extends RemoteService {

	/**
	 * saves a single surveyAssignmentDTO object to the db
	 * 
	 * @param dto
	 */
	public void saveSurveyAssignment(SurveyAssignmentDto dto);
	
	/**
	 * lists all survey assignments
	 * @return
	 */
	public SurveyAssignmentDto[] listSurveyAssignments();
}
