package com.gallatinsystems.survey.domain.refactor;

import java.util.List;

import com.gallatinsystems.framework.domain.BaseDomain;
import com.google.appengine.api.datastore.Key;

public class SurveyGroup extends BaseDomain {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8941584684617286776L;
	private String name = null;
	private String code = null;
	List<Key> surveyList = null;
	
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
	public List<Key> getSurveyList() {
		return surveyList;
	}
	public void setSurveyList(List<Key> surveyList) {
		this.surveyList = surveyList;
	}

}
