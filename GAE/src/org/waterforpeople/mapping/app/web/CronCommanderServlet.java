/*
 *  Copyright (C) 2010-2012, 2018 Stichting Akvo (Akvo Foundation)
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.waterforpeople.mapping.app.web.dto.SurveyTaskRequest;

import com.gallatinsystems.common.util.S3Util;
import com.gallatinsystems.device.dao.DeviceFileJobQueueDAO;
import com.gallatinsystems.device.domain.DeviceFileJobQueue;
import com.gallatinsystems.device.domain.DeviceSurveyJobQueue;
import com.gallatinsystems.notification.helper.NotificationHelper;
import com.gallatinsystems.survey.dao.DeviceSurveyJobQueueDAO;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.dao.SurveyTaskUtil;
import com.google.appengine.api.datastore.Key;


public class CronCommanderServlet extends HttpServlet {

    private static final int TWO_YEARS_AGO = -2;
    private static final long serialVersionUID = 2287175129835274533L;
    private static final Logger log = Logger.getLogger(CronCommanderServlet.class.getName());

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String action = req.getParameter("action");
        if ("buildMap".equals(action)) {
            /*
             * KMLHelper kmlHelper = new KMLHelper(); if (kmlHelper.checkCreateNewMap()) { Queue
             * mapAssemblyQueue = QueueFactory.getQueue("mapAssembly"); TaskOptions task =
             * url("/app_worker/mapassembly").param("action", action).param("action", "buildMap");
             * mapAssemblyQueue.add(task); }
             */
        } else if ("purgeExpiredSurveys".equals(action)) {
            purgeExpiredSurveys();
        } else if ("purgeOrphanJobQueueRecords".equals(action)) {
            purgeOrphanJobQueueRecords();
        } else if ("generateNotifications".equals(action)) {
            generateNotifications();
        } else if ("purgeDeviceFileJobQueueRecords".equals(action)) {
            purgeDeviceFileJobQueueRecords();
        }
    }

    /**
     * scans for and deletes DeviceFileJobQueue entries that are either more than two years old
     * or that refer to files that have been successfully uploaded.
     */
    private void purgeDeviceFileJobQueueRecords() {
        Calendar deadline = Calendar.getInstance();
        deadline.add(Calendar.YEAR, TWO_YEARS_AGO);
        log.info("Starting scan for DFJQ entries, fulfilled or older than: " + deadline.getTime());
        DeviceFileJobQueueDAO dfjqDao = new DeviceFileJobQueueDAO();
        List<DeviceFileJobQueue> dfjqList = dfjqDao.list("all");
        int retirees = 0;
        for (DeviceFileJobQueue item : dfjqList) {
            if (item.getCreatedDateTime() != null
                    && deadline.getTime().after(item.getCreatedDateTime())) {
                //cheap case - old
                log.info("Deleting old DFJQ entry: " + item.getKey().getId());
                SurveyTaskUtil.spawnDeleteTask(SurveyTaskRequest.DELETE_DFJQ_ACTION,
                        item.getKey().getId());
                retirees++;
            } else {//check the (now protected)image file in S3 store - need credentials
                try {
                    String bucket =
                            com.gallatinsystems.common.util.PropertyUtil.getProperty("s3bucket");
                    HttpURLConnection conn = (HttpURLConnection)
                            S3Util.getConnection(bucket, "images/" + item.getFileName());
                    log.info("Checking for " + item.getFileName() +
                            " : " + conn.getResponseCode() + " " + conn.getResponseMessage());
                    if (conn.getResponseCode() == 200) {
                        // best case - fulfilled
                        log.info("Deleting fulfilled DFJQ entry: " + item.getKey().getId());
                        SurveyTaskUtil.spawnDeleteTask(SurveyTaskRequest.DELETE_DFJQ_ACTION,
                                item.getKey().getId());
                        retirees++;
                    }
                } catch (Exception e) {
                    log.warning("Error while connecing to " + item.getFileName() +"\n" + e.getMessage());
                }
            }
        }
        log.info("Attempted to retire " + retirees + " of " + dfjqList.size());
    }

    private void generateNotifications() {
        NotificationHelper helper = new NotificationHelper("rawDataReport",
                null);
        helper.execute();
        NotificationHelper fieldReportHelper = new NotificationHelper("fieldStatusReport", null);
        fieldReportHelper.execute();
    }

    private void purgeExpiredSurveys() {
        DeviceSurveyJobQueueDAO dsjqDao = new DeviceSurveyJobQueueDAO();
        List<DeviceSurveyJobQueue> dsjqList = dsjqDao
                .listAssignmentsWithEarlierExpirationDate(new Date());
        for (DeviceSurveyJobQueue item : dsjqList) {
            SurveyTaskUtil.spawnDeleteTask(SurveyTaskRequest.DELETE_DSJQ_ACTION,
                    item.getAssignmentId());
        }
    }

    private void purgeOrphanJobQueueRecords() {
        DeviceSurveyJobQueueDAO dsjqDao = new DeviceSurveyJobQueueDAO();
        SurveyDAO surveyDao = new SurveyDAO();
        List<Key> surveyIdList = surveyDao.listSurveyIds();
        List<Long> ids = new ArrayList<Long>();

        for (Key key : surveyIdList) {
            ids.add(key.getId());
        }

        for (DeviceSurveyJobQueue item : dsjqDao.listAllJobsInQueue()) {
            Long dsjqSurveyId = item.getSurveyID();
            Boolean found = ids.contains(dsjqSurveyId);
            if (!found) {
                log.info("found orphan assignmentId: " + item.getAssignmentId()
                        + " id: " + item.getId() + " survey: "
                        + item.getSurveyID() + " for deletion");
                SurveyTaskUtil.spawnDeleteTask(SurveyTaskRequest.DELETE_DSJQ_ACTION,
                        item.getAssignmentId());

            }
        }
    }
}
