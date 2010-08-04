package com.gallatinsystems.survey.dao;

import java.util.HashMap;
import java.util.List;

import javax.jdo.PersistenceManager;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import com.gallatinsystems.survey.domain.QuestionGroup;
import com.gallatinsystems.survey.domain.Survey;

public class QuestionGroupDao extends BaseDAO<QuestionGroup> {

	public QuestionGroupDao() {
		super(QuestionGroup.class);
	}

	public QuestionGroup save(QuestionGroup item, Long surveyId, Integer order) {
		item = save(item);
		return item;
	}

	public void delete(QuestionGroup item, Long surveyId) {
		
		delete(item);
	}

	public QuestionGroup getId(String questionGroupCode) {
		return findByProperty("code", questionGroupCode, "String");
	}

	public HashMap<Integer,QuestionGroup> listQuestionGroupsBySurvey(Long surveyId) {
		SurveyDAO surveyDao = new SurveyDAO();
		Survey survey = surveyDao.getByKey(surveyId);
		
		return survey.getQuestionGroupMap();
	}

	@SuppressWarnings("unchecked")
	public QuestionGroup getByPath(String code, String path) {
		PersistenceManager pm = PersistenceFilter.getManager();
		javax.jdo.Query query = pm.newQuery(QuestionGroup.class);
		query.setFilter(" path == pathParam && code == codeParam");
		query.declareParameters("String pathParam, String codeParam");
		List<QuestionGroup> results = (List<QuestionGroup>) query.execute(path, code);
		if (results != null && results.size() > 0) {
			return results.get(0);
		} else {
			return null;
		}
	}
}
