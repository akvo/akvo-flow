package com.gallatinsystems.survey.dao;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.xml.bind.JAXBException;

import org.waterforpeople.mapping.db.PMF;

import com.gallatinsystems.device.app.web.DeviceManagerServlet;
import com.gallatinsystems.survey.domain.SurveyContainer;
import com.gallatinsystems.survey.domain.xml.Survey;
import com.gallatinsystems.survey.xml.SurveyXMLAdapter;


public class SurveyDAO {
	PersistenceManager pm;
	private static final Logger log = Logger
	.getLogger(DeviceManagerServlet.class.getName());

	public Long save(String surveyDefinition) {
		Long id = 0L;
		SurveyContainer sc = new SurveyContainer();
		com.google.appengine.api.datastore.Text surveyText = new com.google.appengine.api.datastore.Text(surveyDefinition);
		sc.setSurveyDocument(surveyText);
		pm.makePersistent(sc);
		return sc.getId();
	}
	
	public void test(){
		com.gallatinsystems.survey.domain.Survey survey = new com.gallatinsystems.survey.domain.Survey();
		survey.setCreatedDateTime(new Date());
		survey.setCreateUserId(1L);
		survey.setName("test");
		survey.setVersion(1);
		pm.makePersistent(survey);
		log.info("BaseDAO test survey key: " + survey.getKey().getId());
		
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
