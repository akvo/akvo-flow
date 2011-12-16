package com.gallatinsystems.survey.domain;

import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;
import com.google.appengine.api.datastore.Text;

/**
 * wrapper class allowing for saving of chunks of xml that represent PART OF a
 * single survey. This is primarily used during survey assembly in the event
 * that a survey is too large to be accommodated in a single operation.
 * 
 * 
 */
@PersistenceCapable
public class SurveyXMLFragment extends BaseDomain {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6955885065118685217L;
	private Long surveyId = null;
	private Long questionGroupId = null;
	private Integer fragmentOrder = null;
	private Text fragment = null;
	private FRAGMENT_TYPE fragmentType = null;
	private Long transactionId = null;

	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}

	public Integer getFragmentOrder() {
		return fragmentOrder;
	}

	public void setFragmentOrder(Integer fragmentOrder) {
		this.fragmentOrder = fragmentOrder;
	}

	public Text getFragment() {
		return fragment;
	}

	public void setFragment(Text fragment) {
		this.fragment = fragment;
	}

	public FRAGMENT_STATUS getStatus() {
		return status;
	}

	public void setStatus(FRAGMENT_STATUS status) {
		this.status = status;
	}

	public void setSurveyId(Long surveyId) {
		this.surveyId = surveyId;
	}

	public Long getSurveyId() {
		return surveyId;
	}

	public void setFragmentType(FRAGMENT_TYPE fragmentType) {
		this.fragmentType = fragmentType;
	}

	public FRAGMENT_TYPE getFragmentType() {
		return fragmentType;
	}

	public void setQuestionGroupId(Long questionGroupId) {
		this.questionGroupId = questionGroupId;
	}

	public Long getQuestionGroupId() {
		return questionGroupId;
	}

	private FRAGMENT_STATUS status = null;

	public enum FRAGMENT_STATUS {
		FINISHED, INPROCESS
	};

	public enum FRAGMENT_TYPE {
		QUESTION_GROUP, QUESTION
	};

}
