package org.waterforpeople.mapping.app.gwt.client.survey;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("surveygroup")
public interface SurveyGroupService extends RemoteService {

	SurveyGroupDto saveSurveyGroup(SurveyGroupDto surveyGroup);

	SurveyGroupDto deleteSurveyGroup(Long surveyGroupId);

}
