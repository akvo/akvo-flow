package com.gallatinsystems.survey.dao;

import java.util.List;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.survey.domain.xml.SurveyGroupAssoc;

public class SurveyGroupAssocDao extends BaseDAO<SurveyGroupAssoc> {

	public SurveyGroupAssocDao() {
		super(SurveyGroupAssoc.class);
	}

	public List<SurveyGroupAssoc> findSurveyGroupAssocByCode(
			String surveyGroupCode) {
		return listByProperty("SurveyGroupCode", surveyGroupCode, "String");
	}
}
