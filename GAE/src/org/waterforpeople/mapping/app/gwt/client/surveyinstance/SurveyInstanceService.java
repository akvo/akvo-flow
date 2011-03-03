package org.waterforpeople.mapping.app.gwt.client.surveyinstance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("surveyinstance")
public interface SurveyInstanceService extends RemoteService {
	public ResponseDto<ArrayList<SurveyInstanceDto>> listSurveyInstance(
			Date beginDate, boolean unapprovedOnlyFlag, String cursorString);

	public List<QuestionAnswerStoreDto> listQuestionsByInstance(Long instanceId);

	public List<QuestionAnswerStoreDto> updateQuestions(List<QuestionAnswerStoreDto> dtoList, boolean isApproved);

	public ResponseDto<ArrayList<QuestionAnswerStoreDto>> listResponsesByQuestion(Long questionId, String cursorString);

	public void deleteSurveyInstance(Long instanceId);
	/**
	 * saves a new survey instance and triggers processing
	 *
	 * @param instance
	 * @return
	 */
	public SurveyInstanceDto submitSurveyInstance(SurveyInstanceDto instance);

	/**
	 * handles marking surveys as approved. Will also save updates to any questions passed in 
	 * @param instance
	 */
	public void approveSurveyInstance(Long instanceId, List<QuestionAnswerStoreDto> changedQuestions);
	
}
