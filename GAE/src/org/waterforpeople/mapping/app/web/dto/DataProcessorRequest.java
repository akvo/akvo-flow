package org.waterforpeople.mapping.app.web.dto;

import javax.servlet.http.HttpServletRequest;

import com.gallatinsystems.framework.rest.RestError;
import com.gallatinsystems.framework.rest.RestRequest;

/**
 * Handles requests to the DataProcessing Rest Service
 * 
 * @author Christopher Fagiani
 * 
 */
public class DataProcessorRequest extends RestRequest {
	private static final long serialVersionUID = -4553663867954174523L;
	public static final String PROJECT_FLAG_UPDATE_ACTION = "projectFlagUpdate";
	public static final String REBUILD_QUESTION_SUMMARY_ACTION = "rebuildQuestionSummary";
	public static final String IMPORT_REMOTE_SURVEY_ACTION = "importRemoteSurvey";
	public static final String FIX_NULL_SUBMITTER_ACTION = "fixNullSubmitter";
	public static final String FIX_DUPLICATE_OTHER_TEXT_ACTION = "fixDuplicateOtherText";
	public static final String TRIM_OPTIONS = "trimOptions";
	public static final String RESCORE_AP_ACTION = "rescoreAp";
	public static final String SOURCE_PARAM = "source";
	public static final String COUNTRY_PARAM = "country";
	public static final String SURVEY_ID_PARAM = "surveyId";

	private String country;
	private String source;
	private Long surveyId;

	@Override
	protected void populateFields(HttpServletRequest req) throws Exception {
		country = req.getParameter(COUNTRY_PARAM);
		source = req.getParameter(SOURCE_PARAM);
		if (req.getParameter(SURVEY_ID_PARAM) != null) {
			try {
				surveyId = new Long(req.getParameter(SURVEY_ID_PARAM).trim());
			} catch (Exception e) {
				addError(new RestError(RestError.BAD_DATATYPE_CODE,
						RestError.BAD_DATATYPE_MESSAGE, SURVEY_ID_PARAM
								+ " must be an integer"));
			}
		}

	}

	@Override
	protected void populateErrors() {
		// no-op

	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getSource() {
		return source;
	}

	public void setSurveyId(Long surveyId) {
		this.surveyId = surveyId;
	}

	public Long getSurveyId() {
		return surveyId;
	}

}
