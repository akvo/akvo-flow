package com.gallatinsystems.survey.dao;

import java.util.List;

import javax.jdo.PersistenceManager;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import com.gallatinsystems.survey.domain.SurveyXMLFragment;

public class SurveyXMLFragmentDao extends BaseDAO<SurveyXMLFragment> {

	public SurveyXMLFragmentDao() {
		super(SurveyXMLFragment.class);
	}

	public List<SurveyXMLFragment> listSurveyFragments(Long surveyId,
			SurveyXMLFragment.FRAGMENT_TYPE type) {
		List<SurveyXMLFragment> surveyFragmentList = listByProperty("surveyId",
				surveyId, "Long", SurveyXMLFragment.class);
		return surveyFragmentList;
	}

	/**
	 * deletes all fragments for the surveyId passed in
	 * 
	 * @param surveyId
	 */
	public void deleteFragmentsForSurvey(Long surveyId) {
		PersistenceManager pm = PersistenceFilter.getManager();
		List<SurveyXMLFragment> surveyFragmentList = listByProperty("surveyId",
				surveyId, "Long", SurveyXMLFragment.class);
		if (surveyFragmentList != null) {
			pm.deletePersistentAll(surveyFragmentList);
		}
	}
}
