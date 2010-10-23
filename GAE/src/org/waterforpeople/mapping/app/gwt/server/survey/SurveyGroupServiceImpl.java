package org.waterforpeople.mapping.app.gwt.server.survey;

import org.waterforpeople.mapping.app.gwt.client.survey.SurveyGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyGroupService;

import com.gallatinsystems.survey.helper.SurveyGroupHelper;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class SurveyGroupServiceImpl extends RemoteServiceServlet implements
		SurveyGroupService {

	private static final long serialVersionUID = -2794945936743643615L;

	public SurveyGroupDto deleteSurveyGroup(Long surveyGroupId) {
		// TODO implement delete survey group
		return null;
	}

	public SurveyGroupDto saveSurveyGroup(SurveyGroupDto surveyGroup) {
		SurveyGroupHelper sgh = new SurveyGroupHelper();
		com.gallatinsystems.survey.domain.SurveyGroup sg = new com.gallatinsystems.survey.domain.SurveyGroup();
		sg.setCode(surveyGroup.getCode());		
		sg = sgh.saveSurveyGroup(sg);
		surveyGroup.setKeyId(sg.getKey().getId());
		return surveyGroup;
	}

}
