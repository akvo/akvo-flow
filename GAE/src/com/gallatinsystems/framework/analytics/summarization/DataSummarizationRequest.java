package com.gallatinsystems.framework.analytics.summarization;

import javax.servlet.http.HttpServletRequest;

import com.gallatinsystems.framework.rest.RestRequest;

/**
 * simple dto for handling summarization requests
 * 
 * @author Christopher Fagiani
 * 
 */
public class DataSummarizationRequest extends RestRequest {

	private static final long serialVersionUID = -3458265878903081822L;
	public static final String OBJECT_KEY = "objectKey";
	public static final String OBJECT_TYPE = "type";

	private String objectKey;
	private String type;

	public String getObjectKey() {
		return objectKey;
	}

	public void setObjectKey(String objectKey) {
		this.objectKey = objectKey;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	protected void populateErrors() {
		// no-op

	}

	@Override
	protected void populateFields(HttpServletRequest req) throws Exception {
		setObjectKey(req.getParameter(OBJECT_KEY));
		setType(req.getParameter(OBJECT_TYPE));
	}

}
