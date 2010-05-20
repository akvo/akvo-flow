package org.waterforpeople.mapping.app.gwt.client.survey;

import java.io.Serializable;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;

public class QuestionDependencyDto extends BaseDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3872186127235609046L;
	
	private Long questionId = null;

	public void setQuestionId(Long questionId) {
		this.questionId = questionId;
	}

	public Long getQuestionId() {
		return questionId;
	}

}
