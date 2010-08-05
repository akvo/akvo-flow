package org.waterforpeople.mapping.app.gwt.server.survey;

import java.util.ArrayList;
import java.util.List;

import org.waterforpeople.mapping.app.gwt.client.survey.SurveyGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyGroupService;

import com.gallatinsystems.survey.helper.SurveyGroupHelper;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class SurveyGroupServiceImpl extends RemoteServiceServlet implements
		SurveyGroupService {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2794945936743643615L;

	public SurveyGroupDto deleteSurveyGroup(Long surveyGroupId) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<SurveyGroupDto> listSurveyGroup(String groupName) {
		SurveyGroupHelper sgh = new SurveyGroupHelper();
		List<com.gallatinsystems.survey.domain.SurveyGroup> sgList = sgh
				.listSurveyGroups("desc");
		List<SurveyGroupDto> surveyGroupDTOList = new ArrayList<SurveyGroupDto>();
		if (sgList != null) {
			for (com.gallatinsystems.survey.domain.SurveyGroup surveyGroup : sgList) {
				SurveyGroupDto item = new SurveyGroupDto();
				item.setCode(surveyGroup.getCode());				
				item.setKeyId(surveyGroup.getKey().getId());
				item.setCreatedDateTime(surveyGroup.getCreatedDateTime());
				item.setLastUpdateDateTime(surveyGroup.getLastUpdateDateTime());
			}
		}
		return surveyGroupDTOList;
	}

	public List<SurveyGroupDto> listSurveyGroups(String orderBy) {
		SurveyGroupHelper sgh = new SurveyGroupHelper();
		List<com.gallatinsystems.survey.domain.SurveyGroup> sgList = sgh
				.listSurveyGroups("desc");
		List<SurveyGroupDto> surveyGroupDTOList = new ArrayList<SurveyGroupDto>();
		if (sgList != null) {
			for (com.gallatinsystems.survey.domain.SurveyGroup surveyGroup : sgList) {
				SurveyGroupDto item = new SurveyGroupDto();
				item.setCode(surveyGroup.getCode());				
				item.setKeyId(surveyGroup.getKey().getId());
				item.setCreatedDateTime(surveyGroup.getCreatedDateTime());
				item.setLastUpdateDateTime(surveyGroup.getLastUpdateDateTime());
			}
		}
		return surveyGroupDTOList;
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
