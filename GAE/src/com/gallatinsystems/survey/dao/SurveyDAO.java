package com.gallatinsystems.survey.dao;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.xml.bind.JAXBException;

import org.waterforpeople.mapping.domain.SurveyQuestion;

import com.gallatinsystems.device.app.web.DeviceManagerServlet;
import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.survey.domain.SurveyContainer;
import com.gallatinsystems.survey.domain.SurveyGroup;
import com.gallatinsystems.survey.xml.SurveyXMLAdapter;
import com.google.appengine.api.datastore.Key;

public class SurveyDAO extends BaseDAO<Survey> {
	private static final Logger log = Logger
			.getLogger(DeviceManagerServlet.class.getName());

	public SurveyDAO() {
		super(Survey.class);
	}

	public SurveyGroup save(SurveyGroup surveyGroup) {
		return super.save(surveyGroup);
	}

	public Survey save(Survey survey, Key surveyGroupKey) {
		survey = super.save(survey);
		
		return survey;
	}

	public Survey getById(Long key) {
		return super.getByKey(key);
	}

	public Survey getByKey(Key key) {
		return super.getByKey(key);
	}

	/**
	 * loads a full survey object (whole object graph, including questions)
	 * 
	 * @param id
	 * @return
	 */
	public Survey loadFullSurvey(Key key) {
		Survey survey = getByKey(key);
		return survey;
	}

	public Long save(Long surveyId, String surveyDocument) {
		SurveyContainer sc = new SurveyContainer();
		sc.setSurveyId(surveyId);
		com.google.appengine.api.datastore.Text surveyText = new com.google.appengine.api.datastore.Text(
				surveyDocument);
		sc.setSurveyDocument(surveyText);
		sc = super.save(sc);
		return sc.getKey().getId();
	}

	public String getForTest() {
		// Question quest = super.getByKey(300L);
		StringBuilder sb = new StringBuilder();

		return sb.toString();
	}


	public com.gallatinsystems.survey.domain.xml.Survey get(Long id) {
		SurveyContainer surveyContainer = getByKey(id, SurveyContainer.class);

		SurveyXMLAdapter sxa = new SurveyXMLAdapter();
		com.gallatinsystems.survey.domain.xml.Survey survey = null;
		try {
			survey = sxa.unmarshall(surveyContainer.getSurveyDocument()
					.toString());
		} catch (JAXBException e) {
			log.log(Level.SEVERE, "Could not unmarshal xml", e);
		}
		return survey;
	}

	public String getSurveyDocument(Long id) {
		SurveyContainer surveyContainer = getByKey(id, SurveyContainer.class);
		return surveyContainer.getSurveyDocument().getValue();
	}

	public List<SurveyContainer> listSurveyContainers() {
		return list(SurveyContainer.class, "all");
	}

	/**
	 * lists all questions of a given type (across all surveys)
	 */
	public List<SurveyQuestion> listQuestionByType(String questionType) {
		return listByProperty("type", questionType, "String",
				SurveyQuestion.class);
	}

	public List<SurveyGroup> listSurveyGroup(String cursorString) {
		return list(SurveyGroup.class, cursorString);
	}

	public List<Survey> listSurveysByGroup(Long surveyGroupId){
		return listByProperty("surveyGroupId", surveyGroupId, "Long");
	}
	

	@SuppressWarnings("unchecked")
	public Survey getByPath(String code, String path) {
		PersistenceManager pm = PersistenceFilter.getManager();
		javax.jdo.Query query = pm.newQuery(Survey.class);
		query.setFilter(" path == pathParam && name == codeParam");
		query.declareParameters("String pathParam, String codeParam");
		List<Survey> results = (List<Survey>) query.execute(path, code);
		if (results != null && results.size() > 0) {
			return results.get(0);
		} else {
			return null;
		}
	}
}
