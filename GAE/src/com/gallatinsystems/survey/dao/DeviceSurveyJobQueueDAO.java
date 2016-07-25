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

package com.gallatinsystems.survey.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jdo.PersistenceManager;

import com.gallatinsystems.device.domain.Device;
import com.gallatinsystems.device.domain.DeviceSurveyJobQueue;
import com.gallatinsystems.framework.servlet.PersistenceFilter;

/**
 * dao for saving deviceSurveyJobQueue objects.
 */
public class DeviceSurveyJobQueueDAO {

    /**
     * lists all objects for a given device
     */
    @SuppressWarnings("unchecked")
    public List<DeviceSurveyJobQueue> get(String devicePhoneNumber, String imei, String androidId) {
        PersistenceManager pm = PersistenceFilter.getManager();

        Set<DeviceSurveyJobQueue> set = new HashSet<>();
        javax.jdo.Query query;

        // Query entities based on Android ID. This is the most reliable ID
        // and should be used whenever possible
        if (androidId != null) {
            query = pm.newQuery(DeviceSurveyJobQueue.class);
            query.setFilter("androidId == androidIdParam");
            query.declareParameters("String androidIdParam");
            set.addAll((List<DeviceSurveyJobQueue>) query.execute(androidId));
        }

        // For legacy reasons, some assignments may only be identified by
        // IMEI or phone number
        List<DeviceSurveyJobQueue> legacy = null;
        if (imei != null && !Device.NO_IMEI.equals(imei)) {
            query = pm.newQuery(DeviceSurveyJobQueue.class);
            query.setFilter("imei == imeiParam");
            query.declareParameters("String imeiParam");
            legacy = (List<DeviceSurveyJobQueue>) query.execute(imei);
        }
        if ((legacy == null || legacy.isEmpty()) && devicePhoneNumber != null && !devicePhoneNumber.isEmpty()) {
            // Fall back to phone number
            query = pm.newQuery(DeviceSurveyJobQueue.class);
            query.setFilter("devicePhoneNumber == devicePhoneNumberParam");
            query.declareParameters("String devicePhoneNumberParam");
            legacy = (List<DeviceSurveyJobQueue>) query.execute(devicePhoneNumber);
        }
        if (legacy != null) {
            set.addAll(legacy);
        }

        return new ArrayList<>(set);
    }

    /**
     * saves or updates and instance
     *
     * @param deviceSurveyJobQueue
     * @return
     */
    public Long save(DeviceSurveyJobQueue deviceSurveyJobQueue) {
        PersistenceManager pm = PersistenceFilter.getManager();
        pm.makePersistent(deviceSurveyJobQueue);
        return deviceSurveyJobQueue.getId();
    }

    /**
     * saves or updates a collection of instances
     *
     * @param itemList
     */
    public void save(List<DeviceSurveyJobQueue> itemList) {
        PersistenceManager pm = PersistenceFilter.getManager();
        pm.makePersistentAll(itemList);
    }

    /**
     * lists all instances
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<DeviceSurveyJobQueue> listAllJobsInQueue() {
        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(DeviceSurveyJobQueue.class);
        List<DeviceSurveyJobQueue> results = (List<DeviceSurveyJobQueue>) query
                .execute();
        return results;
    }

    /**
     * deletes all jobs for a given assignment
     *
     * @param assignmentId
     */
    public void deleteJob(Long assignmentId) {
        if (assignmentId != null) {
            List<DeviceSurveyJobQueue> results = listJobByAssignment(assignmentId);
            if (results != null) {
                delete(results);
            }
        }
    }

    /**
     * deletes all items in the list
     *
     * @param items
     */
    public void delete(List<DeviceSurveyJobQueue> items) {
        PersistenceManager pm = PersistenceFilter.getManager();
        pm.deletePersistentAll(items);
    }

    /**
     * lists all device job queue objects by assignment id
     *
     * @param assignmentId
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<DeviceSurveyJobQueue> listJobByAssignment(Long assignmentId) {
        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(DeviceSurveyJobQueue.class);
        String filterString = "assignmentId == assignmentIdParam";
        String paramString = "Long assignmentIdParam";

        query.setFilter(filterString);
        query.declareParameters(paramString);
        return (List<DeviceSurveyJobQueue>) query.execute(assignmentId);
    }

    /**
     * populates the assignment id for all items with the survey id specified THIS SHOULD NOT BE
     * USED IN NORMAL OPERATION
     *
     * @param surveyId
     * @param assignmentId
     */
    @SuppressWarnings("unchecked")
    public void updateAssignmentIdForSurvey(Long surveyId, Long assignmentId) {
        PersistenceManager pm = PersistenceFilter.getManager();

        javax.jdo.Query query = pm.newQuery(DeviceSurveyJobQueue.class);
        String filterString = "surveyID == surveyIdParam";
        String paramString = "Long surveyIdParam";

        query.setFilter(filterString);
        query.declareParameters(paramString);
        List<DeviceSurveyJobQueue> results = (List<DeviceSurveyJobQueue>) query
                .execute(surveyId);
        if (results != null) {
            for (DeviceSurveyJobQueue job : results) {
                job.setAssignmentId(assignmentId);
            }
            pm.makePersistentAll(results);
        }
    }

    /**
     * lists all instances that have expired prior to the time passed in
     *
     * @param expirationDate
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<DeviceSurveyJobQueue> listAssignmentsWithEarlierExpirationDate(
            Date expirationDate) {
        PersistenceManager pm = PersistenceFilter.getManager();

        javax.jdo.Query query = pm.newQuery(DeviceSurveyJobQueue.class);
        String filterString = "effectiveEndDate < expirationDateParam";
        String paramString = "java.util.Date expirationDateParam";
        query.declareImports("import java.util.Date");
        query.setFilter(filterString);
        query.declareParameters(paramString);
        return (List<DeviceSurveyJobQueue>) query.execute(expirationDate);
    }
}
