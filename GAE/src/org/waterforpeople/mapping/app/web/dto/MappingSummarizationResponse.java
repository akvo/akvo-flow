package org.waterforpeople.mapping.app.web.dto;

import com.gallatinsystems.framework.rest.RestResponse;

/**
 * class to encapsulate the response for region summarization
 * 
 * @author Christopher Fagiani
 * 
 */
public class MappingSummarizationResponse extends RestResponse {

	private String colorCode;

	public String getColorCode() {
		return colorCode;
	}

	public void setColorCode(String colorCode) {
		this.colorCode = colorCode;
	}
}
