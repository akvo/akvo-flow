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

package com.gallatinsystems.device.app.web;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gallatinsystems.device.dao.DeviceDAO;
import com.gallatinsystems.device.domain.Device;
import com.gallatinsystems.device.domain.DeviceSurveyJobQueue;
import com.gallatinsystems.survey.dao.DeviceSurveyJobQueueDAO;

/**
 * Servlet class to handle the device (handheld) related management tasks.
 * 
 * @deprecated
 */
public class DeviceManagerServlet extends HttpServlet {
    private static final long serialVersionUID = 1979457951988807893L;
    private static final Logger log = Logger
            .getLogger(DeviceManagerServlet.class.getName());
    private DeviceDAO deviceDao = new DeviceDAO();

    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        String action = req.getParameter("action");
        if ("listDevices".equals(action)) {
            DeviceSurveyJobQueueDAO dsjqDAO = new DeviceSurveyJobQueueDAO();
            List<DeviceSurveyJobQueue> jobs = dsjqDAO.listAllJobsInQueue();
            for (DeviceSurveyJobQueue item : jobs) {
                try {
                    resp.getWriter().print(item.toString());
                } catch (IOException e) {
                    log.log(Level.SEVERE, "Could not execute list device", e);
                }
            }
        } else {
            try {
                resp.getWriter().print("need an action");
            } catch (IOException e) {
                log.log(Level.SEVERE, "Could not write error", e);
            }
        }
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        String action = req.getParameter("action");
        String outputString = null;
        if ("saveDevice".equals(action)) {
            outputString = createDevice(req);
        } else {
            String devicePhoneNumber = req.getParameter("devicePhoneNumber");
            Long surveyId = new Long(req.getParameter("surveyId"));
            DeviceSurveyJobQueueDAO dsjqDAO = new DeviceSurveyJobQueueDAO();
            DeviceSurveyJobQueue dsjq = new DeviceSurveyJobQueue();
            dsjq.setDevicePhoneNumber(devicePhoneNumber);
            dsjq.setSurveyID(surveyId);
            Long deviceId = dsjqDAO.save(dsjq);
            resp.setContentType("text/html");
            outputString = "Device: " + deviceId + " created";
        }

        try {
            resp.getWriter().print(outputString);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Could not execute device operation", e);
        }
    }

    @SuppressWarnings("unused")
    private String createDevice(HttpServletRequest req) {
        StringBuilder sb = new StringBuilder();
        String deviceType = req.getParameter("deviceType");
        String esn = req.getParameter("esn");
        String inServiceDateString = req.getParameter("inServiceDateString");
        String osVersion = req.getParameter("osVersion");
        String countryIdString = req.getParameter("countryIdString");
        String phoneNumber = req.getParameter("devicePhoneNumber");
        String outServiceDateString = req.getParameter("outServiceDateString");

        phoneNumber = phoneNumber.replace("-", "");
        Device device = new Device();

        device.setDeviceType(Device.DeviceType.CELL_PHONE_ANDROID);

        device.setEsn(esn);
        device.setPhoneNumber(phoneNumber);
        deviceDao.save(device);
        sb.append(device.toString());
        return sb.toString();
    }

}
