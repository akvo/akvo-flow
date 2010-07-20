package com.gallatinsystems.survey.domain.refactor;

import java.util.HashMap;
import java.util.List;

import com.gallatinsystems.framework.domain.BaseDomain;
import com.google.appengine.api.datastore.Key;

public class Survey extends BaseDomain {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8638039212962768687L;
	private HashMap<String,String> nameMap = null;
	private String code = null;
	private HashMap<String,String> descMap  = null;
	private Status status = null;
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

	
}
