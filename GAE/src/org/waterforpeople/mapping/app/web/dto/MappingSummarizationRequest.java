package org.waterforpeople.mapping.app.web.dto;

import javax.servlet.http.HttpServletRequest;

import com.gallatinsystems.framework.rest.RestError;
import com.gallatinsystems.framework.rest.RestRequest;

/**
 * DTO for requests to the summarization apis
 * 
 * @author Christopher Fagiani
 * 
 */
public class MappingSummarizationRequest extends RestRequest {
	
	private static final long serialVersionUID = 2509976216047671455L;
	private static final String REGION_UUID = "regionUUID";
	private static final String TYPE = "type";

	private String regionUUID;
	private String summarizationType;

	public String getRegionUUID() {
		return regionUUID;
	}

	public void setRegionUUID(String regionUUID) {
		this.regionUUID = regionUUID;
	}

	public String getSummarizationType() {
		return summarizationType;
	}

	public void setSummarizationType(String summarizationType) {
		this.summarizationType = summarizationType;
	}

	@Override
	protected void populateFields(HttpServletRequest req) throws Exception {
		regionUUID = req.getParameter(REGION_UUID);
		summarizationType = req.getParameter(TYPE);
	}

	@Override
	protected void populateErrors() {
		if (regionUUID == null) {
			String errorMsg = REGION_UUID + " is mandatory";
			addError(new RestError(RestError.MISSING_PARAM_ERROR_CODE,
					RestError.MISSING_PARAM_ERROR_MESSAGE, errorMsg));
		}
	}
}
