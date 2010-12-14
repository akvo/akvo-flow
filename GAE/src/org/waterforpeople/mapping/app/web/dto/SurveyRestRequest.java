package org.waterforpeople.mapping.app.web.dto;

import javax.servlet.http.HttpServletRequest;

import com.gallatinsystems.framework.rest.RestRequest;

public class SurveyRestRequest extends RestRequest {

	private static final long serialVersionUID = 6876384283589440175L;

	public static final String SAVE_SURVEY_GROUP_ACTION = "saveSurveyGroup";
	public static final String SAVE_SURVEY_ACTION = "saveSurvey";
	public static final String SAVE_QUESTION_GROUP_ACTION = "saveQuestionGroup";
	public static final String SAVE_QUESTION_ACTION = "saveQuestion";
	public static final String LIST_SURVEY_GROUPS_ACTION = "listSurveyGroups";
	public static final String GET_SURVEY_GROUP_ACTION = "getSurveyGroup";
	public static final String LIST_SURVEYS_ACTION = "listSurveys";
	public static final String GET_SURVEY_ACTION = "getSurvey";
	public static final String LIST_GROUP_ACTION = "listGroups";
	public static final String LIST_QUESTION_ACTION = "listQuestions";
	public static final String GET_SUMMARY_ACTION = "getSummary";
	public static final String GET_QUESTION_DETAILS_ACTION = "getQuestionDetails";
	public static final String GET_SURVEY_INSTANCE_ACTION = "getSurveyInstance";
	public static final String DELETE_SURVEY_INSTANCE= "deleteSurveyInstance";
	
	

	private static final String SURVEY_GROUP_NAME_PARAM = "surveyGroupName";
	private static final String SURVEY_NAME_PARAM = "surveyName";
	private static final String QUESTION_GROUP_NAME_PARAM = "questionGroupName";
	private static final String QUESTION_TEXT_PARAM = "questionText";
	private static final String QUESTION_TYPE_PARAM = "questionType";
	private static final String OPTIONS_PARAM = "options";
	private static final String DEPEND_QUESTION_PARAM = "dependQuestion";
	private static final String ALLOW_OTHER_PARAM = "allowOther";
	private static final String ALLOW_MULTIPLE_PARAM = "allowMultiple";
	private static final String MANDATORY_PARAM = "mandatory";
	private static final String QUESTION_GROUP_ORDER_PARAM = "questionGroupOrder";
	public static final String SURVEY_GROUP_ID_PARAM = "surveyGroupId";
	public static final String SURVEY_ID_PARAM = "surveyId";
	public static final String QUESTION_GROUP_ID_PARAM = "questionGroupId";
	public static final String QUESTION_ID_PARAM = "questionID";
	public static final String SCORING_PARAM = "scoring";
	public static final String INSTANCE_PARAM = "instanceId";

	private String surveyGroupName = null;
	private String surveyName = null;
	private Long surveyGroupId = null;
	private String questionGroupName = null;
	private Integer questionId = null;
	private String questionText = null;
	private String questionType = null;
	private String options = null;
	private String dependQuestion = null;
	private Boolean allowOtherFlag = null;
	private Boolean allowMultipleFlag = null;
	private Boolean mandatoryFlag = null;
	private Integer questionGroupOrder = null;
	private Integer surveyId;
	private Integer questionGroupId;
	private String scoring;
	private Long instanceId;

	public Long getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(Long instanceId) {
		this.instanceId = instanceId;
	}

	public String getScoring() {
		return scoring;
	}

	public void setScoring(String scoring) {
		this.scoring = scoring;
	}

	public Integer getSurveyId() {
		return surveyId;
	}

	public void setSurveyId(Integer surveyId) {
		this.surveyId = surveyId;
	}

	public Integer getQuestionGroupId() {
		return questionGroupId;
	}

	public void setQuestionGroupId(Integer questionGruopId) {
		this.questionGroupId = questionGruopId;
	}

	public String getSurveyGroupName() {
		return surveyGroupName;
	}

	public void setSurveyGroupName(String surveyGroupName) {
		this.surveyGroupName = surveyGroupName;
	}

	public String getSurveyName() {
		return surveyName;
	}

	public void setSurveyName(String surveyName) {
		this.surveyName = surveyName;
	}

	public String getQuestionGroupName() {
		return questionGroupName;
	}

	public void setQuestionGroupName(String questionGroupName) {
		this.questionGroupName = questionGroupName;
	}

	public Integer getQuestionId() {
		return questionId;
	}

	public void setQuestionId(Integer questionId) {
		this.questionId = questionId;
	}

	public String getQuestionText() {
		return questionText;
	}

	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}

	public String getQuestionType() {
		return questionType;
	}

	public void setQuestionType(String questionType) {
		this.questionType = questionType;
	}

	public String getOptions() {
		return options;
	}

	public void setOptions(String options) {
		this.options = options;
	}

	public String getDependQuestion() {
		return dependQuestion;
	}

	public void setDependQuestion(String dependQuestion) {
		this.dependQuestion = dependQuestion;
	}

	public Boolean getAllowOtherFlag() {
		return allowOtherFlag;
	}

	public void setAllowOtherFlag(Boolean allowOtherFlag) {
		this.allowOtherFlag = allowOtherFlag;
	}

	public Boolean getAllowMultipleFlag() {
		return allowMultipleFlag;
	}

	public void setAllowMultipleFlag(Boolean allowMultipleFlag) {
		this.allowMultipleFlag = allowMultipleFlag;
	}

	public Boolean getMandatoryFlag() {
		return mandatoryFlag;
	}

	public void setMandatoryFlag(Boolean mandatoryFlag) {
		this.mandatoryFlag = mandatoryFlag;
	}

	@Override
	protected void populateFields(HttpServletRequest req) throws Exception {
		if(req.getParameter(SURVEY_GROUP_ID_PARAM)!=null){
			setSurveyGroupId(Long.parseLong(req.getParameter(SURVEY_GROUP_ID_PARAM)));
		}
		if (req.getParameter(SURVEY_GROUP_NAME_PARAM) != null) {
			surveyGroupName = req.getParameter(SURVEY_GROUP_NAME_PARAM).trim();
		}
		if (req.getParameter(SURVEY_NAME_PARAM) != null) {
			surveyName = req.getParameter(SURVEY_NAME_PARAM).trim();
		}
		if (req.getParameter(QUESTION_GROUP_NAME_PARAM) != null) {
			questionGroupName = req.getParameter(QUESTION_GROUP_NAME_PARAM)
					.trim();
		}
		if (req.getParameter(QUESTION_ID_PARAM) != null) {
			questionId = Integer.parseInt(req.getParameter(QUESTION_ID_PARAM)
					.trim());
		}
		if (req.getParameter(QUESTION_TEXT_PARAM) != null) {
			questionText = req.getParameter(QUESTION_TEXT_PARAM).trim();
		}
		if (req.getParameter(QUESTION_TYPE_PARAM) != null) {
			questionType = req.getParameter(QUESTION_TYPE_PARAM).trim();
		}
		if (req.getParameter(OPTIONS_PARAM) != null) {
			options = req.getParameter(OPTIONS_PARAM).trim();
		}
		if (req.getParameter(DEPEND_QUESTION_PARAM) != null) {
			dependQuestion = req.getParameter(DEPEND_QUESTION_PARAM).trim();
		}
		if (req.getParameter(ALLOW_MULTIPLE_PARAM) != null) {
			allowMultipleFlag = Boolean.parseBoolean(req.getParameter(
					ALLOW_MULTIPLE_PARAM).trim());
		}
		if (req.getParameter(ALLOW_OTHER_PARAM) != null) {
			allowOtherFlag = Boolean.parseBoolean(req.getParameter(
					ALLOW_OTHER_PARAM).trim());
		}
		if (req.getParameter(MANDATORY_PARAM) != null) {
			mandatoryFlag = Boolean.parseBoolean(req.getParameter(
					MANDATORY_PARAM).trim());
		} else {
			mandatoryFlag = false;
		}
		if (req.getParameter(QUESTION_GROUP_ORDER_PARAM) != null) {
			questionGroupOrder = Integer.parseInt(req.getParameter(
					QUESTION_GROUP_ORDER_PARAM).trim());
		}
		if (req.getParameter(SURVEY_ID_PARAM) != null) {
			surveyId = Integer.parseInt(req.getParameter(SURVEY_ID_PARAM)
					.trim());
		}
		if (req.getParameter(QUESTION_GROUP_ID_PARAM) != null) {
			questionGroupId = Integer.parseInt(req.getParameter(
					QUESTION_GROUP_ID_PARAM).trim());
		}
		if (req.getParameter(DEPEND_QUESTION_PARAM) != null) {
			dependQuestion = req.getParameter(DEPEND_QUESTION_PARAM).trim();
		}
		if (req.getParameter(SCORING_PARAM) != null) {
			scoring = req.getParameter(SCORING_PARAM).trim();
			if (scoring != null && scoring.length() == 0) {
				scoring = null;
			}
		}
		if (req.getParameter(INSTANCE_PARAM) != null) {
			instanceId = Long
					.parseLong(req.getParameter(INSTANCE_PARAM).trim());
		}
	}

	@Override
	public void populateErrors() {
		// no-op right now?
	}

	public void setQuestionGroupOrder(Integer questionGroupOrder) {
		this.questionGroupOrder = questionGroupOrder;
	}

	public Integer getQuestionGroupOrder() {
		return questionGroupOrder;
	}

	public void setSurveyGroupId(Long surveyGroupId) {
		this.surveyGroupId = surveyGroupId;
	}

	public Long getSurveyGroupId() {
		return surveyGroupId;
	}

	

}
