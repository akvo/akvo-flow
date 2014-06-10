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

import com.gallatinsystems.framework.rest.RestRequest;

public class DeviceFileRestRequest extends RestRequest {

    /**
	 * 
	 */
    private static final long serialVersionUID = -1363635676106322333L;
    public static final String LIST_DEVICE_FILES_ACTION = "listDeviceFiles";
    public static final String FIND_DEVICE_FILE_ACTION = "findDeviceFile";
    public static final String CURSOR_PARAM = "cursor";
    public static final String PROCESSED_STATUS_PARAM = "processedStatus";
    public static final String DEVICE_FULL_PATH = "deviceFullPath";

    public String getCursor() {
        return cursor;
    }

    public void setCursor(String cursor) {
        this.cursor = cursor;
    }

    public String getProcessedStatus() {
        return processedStatus;
    }

    public void setProcessedStatus(String status) {
        this.processedStatus = status;
    }

    private String cursor = null;
    private String processedStatus = null;
    private String deviceFullPath = null;

    @Override
    protected void populateFields(HttpServletRequest req) throws Exception {
        if (req.getParameter(CURSOR_PARAM) != null) {
            setCursor(req.getParameter(CURSOR_PARAM));
        }
        if (req.getParameter(PROCESSED_STATUS_PARAM) != null) {
            setProcessedStatus(req.getParameter(PROCESSED_STATUS_PARAM));
        }
        if (req.getParameter(DEVICE_FULL_PATH) != null) {
            setDeviceFullPath(req.getParameter(DEVICE_FULL_PATH));
        }

    }

    @Override
    protected void populateErrors() {
        // TODO Auto-generated method stub

    }

    public void setDeviceFullPath(String deviceFullPath) {
        this.deviceFullPath = deviceFullPath;
    }

    public String getDeviceFullPath() {
        return deviceFullPath;
    }

}
