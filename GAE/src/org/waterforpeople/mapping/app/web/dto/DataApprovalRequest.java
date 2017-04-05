/*
 *  Copyright (C) 2017 Stichting Akvo (Akvo Foundation)
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

public class DataApprovalRequest extends RestRequest {

    private static final long serialVersionUID = -4113676890201652313L;
    private static final Logger log = Logger.getLogger(DataApprovalRequest.class.getSimpleName());
    public static final String RETRIEVE_APPROVAL_STEPS_ACTION = "retrieveApprovalSteps";
    public static final String APPROVAL_GROUP_ID_PARAM = "approvalGroupId";
    public Long approvalGroupId;

    @Override
    protected void populateFields(HttpServletRequest req) throws Exception {
        if (req.getParameter(APPROVAL_GROUP_ID_PARAM) != null) {
            try {
                approvalGroupId = Long.parseLong(req.getParameter(APPROVAL_GROUP_ID_PARAM).trim());
            } catch (NumberFormatException e) {
                log.warning(APPROVAL_GROUP_ID_PARAM + " param is missing");
            }
        }
    }

    @Override
    protected void populateErrors() {
        // no need to throw any exceptions currently
    }

}
