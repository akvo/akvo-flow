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

import javax.servlet.http.HttpServletRequest;

import com.gallatinsystems.framework.rest.RestError;
import com.gallatinsystems.framework.rest.RestRequest;

/**
 * Encapsulates requests to the notification task queue
 * 
 * @author Christopher Fagiani
 */
public class NotificationRequest extends RestRequest {

    private static final long serialVersionUID = 5751114948240808962L;
    public static final String DELIMITER = "||";
    public static final String DEST_PARAM = "destinations";
    public static final String SUB_ENTITY_PARAM = "subEntityId";
    public static final String NOTIF_ENTITY_PARAM = "notifEntityId";
    public static final String TYPE_PARAM = "type";
    public static final String METHOD_PARAM = "method";
    public static final String DEST_OPT_PARAM = "destOptions";

    private String destinations;
    private Long subEntityId;
    private Long notifEntityId;
    private String type;
    private String method;
    private String destOptions;

    public String getDestOptions() {
        return destOptions;
    }

    public void setDestOptions(String destOptions) {
        this.destOptions = destOptions;
    }

    public String getDestinations() {
        return destinations;
    }

    public void setDestinations(String destinations) {
        this.destinations = destinations;
    }

    public Long getSubEntityId() {
        return subEntityId;
    }

    public void setSubEntityId(Long entityId) {
        this.subEntityId = entityId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    protected void populateErrors() {
        if (subEntityId == null) {
            addError(new RestError(RestError.MISSING_PARAM_ERROR_CODE,
                    RestError.MISSING_PARAM_ERROR_MESSAGE, SUB_ENTITY_PARAM
                            + " is mandatory"));
        } else if (type == null || type.length() == 0) {
            addError(new RestError(RestError.MISSING_PARAM_ERROR_CODE,
                    RestError.MISSING_PARAM_ERROR_MESSAGE, TYPE_PARAM
                            + " is mandatory"));
        }
    }

    @Override
    protected void populateFields(HttpServletRequest req) throws Exception {
        if (req.getParameter(SUB_ENTITY_PARAM) != null) {
            try {
                subEntityId = Long
                        .parseLong(req.getParameter(SUB_ENTITY_PARAM));
            } catch (NumberFormatException e) {
                // no-op
            }
        }
        if (req.getParameter(NOTIF_ENTITY_PARAM) != null) {
            try {
                notifEntityId = Long.parseLong(req
                        .getParameter(NOTIF_ENTITY_PARAM));
            } catch (NumberFormatException e) {
                // no-op
            }
        }
        if (req.getParameter(TYPE_PARAM) != null) {
            type = req.getParameter(TYPE_PARAM).trim();
        }
        if (notifEntityId == null) {
            notifEntityId = subEntityId;
        }
        destinations = req.getParameter(DEST_PARAM);
        destOptions = req.getParameter(DEST_OPT_PARAM);
        method = req.getParameter(METHOD_PARAM);

    }

    public void setNotifEntityId(Long notifEntityId) {
        this.notifEntityId = notifEntityId;
    }

    public Long getNotifEntityId() {
        return notifEntityId;
    }
}
