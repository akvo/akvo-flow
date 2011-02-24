package org.waterforpeople.mapping.app.web.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import com.gallatinsystems.framework.rest.RestError;
import com.gallatinsystems.framework.rest.RestRequest;

/**
 * encapsulates requests to the bootstrap generator servlet
 * 
 * @author Christopher Fagiani
 * 
 */
public class BootstrapGeneratorRequest extends RestRequest {
	
	private static final long serialVersionUID = 9041460970737380174L;
	
	public static final String GEN_ACTION = "generate";
	public static final String SURVEY_ID_LIST_PARAM = "surveyIds";
	public static final String EMAIL_PARAM = "email";
	public static final String DB_PARAM = "dbInstructions";
	public static final String DELMITER = "||";

	private String email;
	private String dbInstructions;
	private List<Long> surveyIds;

	@Override
	protected void populateErrors() {
		if (surveyIds == null || surveyIds.size() == 0) {
			addError(new RestError(RestError.MISSING_PARAM_ERROR_CODE,
					RestError.MISSING_PARAM_ERROR_MESSAGE, SURVEY_ID_LIST_PARAM
							+ " cannot be null or empty"));
		}
		if (email == null) {
			addError(new RestError(RestError.MISSING_PARAM_ERROR_CODE,
					RestError.MISSING_PARAM_ERROR_MESSAGE, EMAIL_PARAM
							+ " cannot be null or empty"));
		}

	}

	@Override
	protected void populateFields(HttpServletRequest req) throws Exception {
		email = req.getParameter(EMAIL_PARAM);
		dbInstructions = req.getParameter(DB_PARAM);
		String ids = req.getParameter(SURVEY_ID_LIST_PARAM);
		if (ids != null) {
			surveyIds = new ArrayList<Long>();
			StringTokenizer strTok = new StringTokenizer(ids, DELMITER);
			while (strTok.hasMoreTokens()) {
				String id = strTok.nextToken().trim();
				try {
					surveyIds.add(new Long(id));
				} catch (NumberFormatException e) {
					addError(new RestError(
							RestError.BAD_DATATYPE_CODE,
							RestError.BAD_DATATYPE_MESSAGE,
							SURVEY_ID_LIST_PARAM
									+ " must only contain integers delimited by "
									+ DELMITER));
				}
			}
		}
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDbInstructions() {
		return dbInstructions;
	}

	public void setDbInstructions(String dbInstructions) {
		this.dbInstructions = dbInstructions;
	}

	public List<Long> getSurveyIds() {
		return surveyIds;
	}

	public void setSurveyIds(List<Long> surveyIds) {
		this.surveyIds = surveyIds;
	}

}
