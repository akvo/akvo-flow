package org.waterforpeople.mapping.app.gwt.client.survey;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;

/**
 * transfer object for use with SurveyMetricMapping domain
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyMetricMappingDto extends BaseDto {
	private static final long serialVersionUID = -5717392381938379059L;
	private Long surveyId;
	private Long surveyQuestionId;
	private Long questionGroupId;
	private Long metricId;

	public Long getQuestionGroupId() {
		return questionGroupId;
	}

	public void setQuestionGroupId(Long questionGroupId) {
		this.questionGroupId = questionGroupId;
	}

	public Long getSurveyId() {
		return surveyId;
	}

	public void setSurveyId(Long surveyId) {
		this.surveyId = surveyId;
	}

	public Long getSurveyQuestionId() {
		return surveyQuestionId;
	}

	public void setSurveyQuestionId(Long surveyQuestionId) {
		this.surveyQuestionId = surveyQuestionId;
	}

	public Long getMetricId() {
		return metricId;
	}

	public void setMetricId(Long metricId) {
		this.metricId = metricId;
	}

}
