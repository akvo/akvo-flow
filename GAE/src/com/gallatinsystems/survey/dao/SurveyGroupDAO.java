package com.gallatinsystems.survey.dao;

import java.util.List;
import java.util.logging.Logger;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.survey.domain.SurveyGroup;
import com.google.appengine.api.datastore.Key;

/**
 * 
 * Dao for manipulating surveyGroups
 * 
 */
public class SurveyGroupDAO extends BaseDAO<SurveyGroup> {
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(SurveyGroupDAO.class
			.getName());

	private SurveyDAO surveyDao;

	public SurveyGroupDAO() {
		super(SurveyGroup.class);
		surveyDao = new SurveyDAO();
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

	/**
	 * finds a single survey group by code
	 * 
	 * @param name
	 * @return
	 */
	public SurveyGroup findBySurveyGroupName(String name) {
		return super.findByProperty("code", name, "String");
	}

	/**
	 * deletes the survey group and spawns asynchronous delete survey messages
	 * for any surveys contained therein.
	 * 
	 * @param item
	 */
	public void delete(SurveyGroup item) {
		// This probably won't work on the server
		SurveyDAO surveyDao = new SurveyDAO();
		item = super.getByKey(item.getKey().getId());
		for (Survey survey : surveyDao
				.listSurveysByGroup(item.getKey().getId())) {
			SurveyTaskUtil.spawnDeleteTask("deleteSurvey", survey.getKey()
					.getId());
		}
		super.delete(item);
	}

}
