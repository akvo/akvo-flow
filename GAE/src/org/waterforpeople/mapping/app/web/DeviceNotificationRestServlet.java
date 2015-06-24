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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.waterforpeople.mapping.app.web.dto.DeviceNotificationRequest;
import org.waterforpeople.mapping.app.web.dto.DeviceNotificationResponse;

import com.gallatinsystems.device.dao.DeviceDAO;
import com.gallatinsystems.device.dao.DeviceFileJobQueueDAO;
import com.gallatinsystems.device.domain.Device;
import com.gallatinsystems.device.domain.DeviceFileJobQueue;
import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.domain.Survey;

public class DeviceNotificationRestServlet extends AbstractRestApiServlet {

    private static final long serialVersionUID = -2243167279214074216L;

    public DeviceNotificationRestServlet() {
        super();
        setMode(JSON_MODE);
    }

    @Override
    protected RestRequest convertRequest() throws Exception {
        HttpServletRequest req = getRequest();
        RestRequest restRequest = new DeviceNotificationRequest();
        restRequest.populateFromHttpRequest(req);
        return restRequest;
    }

    @Override
    protected RestResponse handleRequest(RestRequest req) throws Exception {
        DeviceNotificationRequest dnReq = (DeviceNotificationRequest) req;
        DeviceNotificationResponse resp = new DeviceNotificationResponse();

        Device d = getDevice(dnReq);
        if (d != null) {
            DeviceFileJobQueueDAO jobDao = new DeviceFileJobQueueDAO();
    
            List<DeviceFileJobQueue> missingByDevice = jobDao.listByDeviceId(d
                    .getKey().getId());
            List<DeviceFileJobQueue> missingUnknown = jobDao.listByUnknownDevice();
    
            resp.setMissingFiles(missingByDevice);
            resp.setMissingUnknown(missingUnknown);
        }
        
        resp.setDeletedSurvey(getDeletedSurveys(dnReq));

        return resp;
    }

    @Override
    protected void writeOkResponse(RestResponse resp) throws Exception {
        getResponse().setStatus(200);
        DeviceNotificationResponse r = (DeviceNotificationResponse) resp;

        // manually building the JSON response as the current version of the
        // JSON library can't handle the resp object

        JSONObject json = new JSONObject();
        JSONArray missingFiles = new JSONArray();
        JSONArray missingUnknown = new JSONArray();
        JSONArray deletedSurveys = new JSONArray();

        for (String mf : r.getMissingFiles()) {
            missingFiles.put(mf);
        }

        for (String mu : r.getMissingUnknown()) {
            missingUnknown.put(mu);
        }

        for (Long id : r.getDeletedSurveys()) {
            deletedSurveys.put(String.valueOf(id));
        }

        json.put("missingFiles", missingFiles);
        json.put("missingUnknown", missingUnknown);
        json.put("deletedForms", deletedSurveys);

        getResponse().getWriter().println(json.toString());
    }

    private Set<Long> getDeletedSurveys(DeviceNotificationRequest req) {
        Set<Long> surveyIds = req.getSurveyIds();
        if (surveyIds.isEmpty()) {
            return surveyIds;
        }
        Set<Long> foundIds = new HashSet<>();
        for (Survey s : new SurveyDAO().listByKeys(surveyIds.toArray(new Long[surveyIds.size()]))) {
            foundIds.add(s.getKey().getId());
        }
        surveyIds.removeAll(foundIds);

        return surveyIds;
    }
    

    private Device getDevice(DeviceNotificationRequest req) {
        DeviceDAO deviceDao = new DeviceDAO();

        if (req.getImei() != null) {
            Device d = deviceDao.getByImei(req.getImei().trim());
            if (d != null) {
                return d;
            }
        }

        if (req.getPhoneNumber() != null) {
            return deviceDao.get(req.getPhoneNumber().trim());
        }

        return null;
    }

}
