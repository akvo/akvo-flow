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
	public static final String OFFSET_KEY = "offset";
	public static final String VALUE_KEY = "value";

	private String objectKey;
	private String type;
	private Integer offset = 0;
	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		if (value != null && value.trim().length() > 0) {
			this.value = value;
		} else {
			this.value = null;
		}
	}

	public Integer getOffset() {
		return offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

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
		setValue(req.getParameter(VALUE_KEY));
		if (req.getParameter(OFFSET_KEY) != null) {
			try {
				offset = Integer.parseInt(req.getParameter(OFFSET_KEY).trim());
			} catch (Exception e) {
				offset = 0;
			}
		}
	}

}
