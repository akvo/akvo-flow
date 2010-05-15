package com.gallatinsystems.survey.dao;

import java.util.List;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.survey.domain.SurveyQuestionGroupAssoc;

public class SurveyQuestionGroupAssocDao extends BaseDAO<SurveyQuestionGroupAssoc> {

	public SurveyQuestionGroupAssocDao(Class e) {
		super(e);
		// TODO Auto-generated constructor stub
	}
	
	public SurveyQuestionGroupAssocDao(){
		super(SurveyQuestionGroupAssoc.class);
	}
	
	public SurveyQuestionGroupAssoc save(SurveyQuestionGroupAssoc obj){
		return super.save(obj);
	}
	
	public List<SurveyQuestionGroupAssoc> listBySurveyId(Long surveyId){
		return super.listByProperty("surveyId",surveyId, "Long");
	}
	
	public List<SurveyQuestionGroupAssoc> listByQuestionGroupId(Long questionGroupId){
		return super.listByProperty("questionGroupId", questionGroupId, "Long");
	}

}
