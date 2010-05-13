package org.waterforpeople.mapping.domain;

import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;

/**
 * domain object to store a mapping from a survey question to an object
 * attribute
 * 
 * @author Christopher Fagiani
 * 
 */
@PersistenceCapable
public class SurveyAttributeMapping extends BaseDomain {

	private static final long serialVersionUID = 2502190477085947868L;
	private String objectName;
	private String attributeName;
	private Long surveyId;
	private String surveyQuestionId;

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public Long getSurveyId() {
		return surveyId;
	}

	public void setSurveyId(Long surveyId) {
		this.surveyId = surveyId;
	}

	public String getSurveyQuestionId() {
		return surveyQuestionId;
	}

	public void setSurveyQuestionId(String surveyQuestionId) {
		this.surveyQuestionId = surveyQuestionId;
	}
}
