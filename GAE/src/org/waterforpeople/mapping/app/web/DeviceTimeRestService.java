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

package org.waterforpeople.mapping.app.web;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.json.JSONObject;

import org.waterforpeople.mapping.app.web.dto.DeviceTimeRestRequest;
import org.waterforpeople.mapping.app.web.dto.DeviceTimeRestResponse;

import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;

/**
 * restful service for serving time
 * 
 */
public class DeviceTimeRestService extends AbstractRestApiServlet {

    private static final long serialVersionUID = -830140106880504436L;

    public DeviceTimeRestService() {
        setMode(JSON_MODE);
    }

    @Override
    protected RestRequest convertRequest() throws Exception {
    	// not needed in this service, simply return object to fulfill override
        RestRequest restRequest = new DeviceTimeRestRequest();
        return restRequest;
    }

    @Override
    protected RestResponse handleRequest(RestRequest req) throws Exception {
        DeviceTimeRestResponse resp = new DeviceTimeRestResponse();
        
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        df.setTimeZone(tz);
        String nowAsISO = df.format(new Date());
        resp.setTime(nowAsISO);
        
        return resp;
    }

    @Override
    protected void writeOkResponse(RestResponse resp) throws Exception {
        getResponse().setStatus(200);
        getResponse().getWriter().println(new JSONObject(resp).toString());
    }

}
