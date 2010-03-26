package com.gallatinsystems.survey.app.web.service;

import java.util.List;

import com.gallatinsystems.survey.app.web.client.dto.SurveyGroup;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("surveygroup")
public interface SurveyGroupService extends RemoteService {
	List<SurveyGroup> listSurveyGroups(String orderBy);

	List<SurveyGroup> listSurveyGroup(String groupName);

	SurveyGroup saveSurveyGroup(SurveyGroup surveyGroup);

	SurveyGroup deleteSurveyGroup(Long surveyGroupId);

}
