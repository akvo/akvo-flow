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

package com.gallatinsystems.notification;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;

/**
 * Receives task queue work items and calls the appropriate handler based on the type (implemented
 * in sub classes)
 * 
 * @author Christopher Fagiani
 */
public abstract class NotificationProcessor extends AbstractRestApiServlet {
    private static final long serialVersionUID = -4726303161585277346L;

    protected Map<String, String> notificationTypeMap;

    public NotificationProcessor() {
        super();
        notificationTypeMap = new HashMap<String, String>();
        initializeTypeMapping();
    }

    /**
     * subclasses should implement this method in such a way that maps a given notificationType to
     * an instance of the appropriate NotificationHelper.
     */
    protected abstract void initializeTypeMapping();

    /**
     * converts the servlet request into an instance of NotificationRequest
     */
    @Override
    protected RestRequest convertRequest() throws Exception {
        HttpServletRequest req = getRequest();
        NotificationRequest restRequest = new NotificationRequest();
        restRequest.populateFromHttpRequest(req);
        return restRequest;
    }

    /**
     * looks up the appropriate notification handler in the type map (using the Type passed in on
     * the request) and invokes it
     */
    @SuppressWarnings("rawtypes")
    @Override
    protected RestResponse handleRequest(RestRequest request) throws Exception {
        RestResponse response = new RestResponse();
        NotificationRequest notificationRequest = (NotificationRequest) request;
        String className = notificationTypeMap.get(notificationRequest
                .getType());
        if (className != null) {
            Class cls = Class.forName(className);
            NotificationHandler handler = (NotificationHandler) cls
                    .newInstance();
            HttpServletRequest req = getRequest();
            String serverBase = req.getScheme()
                    + "://"
                    + req.getServerName()
                    + ((req.getLocalPort() != 80 && req.getLocalPort() != 443 && req
                            .getLocalPort() != 0) ? ":" + req.getLocalPort()
                            : "");
            handler.generateNotification(notificationRequest.getType(),
                    notificationRequest.getNotifEntityId(),
                    notificationRequest.getDestinations(),
                    notificationRequest.getDestOptions(), serverBase);
        } else {
            log("Could not find notification handler for type: "
                    + notificationRequest.getType());
        }
        return response;
    }
}
