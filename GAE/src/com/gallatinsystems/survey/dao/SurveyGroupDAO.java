package com.gallatinsystems.survey.dao;

import java.util.List;
import java.util.logging.Logger;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.QuestionGroup;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.survey.domain.SurveyGroup;
import com.google.appengine.api.datastore.Key;

public class SurveyGroupDAO extends BaseDAO<SurveyGroup> {
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(SurveyGroupDAO.class
			.getName());

	private SurveyDAO surveyDao;
	private QuestionGroupDao qgDao;
	private QuestionDao questionDAO;

	public SurveyGroupDAO() {
		super(SurveyGroup.class);
		surveyDao = new SurveyDAO();
		qgDao = new QuestionGroupDao();
		questionDAO = new QuestionDao();

	}

	/**
	 * saves the survey group and any surveys contained therein
	 * 
	 * @param group
	 * @return
	 */
	public SurveyGroup save(SurveyGroup group) {
		group = super.save(group);
		if (group.getSurveyList() != null) {
			for (Survey s : group.getSurveyList()) {
				s.setSurveyGroupId(group.getKey().getId());
				surveyDao.save(s);
			}
		}
		return group;
	}

	public SurveyGroup getByKey(Long id, boolean includeQuestions) {
		SurveyGroup sg = getByKey(id);

		return sg;
	}

	public SurveyGroup getByKey(Key key) {
		return super.getByKey(key);
	}

	public List<SurveyGroup> list(String cursorString, Boolean loadSurveyFlag,
			Boolean loadQuestionGroupFlag, Boolean loadQuestionFlag) {
		List<SurveyGroup> sgList = null;
		sgList = super.list(cursorString);

		return sgList;
	}

	public SurveyGroup findBySurveyGroupName(String name) {
		return super.findByProperty("code", name, "String");
	}

}
