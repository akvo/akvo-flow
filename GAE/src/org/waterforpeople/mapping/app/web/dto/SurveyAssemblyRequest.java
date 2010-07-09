package org.waterforpeople.mapping.app.web.dto;

import javax.servlet.http.HttpServletRequest;

import com.gallatinsystems.framework.rest.RestRequest;

public class SurveyAssemblyRequest extends RestRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4264292935305363469L;

	public static final String ASSEMBLE_SURVEY = "assembleSurvey";
	public static final String ASSEMBLE_QUESTION_GROUP = "assembleQuestionGroup";
	public static final String ASSEMBLE_SURVEY_FRAGMENTS = "assembleSurveyFragments";

	private static final String SURVEY_ID_PARAM = "surveyId";
	private static final String TYPE_PARAM = "type";
	private static final String START_ROW_PARAM = "startRow";
	private static final String GROUP_ID_PARAM = "questionGroupId";
	private static final String ST_PARAM = "sessionToken";

	private Long surveyId = null;

	public Long getSurveyId() {
		return surveyId;
	}

	public void setSurveyId(Long surveyId) {
		this.surveyId = surveyId;
	}

	private int startRow = 0;

	public int getStartRow() {
		return startRow;
	}

	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}

	private Long questionGroupId = null;

	@Override
	protected void populateErrors() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void populateFields(HttpServletRequest req) throws Exception {
		// TODO Auto-generated method stub

	}

	public void setQuestionGroupId(Long questionGroupId) {
		this.questionGroupId = questionGroupId;
	}

	public Long getQuestionGroupId() {
		return questionGroupId;
	}

}
