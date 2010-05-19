package com.gallatinsystems.survey.domain;

import java.io.Serializable;
import java.util.ArrayList;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.gallatinsystems.framework.domain.BaseDomain;

@PersistenceCapable
public class OptionContainer extends BaseDomain implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3477253982636430524L;
	private ArrayList<QuestionOption> optionsList = null;
	private Boolean allowMultipleFlag = null;
	
	public Boolean getAllowOtherFlag() {
		return allowOtherFlag;
	}
	public void setAllowOtherFlag(Boolean allowOtherFlag) {
		this.allowOtherFlag = allowOtherFlag;
	}
	public void setOptionsList(ArrayList<QuestionOption> optionsList) {
		this.optionsList = optionsList;
	}
	public ArrayList<QuestionOption> getOptionsList() {
		return optionsList;
	}
	private Boolean allowOtherFlag = null;
	public void addQuestionOption(QuestionOption questionOption){
		if(optionsList==null)
			optionsList = new ArrayList<QuestionOption>();
		optionsList.add(questionOption);
	}
	public void setAllowMultipleFlag(Boolean allowMultipleFlag) {
		this.allowMultipleFlag = allowMultipleFlag;
	}
	public Boolean getAllowMultipleFlag() {
		return allowMultipleFlag;
	}
	
}
