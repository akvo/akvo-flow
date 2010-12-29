package org.waterforpeople.mapping.domain;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class AccessPointMappingHistory extends BaseDomain {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1498979194545355847L;

	private Long surveyId = null;
	private Long surveyInstanceId = null;
	private Long questionId = null;
	private String questionText = null;
	private String surveyResponse = null;
	private String accessPointTypes = null;
	private String questionAnswerType = null;
	private String responseAnswerType = null;
	private String mappingMessage = null;
	private String accessPointValue = null;
	private String source = null;

	public Long getSurveyId() {
		return surveyId;
	}

	public void setSurveyId(Long surveyId) {
		this.surveyId = surveyId;
	}

	public Long getSurveyInstanceId() {
		return surveyInstanceId;
	}

	public void setSurveyInstanceId(Long surveyInstanceId) {
		this.surveyInstanceId = surveyInstanceId;
	}

	public Long getQuestionId() {
		return questionId;
	}

	public void setQuestionId(Long questionId) {
		this.questionId = questionId;
	}

	public String getQuestionText() {
		return questionText;
	}

	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}

	public String getSurveyResponse() {
		return surveyResponse;
	}

	public void setSurveyResponse(String surveyResponse) {
		this.surveyResponse = surveyResponse;
	}

	public void setAccessPointTypes(String accessPointTypes) {
		this.accessPointTypes = accessPointTypes;
	}

	public String getAccessPointTypes() {
		return accessPointTypes;
	}

	public void addAccessPointType(String type) {
		if (accessPointTypes != null)
			accessPointTypes = accessPointTypes + "|" + type;
		else
			setAccessPointTypes(type);
	}

	public void setQuestionAnswerType(String questionAnswerType) {
		this.questionAnswerType = questionAnswerType;
	}

	public String getQuestionAnswerType() {
		return questionAnswerType;
	}

	public void setResponseAnswerType(String responseAnswerType) {
		this.responseAnswerType = responseAnswerType;
	}

	public String getResponseAnswerType() {
		return responseAnswerType;
	}

	public void setMappingMessage(String mappingMessage) {
		this.mappingMessage = mappingMessage;
	}

	public String getMappingMessage() {
		return mappingMessage;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getSource() {
		return source;
	}

	public void setAccessPointValue(String accessPointValue) {
		this.accessPointValue = accessPointValue;
	}

	public String getAccessPointValue() {
		return accessPointValue;
	}
}
