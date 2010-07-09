package com.gallatinsystems.survey.domain;

import com.gallatinsystems.framework.domain.BaseDomain;
import com.google.appengine.api.datastore.Text;

public class SurveyXMLFragment extends BaseDomain {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6955885065118685217L;
	private Long surveyId = null;
	private Integer fragmentOrder = null;
	private Text fragment = null;
	private FRAGMENT_TYPE fragmentType = null;

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

	private FRAGMENT_STATUS status = null;

	public enum FRAGMENT_STATUS {
		FINISHED, INPROCESS
	};
	
	public enum FRAGMENT_TYPE{QUESTION_GROUP, QUESTION};

}
