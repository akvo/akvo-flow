package org.waterforpeople.mapping.app.web.dto;

import javax.servlet.http.HttpServletRequest;

import com.gallatinsystems.framework.rest.RestError;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.exception.RestValidationException;

/**
 * request to the task queue servlet
 * 
 * @author Christopher Fagiani
 * 
 */
public class TaskRequest extends RestRequest {

	public static final String ADD_ACCESS_POINT_ACTION = "addAccessPoint";
	public static final String PROCESS_FILE_ACTION = "processFile";

	private static final String FILE_NAME_PARAM = "fileName";
	private static final String SURVEY_ID_PARAM = "surveyId";

	private String fileName;
	private String surveyId;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getSurveyId() {
		return surveyId;
	}

	public void setSurveyId(String surveyId) {
		this.surveyId = surveyId;
	}

	@Override
	protected void populateFields(HttpServletRequest req) throws Exception {
		fileName = req.getParameter(FILE_NAME_PARAM);
		surveyId = req.getParameter(SURVEY_ID_PARAM);
	}

	@Override
	public void validate() throws RestValidationException {
		if (getAction() == null) {
			String errorMsg = ACTION_PARAM + " is mandatory";
			throw new RestValidationException(new RestError(
					RestError.MISSING_PARAM_ERROR_CODE,
					RestError.MISSING_PRAM_ERROR_MESSAGE, errorMsg), errorMsg,
					null);
		}

	}
}
