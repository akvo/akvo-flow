package com.gallatinsystems.survey.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

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
	@Persistent(serialized = "true")
	private List<Key> altNameKeyList = null;
	private String code = null;
	private String name = null;
	private String desc = null;
	@Persistent(serialized = "true")
	private List<Key> altDescKeyList = null;
	private Status status = null;
	@Persistent(serialized = "true")
	private TreeMap<Integer, QuestionGroup> questionGroupMap = null;
	private Double version = null;
	private String path = null;

	public enum Status {
		PUBLISHED, NOT_PUBLISHED, IMPORTED, VERIFIED
	};

	public List<Key> getAltNameList() {
		return altNameKeyList;
	}

	public void setAltNameList(List<Key> altNameList) {
		this.altNameKeyList = altNameList;
	}

	public void addAltName(Key item) {
		if (altNameKeyList == null)
			altNameKeyList = new ArrayList<Key>();
		altNameKeyList.add(item);
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

	
	public void addAltDescKey(Key altDesc) {
		if (getAltDescKeyList() == null)
			setAltDescKeyList(new ArrayList<Key>());
		getAltDescKeyList().add(altDesc);
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

	public void setAltDescKeyList(List<Key> altDescKeyList) {
		this.altDescKeyList = altDescKeyList;
	}

	public List<Key> getAltDescKeyList() {
		return altDescKeyList;
	}

}
