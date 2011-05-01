package com.gallatinsystems.survey.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;

@PersistenceCapable
public class SurveyGroup extends BaseDomain { 

	private static final long serialVersionUID = 8941584684617286776L;
	private String name = null;
	private String code = null;
	private String description = null;
	@NotPersistent
	private HashMap<String, Translation> altTextMap;
	@NotPersistent
	private List<Survey> surveyList = null;

	public HashMap<String, Translation> getAltTextMap() {
		return altTextMap;
	}

	public void setAltTextMap(HashMap<String, Translation> altTextMap) {
		this.altTextMap = altTextMap;
	}

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

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

}
