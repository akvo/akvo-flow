package org.waterforpeople.mapping.app.gwt.client.survey;

import java.util.ArrayList;
import java.util.TreeMap;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * Service to list/edit/create surveyAttributeMapping objects which are used to
 * map survey questions to attributes of another object
 * 
 * @author Christopher Fagiani
 * 
 */
@RemoteServiceRelativePath("surveyattrmaprpcservice")
public interface SurveyAttributeMappingService extends RemoteService {

	public ArrayList<SurveyAttributeMappingDto> listMappingsBySurvey(
			Long surveyId);

	public TreeMap<String,String> listObjectAttributes(String objectName);
	
	public ArrayList<SurveyAttributeMappingDto> saveMappings(ArrayList<SurveyAttributeMappingDto> mappings);
	public ArrayList<SurveyAttributeMappingDto> saveMappings(Long questionGroupId, ArrayList<SurveyAttributeMappingDto> mappings);

}
