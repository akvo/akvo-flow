package com.gallatinsystems.survey.domain;

import java.io.Serializable;

import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;
@PersistenceCapable
public class OptionContainerQuestionOptionAssoc extends BaseDomain implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5951296199744577283L;

	
	private Long optionContainerId = null;
	private Long questionOptionId = null;
	public void setOptionContianerId(Long optionContianerId) {
		this.optionContainerId = optionContianerId;
	}
	public Long getOptionContianerId() {
		return optionContainerId;
	}
	public void setQuestionOptionId(Long questionOptionId) {
		this.questionOptionId = questionOptionId;
	}
	public Long getQuestionOptionId() {
		return questionOptionId;
	}
}
