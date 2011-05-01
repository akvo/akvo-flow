package com.gallatinsystems.framework.rest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.gallatinsystems.framework.rest.exception.RestValidationException;


/**
 * base class for all rest api requests. It handles populating the common api
 * attributes (action, apiKey, start/endRow, maxResults). All apis do NOT need
 * to take these params, but if they do, the param names defined in this class
 * should be used so they can be handled automatically.
 * 
 * @author Christopher Fagiani
 * 
 */
public abstract class RestRequest implements Serializable {
	private static final long serialVersionUID = -8356057661356968219L;
	public static final String ACTION_PARAM = "action";
	private static final String API_KEY_PARAM = "apiKey";
	private static final String STARTROW_PARAM = "startRow";
	private static final String ENDROW_PARAM = "endRow";
	public static final String CURSOR_PARAM = "cursor";
	private static final String DESIRED_RESULTS_PARAM = "maxResults";
	private static final String NULL_STRING = "null";

	private List<RestError> validationErrorList;

	private int startRow;
	private int endRow;
	private int desiredResults;
	private String action;
	private String apiKey;
	private String cursor;

	/**
	 * populates the common fields and then dispatches ot the populateFields
	 * abstract method for subclass specific handling.
	 * 
	 * @param servletRequest
	 * @throws Exception
	 */
	public void populateFromHttpRequest(HttpServletRequest servletRequest)
			throws Exception {
		setAction(servletRequest.getParameter(RestRequest.ACTION_PARAM));
		setApiKey(servletRequest.getParameter(RestRequest.API_KEY_PARAM));
		cursor = servletRequest.getParameter(CURSOR_PARAM);
		if (cursor != null) {
			if (NULL_STRING.equalsIgnoreCase(cursor.trim())
					|| cursor.trim().length() == 0) {
				cursor = null;
			}
		}
		setStartRow(stringToInt(servletRequest
				.getParameter(RestRequest.STARTROW_PARAM)));
		setEndRow(stringToInt(servletRequest
				.getParameter(RestRequest.ENDROW_PARAM)));
		setDesiredResults(stringToInt(servletRequest
				.getParameter(RestRequest.DESIRED_RESULTS_PARAM)));
		populateFields(servletRequest);
	}

	public String getCursor() {
		return cursor;
	}

	public void setCursor(String cursor) {
		this.cursor = cursor;
	}

	public List<RestError> getValidationErrorList() {
		return validationErrorList;
	}

	public int getStartRow() {
		return startRow;
	}

	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}

	public int getEndRow() {
		return endRow;
	}

	public void setEndRow(int endRow) {
		this.endRow = endRow;
	}

	public int getDesiredResults() {
		return desiredResults;
	}

	public void setDesiredResults(int desiredResults) {
		this.desiredResults = desiredResults;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	private int stringToInt(String val) {
		int intVal = -1;
		if (val != null) {
			intVal = Integer.parseInt(val);
		}
		return intVal;
	}

	protected void addError(RestError err) {
		if (validationErrorList == null) {
			validationErrorList = new ArrayList<RestError>();
		}
		validationErrorList.add(err);
	}

	protected abstract void populateFields(HttpServletRequest req)
			throws Exception;

	protected abstract void populateErrors();

	public void validate() throws RestValidationException {
		populateErrors();
		if (validationErrorList != null && validationErrorList.size() > 0) {
			throw new RestValidationException(validationErrorList,
					"Validation error", null);
		}
	}

	protected Long parseLong(String val, String field) {
		Long result = null;
		if (val != null && val.trim().length() > 0) {
			try {
				result = Long.parseLong(val);
			} catch (Exception e) {
				addError(new RestError(RestError.BAD_DATATYPE_CODE,
						RestError.BAD_DATATYPE_MESSAGE, field
								+ " must be an integer"));
			}
		}
		return result;
	}

}