package com.gallatinsystems.survey.dao;

import java.security.Key;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.xml.bind.JAXBException;

import org.waterforpeople.mapping.db.PMF;

import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.survey.domain.SurveyContainer;
import com.gallatinsystems.survey.xml.SurveyXMLAdapter;

public class SurveyDAO {
	PersistenceManager pm;

	public Long save(String surveyDefinition) {
		Long id = 0L;
		SurveyContainer sc = new SurveyContainer();
		com.google.appengine.api.datastore.Text surveyText = new com.google.appengine.api.datastore.Text(surveyDefinition);
		sc.setSurveyDocument(surveyText);
		pm.makePersistent(sc);
		return sc.getId();
	}

	public Survey get(Long id) {
		SurveyContainer surveyContainer = null;

		javax.jdo.Query query = pm.newQuery(SurveyContainer.class);
		query.setFilter("id == idParam");
		query.declareParameters("Long idParam");
		List<SurveyContainer> results = (List<SurveyContainer>) query.execute(id);
		if (results.size() > 0) {
			surveyContainer = results.get(0);
		}
		
		SurveyXMLAdapter sxa = new SurveyXMLAdapter();
		Survey survey = null;
		try {
			survey = sxa.unmarshall(surveyContainer.getSurveyDocument().toString());
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return survey;
	}
	
	public String getSurveyDocument(Long id) {
		SurveyContainer surveyContainer = null;

		javax.jdo.Query query = pm.newQuery(SurveyContainer.class);
		query.setFilter("id == idParam");
		query.declareParameters("Long idParam");
		List<SurveyContainer> results = (List<SurveyContainer>) query.execute(id);
		if (results.size() > 0) {
			surveyContainer = results.get(0);
		}
		
		return surveyContainer.getSurveyDocument().getValue();
	}

	public List<SurveyContainer> listSurveyContainers(){
			SurveyContainer surveyContainer = null;

			javax.jdo.Query query = pm.newQuery(SurveyContainer.class);

			List<SurveyContainer> results = (List<SurveyContainer>) query.execute();
			return results;
	}
	
	private void init() {
		pm = PMF.get().getPersistenceManager();
	}

	public SurveyDAO() {
		init();
	}
}
