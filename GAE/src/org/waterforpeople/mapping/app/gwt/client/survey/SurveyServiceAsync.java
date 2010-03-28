package org.waterforpeople.mapping.app.gwt.client.survey;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SurveyServiceAsync {


	public void listSurvey(AsyncCallback<SurveyDto[]> callback);

}
