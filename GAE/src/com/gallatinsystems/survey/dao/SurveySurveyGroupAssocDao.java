package com.gallatinsystems.survey.dao;

import java.util.List;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.survey.domain.SurveySurveyGroupAssoc;

public class SurveySurveyGroupAssocDao extends BaseDAO<SurveySurveyGroupAssoc> {

	public SurveySurveyGroupAssocDao() {
		super(SurveySurveyGroupAssoc.class);
	}

	public List<SurveySurveyGroupAssoc> listBySurveyGroupId(Long id) {
		return listByProperty("surveyGroupId", id, "Long");
	}

}
