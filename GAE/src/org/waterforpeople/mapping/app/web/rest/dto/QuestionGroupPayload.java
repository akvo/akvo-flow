package org.waterforpeople.mapping.app.web.rest.dto;

import java.io.Serializable;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;

public class QuestionGroupPayload implements Serializable {

	private static final long serialVersionUID = -1111440035804928338L;
	QuestionGroupDto question_group = null;

	public QuestionGroupDto getQuestion_group() {
		return question_group;
	}

	public void setQuestion_group(QuestionGroupDto question_group) {
		this.question_group = question_group;
	}
}
