package com.gallatinsystems.metric.domain;

import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;

/**
 * maps survey questions to metrics
 * 
 * @author Christopher Fagiani
 *
 */
@PersistenceCapable
public class SurveyMetricMapping extends BaseDomain{
	private static final long serialVersionUID = -8714047945077756907L;
	private Long surveyId;
	private Long surveyQuestionId;
	private Long metricId;

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
