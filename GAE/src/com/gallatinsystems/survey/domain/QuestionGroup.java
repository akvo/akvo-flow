package com.gallatinsystems.survey.domain;

import java.util.HashMap;
import java.util.TreeMap;

import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;

@PersistenceCapable
public class QuestionGroup extends BaseDomain {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6831602386813027856L;
	private String name = null;
	private String desc = null;

	@NotPersistent
	private TreeMap<Integer, Question> questionMap;
	@NotPersistent
	private HashMap<String, Translation> translationMap;
	private String code = null;
	private String path = null;
	private Long surveyId;
	private Integer order;

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public Long getSurveyId() {
		return surveyId;
	}

	public void setSurveyId(Long surveyId) {
		this.surveyId = surveyId;
	}

	public HashMap<String, Translation> getTranslationMap() {
		return translationMap;
	}

	public void setTranslationMap(HashMap<String, Translation> translationMap) {
		this.translationMap = translationMap;
	}

	public void addQuestion(Integer order, Question question) {
		if (getQuestionMap() == null)
			setQuestionMap(new TreeMap<Integer, Question>());
		getQuestionMap().put(order, question);
	}

	public void setQuestionMap(TreeMap<Integer, Question> questionMap) {
		this.questionMap = questionMap;
	}

	public TreeMap<Integer, Question> getQuestionMap() {
		return questionMap;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getDesc() {
		return desc;
	}

	public void addTranslation(Translation t) {
		if (translationMap == null) {
			translationMap = new HashMap<String, Translation>();
		}
		translationMap.put(t.getLanguageCode(), t);
	}

}
