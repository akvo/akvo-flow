package com.gallatinsystems.survey.domain;

import java.util.HashMap;

import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;

/**
 * 
 * Option for multiple choice questions.
 *
 */
@PersistenceCapable
public class QuestionOption extends BaseDomain {

	private static final long serialVersionUID = 2794521663923141747L;
	private String code = null;
	private String text;
	@NotPersistent
	private HashMap<String, Translation> translationMap;
	private Long questionId;
	private Integer order;

	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public Long getQuestionId() {
		return questionId;
	}

	public void setQuestionId(Long questionId) {
		this.questionId = questionId;
	}

	public HashMap<String, Translation> getTranslationMap() {
		return translationMap;
	}

	public void setTranslationMap(HashMap<String, Translation> translationMap) {
		this.translationMap = translationMap;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void addTranslation(Translation t) {
		if (translationMap == null) {
			translationMap = new HashMap<String, Translation>();
		}
		translationMap.put(t.getLanguageCode(), t);
	}

}
