package com.gallatinsystems.survey.domain;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.gallatinsystems.framework.domain.BaseDomain;
import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class SurveySurveyGroupAssoc extends BaseDomain {

	private Long surveyContainerId;
	private Long surveyGroupId;

	public Long getSurveyContainerId() {
		return surveyContainerId;
	}

	public void setSurveyContainerId(Long surveyContainerId) {
		this.surveyContainerId = surveyContainerId;
	}

	public Long getSurveyGroupId() {
		return surveyGroupId;
	}

	public void setSurveyGroupId(Long surveyGroupId) {
		this.surveyGroupId = surveyGroupId;
	}
}
