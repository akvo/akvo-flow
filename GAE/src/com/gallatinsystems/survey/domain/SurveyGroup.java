package com.gallatinsystems.survey.domain;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.gallatinsystems.framework.domain.BaseDomain;
import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class SurveyGroup extends BaseDomain {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8941584684617286776L;
	private String name = null;
	private String code = null;
	@Persistent(serialized = "true")
	private List<Key> altNameKeyList = null;
	@Persistent(serialized = "true")
	private List<Survey> surveyList = null;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setSurveyList(List<Survey> surveyList) {
		this.surveyList = surveyList;
	}

	public List<Survey> getSurveyList() {
		return surveyList;
	}

	public void addSurvey(Survey survey) {
		if (surveyList == null)
			surveyList = new ArrayList<Survey>();
		surveyList.add(survey);
	}

	public void addAltNameKeyList(Key altNameKey){
		if(altNameKey==null)
			this.altNameKeyList = new ArrayList<Key>();
		getAltNameKeyList().add(altNameKey);
	}
	
	public void setAltNameKey(List<Key> altNameKey) {
		this.altNameKeyList = altNameKey;
	}

	public List<Key> getAltNameKeyList() {
		return altNameKeyList;
	}

	
}
