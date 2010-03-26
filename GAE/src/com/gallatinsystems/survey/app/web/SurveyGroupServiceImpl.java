package com.gallatinsystems.survey.app.web;

import java.util.ArrayList;
import java.util.List;

import com.gallatinsystems.survey.app.web.client.dto.SurveyGroup;
import com.gallatinsystems.survey.app.web.service.SurveyGroupService;
import com.gallatinsystems.survey.helper.SurveyGroupHelper;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class SurveyGroupServiceImpl extends RemoteServiceServlet implements
		SurveyGroupService {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2794945936743643615L;

	public SurveyGroup deleteSurveyGroup(Long surveyGroupId) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<SurveyGroup> listSurveyGroup(String groupName) {
		SurveyGroupHelper sgh = new SurveyGroupHelper();
		List<com.gallatinsystems.survey.domain.SurveyGroup> sgList = sgh.listSurveyGroups("desc");
		List<SurveyGroup> surveyGroupDTOList=new ArrayList<SurveyGroup>();
		for(com.gallatinsystems.survey.domain.SurveyGroup surveyGroup:sgList){
			SurveyGroup item = new SurveyGroup();
			item.setCode(surveyGroup.getCode());
			item.setDescription(surveyGroup.getDescription());
			item.setKeyId(surveyGroup.getKey().getId());
			item.setCreatedDateTime(surveyGroup.getCreatedDateTime());
			item.setLastUpdateDateTime(surveyGroup.getLastUpdateDateTime());
		}
		return surveyGroupDTOList;
	}

	public List<SurveyGroup> listSurveyGroups(String orderBy) {
		SurveyGroupHelper sgh = new SurveyGroupHelper();
		List<com.gallatinsystems.survey.domain.SurveyGroup> sgList = sgh.listSurveyGroups("desc");
		List<SurveyGroup> surveyGroupDTOList=new ArrayList<SurveyGroup>();
		for(com.gallatinsystems.survey.domain.SurveyGroup surveyGroup:sgList){
			SurveyGroup item = new SurveyGroup();
			item.setCode(surveyGroup.getCode());
			item.setDescription(surveyGroup.getDescription());
			item.setKeyId(surveyGroup.getKey().getId());
			item.setCreatedDateTime(surveyGroup.getCreatedDateTime());
			item.setLastUpdateDateTime(surveyGroup.getLastUpdateDateTime());
		}
		return surveyGroupDTOList;
	}

	public SurveyGroup saveSurveyGroup(SurveyGroup surveyGroup) {
		SurveyGroupHelper sgh = new SurveyGroupHelper();
		com.gallatinsystems.survey.domain.SurveyGroup sg = new com.gallatinsystems.survey.domain.SurveyGroup();
		sg.setCode(surveyGroup.getCode());
		sg.setDescription(surveyGroup.getDescription());
		sg =sgh.saveSurveyGroup(sg);
		surveyGroup.setKeyId(sg.getKey().getId());
		return surveyGroup;
	}

}
