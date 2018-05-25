/*
 *  Copyright (C) 2018 Stichting Akvo (Akvo Foundation)
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

package org.akvo.flow.rest.dto;

import javax.servlet.http.HttpServletRequest;

import com.gallatinsystems.framework.rest.RestError;
import com.gallatinsystems.framework.rest.RestRequest;

public class ReportTaskRequest extends RestRequest {
    public static final String ID_PARAM = "id";
    public static final String START_ACTION = "start";

    private static final long serialVersionUID = 8374279438245797012L;

    private Long id;

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
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }


}
