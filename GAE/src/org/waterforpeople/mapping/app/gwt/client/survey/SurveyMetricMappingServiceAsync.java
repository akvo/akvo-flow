package org.waterforpeople.mapping.app.gwt.client.survey;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SurveyMetricMappingServiceAsync {

	void listMappingsBySurvey(Long surveyId,
			AsyncCallback<List<SurveyMetricMappingDto>> callback);

	
	void saveMappings(Long questionGroupId,
			List<SurveyMetricMappingDto> mappings,
			AsyncCallback<List<SurveyMetricMappingDto>> callback);


	void listMappingsByQuestion(Long questionId,
			AsyncCallback<List<SurveyMetricMappingDto>> callback);


	void saveMapping(SurveyMetricMappingDto mapping,
			AsyncCallback<SurveyMetricMappingDto> callback);


	void deleteMetricMapping(Long questionId, AsyncCallback<Void> callback);

}
