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

package com.gallatinsystems.device;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.gallatinsystems.device.domain.DeviceSurveyJobQueue;
import com.gallatinsystems.framework.analytics.summarization.DataSummarizer;
import com.gallatinsystems.framework.domain.DataChangeRecord;
import com.gallatinsystems.survey.dao.DeviceSurveyJobQueueDAO;

/**
 * deletes unneeded DeviceSurveyJobQueue records based on an update to SurveyAssignment
 * 
 * @author Christopher Fagiani
 */
public class DeviceSurveyJobQueueUpdater implements DataSummarizer {

    private DeviceSurveyJobQueueDAO deviceSurveyJobQueueDao;
    @SuppressWarnings("unused")
    private static Logger logger = Logger
            .getLogger(DeviceSurveyJobQueueUpdater.class.getName());

    public DeviceSurveyJobQueueUpdater() {
        deviceSurveyJobQueueDao = new DeviceSurveyJobQueueDAO();
    }

    @Override
    public String getCursor() {
        // no-op
        return null;
    }

    @Override
    public boolean performSummarization(String key, String type, String value,
            Integer offset, String cursor) {
        DataChangeRecord change = new DataChangeRecord(value);
        deleteRecords(change.getOldVal(), change.getId());
        return true;
    }

    /**
     * unpacks the packed string an deletes the necessary records from the DeviceSurveyJobQueue
     * table
     * 
     * @param packedString
     * @param assignmentId
     */
    private void deleteRecords(String packedString, String assignmentId) {
        if (packedString != null) {
            List<DeviceSurveyJobQueue> queueItems = deviceSurveyJobQueueDao
                    .listJobByAssignment(new Long(assignmentId));
            List<DeviceSurveyJobQueue> itemsToDelete = new ArrayList<DeviceSurveyJobQueue>();
            if (packedString.contains("d")) {
                String deviceIds = packedString.substring(packedString
                        .indexOf("d") + 1, packedString.indexOf("s"));
                String[] nums = {
                    deviceIds
                };
                if (deviceIds.contains("xx")) {
                    nums = deviceIds.split("xx");
                }
                for (int i = 0; i < nums.length; i++) {
                    if (nums[i].trim().length() > 0) {
                        for (DeviceSurveyJobQueue job : queueItems) {
                            if (job.getDevicePhoneNumber().equals(nums[i])) {
                                itemsToDelete.add(job);
                            }
                        }
                    }
                }
            }
            if (packedString.contains("s")) {
                String surveyIds = packedString.substring(packedString
                        .indexOf("s") + 1);
                String[] ids = {
                    surveyIds
                };
                if (surveyIds.contains("xx")) {
                    ids = surveyIds.split("xx");
                }
                for (int i = 0; i < ids.length; i++) {
                    if (ids[i].trim().length() > 0) {
                        for (DeviceSurveyJobQueue job : queueItems) {
                            if (job.getSurveyID().toString().equals(ids[i])) {
                                itemsToDelete.add(job);
                            }
                        }
                    }
                }
            }
            if (itemsToDelete != null && itemsToDelete.size() > 0) {
                deviceSurveyJobQueueDao.delete(itemsToDelete);
            }
        }
    }
}
