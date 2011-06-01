package org.waterforpeople.mapping.app.gwt.client.survey;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SurveyMetricMappingServiceAsync {

	void listMappingsBySurvey(Long surveyId,
			AsyncCallback<List<SurveyMetricMappingDto>> callback);

	void listMetrics(String organizationName,
			AsyncCallback<List<MetricDto>> callback);

	void saveMappings(Long questionGroupId,
			List<SurveyMetricMappingDto> mappings,
			AsyncCallback<List<SurveyMetricMappingDto>> callback);

}
