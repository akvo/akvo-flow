package com.gallatinsystems.survey.helper;

import java.util.List;
import java.util.logging.Logger;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.survey.dao.SurveyGroupDAO;
import com.gallatinsystems.survey.domain.SurveyGroup;
import com.gallatinsystems.survey.xml.SurveyXMLAdapter;

public class SurveyGroupHelper {
	private static final Logger log = Logger.getLogger(SurveyGroupHelper.class
			.getName());
	
	public SurveyGroup deleteSurveyGroup(Long surveyGroupId) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<SurveyGroup> listSurveyGroup(String groupName) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<SurveyGroup> listSurveyGroups(String orderBy) {
		// TODO Auto-generated method stub
		return null;
	}

	public SurveyGroup saveSurveyGroup(SurveyGroup surveyGroup) {
		BaseDAO<SurveyGroup> sgBaseDAO = new BaseDAO<SurveyGroup>(SurveyGroup.class);
		return sgBaseDAO.save(surveyGroup);
	}
}
