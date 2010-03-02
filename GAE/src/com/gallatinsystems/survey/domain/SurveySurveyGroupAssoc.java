package com.gallatinsystems.survey.domain;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class SurveySurveyGroupAssoc {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;
	private Long surveyContainerId;
	private Long surveyGroupId;
	public Key getKey() {
		return key;
	}
	public void setKey(Key key) {
		this.key = key;
	}
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
