package org.waterforpeople.mapping.dao;

import java.util.List;

import org.waterforpeople.mapping.domain.SurveyAttributeMapping;

import com.gallatinsystems.framework.dao.BaseDAO;

/**
 * data access object for persisting/retrieving surveyAttributeMapping objects
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyAttributeMappingDao extends BaseDAO<SurveyAttributeMapping> {

	public SurveyAttributeMappingDao() {
		super(SurveyAttributeMapping.class);
	}

	/**
	 * lists all mappings for a particular survey id
	 * 
	 * @param surveyId
	 * @return
	 */
	public List<SurveyAttributeMapping> listMappingsBySurvey(Long surveyId) {
		return listByProperty("surveyId", surveyId, "Long");
	}

}
