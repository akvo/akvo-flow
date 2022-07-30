/*
 *  Copyright (C) 2022 Stichting Akvo (Akvo Foundation)
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

import com.gallatinsystems.framework.rest.RestError;
import com.gallatinsystems.framework.rest.RestRequest;

import javax.servlet.http.HttpServletRequest;

public class PostUserRegistrationRestRequest extends RestRequest {
    public static final String EMAIL_PARAM = "email";

    private String email;

    private String userName;

    @Override
    protected void populateFields(HttpServletRequest req) throws Exception {
        if (req.getParameter(EMAIL_PARAM) != null) {
            setEmail(req.getParameter(EMAIL_PARAM));
            setUserName(this.email.substring(0, this.email.indexOf("@")));
        }
    }

    @Override
    protected void populateErrors() {
        if (getEmail() == null) {
            String errorMsg = EMAIL_PARAM + " is mandatory";
            addError(new RestError(RestError.MISSING_PARAM_ERROR_CODE,
                    RestError.MISSING_PARAM_ERROR_MESSAGE, errorMsg));
        }
     }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getUserName() { return userName; }

    public void setUserName(String userName) { this.userName = userName; }
}
