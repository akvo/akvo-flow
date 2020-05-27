/*
 *  Copyright (C) 2020 Stichting Akvo (Akvo Foundation)
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

package org.akvo.flow.api.app;

import javax.servlet.http.HttpServletRequest;

import com.gallatinsystems.common.util.PropertyUtil;
import com.gallatinsystems.framework.rest.RestError;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.servlet.RestAuthFilter;
import com.gallatinsystems.survey.domain.WebForm;

import org.akvo.flow.util.OneTimePadCypher;

/**
 * data structure for transferring data points
 */
public class WebFormRequest extends RestRequest {
    private static final long serialVersionUID = 1L;

    private String webFormId;
    private String surveyId;

    @Override
    protected void populateErrors() {
        if (webFormId == null) {
            addError(new RestError(RestError.MISSING_PARAM_ERROR_CODE, "parameter wrong",
                    "missing webformId"));
        }
        if (surveyId == null) {
            addError(new RestError(RestError.MISSING_PARAM_ERROR_CODE, "parameter wrong",
                    "there is not a related surveyId based on current webFormId"));
        }
    }

    @Override
    protected void populateFields(HttpServletRequest req) throws Exception {
        webFormId = req.getPathInfo().substring(1);
        surveyId = OneTimePadCypher.decrypt(PropertyUtil.getProperty(RestAuthFilter.REST_PRIVATE_KEY_PROP), webFormId);

    }

    public String getWebFormId() {
        return webFormId;
    }

    public void setWebFormId(String webFormId) {
        this.webFormId = webFormId;
    }

    public String getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(String surveyId) {
        this.surveyId = surveyId;
    }

}
