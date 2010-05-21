package com.gallatinsystems.survey.domain;

import java.io.Serializable;

import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;
@PersistenceCapable
public class QuestionDependency extends BaseDomain implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2875892909993388821L;
	
	private Long questionId = null;

	public void setQuestionId(Long questionId) {
		this.questionId = questionId;
	}

	public Long getQuestionId() {
		return questionId;
	}
	
	public void setAnswerValue(String answerValue) {
		this.answerValue = answerValue;
	}

	public String getAnswerValue() {
		return answerValue;
	}

	private String answerValue = null;

}
