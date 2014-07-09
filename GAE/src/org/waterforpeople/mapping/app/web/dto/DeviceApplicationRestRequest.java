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

package org.waterforpeople.mapping.app.web.dto;

import javax.servlet.http.HttpServletRequest;

import com.gallatinsystems.framework.rest.RestError;
import com.gallatinsystems.framework.rest.RestRequest;

/**
 * encapsulates request to the devapp service
 * 
 * @author Christopher Fagiani
 */
public class DeviceApplicationRestRequest extends RestRequest {

    private static final long serialVersionUID = -158448412036367889L;

    public static final String GET_LATEST_VERSION_ACTION = "getLatestVersion";
    public static final String DEV_TYPE_PARAM = "deviceType";
    public static final String APP_CODE_PARAM = "appCode";

    private String deviceType;
    private String appCode;

    @Override
    protected void populateFields(HttpServletRequest req) throws Exception {
        deviceType = req.getParameter(DEV_TYPE_PARAM);
        appCode = req.getParameter(APP_CODE_PARAM);
    }

    @Override
    protected void populateErrors() {
        if (deviceType == null) {
            addError(new RestError(RestError.MISSING_PARAM_ERROR_CODE,
                    RestError.MISSING_PARAM_ERROR_MESSAGE, DEV_TYPE_PARAM
                            + " cannot be null"));
        }
        if (appCode == null) {
            addError(new RestError(RestError.MISSING_PARAM_ERROR_CODE,
                    RestError.MISSING_PARAM_ERROR_MESSAGE, APP_CODE_PARAM
                            + " cannot be null"));
        }
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

}
