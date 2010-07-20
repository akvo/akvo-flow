package com.gallatinsystems.survey.domain.refactor;

import java.util.HashMap;

import com.gallatinsystems.framework.domain.BaseDomain;
import com.google.appengine.api.datastore.Key;

public class QuestionGroup extends BaseDomain {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6831602386813027856L;
	private HashMap<Integer, Key> questionMap = null;
	private HashMap<String, String> nameMap = null;
	private HashMap<String, String> descMap = null;

	public HashMap<Integer, Key> getQuestionMap() {
		return questionMap;
	}

	public void setQuestionMap(HashMap<Integer, Key> questionMap) {
		this.questionMap = questionMap;
	}

	public HashMap<String, String> getNameMap() {
		return nameMap;
	}

	public void setNameMap(HashMap<String, String> nameMap) {
		this.nameMap = nameMap;
	}

	public HashMap<String, String> getDescMap() {
		return descMap;
	}

	public void setDescMap(HashMap<String, String> descMap) {
		this.descMap = descMap;
	}

	public void addQuestion(Integer order, Key questionKey) {
		if (questionMap == null)
			questionMap = new HashMap<Integer, Key>();
		questionMap.put(order, questionKey);
	}

	public void addName(String langCode, String name) {
		if (nameMap == null)
			nameMap = new HashMap<String, String>();
		nameMap.put(langCode, name);
	}

	public void addDesc(String langCode, String desc) {
		if (descMap == null)
			descMap = new HashMap<String, String>();
		descMap.put(langCode, desc);
	}
}
