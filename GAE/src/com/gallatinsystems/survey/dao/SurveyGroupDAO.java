package com.gallatinsystems.survey.dao;

import java.util.List;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.survey.domain.SurveyGroup;
import com.gallatinsystems.survey.domain.SurveySurveyGroupAssoc;
import com.gallatinsystems.survey.domain.xml.SurveyGroupAssoc;

public class SurveyGroupDAO extends BaseDAO<SurveyGroup> {

	public SurveyGroupDAO() {
		super(SurveyGroup.class);		
	}

	public SurveyGroup save(SurveyGroup surveyGroup) {
		return getPersistenceManager().makePersistent(surveyGroup);
	}

	public SurveyGroupAssoc save(SurveyGroupAssoc surveyGroupAssoc) {
		return getPersistenceManager().makePersistent(surveyGroupAssoc);
	}

	public SurveySurveyGroupAssoc save(
			SurveySurveyGroupAssoc surveySurveyGroupAssoc) {
		return getPersistenceManager().makePersistent(surveySurveyGroupAssoc);
	}

	@SuppressWarnings("unchecked")
	public List<SurveyGroup> listSurveyGroup() {

		javax.jdo.Query query = getPersistenceManager().newQuery(
				SurveyGroup.class);
		List<SurveyGroup> results = (List<SurveyGroup>) query.execute();

		return results;
	}
}
