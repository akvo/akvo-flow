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

import java.util.Date;
import java.util.List;

import javax.jdo.PersistenceManager;

import com.gallatinsystems.device.domain.Device;
import com.gallatinsystems.device.domain.DeviceSurveyJobQueue;
import com.gallatinsystems.framework.servlet.PersistenceFilter;

/**
 * dao for saving deviceSurveyJobQueue objects.
 */
public class DeviceSurveyJobQueueDAO {

    /**
     * lists all objects for a given imei or phoneNumber
     * 
     * @param devicePhoneNumber
     * @param imei
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<DeviceSurveyJobQueue> get(String devicePhoneNumber, String imei) {
        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(DeviceSurveyJobQueue.class);

        List<DeviceSurveyJobQueue> results = null;
        // lookup by imei first and filter out NO_IMEI ones
        if (imei != null && !Device.NO_IMEI.equals(imei)) {
            query.setFilter("imei == imeiParam");
            query.declareParameters("String imeiParam");
            results = (List<DeviceSurveyJobQueue>) query.execute(imei);
        }
        if (results == null || results.size() == 0) {
            // fall back to phonenumber
            query.setFilter("devicePhoneNumber == devicePhoneNumberParam");
            query.declareParameters("String devicePhoneNumberParam");
            results = (List<DeviceSurveyJobQueue>) query.execute(devicePhoneNumber);
        }
        return results;
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
     * delete a job with the phone number and survey id passed in
     * 
     * @param phoneNumbers
     * @param surveyId
     */
    @SuppressWarnings("unchecked")
    public void deleteJob(String phone, Long surveyId) {
        PersistenceManager pm = PersistenceFilter.getManager();
        if (phone != null && surveyId != null) {

            javax.jdo.Query query = pm.newQuery(DeviceSurveyJobQueue.class);
            String filterString = "devicePhoneNumber == devicePhoneParam && surveyID == surveyIdParam";
            String paramString = "String devicePhoneParam, Long surveyIdParam";

            query.setFilter(filterString);
            query.declareParameters(paramString);
            List<DeviceSurveyJobQueue> results = (List<DeviceSurveyJobQueue>) query
                    .execute(phone, surveyId);
            if (results != null) {
                pm.deletePersistentAll(results);
            }

        }
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
