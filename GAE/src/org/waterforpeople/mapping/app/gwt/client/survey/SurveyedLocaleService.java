package org.waterforpeople.mapping.app.gwt.client.survey;

import java.util.ArrayList;
import java.util.List;

import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointSearchCriteriaDto;

import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * GWT-RPC service for saving/finding/deleting SurveyedLocales
 * 
 * @author Christopher Fagiani
 * 
 */
@RemoteServiceRelativePath("surveyedlocalerpcservice")
public interface SurveyedLocaleService extends RemoteService {

	public void deleteLocale(Long localeId);

	public ResponseDto<ArrayList<SurveyedLocaleDto>> listLocales(
			AccessPointSearchCriteriaDto searchCriteria, String cursorString);

	/**
	 * lists all the surveyalValue objects for a single surveyinstance
	 * 
	 * @param surveyInstanceId
	 * @return
	 */
	public List<SurveyalValueDto> listSurveyalValuesByInstance(
			Long surveyInstanceId);

	/**
	 * saves a surveyedLocale to the database, including any nested
	 * surveyalValues
	 * 
	 * @param locale
	 * @return
	 */
	public SurveyedLocaleDto saveSurveyedLocale(SurveyedLocaleDto locale);
}
