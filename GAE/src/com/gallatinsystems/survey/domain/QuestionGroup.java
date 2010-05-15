package com.gallatinsystems.survey.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;

@PersistenceCapable
public class QuestionGroup extends BaseDomain {

	private static final long serialVersionUID = -7253934961271624253L;
	/**
	 * 
	 */

	private String code;
	private String description;
	private List<Question> questionList;

	@NotPersistent
	private HashMap<Integer, Question> questionMap = null;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public HashMap<Integer, Question> getQuestionMap() {
		return questionMap;
	}

	public void addQuestion(Question item, Integer key) {
		if (questionMap == null || questionList == null) {
			questionMap = new HashMap<Integer, Question>();
			questionList = new ArrayList<Question>();
		}
		questionList.add(item);
		questionMap.put(key, item);
	}

	public void setQuestionList(List<Question> qList) {
		questionList = qList;
		questionMap = new HashMap<Integer, Question>();
		if (questionList != null) {
			for (int i = 0; i < questionList.size(); i++) {
				questionMap.put(i, questionList.get(i));
			}
		}
	}

}
