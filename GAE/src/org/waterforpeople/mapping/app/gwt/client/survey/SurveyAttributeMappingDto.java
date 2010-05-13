package org.waterforpeople.mapping.app.gwt.client.survey;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;

/**
 * transfer object to map survey questions to object fields
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyAttributeMappingDto extends BaseDto {

	private static final long serialVersionUID = -1318176575596948441L;

	private Long surveyKeyId;
	private String surveyQuestionId;
	private String objectName;
	private String attributeName;

	public Long getSurveyKeyId() {
		return surveyKeyId;
	}

	public void setSurveyKeyId(Long surveyKeyId) {
		this.surveyKeyId = surveyKeyId;
	}

	public String getSurveyQuestionId() {
		return surveyQuestionId;
	}

	public void setSurveyQuestionId(String surveyQuestionId) {
		this.surveyQuestionId = surveyQuestionId;
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String targetObjectName) {
		this.objectName = targetObjectName;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String targetAttributeName) {
		this.attributeName = targetAttributeName;
	}
}
