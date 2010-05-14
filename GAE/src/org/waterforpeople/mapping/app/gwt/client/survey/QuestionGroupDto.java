package org.waterforpeople.mapping.app.gwt.client.survey;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;

public class QuestionGroupDto extends BaseDto{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7253934961271624253L;
	/**
	 * 
	 */

	private String code;
	private String description;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
