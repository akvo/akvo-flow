package com.gallatinsystems.metric.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import com.gallatinsystems.metric.domain.SurveyMetricMapping;

/**
 * persists and retrieves SurveyMetricMapping objects from the datastore
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyMetricMappingDao extends BaseDAO<SurveyMetricMapping> {

	public SurveyMetricMappingDao() {
		super(SurveyMetricMapping.class);
	}

	/**
	 * finds all metric mappings for a given survey and organization
	 * 
	 * @param surveyId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<SurveyMetricMapping> listMappingsBySurvey(Long surveyId) {
		PersistenceManager pm = PersistenceFilter.getManager();
		javax.jdo.Query query = pm.newQuery(SurveyMetricMapping.class);
		Map<String, Object> paramMap = null;

		StringBuilder filterString = new StringBuilder();
		StringBuilder paramString = new StringBuilder();
		paramMap = new HashMap<String, Object>();

		appendNonNullParam("surveyId", filterString, paramString, "Long",
				surveyId, paramMap);
		if (surveyId != null) {
			query.setFilter(filterString.toString());
			query.declareParameters(paramString.toString());
			return (List<SurveyMetricMapping>) query.executeWithMap(paramMap);
		} else {
			return list(CURSOR_TYPE.all.toString());
		}
	}

	/**
	 * returns all SurveyMetricMappings for the given questionGroupId
	 * 
	 * @param questionGroupId
	 * @return
	 */
	public List<SurveyMetricMapping> listMappingsByQuestionGroup(
			Long questionGroupId) {
		return listByProperty("questionGroupId", questionGroupId, "Long");
	}

	/**
	 * deletes all mappings for a given questionGroupId
	 * 
	 * @param surveyId
	 */
	public void deleteMappingsForQuestionGroup(Long questionGroupId) {
		List<SurveyMetricMapping> mappings = listMappingsByQuestionGroup(questionGroupId);
		if (mappings != null && mappings.size() > 0) {
			delete(mappings);
		}
	}
}
