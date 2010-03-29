package org.waterforpeople.mapping.app.gwt.client.survey;

import java.util.Date;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SurveyServiceAsync {

	public void listSurvey(AsyncCallback<SurveyDto[]> callback);

	void listSurveyActivityByDate(Date startDate, Date endDate,
			String rollUpType, AsyncCallback<SurveyActivityDto[]> callback);

}
