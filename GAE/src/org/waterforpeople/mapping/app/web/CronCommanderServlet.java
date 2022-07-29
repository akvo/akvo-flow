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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.nio.file.Paths;
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
import org.akvo.flow.domain.persistent.Report;
import org.waterforpeople.mapping.app.web.dto.SurveyTaskRequest;
import org.waterforpeople.mapping.dao.QuestionAnswerStoreDao;
import org.waterforpeople.mapping.domain.QuestionAnswerStore;
import org.waterforpeople.mapping.domain.response.value.Location;
import org.waterforpeople.mapping.domain.response.value.Media;
import org.waterforpeople.mapping.serialization.response.MediaResponse;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.lang.Rational;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.GpsDirectory;
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

// TODO: Methods should be abstracted to allow for a custom date instead of being
//  arbitrarily set to 1 year old/1 month, even if not explicitly used.
//  Additionally, a whole new approach may be desirable in favor of scalability.
public class CronCommanderServlet extends HttpServlet {

    // Reconsider necessity.
    private final long serialVersionUID = 2287175129835274533L;
    private final Logger log = Logger.getLogger(CronCommanderServlet.class.getName());

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String action = req.getParameter("action");
        switch (action) {
            case "buildMap":
                /*
                 * KMLHelper kmlHelper = new KMLHelper(); if (kmlHelper.checkCreateNewMap()) { Queue
                 * mapAssemblyQueue = QueueFactory.getQueue("mapAssembly"); TaskOptions task =
                 * url("/app_worker/mapassembly").param("action", action).param("action", "buildMap");
                 * mapAssemblyQueue.add(task); }
                 */
                break;
            case "purgeExpiredSurveys":
                purgeExpiredSurveys();
                break;
            case "purgeOrphanJobQueueRecords":
                purgeOrphanJobQueueRecords();
                break;
            case "generateNotifications":
                generateNotifications();
                break;
            case "purgeDeviceFileJobQueueRecords":
                purgeDeviceFileJobQueueRecords();
                break;
            case "purgeExpiredDevices":
                purgeExpiredDevices();
                break;
            case "extractImageFileGeotags":
                extractImageFileGeotags();
                break;
            case "purgeReportRecords":
                purgeReportRecords();
                break;
            case "purgeOldMessages":
                purgeOldMessages();
                break;
        }
    }

    /**
     * Scans for and deletes Device entries that have not been seen in more than a year.
     */
    private void purgeExpiredDevices() {
        Date date = this.getModifiedDate(Calendar.YEAR, -1);

        log.info("Starting scan for Devices not seen since: " + date);

        DeviceDAO deviceDao = new DeviceDAO();
        DeviceSurveyJobQueueDAO dsjqDao = new DeviceSurveyJobQueueDAO();
        DeviceFileJobQueueDAO dfjqDao = new DeviceFileJobQueueDAO();
        SurveyAssignmentDao saDao = new SurveyAssignmentDao();
        List<Device> deviceList = deviceDao.listAllWithBeaconBefore(date);

        log.info("Found " + deviceList.size() + " old Devices");

        for (Device d : deviceList) { // Clean up everything referencing this device
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
     * Scans for and deletes Report entries that are more than a year old.
     */
    private void purgeReportRecords() {
        Date date = this.getModifiedDate(Calendar.YEAR, -1);

        log.info("Starting scan for Report entries older than: " + date);

        ReportDao reportDao = new ReportDao();
        List<Report> reportList = reportDao.listAllCreatedBefore(date);

        log.fine("Deleting " + reportList.size() + " old Report entries");
        reportDao.delete(reportList);
    }

    /**
     * Scans for and deletes DeviceFileJobQueue entries that are either more
     * than a year old or that refer to files that have been successfully uploaded.
     */
    private void purgeDeviceFileJobQueueRecords() {
        Date date = this.getModifiedDate(Calendar.YEAR, -1);

        log.info("Starting scan for DFJQ entries, fulfilled or older than: " + date);

        DeviceFileJobQueueDAO dfjqDao = new DeviceFileJobQueueDAO();
        List<DeviceFileJobQueue> dfjqList = dfjqDao.list("all");

        int retirees = 0;

        for (DeviceFileJobQueue item : dfjqList) {
            if (item.getCreatedDateTime() != null && date.after(item.getCreatedDateTime())) {
                // Cheap case - old
                log.fine("Deleting old DFJQ entry: " + item.getKey().getId());

                SurveyTaskUtil.spawnDeleteTask(
                        SurveyTaskRequest.DELETE_DFJQ_ACTION,
                        item.getKey().getId()
                );

                retirees++;
            } else { // Check the (now protected) image file in S3 store - need credentials
                try {
                    String bucket = com.gallatinsystems.common.util.PropertyUtil.getProperty("s3bucket");
                    HttpURLConnection conn = (HttpURLConnection) S3Util.getConnection(
                            bucket, "images/" + item.getFileName()
                    );

                    log.fine("Checking for " + item.getFileName() + " : " + conn.getResponseCode() + " " + conn.getResponseMessage());

                    if (conn.getResponseCode() == 200) {
                        // Best case - fulfilled
                        log.fine("Deleting fulfilled DFJQ entry: " + item.getKey().getId());

                        SurveyTaskUtil.spawnDeleteTask(
                                SurveyTaskRequest.DELETE_DFJQ_ACTION,
                                item.getKey().getId()
                        );

                        retirees++;
                    }
                } catch (Exception e) {
                    log.warning("Error while connecting to " + item.getFileName() + "\n" + e.getMessage());
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
        List<DeviceSurveyJobQueue> dsjqList = dsjqDao.listAssignmentsWithEarlierExpirationDate(new Date());

        for (DeviceSurveyJobQueue item : dsjqList)
            SurveyTaskUtil.spawnDeleteTask(
                    SurveyTaskRequest.DELETE_DSJQ_ACTION,
                    item.getAssignmentId()
            );
    }

    /**
     * Remove assignments with nonexistent forms
     * TODO: remove those with nonexistent devices
     */
    private void purgeOrphanJobQueueRecords() {
        DeviceSurveyJobQueueDAO dsjqDao = new DeviceSurveyJobQueueDAO();
        SurveyDAO surveyDao = new SurveyDAO();
        List<Key> surveyIdList = surveyDao.listSurveyIds();
        List<Long> ids = new ArrayList<>();

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
     * Scans for and extracts geotags from image answers less than one month old.
     * <p>
     * Intended to be run every day.
     */
    private void extractImageFileGeotags() {
        Date date = this.getModifiedDate(Calendar.MONTH, -1);

        log.info("Starting scan for image answers, newer than: " + date);

        int json = 0;
        int nonJson = 0;

        QuestionAnswerStoreDao qaDao = new QuestionAnswerStoreDao();
        String cursor = "";
        Media media;

        while (true) {
            List<QuestionAnswerStore> qaList = qaDao.listByTypeAndDate("IMAGE", null, date, cursor, 1000);

            if (qaList == null || qaList.size() == 0) break; // No more answers

            cursor = QuestionAnswerStoreDao.getCursor(qaList);

            // Loop over this batch
            for (QuestionAnswerStore item : qaList) {
                boolean forceSave = false;
                String value = item.getValue();

                log.fine(String.format(" Old IMAGE value '%s'", value));

                if (value != null && !value.trim().isEmpty()) {
                    if (value.startsWith("{")) {
                        json++;

                        // Parse it
                        media = MediaResponse.parse(value);

                        // Best case: Already known (could check validity)
                        if (media.getLocation() != null) continue; //Skip

                        // Also want to skip if location is present, but null, to avoid re-evaluation
                        if (value.matches("\"location\":null")) {
                            log.fine(String.format("Null location in IMAGE %d: '%s'", item.getKey().getId(), value));
                            continue; // Skip
                        }
                    } else {
                        nonJson++;
                        forceSave = true; // Handle legacy values: convert them to JSON while we're here
                        value = Paths.get(value).getFileName().toString(); // Strip path, it is never used
                        media = new Media();
                        media.setFilename(value);
                    }
                } else {
                    log.warning(String.format("null or empty value for IMAGE %d: '%s'", item.getKey().getId(), value));
                    continue; // Bad data - punt
                }

                // No location known; must read the file
                Location loc = new Location();
                Boolean tagFound = fetchLocationFromJpegInS3(media.getFilename(), loc);

                if (tagFound != null || forceSave) {
                    // We cannot know (right now - the file may arrive later)
                    if (tagFound == null) value = MediaResponse.formatWithoutGeotag(media);
                    else { // We do know!
                        if (tagFound.equals(Boolean.FALSE)) loc = null; //There is no tag!

                        media.setLocation(loc);
                        value = MediaResponse.formatWithGeotag(media);
                    }

                    log.fine(String.format("New IMAGE value '%s'", value));
                    item.setValue(value);

                    qaDao.save(item);
                }

            }
        }

        log.fine("Found " + json + " JSON answers.");
        log.fine("Found " + nonJson + " Non-JSON answers.");
    }


    /*
     * Sample exif command output:
     *  [GPS] GPS Latitude - 59/1 17/1 25324/1000
     *  [GPS] GPS Longitude - 17/1 57/1 7827/1000
     *  [GPS] GPS Altitude - 0 metres
     *  [GPS] GPS Time-Stamp - 00:42:48.000 UTC
     *  [GPS] GPS Processing Method - NETWORK
     *  [GPS] GPS Date Stamp - 2013:04:13
     */
    private Boolean fetchLocationFromJpegInS3(String filename, Location loc) {
        InputStream image = fetchImageFileFromS3(filename);

        if (image != null) try {
            Metadata metadata = JpegMetadataReader.readMetadata(image);

            log.fine("Using JpegMetadataReader");

            Directory directory = metadata.getFirstDirectoryOfType(GpsDirectory.class);

            if (directory == null) return false; // No GPS tag

            Rational[] latTag = directory.getRationalArray(GpsDirectory.TAG_LATITUDE);
            String latRefTag = directory.getString(GpsDirectory.TAG_LATITUDE_REF);
            Rational[] lonTag = directory.getRationalArray(GpsDirectory.TAG_LONGITUDE);
            String lonRefTag = directory.getString(GpsDirectory.TAG_LONGITUDE_REF);
            Rational[] altTag = directory.getRationalArray(GpsDirectory.TAG_ALTITUDE);
            Integer altRefTag = directory.getInteger(GpsDirectory.TAG_ALTITUDE_REF);
            Rational[] accTag = directory.getRationalArray(GpsDirectory.TAG_H_POSITIONING_ERROR);

            if (latTag == null || lonTag == null) return false; // Bad GPS tag

            double lat = latTag[0].doubleValue() + latTag[1].doubleValue() / 60.0 + latTag[2].doubleValue() / 3600.0;
            double lon = lonTag[0].doubleValue() + lonTag[1].doubleValue() / 60.0 + lonTag[2].doubleValue() / 3600.0;
            double alt = altTag != null ? altTag[0].doubleValue() : 0.0;
            float acc = accTag != null ? accTag[0].floatValue() : 0.0f;

            if (latRefTag != null && latRefTag.contentEquals("S")) lat = -lat;
            if (lonRefTag != null && lonRefTag.contentEquals("W")) lon = -lon;
            if (lat == 0.0 || lon == 0.0) return false; // While technically valid, treat as Bad GPS tag
            if (altRefTag != null && altRefTag.equals(1)) alt = -alt; // 0 = above, 1 below sea level

            loc.setLatitude(lat);
            loc.setLongitude(lon);
            loc.setAltitude(alt);
            loc.setAccuracy(acc);

            log.fine(String.format(" Extracted location N %f, E %f, up %f, acc %f\n", lat, lon, alt, acc)); // Debug

            return true;
        } catch (JpegProcessingException e) {
            log.warning("Could not process JPEG file: " + e.getMessage());
        } catch (IOException e) {
            log.warning("Could not read image file: " + e.getMessage());
        }
        return null; // Can't tell
    }


    private InputStream fetchImageFileFromS3(String filename) {
        // Attempt to retrieve image file.
        URLConnection conn;
        String s3bucket = com.gallatinsystems.common.util.PropertyUtil.getProperty("s3bucket");

        filename = Paths.get(filename).getFileName().toString(); // Strip path, it is not used in S3

        log.fine("Fetching " + filename);

        try {
            conn = S3Util.getConnection(s3bucket, "images/" + filename);
            return new BufferedInputStream(conn.getInputStream());
        } catch (IOException e) {
            log.warning("Could not fetch image file: " + e.getMessage());
            return null;
        }
    }


    /**
     * scans for and deletes Message entries that are more than one year old
     */
    private void purgeOldMessages() {
        Date date = this.getModifiedDate(Calendar.YEAR, -1);

        log.info("Starting scan for Message entries older than: " + date);

        MessageDao messageDao = new MessageDao();
        List<Key> purgable = messageDao.listKeysCreatedBefore(date);

        log.fine("Deleting " + purgable.size() + " old Message entries");

        messageDao.deleteByKeys(purgable);
    }

    /**
     * Creates a calendar instance, modifies the field by the given amount
     * and returns the resulting date.
     *
     * @param field  the field to modify
     * @param amount the amount to modify the field by
     * @return the resulting date
     */
    private Date getModifiedDate(int field, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(field, amount);

        return calendar.getTime();
    }
}
