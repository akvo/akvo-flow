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
import com.gallatinsystems.survey.dao.DeviceSurveyJobQueueDAO;

public class SurveyTaskRequest extends RestRequest {
    public static final String ID_PARAM = "id";
    public static final String ID_LIST_PARAM = "idList";
    public static final String DELETE_SURVEY_ACTION = "deleteSurvey";
    public static final String DELETE_QUESTION_GROUP_ACTION = "deleteQuestionGroup";
    public static final String DELETE_QUESTION_ACTION = "deleteQuestion";
    public static final String DELETE_QUESTION_HELP_ACTION = "deleteQuestionHelp";
    public static final String DELETE_QUESTION_TRANSLATION_ACTION = "deleteQuestionTranslation";
    public static final String DELETE_QUESTION_OPTION_ACTION = "deleteQuestionOptions";
    public static final String DELETE_DSJQ_ACTION = "deleteDeviceSurveyJobQueue";
    public static final String DELETE_DFJQ_ACTION = "deleteDeviceFileJobQueue";
    public static final String REMAP_SURVEY_INSTANCE = "reprocessMapSurveyInstance";

    private static final long serialVersionUID = 8374278438245797012L;

    private Long id;
    private String idList;

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
        if (req.getParameter(ID_PARAM) != null) {
            setId(new Long(req.getParameter(ID_PARAM)));
        }
        idList = req.getParameter(ID_LIST_PARAM);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getIdList() {
        return idList;
    }

    public void setIdList(String idList) {
        this.idList = idList;
    }

}
