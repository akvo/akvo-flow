package com.gallatinsystems.survey.app.web.service;

import java.util.List;

import org.waterforpeople.mapping.app.gwt.client.survey.SurveyGroupDto;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("surveygroup")
public interface SurveyGroupService extends RemoteService {
	List<SurveyGroupDto> listSurveyGroups(String orderBy);

	List<SurveyGroupDto> listSurveyGroup(String groupName);

	SurveyGroupDto saveSurveyGroup(SurveyGroupDto surveyGroup);

	SurveyGroupDto deleteSurveyGroup(Long surveyGroupId);

}
