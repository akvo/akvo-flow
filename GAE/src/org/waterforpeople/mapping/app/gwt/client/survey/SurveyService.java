package org.waterforpeople.mapping.app.gwt.client.survey;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("surveyrpcservice")
public interface SurveyService extends RemoteService{

	public SurveyDto[] listSurvey();
	
}
