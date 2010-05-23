package org.waterforpeople.mapping.app.web.dto;

import javax.servlet.http.HttpServletRequest;

import com.gallatinsystems.framework.rest.RestError;
import com.gallatinsystems.framework.rest.RestRequest;

public class SpreadsheetImportRequest extends RestRequest {

	public static final String PROCESS_FILE_ACTION = "processFile";

	private static final String ID_PARAM = "identifier";
	private static final String TYPE_PARAM = "type";

	private String identifier;
	private String type;

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	protected void populateFields(HttpServletRequest req) throws Exception {
		identifier = req.getParameter(ID_PARAM);
		type = req.getParameter(TYPE_PARAM);
	}

	@Override
	public void populateErrors() {
		if (getAction() == null) {
			String errorMsg = ACTION_PARAM + " is mandatory";
			addError(new RestError(RestError.MISSING_PARAM_ERROR_CODE,
					RestError.MISSING_PRAM_ERROR_MESSAGE, errorMsg));
		}
	}
}
