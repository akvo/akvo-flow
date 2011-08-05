package org.waterforpeople.mapping.app.gwt.client.survey;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto.QuestionType;

import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SurveyServiceAsync {

	public void listSurvey(AsyncCallback<SurveyDto[]> callback);

	public void listSurveyQuestionByType(Long surveyId, QuestionType type,
			boolean loadTranslations, AsyncCallback<QuestionDto[]> callback);

	void listSurveysByGroup(String selectedGroupId,
			AsyncCallback<ArrayList<SurveyDto>> callback);

	void save(SurveyGroupDto value, AsyncCallback<SurveyGroupDto> callback);

	void loadFullSurvey(Long surveyId, AsyncCallback<SurveyDto> callback);

	void listSurveyGroups(String cursorString, Boolean loadSurveyFlag,
			Boolean loadQuestionGroupFlag, Boolean loadQuestionFlag,
			AsyncCallback<ResponseDto<ArrayList<SurveyGroupDto>>> callback);

	void listSurveysForSurveyGroup(String surveyGroupCode,
			AsyncCallback<List<SurveyDto>> callback);

	void listQuestionGroupsBySurvey(String surveyId,
			AsyncCallback<ArrayList<QuestionGroupDto>> callback);

	void listQuestionsByQuestionGroup(String questionGroupId,
			boolean needDetails, AsyncCallback<ArrayList<QuestionDto>> callback);

	void deleteQuestion(QuestionDto value, Long questionGroupId,
			AsyncCallback<String> callback);

	void saveSurvey(SurveyDto surveyDto, Long surveyGroupId,
			AsyncCallback<SurveyDto> callback);

	void saveQuestion(QuestionDto value, Long questionGroupId,
			boolean forceReorder, AsyncCallback<QuestionDto> callback);

	void saveQuestionGroup(QuestionGroupDto dto, Long surveyId,
			AsyncCallback<QuestionGroupDto> callback);

	void saveSurveyGroup(SurveyGroupDto dto,
			AsyncCallback<SurveyGroupDto> callback);

	void publishSurvey(Long surveyId, AsyncCallback<String> callback);

	void loadQuestionDetails(Long questionId,
			AsyncCallback<QuestionDto> callback);

	void publishSurveyAsync(Long surveyId, AsyncCallback<Void> callback);

	void deleteSurveyGroup(SurveyGroupDto value, AsyncCallback<String> callback);

	void deleteQuestionGroup(QuestionGroupDto value, Long surveyId,
			AsyncCallback<String> callback);

	void deleteSurvey(SurveyDto value, Long surveyGroupId,
			AsyncCallback<String> callback);

	void saveTranslations(List<TranslationDto> translations,
			AsyncCallback<List<TranslationDto>> callback);

	void rerunAPMappings(Long surveyId, AsyncCallback<Void> callback);

	void listHelpByQuestion(Long questionId,
			AsyncCallback<List<QuestionHelpDto>> callback);

	void saveHelp(List<QuestionHelpDto> helpList,
			AsyncCallback<List<QuestionHelpDto>> callback);

	void listTranslations(Long parentId, String parentType,
			AsyncCallback<Map<String, TranslationDto>> callback);

	void saveQuestionGroups(List<QuestionGroupDto> dtoList,
			AsyncCallback<List<QuestionGroupDto>> callback);

	void copyQuestion(QuestionDto existingQuestion,
			QuestionGroupDto newParentGroup, AsyncCallback<QuestionDto> callback);

	void updateQuestionOrder(List<QuestionDto> questions,
			AsyncCallback<Void> callback);

	void updateQuestionGroupOrder(List<QuestionGroupDto> groups,
			AsyncCallback<Void> callback);

	void updateQuestionDependency(Long questionId, QuestionDependencyDto dep,
			AsyncCallback<Void> callback);

	void getPublishedSurvey(String surveyId, AsyncCallback<SurveyDto> callback);

	void generateBootstrapFile(List<Long> surveyIdList, String dbInstructions,
			String notificationEmail, AsyncCallback<Void> callback);

	void findSurvey(Long id, AsyncCallback<SurveyDto> callback);

	void markSurveyChangesComplete(Long id, AsyncCallback<Void> callback);

}
