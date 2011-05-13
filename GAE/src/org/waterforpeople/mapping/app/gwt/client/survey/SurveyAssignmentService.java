package org.waterforpeople.mapping.app.gwt.client.survey;

import java.util.ArrayList;

import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("surveyassnrpcservice")
public interface SurveyAssignmentService extends RemoteService {

	/**
	 * saves a single surveyAssignmentDTO object to the db
	 * 
	 * @param dto
	 */
	public SurveyAssignmentDto saveSurveyAssignment(SurveyAssignmentDto dto);

	/**
	 * lists all survey assignments
	 * 
	 * @return
	 */
	public SurveyAssignmentDto[] listSurveyAssignments();

	/**
	 * lists survey assignments with pagination. 
	 * 
	 * @param cursor
	 * @return
	 */
	public ResponseDto<ArrayList<SurveyAssignmentDto>> listSurveyAssignments(
			String cursor);

	public void deleteSurveyAssignment(SurveyAssignmentDto dto);
}
