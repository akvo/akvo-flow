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

/**
 * Request object for the SurveyEventHandlerServelt
 * 
 * @author Christopher Fagiani
 */
public class SurveyEventRequest extends RestRequest {

    private static final long serialVersionUID = 8520104533378053118L;
    public static final String SURVEY_ID_PARAM = "surveyId";
    public static final String SURVEY_INSTANCE_ID_PARAM = "surveyInstanceId";
    public static final String EVENT_TYPE_PARAM = "eventType";
    public static final String FIRE_EVENT_ACTION = "fireEvent";

    private Long surveyId;
    private Long surveyInstanceId;
    private String eventType;

    @Override
    protected void populateFields(HttpServletRequest req) throws Exception {
        eventType = req.getParameter(EVENT_TYPE_PARAM);
        if (req.getParameter(SURVEY_ID_PARAM) != null) {
            try {
                surveyId = Long.parseLong(req.getParameter(SURVEY_ID_PARAM));
            } catch (Exception e) {
                addError(new RestError(RestError.BAD_DATATYPE_CODE,
                        RestError.BAD_DATATYPE_MESSAGE, SURVEY_ID_PARAM
                                + " must be an integer"));
            }
        }
        if (req.getParameter(SURVEY_INSTANCE_ID_PARAM) != null) {
            try {
                surveyInstanceId = Long.parseLong(req
                        .getParameter(SURVEY_INSTANCE_ID_PARAM));
            } catch (Exception e) {
                addError(new RestError(RestError.BAD_DATATYPE_CODE,
                        RestError.BAD_DATATYPE_MESSAGE,
                        SURVEY_INSTANCE_ID_PARAM + " must be an integer"));
            }
        }
    }

    @Override
    protected void populateErrors() {
        if (eventType == null) {
            addError(new RestError(RestError.MISSING_PARAM_ERROR_CODE,
                    RestError.MISSING_PARAM_ERROR_MESSAGE, EVENT_TYPE_PARAM
                            + " is mandatory"));
        }
    }

    public void setSurveyId(Long surveyId) {
        this.surveyId = surveyId;
    }

    public Long getSurveyId() {
        return surveyId;
    }

    public void setSurveyInstanceId(Long surveyInstanceId) {
        this.surveyInstanceId = surveyInstanceId;
    }

    public Long getSurveyInstanceId() {
        return surveyInstanceId;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventType() {
        return eventType;
    }

}
