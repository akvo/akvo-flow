package com.gallatinsystems.survey.domain.refactor;

import java.util.HashMap;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.gallatinsystems.framework.domain.BaseDomain;
import com.google.appengine.api.datastore.Key;
@PersistenceCapable
public class Survey extends BaseDomain {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8638039212962768687L;
	@Persistent(serialized="true") 
	private HashMap<String, String> nameMap = null;
	private String code = null;
	@Persistent(serialized="true") 
	private HashMap<String, String> descMap = null;
	private Status status = null;
	@Persistent(serialized="true") 
	private HashMap<Integer, Key> questionGroupMap = null;
	private Double version = null;

	public enum Status {
		PUBLISHED, NOT_PUBLISHED
	};

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

	public void setQuestionGroupMap(HashMap<Integer, Key> questionGroupMap) {
		this.questionGroupMap = questionGroupMap;
	}

	public HashMap<Integer, Key> getQuestionGroupMap() {
		return questionGroupMap;
	}

	public void addQuestionGroup(Integer order, Key questionGroupKey) {
		if (questionGroupMap == null)
			questionGroupMap = new HashMap<Integer, Key>();
		questionGroupMap.put(order, questionGroupKey);
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
