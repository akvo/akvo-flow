package org.waterforpeople.mapping.app.gwt.client.survey;

import java.util.ArrayList;
import java.util.TreeMap;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SurveyAttributeMappingServiceAsync {

	void listMappingsBySurvey(Long surveyId,
			AsyncCallback<ArrayList<SurveyAttributeMappingDto>> callback);

	void listObjectAttributes(String objectName,
			AsyncCallback<TreeMap<String,String>> callback);

	void saveMappings(ArrayList<SurveyAttributeMappingDto> mappings,
			AsyncCallback<ArrayList<SurveyAttributeMappingDto>> callback);
}
