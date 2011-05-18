package org.waterforpeople.mapping.app.gwt.client.standardscoring;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;

public class StandardScoreBucket extends BaseDto {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5634233227797937899L;
	private String name = null;

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
