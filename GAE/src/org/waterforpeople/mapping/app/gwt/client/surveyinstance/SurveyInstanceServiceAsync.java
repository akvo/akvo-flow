package org.waterforpeople.mapping.app.gwt.client.surveyinstance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SurveyInstanceServiceAsync {

	void listSurveyInstance(Date beginDate, Date endDate, boolean unapprovedOnlyFlag,String cursorString,
			AsyncCallback<ResponseDto<ArrayList<SurveyInstanceDto>>> callback);
	
	

	void listQuestionsByInstance(Long instanceId,
			AsyncCallback<List<QuestionAnswerStoreDto>> callback);

	void updateQuestions(List<QuestionAnswerStoreDto> dtoList,
			boolean isApproved,
			AsyncCallback<List<QuestionAnswerStoreDto>> callback);

	void listResponsesByQuestion(
			Long questionId,
			String cursorString,
			AsyncCallback<ResponseDto<ArrayList<QuestionAnswerStoreDto>>> callback);

	void deleteSurveyInstance(Long instanceId, AsyncCallback<Void> callback);

	void submitSurveyInstance(SurveyInstanceDto instance,
			AsyncCallback<SurveyInstanceDto> callback);

	void approveSurveyInstance(Long instanceId,
			List<QuestionAnswerStoreDto> changedQuestions,
			AsyncCallback<Void> callback);



	void listInstancesByLocale(Long localeId, Date dateFrom, Date dateTo,
			String cursor,
			AsyncCallback<ResponseDto<ArrayList<SurveyInstanceDto>>> callback);

}
