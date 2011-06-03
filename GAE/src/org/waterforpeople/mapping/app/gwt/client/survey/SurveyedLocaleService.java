package org.waterforpeople.mapping.app.gwt.client.survey;

import java.util.ArrayList;

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

	
}
