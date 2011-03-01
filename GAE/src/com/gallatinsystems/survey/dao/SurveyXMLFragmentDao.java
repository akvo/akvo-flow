package com.gallatinsystems.survey.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import com.gallatinsystems.survey.domain.SurveyXMLFragment;

public class SurveyXMLFragmentDao extends BaseDAO<SurveyXMLFragment> {

	public SurveyXMLFragmentDao() {
		super(SurveyXMLFragment.class);
	}

	@SuppressWarnings("unchecked")
	public List<SurveyXMLFragment> listSurveyFragments(Long surveyId,
			SurveyXMLFragment.FRAGMENT_TYPE type, Long transactionId) {
		PersistenceManager pm = PersistenceFilter.getManager();
		javax.jdo.Query query = pm.newQuery(SurveyXMLFragment.class);

		Map<String, Object> paramMap = null;

		StringBuilder filterString = new StringBuilder();
		StringBuilder paramString = new StringBuilder();
		paramMap = new HashMap<String, Object>();

		appendNonNullParam("surveyId", filterString, paramString, "String",
				surveyId.toString(), paramMap);
		appendNonNullParam("transactionId", filterString, paramString, "Long",
				transactionId, paramMap);
		query.setFilter(filterString.toString());
		query.declareParameters(paramString.toString());
		query.setOrdering("fragmentOrder");
		List<SurveyXMLFragment> results = (List<SurveyXMLFragment>) query
				.execute(surveyId, transactionId);
		return results;
	}

	/**
	 * deletes all fragments for the surveyId passed in
	 * 
	 * @param surveyId
	 */
	public void deleteFragmentsForSurvey(Long surveyId, Long transactionId) {
		PersistenceManager pm = PersistenceFilter.getManager();
		List<SurveyXMLFragment> surveyFragmentList = listSurveyFragments(
				surveyId, null, transactionId);
		if (surveyFragmentList != null) {
			pm.deletePersistentAll(surveyFragmentList);
		}
	}
}
