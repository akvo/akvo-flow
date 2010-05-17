package org.waterforpeople.mapping.app.gwt.client.survey;

import java.util.ArrayList;
import java.util.List;

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


	void loadFullSurvey(String surveyName, AsyncCallback<SurveyDto> callback);


	void listSurveysForSurveyGroup(String surveyGroupCode,
			AsyncCallback<List<SurveyDto>> callback);


	void listQuestionForQuestionGroup(String questionGroupCode,
			AsyncCallback<List<QuestionDto>> callback);


	void listQuestionGroupsBySurvey(String surveyId,
			AsyncCallback<ArrayList<QuestionGroupDto>> callback);


	void listQuestionsByQuestionGroup(String questionGroupId,
			AsyncCallback<ArrayList<QuestionDto>> callback);

	void deleteQuestion(QuestionDto value, Long questionGroupId, AsyncCallback<Void> callback);


	void saveSurvey(SurveyDto surveyDto, Long surveyGroupId,
			AsyncCallback<SurveyDto> callback);


	void saveQuestion(QuestionDto value, Long questionGroupId,
			AsyncCallback<QuestionDto> callback);


	void saveQuestionGroup(QuestionGroupDto dto, Long surveyId,
			AsyncCallback<QuestionGroupDto> callback);


	void saveSurveyGroup(SurveyGroupDto dto,
			AsyncCallback<SurveyGroupDto> callback);
}
