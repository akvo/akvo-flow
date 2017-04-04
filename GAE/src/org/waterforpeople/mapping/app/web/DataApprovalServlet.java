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

package org.waterforpeople.mapping.app.web;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.waterforpeople.mapping.app.web.dto.DataApprovalRequest;

import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;

public class DataApprovalServlet extends AbstractRestApiServlet {

    private static final long serialVersionUID = 1706847643104220714L;
    private static final Logger log = Logger.getLogger(DataApprovalServlet.class.getSimpleName());

    public DataApprovalServlet() {
        setMode(JSON_MODE);
    }

    @Override
    protected RestRequest convertRequest() throws Exception {
        HttpServletRequest req = super.getRequest();
        RestRequest restRequest = new DataApprovalRequest();
        restRequest.populateFromHttpRequest(req);
        return restRequest;
    }

    @Override
    protected RestResponse handleRequest(RestRequest req) throws Exception {
        return null;
    }

    @Override
    protected void writeOkResponse(RestResponse resp) throws Exception {
    }

}
