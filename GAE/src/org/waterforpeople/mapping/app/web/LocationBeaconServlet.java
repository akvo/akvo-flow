/*
 *  Copyright (C) 2010-2016 Stichting Akvo (Akvo Foundation)
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

import org.waterforpeople.mapping.app.web.dto.LocationBeaconRequest;

import com.gallatinsystems.device.dao.DeviceDAO;
import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;

/**
 * Simple service that can capture Location Beacons and update device records with their last-known
 * position
 * 
 * @author Christopher Fagiani
 */
public class LocationBeaconServlet extends AbstractRestApiServlet {

    private static final long serialVersionUID = 8337560827269082733L;

    @Override
    protected RestRequest convertRequest() throws Exception {
        HttpServletRequest req = getRequest();
        RestRequest restRequest = new LocationBeaconRequest();
        restRequest.populateFromHttpRequest(req);
        return restRequest;

    }

    @Override
    protected RestResponse handleRequest(RestRequest req) throws Exception {
        RestResponse resp = new RestResponse();
        LocationBeaconRequest lbReq = (LocationBeaconRequest) req;
        DeviceDAO deviceDao = new DeviceDAO();
        deviceDao.updateDevice(lbReq.getPhoneNumber(),
                lbReq.getLat(),
                lbReq.getLon(),
                lbReq.getAccuracy(),
                lbReq.getAppVersion(),
                lbReq.getDeviceIdentifier(),
                lbReq.getImei(),
                lbReq.getOsVersion(),
                lbReq.getAndroidId());
        return resp;
    }

    @Override
    protected void writeOkResponse(RestResponse resp) throws Exception {
        getResponse().setStatus(200);
    }

}
