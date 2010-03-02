package com.gallatinsystems.survey.domain.xml;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class SurveyGroupAssoc {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;
	private String surveyGroupFromCode;
	private String surveyGroupToCode;
	private Date createDateTime;
	private Date lastUpdateDateTime;
	private Date effectiveStartDate;
	private Date effectiveEndDate;
	private Long lastUpdateUserId;
	private Long createUserId;
	public Key getKey() {
		return key;
	}
	public void setKey(Key key) {
		this.key = key;
	}
	
	public String getSurveyGroupFromCode() {
		return surveyGroupFromCode;
	}
	public void setSurveyGroupFromCode(String surveyGroupFromCode) {
		this.surveyGroupFromCode = surveyGroupFromCode;
	}
	public String getSurveyGroupToCode() {
		return surveyGroupToCode;
	}
	public void setSurveyGroupToCode(String surveyGroupToCode) {
		this.surveyGroupToCode = surveyGroupToCode;
	}
	public Date getCreateDateTime() {
		return createDateTime;
	}
	public void setCreateDateTime(Date createDateTime) {
		this.createDateTime = createDateTime;
	}
	public Date getLastUpdateDateTime() {
		return lastUpdateDateTime;
	}
	public void setLastUpdateDateTime(Date lastUpdateDateTime) {
		this.lastUpdateDateTime = lastUpdateDateTime;
	}
	public Date getEffectiveStartDate() {
		return effectiveStartDate;
	}
	public void setEffectiveStartDate(Date effectiveStartDate) {
		this.effectiveStartDate = effectiveStartDate;
	}
	public Date getEffectiveEndDate() {
		return effectiveEndDate;
	}
	public void setEffectiveEndDate(Date effectiveEndDate) {
		this.effectiveEndDate = effectiveEndDate;
	}
	public Long getLastUpdateUserId() {
		return lastUpdateUserId;
	}
	public void setLastUpdateUserId(Long lastUpdateUserId) {
		this.lastUpdateUserId = lastUpdateUserId;
	}
	public Long getCreateUserId() {
		return createUserId;
	}
	public void setCreateUserId(Long createUserId) {
		this.createUserId = createUserId;
	}
}
