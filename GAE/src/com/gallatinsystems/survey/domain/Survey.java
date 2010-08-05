package com.gallatinsystems.survey.domain;

import java.util.HashMap;
import java.util.TreeMap;

import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;

@PersistenceCapable
public class Survey extends BaseDomain {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8638039212962768687L;
	@NotPersistent
	private HashMap<String, Translation> translationMap;
	private String code = null;
	private String name = null;
	private String desc = null;
	private Status status = null;
	@NotPersistent
	private TreeMap<Integer, QuestionGroup> questionGroupMap = null;
	private Double version = null;
	private String path = null;
	private Long surveyGroupId;

	public enum Status {
		PUBLISHED, NOT_PUBLISHED, IMPORTED, VERIFIED
	};

	public Long getSurveyGroupId() {
		return surveyGroupId;
	}

	public void setSurveyGroupId(Long surveyGroupId) {
		this.surveyGroupId = surveyGroupId;
	}

	public HashMap<String, Translation> getTranslationMap() {
		return translationMap;
	}

	public void setTranslationMap(HashMap<String, Translation> translationMap) {
		this.translationMap = translationMap;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Double getVersion() {
		return version;
	}

	public void setVersion(Double version) {
		this.version = version;
	}

	public void setQuestionGroupMap(
			TreeMap<Integer, QuestionGroup> questionGroupMap) {
		this.questionGroupMap = questionGroupMap;
	}

	public TreeMap<Integer, QuestionGroup> getQuestionGroupMap() {
		return questionGroupMap;
	}

	public void addQuestionGroup(Integer order, QuestionGroup questionGroup) {
		if (questionGroupMap == null)
			questionGroupMap = new TreeMap<Integer, QuestionGroup>();
		questionGroupMap.put(order, questionGroup);
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public void addTranslation(Translation t) {
		if (translationMap == null) {
			translationMap = new HashMap<String, Translation>();
		}
		translationMap.put(t.getLanguageCode(), t);
	}

}
