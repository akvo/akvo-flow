package org.waterforpeople.mapping.app.gwt.client.survey;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("surveysummaryrpcservice")
public interface SurveySummaryService extends RemoteService {
	public SurveySummaryDto[] listResponses(String questionId);
}