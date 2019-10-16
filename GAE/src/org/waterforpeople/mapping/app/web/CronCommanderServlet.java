/*
 *  Copyright (C) 2010-2012, 2018-2019 Stichting Akvo (Akvo Foundation)
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

import org.akvo.flow.dao.MessageDao;
import org.akvo.flow.dao.ReportDao;
import org.akvo.flow.dao.SurveyAssignmentDao;
import org.akvo.flow.domain.Message;
import org.akvo.flow.domain.persistent.Report;
import org.waterforpeople.mapping.app.web.dto.SurveyTaskRequest;
import com.gallatinsystems.common.util.S3Util;
import com.gallatinsystems.device.dao.DeviceDAO;
import com.gallatinsystems.device.dao.DeviceFileJobQueueDAO;
import com.gallatinsystems.device.domain.Device;
import com.gallatinsystems.device.domain.DeviceFileJobQueue;
import com.gallatinsystems.device.domain.DeviceSurveyJobQueue;
import com.gallatinsystems.notification.helper.NotificationHelper;
import com.gallatinsystems.survey.dao.DeviceSurveyJobQueueDAO;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.dao.SurveyTaskUtil;
import com.google.appengine.api.datastore.Key;


public class CronCommanderServlet extends HttpServlet {

    private static final int ONE_YEAR_AGO = -1;
    private static final int TWO_YEARS_AGO = -2;
    private static final long serialVersionUID = 2287175129835274533L;
    private static final Logger log = Logger.getLogger(CronCommanderServlet.class.getName());

    @Override
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
        } else if ("purgeExpiredDevices".equals(action)) {
            purgeExpiredDevices();
        } else if ("purgeReportRecords".equals(action)) {
            purgeReportRecords();
        } else if ("purgeExpiredMessages".equals(action)) {
            purgeExpiredMessages();
        }
    }

    /**
     * scans for and deletes Device entries that have not been seen in more than two years
     */
    private void purgeExpiredDevices() {
        Calendar deadline = Calendar.getInstance();
        deadline.add(Calendar.YEAR, ONE_YEAR_AGO);
        log.info("Starting scan for Devices not seen since: " + deadline.getTime());
        DeviceDAO deviceDao = new DeviceDAO();
        DeviceSurveyJobQueueDAO dsjqDao = new DeviceSurveyJobQueueDAO();
        DeviceFileJobQueueDAO dfjqDao = new DeviceFileJobQueueDAO();
        SurveyAssignmentDao saDao = new SurveyAssignmentDao();
        List<Device> deviceList = deviceDao.listAllWithBeaconBefore(deadline.getTime());
        log.info("Found " + deviceList.size() + " old Devices");

        for (Device d: deviceList) { //Clean up everything referencing this device

            long did = d.getKey().getId();
            List<DeviceSurveyJobQueue> djql = dsjqDao.get(d.getPhoneNumber(), d.getEsn(), d.getAndroidId());
            if (djql.size() > 0) {
                log.fine("Deleting " + djql.size() + " form assignments for device " + did);
                dsjqDao.delete(djql);
            }
            List<DeviceFileJobQueue> dfql = dfjqDao.listByDeviceId(did);
            if (dfql.size() > 0) {
                log.fine("Deleting " + dfql.size() + " file requests for device " + did);
                dfjqDao.delete(dfql);
            }

            int affected = saDao.removeDevice(did);
            log.fine("Removed device " + did + " from " + affected + " assignments.");

        }
        deviceDao.delete(deviceList);
    }

    /**
     * scans for and deletes Report entries that are more than one year old
     */
    private void purgeReportRecords() {
        Calendar deadline = Calendar.getInstance();
        deadline.add(Calendar.YEAR, ONE_YEAR_AGO);
        log.info("Starting scan for Report entries older than: " + deadline.getTime());
        ReportDao reportDao = new ReportDao();
        List<Report> reportList = reportDao.listAllCreatedBefore(deadline.getTime());
        log.fine("Deleting " + reportList.size() + " old Report entries");
        reportDao.delete(reportList);
    }

    /**
     * scans for and deletes DeviceFileJobQueue entries that are either more than two years old
     * or that refer to files that have been successfully uploaded.
     */
    private void purgeDeviceFileJobQueueRecords() {
        Calendar deadline = Calendar.getInstance();
        deadline.add(Calendar.YEAR, ONE_YEAR_AGO);
        log.info("Starting scan for DFJQ entries, fulfilled or older than: " + deadline.getTime());
        DeviceFileJobQueueDAO dfjqDao = new DeviceFileJobQueueDAO();
        List<DeviceFileJobQueue> dfjqList = dfjqDao.list("all");
        int retirees = 0;
        for (DeviceFileJobQueue item : dfjqList) {
            if (item.getCreatedDateTime() != null
                    && deadline.getTime().after(item.getCreatedDateTime())) {
                //cheap case - old
                log.fine("Deleting old DFJQ entry: " + item.getKey().getId());
                SurveyTaskUtil.spawnDeleteTask(SurveyTaskRequest.DELETE_DFJQ_ACTION,
                        item.getKey().getId());
                retirees++;
            } else { //check the (now protected) image file in S3 store - need credentials
                try {
                    String bucket =
                            com.gallatinsystems.common.util.PropertyUtil.getProperty("s3bucket");
                    HttpURLConnection conn = (HttpURLConnection)
                            S3Util.getConnection(bucket, "images/" + item.getFileName());
                    log.fine("Checking for " + item.getFileName() +
                            " : " + conn.getResponseCode() + " " + conn.getResponseMessage());
                    if (conn.getResponseCode() == 200) {
                        // best case - fulfilled
                        log.fine("Deleting fulfilled DFJQ entry: " + item.getKey().getId());
                        SurveyTaskUtil.spawnDeleteTask(SurveyTaskRequest.DELETE_DFJQ_ACTION,
                                item.getKey().getId());
                        retirees++;
                    }
                } catch (Exception e) {
                    log.warning("Error while connecing to " + item.getFileName() + "\n" + e.getMessage());
                }
            }
        }
        log.fine("Attempted to retire " + retirees + " of " + dfjqList.size());
    }

    private void generateNotifications() {
        NotificationHelper helper = new NotificationHelper("rawDataReport", null);
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

    /**
     * Remove assignments with nonexistent forms
     * TODO: remove those with nonexistent devices
     */
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
            boolean found = ids.contains(dsjqSurveyId);
            if (!found) {
                log.info("found orphan assignmentId: " + item.getAssignmentId()
                        + " id: " + item.getId() + " survey: "
                        + item.getSurveyID() + " for deletion");
                SurveyTaskUtil.spawnDeleteTask(SurveyTaskRequest.DELETE_DSJQ_ACTION,
                        item.getAssignmentId());

            }
        }
    }

    /**
     * scans for and deletes Message entries that are more than one year old
     */
    private void purgeExpiredMessages() {
        Calendar deadline = Calendar.getInstance();
        deadline.add(Calendar.YEAR, ONE_YEAR_AGO);
        log.info("Starting scan for Message entries older than: " + deadline.getTime());
        MessageDao messageDao = new MessageDao();
        List<Message> messageList = messageDao.listAllCreatedBefore(deadline.getTime());
        log.fine("Deleting " + messageList.size() + " old Message entries");
        messageDao.delete(messageList);
    }

}
