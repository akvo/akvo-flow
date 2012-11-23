package org.waterforpeople.mapping.app.web.rest.dto;

import java.io.Serializable;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;

public class QuestionPayload implements Serializable {

	private static final long serialVersionUID = 4860651872541157075L;
	QuestionDto question = null;

	public QuestionDto getQuestion() {
		return question;
	}

	public void setQuestion(QuestionDto question) {
		this.question = question;
	}
}
