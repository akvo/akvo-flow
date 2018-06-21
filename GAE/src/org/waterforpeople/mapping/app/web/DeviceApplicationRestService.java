/*
 *  Copyright (C) 2010-2012,2018 Stichting Akvo (Akvo Foundation)
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

import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;
import org.json.JSONObject;
import org.waterforpeople.mapping.app.web.dto.DeviceApplicationRestRequest;
import org.waterforpeople.mapping.app.web.dto.DeviceApplicationRestResponse;
import org.waterforpeople.mapping.dao.DeviceApplicationDao;
import org.waterforpeople.mapping.domain.DeviceApplication;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Properties;

/**
 * restful service for DeviceApplications
 *
 * @author Christopher Fagiani
 */
public class DeviceApplicationRestService extends AbstractRestApiServlet {

    private static final long serialVersionUID = -830140106880504436L;
    private static final int MINIMUM_SUPPORTED_ANDROID_VERSION = 15;
    private DeviceApplicationDao devAppDao;

    public DeviceApplicationRestService() {
        setMode(JSON_MODE);
        devAppDao = new DeviceApplicationDao();
    }

    @Override
    protected RestRequest convertRequest() throws Exception {
        HttpServletRequest req = getRequest();
        RestRequest restRequest = new DeviceApplicationRestRequest();
        restRequest.populateFromHttpRequest(req);
        return restRequest;
    }

    @Override
    protected RestResponse handleRequest(RestRequest req) throws Exception {
        DeviceApplicationRestResponse resp = new DeviceApplicationRestResponse();
        Properties props = System.getProperties();
        String autoUpdateApk = props.getProperty("autoUpdateApk");
        if (DeviceApplicationRestRequest.GET_LATEST_VERSION_ACTION
                .equalsIgnoreCase(req.getAction()) && autoUpdateApk != null
                && autoUpdateApk.equalsIgnoreCase("true")) {

            DeviceApplicationRestRequest daReq = (DeviceApplicationRestRequest) req;
            String androidBuildVersion = daReq.getAndroidBuildVersion();
            if (androidBuildVersion == null || parseBuildVersion(
                    androidBuildVersion) < MINIMUM_SUPPORTED_ANDROID_VERSION) {
                DeviceApplication devApp = devAppDao
                        .listAppVersionForUnsupportedDevices(daReq.getDeviceType(),
                                daReq.getAppCode());
                if (devApp != null) {
                    resp.setVersion(devApp.getVersion());
                    resp.setFileName(devApp.getFileName());
                    resp.setMd5Checksum(devApp.getMd5Checksum());
                }
            } else {
                List<DeviceApplication> devAppList = devAppDao
                        .listByDeviceTypeAndAppCode(daReq.getDeviceType(),
                                daReq.getAppCode(), 1);
                if (devAppList != null && devAppList.size() > 0) {
                    DeviceApplication deviceApplication = devAppList.get(0);
                    resp.setVersion(deviceApplication.getVersion());
                    resp.setFileName(deviceApplication.getFileName());
                    resp.setMd5Checksum(deviceApplication.getMd5Checksum());
                }
            }
        }
        return resp;
    }

    private int parseBuildVersion(String androidBuildVersion) {
        try {
            return Integer.parseInt(androidBuildVersion);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    protected void writeOkResponse(RestResponse resp) throws Exception {
        getResponse().setStatus(200);
        getResponse().getWriter().println(new JSONObject(resp).toString());
    }

}
