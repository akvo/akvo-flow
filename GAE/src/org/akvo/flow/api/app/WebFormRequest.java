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
import javax.servlet.http.HttpServletRequest;
import com.gallatinsystems.framework.rest.RestError;
import com.gallatinsystems.framework.rest.RestRequest;

/**
 * data structure for transferring data points
 */
public class WebFormRequest extends RestRequest {
    private static final long serialVersionUID = 1L;
    private static final String ANDROID_ID_PARAM = "androidId";
    private static final String DEVICE_ID_PARAM = "deviceId";

    private String androidId;
    private String deviceId;

    @Override
    protected void populateErrors() {
        // TODO do a better job in error population
        if (androidId == null) {
            addError(new RestError(RestError.MISSING_PARAM_ERROR_CODE, "parameter wrong",
                    "missing androidId"));
        }
        if (deviceId == null) {
            addError(new RestError(RestError.MISSING_PARAM_ERROR_CODE, "parameter wrong",
                    "missing deviceId"));
        }
    }

    @Override
    protected void populateFields(HttpServletRequest req) throws Exception {
        androidId = req.getParameter(ANDROID_ID_PARAM);
        deviceId = req.getParameter(DEVICE_ID_PARAM);
    }

    public String getAndroidId() {
        return androidId;
    }

    public void setAndroidId(String androidId) {
        this.androidId = androidId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

}
