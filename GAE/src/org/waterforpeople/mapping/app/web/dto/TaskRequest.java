package org.waterforpeople.mapping.app.web.dto;

import javax.servlet.http.HttpServletRequest;

import com.gallatinsystems.framework.rest.RestError;
import com.gallatinsystems.framework.rest.RestRequest;

/**
 * request to the task queue servlet
 * 
 * @author Christopher Fagiani
 * 
 */
public class TaskRequest extends RestRequest {

	private static final long serialVersionUID = 3002548651592779931L;
	public static final String ADD_ACCESS_POINT_ACTION = "addAccessPoint";
	public static final String PROCESS_FILE_ACTION = "processFile";

	private static final String FILE_NAME_PARAM = "fileName";
	private static final String SURVEY_ID_PARAM = "surveyId";
	private static final String PHONE_NUM_PARAM = "phoneNumber";
	private static final String CHECKSUM_PARAM = "checksum";

	private String fileName;
	private Long surveyId;
	private String phoneNumber;
	private String checksum;

	public String getChecksum() {
		return checksum;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Long getSurveyId() {
		return surveyId;
	}

	public void setSurveyId(Long surveyId) {
		this.surveyId = surveyId;
	}

	@Override
	protected void populateFields(HttpServletRequest req) throws Exception {
		fileName = req.getParameter(FILE_NAME_PARAM);
		phoneNumber = req.getParameter(PHONE_NUM_PARAM);
		checksum = req.getParameter(CHECKSUM_PARAM);
		try {
			if (req.getParameter(SURVEY_ID_PARAM) != null) {
				surveyId = Long.parseLong(req.getParameter(SURVEY_ID_PARAM));
			}
		} catch (Exception e) {
			addError(new RestError(RestError.BAD_DATATYPE_CODE,
					RestError.BAD_DATATYPE_MESSAGE, SURVEY_ID_PARAM
							+ " must be an integer"));
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
