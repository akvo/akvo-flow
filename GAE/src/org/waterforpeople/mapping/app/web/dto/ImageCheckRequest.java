/*
 *  Copyright (C) 2014 Stichting Akvo (Akvo Foundation)
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

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;

import com.gallatinsystems.framework.rest.RestRequest;

public class ImageCheckRequest extends RestRequest {

    private static final long serialVersionUID = 5103416013238572162L;

    public static final String FILENAME_PARAM = "fileName";
    public static final String ATTEMPT_PARAM = "attempt";
    public static final String QAS_ID_PARAM = "qasId";
    public static final String DEVICE_ID_PARAM = "deviceId";

    String fileName;
    Integer attempt;
    Long qasId;
    Long deviceId;

    public String getFileName() {
        return fileName;
    }

    public Integer getAttempt() {
        return attempt;
    }

    public Long getQasId() {
        return qasId;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    @Override
    protected void populateFields(HttpServletRequest req) throws Exception {
        fileName = req.getParameter(FILENAME_PARAM);
        attempt = Integer.valueOf(req.getParameter(ATTEMPT_PARAM));
        qasId = Long.valueOf(req.getParameter(QAS_ID_PARAM));

        String deviceIdParam = req.getParameter(DEVICE_ID_PARAM);
        if (deviceIdParam != null && !"null".equalsIgnoreCase(deviceIdParam)) {
            deviceId = Long.valueOf(deviceIdParam);
        }
    }

    @Override
    protected void populateErrors() {
    }

    @Override
    public String toString() {
        return new JSONObject(this).toString();
    }

}
