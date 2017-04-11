/*
 *  Copyright (C) 2010-2017 Stichting Akvo (Akvo Foundation)
 *
 *  This file is part of Akvo FLOW.
 *
 *  Akvo FLOW is free software: you can redistribute it and modify it under the terms of
 *  the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 *  either version 3 of the License or any later version.
 *
 *  Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Affero General Public License included below for more details.
 *
 *  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */

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
    public static final String LIST_QUESTION_GROUP_ACTION = "listQuestionGroups";
    public static final String LIST_QUESTION_ACTION = "listQuestions";
    public static final String LIST_SURVEY_QUESTIONS_ACTION = "listSurveyQuestions";
    public static final String LIST_QUESTION_OPTIONS_ACTION = "listQuestionOptions";
    public static final String LIST_SURVEY_QUESTION_OPTIONS_ACTION = "listSurveyQuestionOptions";
    public static final String GET_SUMMARY_ACTION = "getSummary";
    public static final String GET_QUESTION_DETAILS_ACTION = "getQuestionDetails";
    public static final String GET_SURVEY_INSTANCE_ACTION = "getSurveyInstance";
    public static final String DELETE_SURVEY_INSTANCE = "deleteSurveyInstance";
    public static final String GET_GRAPH_ACTION = "getGraph";
    public static final String UPDATE_QUESTION_ORDER_ACTION = "updateQuestionOrder";

    public static final String SURVEY_GROUP_NAME_PARAM = "surveyGroupName";
    public static final String SURVEY_NAME_PARAM = "surveyName";
    public static final String QUESTION_GROUP_NAME_PARAM = "questionGroupName";
    public static final String QUESTION_TEXT_PARAM = "questionText";
    public static final String QUESTION_TYPE_PARAM = "questionType";
    public static final String OPTIONS_PARAM = "options";
    public static final String DEPEND_QUESTION_PARAM = "dependQuestion";
    public static final String ALLOW_OTHER_PARAM = "allowOther";
    public static final String ALLOW_MULTIPLE_PARAM = "allowMultiple";
    public static final String MANDATORY_PARAM = "mandatory";
    public static final String QUESTION_GROUP_ORDER_PARAM = "questionGroupOrder";
    public static final String QUESTION_ORDER_PARAM = "questionOrder";
    public static final String SURVEY_GROUP_ID_PARAM = "surveyGroupId";
    public static final String SURVEY_ID_PARAM = "surveyId";
    public static final String QUESTION_GROUP_ID_PARAM = "questionGroupId";
    public static final String QUESTION_ID_PARAM = "questionID";
    public static final String QUESTION_ID_PARAM_ALT = "questionId";
    public static final String SCORING_PARAM = "scoring";
    public static final String VALIDATION_MIN_PARAM = "minVal";
    public static final String VALIDATION_MAX_PARAM = "maxVal";
    public static final String VALIDATION_ALLOW_SIGN_PARAM = "allowSign";
    public static final String VALIDATION_ALLOW_DECIMAL_PARAM = "allowDecimal";
    public static final String VALIDATION_IS_NAME_PARAM = "isName";
    public static final String INSTANCE_PARAM = "instanceId";
    public static final String GRAPH_TYPE_PARAM = "graphType";
    public static final String METRIC_NAME_PARAM = "metricName";
    public static final String METRIC_GROUP_PARAM = "metricGroup";

    private String surveyGroupName;
    private String surveyName;
    private Long surveyGroupId;
    private String questionGroupName;
    private Long questionId;
    private String questionText;
    private String questionType;
    private String options;
    private String dependQuestion;
    private Boolean allowOtherFlag;
    private Boolean allowMultipleFlag;
    private Boolean mandatoryFlag;
    private Integer questionGroupOrder;
    private Long surveyId;
    private Long questionGroupId;
    private String scoring;
    private Long instanceId;
    private String graphType;
    private Integer questionOrder;
    private Boolean isName;
    private Boolean allowSign;
    private Boolean allowDecimal;
    private Double minVal;
    private Double maxVal;
    private String metricName;
    private String metricGroup;

    public Boolean getIsName() {
        return isName;
    }

    public void setIsName(Boolean isName) {
        this.isName = isName;
    }

    public Boolean getAllowSign() {
        return allowSign;
    }

    public void setAllowSign(Boolean allowSign) {
        this.allowSign = allowSign;
    }

    public Boolean getAllowDecimal() {
        return allowDecimal;
    }

    public void setAllowDecimal(Boolean allowDecimal) {
        this.allowDecimal = allowDecimal;
    }

    public Double getMinVal() {
        return minVal;
    }

    public void setMinVal(Double minVal) {
        this.minVal = minVal;
    }

    public Double getMaxVal() {
        return maxVal;
    }

    public void setMaxVal(Double maxVal) {
        this.maxVal = maxVal;
    }

    public String getGraphType() {
        return graphType;
    }

    public void setGraphType(String graphType) {
        this.graphType = graphType;
    }

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

    public Long getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Long surveyId) {
        this.surveyId = surveyId;
    }

    public Long getQuestionGroupId() {
        return questionGroupId;
    }

    public void setQuestionGroupId(Long questionGruopId) {
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

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
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
        if (req.getParameter(SURVEY_GROUP_ID_PARAM) != null) {
            setSurveyGroupId(Long.parseLong(req
                    .getParameter(SURVEY_GROUP_ID_PARAM)));
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
            questionId = Long.parseLong(req.getParameter(QUESTION_ID_PARAM)
                    .trim());
        }
        if (req.getParameter(QUESTION_ID_PARAM_ALT) != null) {
            questionId = Long.parseLong(req.getParameter(
                    QUESTION_ID_PARAM_ALT).trim());
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
        mandatoryFlag = parseBooleanValue(req.getParameter(MANDATORY_PARAM),
                false);

        if (req.getParameter(QUESTION_GROUP_ORDER_PARAM) != null) {
            questionGroupOrder = Integer.parseInt(req.getParameter(
                    QUESTION_GROUP_ORDER_PARAM).trim());
        }
        if (req.getParameter(QUESTION_ORDER_PARAM) != null) {
            setQuestionOrder(Integer.parseInt(req.getParameter(
                    QUESTION_ORDER_PARAM).trim()));
        }
        if (req.getParameter(SURVEY_ID_PARAM) != null) {
            surveyId = Long.parseLong(req.getParameter(SURVEY_ID_PARAM)
                    .trim());
        }
        if (req.getParameter(QUESTION_GROUP_ID_PARAM) != null) {
            questionGroupId = Long.parseLong(req.getParameter(
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
        if (req.getParameter(GRAPH_TYPE_PARAM) != null) {
            graphType = req.getParameter(GRAPH_TYPE_PARAM).trim();
        }
        metricName = req.getParameter(METRIC_NAME_PARAM);
        metricGroup = req.getParameter(METRIC_GROUP_PARAM);

        allowDecimal = parseBooleanValue(
                req.getParameter(VALIDATION_ALLOW_DECIMAL_PARAM), null);
        allowSign = parseBooleanValue(
                req.getParameter(VALIDATION_ALLOW_SIGN_PARAM), null);
        isName = parseBooleanValue(req.getParameter(VALIDATION_IS_NAME_PARAM),
                null);
        minVal = parseDoubleVal(req.getParameter(VALIDATION_MIN_PARAM));
        maxVal = parseDoubleVal(req.getParameter(VALIDATION_MAX_PARAM));

    }

    private Double parseDoubleVal(String val) {
        if (val != null && val.trim().length() > 0) {
            return new Double(val.trim());
        } else {
            return null;
        }
    }

    private Boolean parseBooleanValue(String val, Boolean defaultVal) {
        if (val != null && val.trim().length() > 0) {
            return new Boolean(val.trim());
        } else {
            return defaultVal;
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

    public void setQuestionOrder(Integer questionOrder) {
        this.questionOrder = questionOrder;
    }

    public Integer getQuestionOrder() {
        return questionOrder;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public String getMetricName() {
        return metricName;
    }

    public void setMetricGroup(String metricGroup) {
        this.metricGroup = metricGroup;
    }

    public String getMetricGroup() {
        return metricGroup;
    }

}
