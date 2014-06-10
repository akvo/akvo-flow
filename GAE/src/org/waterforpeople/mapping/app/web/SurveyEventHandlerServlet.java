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

import org.waterforpeople.mapping.app.web.dto.SurveyEventRequest;

import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;
import com.gallatinsystems.notification.helper.NotificationHelper;

/**
 * Servlet that can handle responding to survey events (approval, submission).
 * 
 * @author Christopher Fagiani
 */
public class SurveyEventHandlerServlet extends AbstractRestApiServlet {

    private static final long serialVersionUID = -8115326670034354961L;

    @Override
    protected RestRequest convertRequest() throws Exception {
        HttpServletRequest req = getRequest();
        RestRequest restRequest = new SurveyEventRequest();
        restRequest.populateFromHttpRequest(req);
        return restRequest;
    }

    @Override
    protected RestResponse handleRequest(RestRequest req) throws Exception {
        SurveyEventRequest eventReq = (SurveyEventRequest) req;
        RestResponse response = new RestResponse();
        // if we have other ways of handling an event, will probably want to
        // refactor this to look at the event type but for now, we just send
        // notifications
        generateNotifications(eventReq.getEventType(), eventReq.getSurveyId(),
                eventReq.getSurveyInstanceId());

        return response;
    }

    private void generateNotifications(String type, Long surveyId,
            Long surveyInstanceId) {
        NotificationHelper helper = new NotificationHelper(type, surveyId,
                surveyInstanceId);
        helper.execute();
    }

    @Override
    protected void writeOkResponse(RestResponse resp) throws Exception {
        getResponse().setStatus(200);
    }

}
