package com.gallatinsystems.survey.device.domain;

import com.gallatinsystems.survey.device.util.ConstantUtil;

public class QuestionResponse {

	private String value;
	private String type;
	private Long id;
	private Long respondentId;
	private String questionId;

	public QuestionResponse(Long id, Long respondentId, String qId, String val,
			String t) {
		this.id = id;
		value = val;
		type = t;
		this.respondentId = respondentId;
		questionId = qId;
	}

	public QuestionResponse(String val, String t, String questionId) {
		id = null;
		type = t;
		value = val;
		this.questionId = questionId;
	}

	public QuestionResponse() {
		id = null;
		type = null;
		value = null;
		respondentId = null;
		questionId = null;
	}

	public boolean isValid() {
		if (!ConstantUtil.OTHER_RESPONSE_TYPE.equals(type)) {
			// if the response isn't "OTHER" then we have to check that it has a
			// value that isn't just a blank
			if (value == null || value.trim().length() == 0) {
				return false;
			}
		}
		return true;
	}

	public String getQuestionId() {
		return questionId;
	}

	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}

	public Long getRespondentId() {
		return respondentId;
	}

	public void setRespondentId(Long respondentId) {
		this.respondentId = respondentId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
