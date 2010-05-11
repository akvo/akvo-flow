package com.gallatinsystems.survey.dao;

import java.util.List;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.survey.domain.xml.SurveyGroupAssoc;


public class SurveyGroupAssocDao extends BaseDAO {

	public SurveyGroupAssocDao(Class e) {
		super(e);
		// TODO Auto-generated constructor stub
	}

	
	public List<SurveyGroupAssoc> findSurveyGroupAssocByCode(String surveyGroupCode){
		return (List<SurveyGroupAssoc>)super.listByProperty("SurveyGroupCode", surveyGroupCode, "String");
	}
}
