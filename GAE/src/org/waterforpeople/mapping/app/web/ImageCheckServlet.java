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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.waterforpeople.mapping.app.web.dto.ImageCheckRequest;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.common.util.PropertyUtil;
import com.gallatinsystems.device.dao.DeviceFileJobQueueDAO;
import com.gallatinsystems.device.domain.DeviceFileJobQueue;
import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

public class ImageCheckServlet extends AbstractRestApiServlet {
    private static final Logger log = Logger.getLogger(ImageCheckServlet.class
            .getName());
    private static final long serialVersionUID = 9187987692591327059L;
    private static final long MAX_ATTEMPTS = 3;
    private static final long DELAY = 1000 * 60 * 5; // 5min

    @Override
    protected RestRequest convertRequest() throws Exception {
        HttpServletRequest req = getRequest();
        RestRequest restRequest = new ImageCheckRequest();
        restRequest.populateFromHttpRequest(req);
        return restRequest;
    }

    @Override
    protected RestResponse handleRequest(RestRequest req) throws Exception {
        final ImageCheckRequest checkReq = (ImageCheckRequest) req;

        if (checkReq.getFileName() == null || checkReq.getFileName().equals("")) {
            log.log(Level.SEVERE, "No filename was provided, aborting check");
            return new RestResponse();
        }

        if (checkReq.getAttempt() == null || checkReq.getAttempt().equals("")) {
            log.log(Level.SEVERE,
                    "No attempt number was specified, aborting check");
            return new RestResponse();
        }

        // NOTE: baseUrl contains a trailing slash
        final String baseUrl = PropertyUtil.getProperty("photo_url_root");
        final String imageUrl = baseUrl + checkReq.getFileName();

        // MalformedURLException exception caught by method signature
        final URL url = new URL(imageUrl);

        try {

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(Constants.CONNECTION_TIMEOUT);
            conn.setRequestMethod("HEAD");

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                if (checkReq.getAttempt() == MAX_ATTEMPTS) {
                    log.log(Level.INFO, "Adding file as missing: " + checkReq);
                    DeviceFileJobQueueDAO jobDao = new DeviceFileJobQueueDAO();
                    DeviceFileJobQueue df = new DeviceFileJobQueue();
                    df.setFileName(checkReq.getFileName());
                    df.setDeviceId(checkReq.getDeviceId());
                    df.setQasId(checkReq.getQasId());
                    jobDao.save(df);
                } else {
                    rescheduleTask(checkReq, true);
                }
            }
        } catch (SocketTimeoutException timeout) {
            // reschedule the task without delay
            // Possible a hiccup in GAE side
            rescheduleTask(checkReq, false);
        } catch (IOException e) {
            // IOException possible a http 403, reschedule the task
            rescheduleTask(checkReq, true);
        }

        return new RestResponse();
    }

    @Override
    protected void writeOkResponse(RestResponse resp) throws Exception {
        getResponse().setStatus(200);
    }

    /**
     * Reschedules the CheckImage task with a possible delay<br>
     * delay = 3 min * attempt number
     */
    private void rescheduleTask(ImageCheckRequest req, boolean delay) {

        int attempt = delay ? req.getAttempt() + 1 : req.getAttempt();

        log.log(Level.INFO, "Rescheduling image check: " + req);

        Queue queue = QueueFactory.getQueue("background-processing");
        TaskOptions to = TaskOptions.Builder
                .withUrl("/app_worker/imagecheck")
                .param(ImageCheckRequest.FILENAME_PARAM, req.getFileName())
                .param(ImageCheckRequest.DEVICE_ID_PARAM,
                        String.valueOf(req.getDeviceId()))
                .param(ImageCheckRequest.QAS_ID_PARAM,
                        String.valueOf(req.getQasId()))
                .param(ImageCheckRequest.ATTEMPT_PARAM, String.valueOf(attempt))
                .countdownMillis(delay ? DELAY * req.getAttempt() : 0);
        queue.add(to);
    }
}
