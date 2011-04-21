package org.waterforpeople.mapping.app.web.dto;

import java.util.List;

import org.waterforpeople.mapping.app.gwt.client.surveyinstance.QuestionAnswerStoreDto;

import com.gallatinsystems.framework.rest.RestResponse;

/**
 * response encapsulating calls to methods that list questionAnswerStore objects
 * 
 * @author Christopher Fagiani
 * 
 */
public class QuestionAnswerResponse extends RestResponse {
	private static final long serialVersionUID = 1548249617327473969L;
	private List<QuestionAnswerStoreDto> answers;
	private String cursor;

	public List<QuestionAnswerStoreDto> getAnswers() {
		return answers;
	}

	public void setAnswers(List<QuestionAnswerStoreDto> answers) {
		this.answers = answers;
	}

	public String getCursor() {
		return cursor;
	}

	public void setCursor(String cursor) {
		this.cursor = cursor;
	}

}
