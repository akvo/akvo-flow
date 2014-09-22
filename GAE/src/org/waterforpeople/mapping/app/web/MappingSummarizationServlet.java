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

package org.waterforpeople.mapping.app.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.waterforpeople.mapping.app.web.dto.MappingSummarizationRequest;
import org.waterforpeople.mapping.app.web.dto.MappingSummarizationResponse;
import org.waterforpeople.mapping.helper.MappingSummarizationHelper;

import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;

/**
 * Servlet that will process summarization API requests
 * 
 * @author Christopher Fagiani
 */
public class MappingSummarizationServlet extends AbstractRestApiServlet {

    private static final long serialVersionUID = -3781006669225793054L;
    private MappingSummarizationHelper helper;

    public MappingSummarizationServlet() {
        helper = new MappingSummarizationHelper();
    }

    /**
     * converts the incoming servlet request into a MappingSummarizationRequest
     */
    @Override
    @SuppressWarnings("unused")
    protected RestRequest convertRequest() throws Exception {
        HttpServletRequest req = getRequest();
        String action = req.getParameter(RestRequest.ACTION_PARAM);
        RestRequest restRequest = new MappingSummarizationRequest();
        restRequest.populateFromHttpRequest(req);
        return restRequest;
    }

    /**
     * looks at the action in the request object and dispatches handling to the appropriate method.
     */
    @Override
    protected RestResponse handleRequest(RestRequest request) throws Exception {
        MappingSummarizationResponse response = new MappingSummarizationResponse();
        MappingSummarizationRequest summReq = (MappingSummarizationRequest) request;
        String result = helper.processSummarization(summReq.getRegionUUID(),
                summReq.getSummarizationType());
        response.setColorCode(result);
        return response;
    }

    /**
     * write the response to the servlet response object
     */
    @Override
    protected void writeOkResponse(RestResponse restResponse) throws Exception {
        HttpServletResponse resp = getResponse();
        // for now, just output the value
        String val = ((MappingSummarizationResponse) restResponse)
                .getColorCode();
        if (val != null) {
            resp.getWriter().print("The status color is: " + val);
        } else {
            resp.getWriter().print("NO DATA");
        }
    }
}
