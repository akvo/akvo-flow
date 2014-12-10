/*
 *  Copyright (C) 2010-2014 Stichting Akvo (Akvo Foundation)
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

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gallatinsystems.device.dao.DeviceDAO;
import com.gallatinsystems.device.dao.DeviceFileJobQueueDAO;
import com.gallatinsystems.device.domain.Device;
import com.gallatinsystems.device.domain.DeviceFileJobQueue;
import com.gallatinsystems.messaging.dao.MessageDao;
import com.gallatinsystems.messaging.domain.Message;
import com.gallatinsystems.survey.dao.CascadeResourceDao;
import com.gallatinsystems.survey.domain.CascadeResource;
import com.gallatinsystems.survey.domain.CascadeResource.Status;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

/**
 * Servlet used by app to trigger processing of new survey data TODO: move parameter name strings
 * into constants
 */
public class ProcessorServlet extends HttpServlet {

    private static final long serialVersionUID = -7062679258542909086L;
    private static final Logger log = Logger.getLogger(ProcessorServlet.class
            .getName());

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String action = req.getParameter("action");
        String fileName = req.getParameter("fileName");

        if (action == null) {
            return;
        }

        log.info("	ProcessorServlet->action->" + action);

        if (action.equals("submit")) {
            if (fileName != null) {
                log.info("  ProcessorServlet->filename->" + fileName);
                String phoneNumber = req.getParameter("phoneNumber");
                String imei = req.getParameter("imei");
                String checksum = req.getParameter("checksum");
                if (checksum == null) {
                    checksum = "null";
                }
                if (phoneNumber == null) {
                    phoneNumber = "null";
                }
                if (imei == null) {
                    imei = "null";
                }
                log.info("about to submit task for fileName: " + fileName);
                // Submit the fileName for processing
                Queue queue = QueueFactory.getDefaultQueue();

                queue.add(TaskOptions.Builder.withUrl("/app_worker/task")
                        .param("action", "processFile").param("fileName", fileName)
                        .param("phoneNumber", phoneNumber).param("checksum", checksum)
                        .param("imei", imei));
                log.info("submiting task for fileName: " + fileName);
            }
        } else if (action.equals("image")) {
            String imei = req.getParameter("imei");
            String phoneNumber = req.getParameter("phoneNumber");

            Device d = null;
            DeviceDAO dao = new DeviceDAO();

            if (imei != null) {
                d = dao.getByImei(imei.trim());
            }

            if (d == null && phoneNumber != null) {
                d = dao.get(phoneNumber.trim());
            }

            if (d == null) {
                log.severe(String.format(
                        "No device found with imei %s or phoneNumber %s",
                        imei, phoneNumber));
                return;
            }

            DeviceFileJobQueueDAO dfDao = new DeviceFileJobQueueDAO();
            List<DeviceFileJobQueue> missing = dfDao.listByDeviceAndFile(d
                    .getKey().getId(), fileName);
            log.info(String.format(
                    "Deleting %s entities matching the fileName %s",
                    missing.size(), fileName));
            dfDao.delete(missing);
        } else if (action.equals("cascade")) {
            Long crId = null;
            final String status = req.getParameter("status");
            final CascadeResourceDao crDao = new CascadeResourceDao();

            try {
                crId = Long.valueOf(req.getParameter("cascadeResourceId"));
            } catch (NumberFormatException e) {
            }

            if (crId == null || status == null) {
                return;
            }

            CascadeResource cr = crDao.getByKey(crId);


            if (cr == null) {
                return;
            }

            final MessageDao mDao = new MessageDao();
            final Message m = new Message();
            m.setActionAbout("cascadePublish");

            if ("published".equals(status)) {
                cr.setStatus(Status.PUBLISHED);
                m.setShortMessage("Cascade resource " + cr.getName() + " successfully published");
            } else {
                cr.setStatus(Status.NOT_PUBLISHED);
                m.setShortMessage("Failed to publish cascade resource " + cr.getName());
            }

            crDao.save(cr);
            mDao.save(m);

        }

    }
}
