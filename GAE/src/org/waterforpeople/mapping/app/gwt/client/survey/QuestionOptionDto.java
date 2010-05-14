package org.waterforpeople.mapping.app.gwt.client.survey;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;

public class QuestionOptionDto extends BaseDto {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8925764841259376220L;
	/**
	 * 
	 */
	private String text;
	private String code;
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
}
