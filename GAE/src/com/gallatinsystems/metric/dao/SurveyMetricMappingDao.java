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
	public List<SurveyMetricMapping> listMappingsBySurvey(Long surveyId,
			String organization) {
		PersistenceManager pm = PersistenceFilter.getManager();
		javax.jdo.Query query = pm.newQuery(SurveyMetricMapping.class);
		Map<String, Object> paramMap = null;

		StringBuilder filterString = new StringBuilder();
		StringBuilder paramString = new StringBuilder();
		paramMap = new HashMap<String, Object>();

		appendNonNullParam("organization", filterString, paramString, "String",
				organization, paramMap);
		appendNonNullParam("surveyId", filterString, paramString, "Long",
				surveyId, paramMap);
		query.setFilter(filterString.toString());
		query.declareParameters(paramString.toString());

		return (List<SurveyMetricMapping>) query.executeWithMap(paramMap);

	}

}
