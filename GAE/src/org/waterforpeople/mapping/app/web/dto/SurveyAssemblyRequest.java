/*
 *  Copyright (C) 2010-2012 Stichting Akvo (Akvo Foundation)
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

import com.gallatinsystems.framework.rest.RestError;
import com.gallatinsystems.framework.rest.RestRequest;

public class SurveyAssemblyRequest extends RestRequest {

    /**
	 * 
	 */
    private static final long serialVersionUID = -4264292935305363469L;

    public static final String ASSEMBLE_SURVEY = "assembleSurvey";
    public static final String DISPATCH_ASSEMBLE_QUESTION_GROUP = "dispatchAssembleQuestionGroup";
    public static final String ASSEMBLE_QUESTION_GROUP = "assembleQuestionGroup";
    public static final String ASSEMBLE_QUESTIONS = "assembleQuestions";
    public static final String ASSEMBLE_SURVEY_FRAGMENTS = "assembleSurveyFragments";
    public static final String DISTRIBUTE_SURVEY = "distributeSurvey";
    public static final String CLEANUP = "cleanup";

    public static final String SURVEY_ID_PARAM = "surveyId";
    public static final String IS_FWD_PARAM = "isFwd";
    private static final String START_ROW_PARAM = "startRow";
    private static final String GROUP_ID_PARAM = "questionGroupId";
    private static final String TRANSACTION_ID_PARAM = "transactionId";

    private static final String LAST_GROUP_FLAG_PARAM = "lastGroupFlag";

    private Long surveyId = null;
    private Boolean lastGroupFlag = null;
    private int startRow = 0;
    private String questionGroupId = null;
    private Long transactionId = null;
    private Boolean isForwarded = false;

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Long getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Long surveyId) {
        this.surveyId = surveyId;
    }

    public int getStartRow() {
        return startRow;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    @Override
    protected void populateErrors() {
        if (surveyId == null) {
            addError(new RestError(RestError.MISSING_PARAM_ERROR_CODE,
                    RestError.MISSING_PARAM_ERROR_MESSAGE, SURVEY_ID_PARAM
                            + " cannot be null"));
        }
    }

    @Override
    protected void populateFields(HttpServletRequest req) throws Exception {
        if (req.getParameter(SURVEY_ID_PARAM) != null)
            surveyId = Long.parseLong(req.getParameter(SURVEY_ID_PARAM));
        if (req.getParameter(GROUP_ID_PARAM) != null)
            questionGroupId = req.getParameter(GROUP_ID_PARAM);
        if (req.getParameter(START_ROW_PARAM) != null)
            startRow = Integer.parseInt(req.getParameter(START_ROW_PARAM));
        if (req.getParameter(LAST_GROUP_FLAG_PARAM) != null)
            setLastGroupFlag(Boolean.parseBoolean(req
                    .getParameter(LAST_GROUP_FLAG_PARAM)));
        if (req.getParameter(TRANSACTION_ID_PARAM) != null)
            setTransactionId(Long.parseLong(req
                    .getParameter(TRANSACTION_ID_PARAM)));
        if (req.getParameter(IS_FWD_PARAM) != null) {
            setIsForwarded(Boolean.parseBoolean(req.getParameter(IS_FWD_PARAM)));
        }
    }

    public void setQuestionGroupId(String questionGroupId) {
        this.questionGroupId = questionGroupId;
    }

    public String getQuestionGroupId() {
        return questionGroupId;
    }

    public void setLastGroupFlag(Boolean lastGroupFlag) {
        this.lastGroupFlag = lastGroupFlag;
    }

    public Boolean getLastGroupFlag() {
        return lastGroupFlag;
    }

    public void setIsForwarded(Boolean isForwarded) {
        this.isForwarded = isForwarded;
    }

    public Boolean getIsForwarded() {
        return isForwarded;
    }

}
