package com.gallatinsystems.survey.dao;

import java.util.List;

import javax.xml.bind.JAXBException;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.survey.domain.SurveyContainer;
import com.gallatinsystems.survey.domain.SurveyGroup;
import com.gallatinsystems.survey.domain.SurveyGroupAssoc;
import com.gallatinsystems.survey.domain.SurveySurveyGroupAssoc;
import com.gallatinsystems.survey.xml.SurveyXMLAdapter;

public class SurveyGroupDAO extends BaseDAO {
	public SurveySurveyGroupAssoc save(
			SurveySurveyGroupAssoc surveySurveyGroupAssoc) {
		return super.getPersistenceManager().makePersistent(
				surveySurveyGroupAssoc);
	}

	public SurveyGroup save(SurveyGroup surveyGroup) {
		return super.getPersistenceManager().makePersistent(surveyGroup);
	}

	public SurveyGroupAssoc save(SurveyGroupAssoc surveyGroupAssoc) {
		return super.getPersistenceManager().makePersistent(surveyGroupAssoc);
	}
	
	public List<SurveyGroup> listSurveyGroup(){
		
		javax.jdo.Query query = super.getPersistenceManager().newQuery(SurveyGroup.class);
		List<SurveyGroup> results = (List<SurveyGroup>) query.execute();
		
		return results;
	}
}
