package com.gallatinsystems.survey.domain;

import java.util.HashMap;
import java.util.Map;

import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;

@PersistenceCapable
public class QuestionHelpMedia extends BaseDomain {
	
	private static final long serialVersionUID = 7035250558880867571L;
	private String resourceUrl = null;
	private Type type = null;
	private String text = null;
	private Long questionId;
	@NotPersistent
	private Map<String, Translation> translationMap;

	public enum Type {
		PHOTO, VIDEO, TEXT, ACTIVITY
	}

	public Long getQuestionId() {
		return questionId;
	}

	public void setQuestionId(Long questionId) {
		this.questionId = questionId;
	}

	public Map<String, Translation> getTranslationMap() {
		return translationMap;
	}

	public void setTranslationMap(Map<String, Translation> translationMap) {
		this.translationMap = translationMap;
	}

	public String getResourceUrl() {
		return resourceUrl;
	}

	public void setResourceUrl(String url) {
		this.resourceUrl = url;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void addTranslation(Translation t) {
		if (translationMap == null) {
			translationMap = new HashMap<String, Translation>();
		}
		translationMap.put(t.getLanguageCode(), t);
	}

}
