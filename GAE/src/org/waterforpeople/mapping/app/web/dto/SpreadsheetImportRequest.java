package org.waterforpeople.mapping.app.web.dto;

import javax.servlet.http.HttpServletRequest;

import com.gallatinsystems.framework.rest.RestError;
import com.gallatinsystems.framework.rest.RestRequest;

public class SpreadsheetImportRequest extends RestRequest {

	private static final long serialVersionUID = 8472898722818764987L;

	public static final String PROCESS_FILE_ACTION = "processFile";

	private static final String ID_PARAM = "identifier";
	private static final String TYPE_PARAM = "type";
	private static final String START_ROW_PARAM = "startRow";
	private static final String GROUP_ID_PARAM = "questionGroupId";
	private static final String ST_PARAM = "sessionToken";

	private String identifier;
	private String type;
	private int startRow = 0;
	private long groupId;

	private String sessionToken;

	public String getSessionToken() {
		return sessionToken;
	}

	public void setSessionToken(String sessionToken) {
		this.sessionToken = sessionToken;
	}

	public int getStartRow() {
		return startRow;
	}

	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}

	public long getGroupId() {
		return groupId;
	}

	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}

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
		sessionToken = req.getParameter(ST_PARAM);
		String start = req.getParameter(START_ROW_PARAM);
		String groupId = req.getParameter(GROUP_ID_PARAM);

		if (start != null) {
			try {
				startRow = Integer.parseInt(start);
			} catch (NumberFormatException e) {
				addError(new RestError(RestError.BAD_DATATYPE_CODE,
						RestError.BAD_DATATYPE_MESSAGE,
						"Start row must be an integer"));
			}
		}
		if (groupId != null) {
			try {
				this.groupId = Long.parseLong(groupId);
			} catch (NumberFormatException e) {
				addError(new RestError(RestError.BAD_DATATYPE_CODE,
						RestError.BAD_DATATYPE_MESSAGE,
						"GroupId must be an integer"));
			}
		}
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
