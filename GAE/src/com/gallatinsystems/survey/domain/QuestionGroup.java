package com.gallatinsystems.survey.domain;

import java.util.HashMap;

import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;

@PersistenceCapable
public class QuestionGroup extends BaseDomain{
	
	private static final long serialVersionUID = -7253934961271624253L;
	/**
	 * 
	 */
	
	private String code;
	private String description;
	private HashMap<Integer,Question> questionMap = null;
	
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
	public void setQuestionMap(HashMap<Integer,Question> questionMap) {
		this.questionMap = questionMap;
	}
	public HashMap<Integer,Question> getQuestionMap() {
		return questionMap;
	}
	public void addQuestion(Question item, Integer order){
		if(questionMap==null){
			questionMap = new HashMap<Integer,Question>();
		}
		questionMap.put(order, item);
	}
}
