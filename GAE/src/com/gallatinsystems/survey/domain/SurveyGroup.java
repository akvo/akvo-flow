package com.gallatinsystems.survey.domain;

import java.util.ArrayList;
import java.util.Date;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Unique;

import com.gallatinsystems.framework.domain.BaseDomain;
import com.google.appengine.api.datastore.Key;
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class SurveyGroup extends BaseDomain {
	
	private static final long serialVersionUID = 9001451397587572330L;	
	private String description;
	@Unique(name="SURVEYGROUP_CODE_IDX")
	private String code;
	private Date createdDateTime;
	private Date lastUpdateDateTime;
	
	@NotPersistent 
	private ArrayList<Survey> surveyList = null;
	public Key getKey() {
		return key;
	}
	public void setKey(Key key) {
		this.key = key;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Date getCreatedDateTime() {
		return createdDateTime;
	}
	public void setCreatedDateTime(Date createdDateTime) {
		this.createdDateTime = createdDateTime;
	}
	public Date getLastUpdateDateTime() {
		return lastUpdateDateTime;
	}
	public void setLastUpdateDateTime(Date lastUpdateDateTime) {
		this.lastUpdateDateTime = lastUpdateDateTime;
	}
	public void setSurveyList(ArrayList<Survey> surveyList) {
		this.surveyList = surveyList;
	}
	public ArrayList<Survey> getSurveyList() {
		return surveyList;
	}
	public void addSurvey(Survey item){
		if(surveyList==null)
			surveyList = new ArrayList<Survey>();
		surveyList.add(item);
	}
}
