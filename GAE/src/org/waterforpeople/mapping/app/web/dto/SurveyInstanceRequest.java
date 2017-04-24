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

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.gallatinsystems.framework.rest.RestRequest;

public class SurveyInstanceRequest extends RestRequest {

    private static final Logger log = Logger.getLogger(SurveyInstanceRequest.class.getName());
    private static final long serialVersionUID = 6642806619258697157L;
    private static final String FIELD_NAME_PARAM = "fieldName";
    private static final String VALUE_NAME_PARAM = "value";
    public static final String GET_INSTANCE_DATA_ACTION = "getInstanceData";
    public static final String SURVEY_INSTANCE_ID_PARAM = "surveyInstanceId";

    private String fieldName = null;
    public Long surveyInstanceId;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    private String value = null;

    @Override
    protected void populateFields(HttpServletRequest req) throws Exception {
        if (req.getParameter(FIELD_NAME_PARAM) != null) {
            setFieldName(req.getParameter(FIELD_NAME_PARAM));
        }
        if (req.getParameter(VALUE_NAME_PARAM) != null) {
            setValue(req.getParameter(VALUE_NAME_PARAM));
        }
        if (req.getParameter(SURVEY_INSTANCE_ID_PARAM) != null) {
            try {
                this.surveyInstanceId = Long.parseLong(req.getParameter(SURVEY_INSTANCE_ID_PARAM));
            } catch (NumberFormatException e) {
                log.warning("Could not convert survey instance id: " + surveyInstanceId);
            }
        }
    }

    @Override
    protected void populateErrors() {
        // TODO Auto-generated method stub

    }

}
