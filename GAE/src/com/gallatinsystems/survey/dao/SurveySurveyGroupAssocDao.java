package com.gallatinsystems.survey.dao;

import java.util.List;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.survey.domain.SurveySurveyGroupAssoc;

public class SurveySurveyGroupAssocDao extends BaseDAO<SurveySurveyGroupAssoc> {

	public SurveySurveyGroupAssocDao(Class e) {
		super(e);
		// TODO Auto-generated constructor stub
	}
	public SurveySurveyGroupAssocDao(){
		super(SurveySurveyGroupAssoc.class);
	}
	
	@SuppressWarnings("unchecked")
	public SurveySurveyGroupAssoc save(SurveySurveyGroupAssoc ssga){
		return (SurveySurveyGroupAssoc) super.save(ssga);
	}
	@SuppressWarnings("unchecked")
	public List<SurveySurveyGroupAssoc> findBySurveyGroupId(Long id){
		return (List<SurveySurveyGroupAssoc>) super.findByProperty("surveyGroupId", id, "Long");
	}

}
