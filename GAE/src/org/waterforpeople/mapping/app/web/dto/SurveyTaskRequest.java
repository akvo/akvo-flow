package org.waterforpeople.mapping.app.web.dto;

import javax.servlet.http.HttpServletRequest;

import com.gallatinsystems.framework.rest.RestError;
import com.gallatinsystems.framework.rest.RestRequest;

public class SurveyTaskRequest extends RestRequest {
	public static final String ID_PARAM = "id";
	public static final String DELETE_SURVEY_ACTION = "deleteSurveyAction";
	public static final String DELETE_QUESTION_GROUP_ACTION = "deleteQuestionGroupAction";
	public static final String DELETE_QUESTION_ACTION = "deleteQuestion";
	public static final String DELETE_QUESTION_HELP_ACTION = "deleteQuestionHelpAction";
	public static final String DELETE_QUESTION_TRANSLATION_ACTION = "deleteQuestionTranslationAction";
	public static final String DELETE_QUESTION_OPTION_ACTION = "deleteQuestionOptionAction";

	private Long id = null;
	/**
	 * 
	 */
	private static final long serialVersionUID = 8374278438245797012L;

	@Override
	protected void populateErrors() {
		if (getAction() == null) {
			String errorMsg = ACTION_PARAM + " is mandatory";
			addError(new RestError(RestError.MISSING_PARAM_ERROR_CODE,
					RestError.MISSING_PARAM_ERROR_MESSAGE, errorMsg));
		}
		if (getId() == null) {
			String errorMsg = "Id is mandatory";
			addError(new RestError(RestError.MISSING_PARAM_ERROR_CODE,
					RestError.MISSING_PARAM_ERROR_MESSAGE, errorMsg));
		}
	}

	@Override
	protected void populateFields(HttpServletRequest req) throws Exception {
		if (req.getParameter(ID_PARAM) != null)
			setId(new Long(req.getParameter(ID_PARAM)));
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

}
