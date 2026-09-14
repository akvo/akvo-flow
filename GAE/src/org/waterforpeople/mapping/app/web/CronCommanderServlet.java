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


public class CronCommanderServlet extends HttpServlet {

    private static final int ONE_YEAR_AGO = -1;
    private static final int ONE_MONTH_AGO = -1;
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
        } else if ("extractImageFileGeotags".equals(action)) {
            extractImageFileGeotags();
        } else if ("purgeReportRecords".equals(action)) {
            purgeReportRecords();
        } else if ("purgeOldMessages".equals(action)) {
            purgeOldMessages();
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
     * True when the image was read and carried no GPS tag: formatWithGeotag keeps nulls, so the
     * value has "location":null. A file not yet in S3 omits the key entirely and is retried.
     */
    static boolean isKnownToHaveNoGeotag(String value) {
        // contains, not matches: String.matches anchors the whole string, so against a JSON
        // object it is always false and every untagged image was re-read from S3 on every run.
        return value.contains("\"location\":null");
    }

    static final int GEOTAG_PAGE_SIZE = 1000;

    /**
     * How many pages one run may examine before stopping and leaving the rest for tomorrow.
     *
     * The scan used to read every answer in the window in a single request, and on the
     * instances holding the most data that exhausted an F1's 256MB: the process was killed,
     * the request 503'd, and App Engine retried the job roughly a thousand times a day
     * without it ever completing. Bounding the work is what lets the job finish and return
     * 200, which is the only thing that stops the retries.
     *
     * Five is deliberately conservative rather than measured. The instances that failed give
     * no usable figure -- they died 25 seconds in, before finishing a run -- so the number to
     * beat is unknown and the cost of guessing high is the failure we are fixing. Raise it
     * once a few runs have completed and the headroom is visible.
     */
    static final int GEOTAG_MAX_PAGES_PER_RUN = 5;

    /**
     * scans for and extracts geotags from image answers less than 1 month old
     * Intended to be run every day
     */
    private void extractImageFileGeotags() {
        extractImageFileGeotags(GEOTAG_MAX_PAGES_PER_RUN, GEOTAG_PAGE_SIZE);
    }

    /**
     * The bounded scan. Returns the number of answers examined, which is what the page limit
     * actually caps and therefore the only thing worth asserting in a test.
     *
     * Stopping early loses nothing, because a run always resumes where the last one left off
     * without storing a cursor. The query filters on lastUpdateDateTime and, carrying an
     * inequality, is ordered by it -- so the oldest answers in the window come first. Every
     * answer this scan rewrites goes through BaseDAO.save, which stamps lastUpdateDateTime
     * with the current time and therefore moves it to the back of that order. Answers it skips
     * are not rewritten, keep their timestamp, and fall out of the one-month window on their
     * own. Either way the front of the query is the work still to do.
     *
     * That same stamping is why the window is not a fixed set: rewriting an answer puts it
     * back inside the month it is being selected by, so the scan keeps meeting its own output
     * for a further month. It is cheap to skip -- a rewritten answer carries its location or
     * an explicit null, so it costs no S3 read -- but it is not free to page through, which
     * is a large part of why the busiest instances are the ones that ran out of memory.
     */
    int extractImageFileGeotags(int maxPages, int pageSize) {
        Calendar deadline = Calendar.getInstance();
        deadline.add(Calendar.MONTH, ONE_MONTH_AGO);
        log.info("Starting scan for image answers, newer than: " + deadline.getTime());
        QuestionAnswerStoreDao qaDao = new QuestionAnswerStoreDao();
        String cursor = "";
        int json = 0;
        int nonjson = 0;
        int examined = 0;
        int pages = 0;
        Media media;

        do {
            List<QuestionAnswerStore> qaList = qaDao.listByTypeAndDate("IMAGE", null, deadline.getTime(), cursor, pageSize);
            if (qaList == null || qaList.size() == 0) break; //no more answers
            cursor = QuestionAnswerStoreDao.getCursor(qaList);
            examined += qaList.size();
            pages++;

            //loop over this batch
            for (QuestionAnswerStore item : qaList) {
                boolean forceSave = false;
                String v = item.getValue();
                log.fine(String.format(" Old IMAGE value '%s'", v));
                if (v != null && !v.trim().equals("")) {
                    if (v.startsWith("{")) {
                        json++;
                        //Parse it
                        media = MediaResponse.parse(v);
                        if (media.getLocation() != null) { //Best case: Already known (could check validity)
                            continue; //Skip
                        }
                        if (isKnownToHaveNoGeotag(v)) {
                            log.fine(String.format("Null location in IMAGE %d: '%s'", item.getKey().getId(), v));
                            continue; //Skip
                        }
                    } else {
                        nonjson++;
                        forceSave = true; //handle legacy values: convert them to JSON while we're here
                        v = Paths.get(v).getFileName().toString(); //strip path, it is never used
                        media = new Media();
                        media.setFilename(v);
                    }
                } else {
                    log.warning(String.format("null or empty value for IMAGE %d: '%s'", item.getKey().getId(), v));
                    continue; //Bad data - punt
                }
                //No location known; must read the file
                Location loc = new Location();
                Boolean tagFound = fetchLocationFromJpegInS3(media.getFilename(), loc);
                if (tagFound != null || forceSave) {

                    if (tagFound == null) { // We cannot know (right now - the file may arrive later)
                        v = MediaResponse.formatWithoutGeotag(media);
                    } else { //We do know!
                        if (tagFound.equals(Boolean.FALSE)) {
                            loc = null; //There is no tag!
                        }
                        media.setLocation(loc);
                        v = MediaResponse.formatWithGeotag(media);
                    }
                    log.fine(String.format("New IMAGE value '%s'", v));
                    item.setValue(v);

                    qaDao.save(item);
                }

            }

            if (pages >= maxPages) {
                log.info(String.format(
                        "Stopping after %d pages (%d answers); the rest resume on the next run.",
                        pages, examined));
                break;
            }
        } while (true);

        log.fine("Found " + json + " JSON answers.");
        log.fine("Found " + nonjson + " Non-JSON answers.");
        return examined;
    }


    /*
     * Sample exif command output:
    [GPS] GPS Latitude - 59/1 17/1 25324/1000
    [GPS] GPS Longitude - 17/1 57/1 7827/1000
    [GPS] GPS Altitude - 0 metres
    [GPS] GPS Time-Stamp - 00:42:48.000 UTC
    [GPS] GPS Processing Method - NETWORK
    [GPS] GPS Date Stamp - 2013:04:13
     */
    Boolean fetchLocationFromJpegInS3(String filename, Location loc) {
        InputStream s = fetchImageFileFromS3(filename);
        if (s != null) {
            try {
                Metadata metadata = JpegMetadataReader.readMetadata(s);

                log.fine("Using JpegMetadataReader");
                Directory directory = metadata.getFirstDirectoryOfType(GpsDirectory.class);
                if (directory == null) { //No GPS tag
                    return false;
                }

                Rational[] latTag = directory.getRationalArray(GpsDirectory.TAG_LATITUDE);
                String latRefTag = directory.getString(GpsDirectory.TAG_LATITUDE_REF);
                Rational[] lonTag = directory.getRationalArray(GpsDirectory.TAG_LONGITUDE);
                String lonRefTag = directory.getString(GpsDirectory.TAG_LONGITUDE_REF);
                Rational[] altTag = directory.getRationalArray(GpsDirectory.TAG_ALTITUDE);
                Integer altRefTag = directory.getInteger(GpsDirectory.TAG_ALTITUDE_REF);
                Rational[] accTag = directory.getRationalArray(GpsDirectory.TAG_H_POSITIONING_ERROR);
                if (latTag == null || lonTag == null) {
                    return false; //Bad GPS tag
                }
                Double lat = latTag[0].doubleValue() + latTag[1].doubleValue()/60.0 + latTag[2].doubleValue()/3600.0;
                if (latRefTag != null && latRefTag.contentEquals("S")) {
                    lat = -lat;
                }
                Double lon = lonTag[0].doubleValue() + lonTag[1].doubleValue()/60.0 + lonTag[2].doubleValue()/3600.0;
                if (lonRefTag != null && lonRefTag.contentEquals("W")) {
                    lon = -lon;
                }
                if (lat == 0.0 || lon == 0.0) {
                    return false; //While technically valid, treat as Bad GPS tag
                }
                Double alt;
                if (altTag != null) {
                    alt = altTag[0].doubleValue();
                } else {
                    alt = 0.0; //Optional; default to 0
                }
                if (altRefTag != null && altRefTag.equals(1)) { //0 = above, 1 below sea level
                    alt = -alt;
                }
                Float acc;
                if (accTag != null) {
                    acc = accTag[0].floatValue();
                } else {
                    acc = 0.0f; //Optional; default to 0
                }
                loc.setLatitude(lat);
                loc.setLongitude(lon);
                loc.setAltitude(alt);
                loc.setAccuracy(acc);
                log.fine(String.format(" Extracted location N %f, E %f, up %f, acc %f\n", lat, lon, alt, acc));//Debug
                return true;
            } catch (JpegProcessingException e) {
                log.warning("Could not process JPEG file: " + e.getMessage());
            } catch (IOException e) {
                log.warning("Could not read image file: " + e.getMessage());
            }
        }
        return null; //Can't tell
    }


    InputStream fetchImageFileFromS3(String filename) {
        // attempt to retrieve image file
        URLConnection conn = null;
        String s3bucket = com.gallatinsystems.common.util.PropertyUtil.getProperty("s3bucket");
        filename = Paths.get(filename).getFileName().toString(); //strip path, it is not used in S3
        log.fine("Fetching " + filename);

        try {
            conn = S3Util.getConnection(s3bucket, "images/" + filename);
            return new BufferedInputStream(conn.getInputStream());
        } catch (Exception e) {
            log.warning("Could not fetch image file: " + e.getMessage());
            return null;
        }
    }

    /**
     * scans for and deletes Message entries that are more than one year old
     */
    private void purgeOldMessages() {
        Calendar deadline = Calendar.getInstance();
        deadline.add(Calendar.YEAR, ONE_YEAR_AGO);
        log.info("Starting scan for Message entries older than: " + deadline.getTime());
        MessageDao messageDao = new MessageDao();
        List<Key> purgable = messageDao.listKeysCreatedBefore(deadline.getTime());
        log.fine("Deleting " + purgable.size() + " old Message entries");
        messageDao.deleteByKeys(purgable);
    }


}
