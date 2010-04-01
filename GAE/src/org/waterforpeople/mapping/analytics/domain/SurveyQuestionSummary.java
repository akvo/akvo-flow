package org.waterforpeople.mapping.analytics.domain;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.gallatinsystems.framework.domain.BaseDomain;

@PersistenceCapable
public class SurveyQuestionSummary extends BaseDomain {

	private static final long serialVersionUID = -8084700199272031995L;

	@Persistent
	private String questionId;
	
	@Persistent
	private String response;
	@Persistent
	private Long count;

	public String getQuestionId() {
		return questionId;
	}

	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}
	
	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

}
