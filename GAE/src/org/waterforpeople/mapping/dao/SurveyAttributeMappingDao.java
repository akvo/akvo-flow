package org.waterforpeople.mapping.dao;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.waterforpeople.mapping.domain.SurveyAttributeMapping;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;

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

	/**
	 * lists all mappings for a particular question id
	 * 
	 * @param questionGroupId
	 * @return
	 */
	public List<SurveyAttributeMapping> listMappingsByQuestionGroup(
			Long questionGroupId) {
		return listByProperty("questionGroupId", questionGroupId, "Long");
	}

	/**
	 * deletes all mappings for a given surveyId
	 * 
	 * @param surveyId
	 */
	public void deleteMappingsForSurvey(Long surveyId) {
		List<SurveyAttributeMapping> mappings = listMappingsBySurvey(surveyId);
		if (mappings != null && mappings.size() > 0) {
			PersistenceManager pm = PersistenceFilter.getManager();
			pm.deletePersistentAll(mappings);
		}
	}

	/**
	 * deletes all mappings for a given questionGroupId
	 * 
	 * @param surveyId
	 */
	public void deleteMappingsForQuestionGroup(Long questionGroupId) {
		List<SurveyAttributeMapping> mappings = listMappingsByQuestionGroup(questionGroupId);
		if (mappings != null && mappings.size() > 0) {
			PersistenceManager pm = PersistenceFilter.getManager();
			pm.deletePersistentAll(mappings);
		}
	}

	/**
	 * returns a single mapping object that corresponds to the attribute passed
	 * in for a given survey.
	 * 
	 * @param surveyId
	 * @param attributeName
	 * @return - mapping or null if no match
	 */
	@SuppressWarnings("unchecked")
	public SurveyAttributeMapping findMappingForAttribute(Long surveyId,
			String attributeName) {
		PersistenceManager pm = PersistenceFilter.getManager();
		Query q = pm.newQuery(SurveyAttributeMapping.class);
		q
				.setFilter("surveyId == surveyIdParam && attributeName == attrNameParam");
		q.declareParameters("Long surveyIdParam, String attrNameParam");
		List<SurveyAttributeMapping> mappingList = (List<SurveyAttributeMapping>) q
				.execute(surveyId, attributeName);
		if (mappingList != null && mappingList.size() > 0) {
			return mappingList.get(0);
		} else {
			return null;
		}
	}

	/**
	 * finds the mapping for the question id passed in
	 * 
	 * @param questionId
	 * @return
	 */
	public SurveyAttributeMapping findMappingForQuestion(String questionId) {
		return findByProperty("surveyQuestionId", questionId, "String");
	}

}
