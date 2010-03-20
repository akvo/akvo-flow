package org.waterforpeople.mapping.app.web.dto;

import javax.servlet.http.HttpServletRequest;

import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.exception.RestValidationException;

/**
 * DTO for requests to the summarization apis
 * 
 * @author Christopher Fagiani
 * 
 */
public class MappingSummarizationRequest extends RestRequest {
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
	public void validate() throws RestValidationException {
		// TODO Auto-generated method stub

	}

}
