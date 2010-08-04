package com.gallatinsystems.survey.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.jdo.annotations.Element;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.gallatinsystems.framework.domain.BaseDomain;
import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class QuestionGroup extends BaseDomain {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6831602386813027856L;
	private String name = null;
	private String desc = null;

	@Persistent(serialized = "true")
	private HashMap<Integer, Question> questionMap = null;
	@Persistent(serialized = "true")
	private List<Key> altNameKeyList = null;
	@Persistent(serialized = "true")
	private List<Key> altDescList = null;
	private String code = null;
	private String path = null;

	public List<Key> getNameKeyList() {
		return altNameKeyList;
	}

	public void setNameKeyList(List<Key> nameList) {
		this.altNameKeyList = nameList;
	}

	public void addQuestion(Integer order, Question question) {
		if (getQuestionMap() == null)
			setQuestionMap(new HashMap<Integer, Question>());
		getQuestionMap().put(order, question);
	}

	public void addAltNameKey(Key altNameKey) {
		if (altNameKeyList == null)
			altNameKeyList = new ArrayList<Key>();
		altNameKeyList.add(altNameKey);
	}

	public void addAltDescKey(Key altDesc) {
		if (getAltDescList() == null)
			setAltDescList(new ArrayList<Key>());
		getAltDescList().add(altDesc);
	}

	public void setQuestionMap(HashMap<Integer, Question> questionMap) {
		this.questionMap = questionMap;
	}

	public HashMap<Integer, Question> getQuestionMap() {
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

	public void setAltDescList(List<Key> altDescList) {
		this.altDescList = altDescList;
	}

	public List<Key> getAltDescList() {
		return altDescList;
	}
}
