package org.waterforpeople.mapping.app.gwt.client.survey;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SurveySummaryServiceAsync {

	void listResponses(String questionId,
			AsyncCallback<SurveySummaryDto[]> callback);

	void listInstanceSummaryByLocation(String countryCode,
			String communityCode, AsyncCallback<SurveySummaryDto[]> callback);

}
