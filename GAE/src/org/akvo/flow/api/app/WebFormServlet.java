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

import com.gallatinsystems.common.util.PropertyUtil;
import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;
import com.google.appengine.api.utils.SystemProperty;

import org.waterforpeople.mapping.app.web.EnvServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;


/**
 * Redirect to tech-consultancy url or show the error
 */
@SuppressWarnings("serial")
public class WebFormServlet extends AbstractRestApiServlet {
    private static final Logger log = Logger.getLogger(WebFormServlet.class.getName());

    public WebFormServlet() {
        setMode(JSON_MODE);
    }

    @Override
    protected RestRequest convertRequest() throws Exception {
        HttpServletRequest req = getRequest();
        RestRequest restRequest = new WebFormRequest();
        restRequest.populateFromHttpRequest(req);
        return restRequest;
    }

    @Override
    protected RestResponse handleRequest(RestRequest req) throws Exception {
        RestResponse res = new RestResponse();
        res.setCode(String.valueOf(HttpServletResponse.SC_OK));
        String surveyId = ((WebFormRequest)req).getSurveyId();
        String url = String.format("%s/%s/%s", PropertyUtil.getProperty(EnvServlet.WEBFORM_REDIRECTION_URL), PropertyUtil.getProperty("alias").split("\\.")[0], surveyId);
        res.setMessage(url);
        return res;
    }

   
    /**
     * writes response as a JSON string
     */
    @Override
    protected void writeOkResponse(RestResponse resp) throws Exception {
        int sc;
        try {
            sc = Integer.valueOf(resp.getCode());
        } catch (NumberFormatException ignored) {
            // Status code was not properly set in the RestResponse
            sc = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        }
        getResponse().setStatus(sc);
        if (sc == HttpServletResponse.SC_OK) {
            HttpServletResponse response = getResponse();
            response.setContentType("text/html");
            response.setStatus(response.SC_MOVED_TEMPORARILY);
            response.setHeader("Location", resp.getMessage());
        } else {
            getResponse().getWriter().println(resp.getMessage());
        }
    }

}
