/*
 *  Copyright (C) 2020 Stichting Akvo (Akvo Foundation)
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

package org.akvo.flow.api.app;

import com.gallatinsystems.device.dao.DeviceDAO;
import com.gallatinsystems.device.domain.Device;
import com.gallatinsystems.surveyal.dao.SurveyedLocaleDao;
import com.gallatinsystems.surveyal.domain.SurveyedLocale;
import org.akvo.flow.dao.DataPointAssignmentDao;
import org.akvo.flow.domain.persistent.DataPointAssignment;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.SurveyInstance;

import java.util.List;
import java.util.logging.Logger;


public class FormInstanceUtil {
    private static final Logger log = Logger.getLogger(FormInstanceUtil.class.getName());
    SurveyedLocaleDao surveyedLocaleDao = new SurveyedLocaleDao();
    DataPointAssignmentDao dataPointAssignmentDao = new DataPointAssignmentDao();

    public List<SurveyInstance> getFormInstances(String androidId, long dataPointId, Integer pageSize, String cursor) throws Exception {
        DeviceDAO deviceDao = new DeviceDAO();
        Device device = deviceDao.getDevice(androidId, null, null);
        if (device == null) {
            throw new Exception("Device not found");
        }
        log.fine("Found device: " + device);


        SurveyedLocale dataPoint = surveyedLocaleDao.getById(dataPointId);
        if (dataPoint == null) {
            throw new Exception("Datapoint not found");
        }
        log.fine("Datapoint: " + dataPoint);

        List<DataPointAssignment> dataPointAssignments =
                dataPointAssignmentDao.listByDeviceAndSurvey(device.getKey().getId(), dataPoint.getSurveyGroupId());
        if (dataPointAssignments.isEmpty()) {
            throw new NoDataPointsAssignedException("No assignments found");
        }

        SurveyInstanceDAO siDAO = new SurveyInstanceDAO();

        return siDAO.listInstancesByLocale(dataPoint.getKey().getId(), null, null, pageSize, cursor);
    }
}
