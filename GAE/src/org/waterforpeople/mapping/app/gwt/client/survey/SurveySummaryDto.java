package org.waterforpeople.mapping.app.gwt.client.survey;

import java.io.Serializable;

public class SurveySummaryDto implements Serializable {

	
	private static final long serialVersionUID = -1966405747413629647L;
	private String questionId;
	private String responseText;
	private Long count;

	public String getQuestionId() {
		return questionId;
	}

	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}

	public String getResponseText() {
		return responseText;
	}

	public void setResponseText(String responseText) {
		this.responseText = responseText;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

}