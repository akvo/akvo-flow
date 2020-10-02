/*
 *  Copyright (C) 2019 Stichting Akvo (Akvo Foundation)
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
import com.gallatinsystems.survey.dao.QuestionDao;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.surveyal.dao.SurveyedLocaleDao;
import com.gallatinsystems.surveyal.domain.SurveyedLocale;
import com.google.api.server.spi.config.Nullable;
import org.akvo.flow.dao.DataPointAssignmentDao;
import org.akvo.flow.dao.SurveyAssignmentDao;
import org.akvo.flow.domain.DataUtils;
import org.akvo.flow.domain.persistent.DataPointAssignment;
import org.akvo.flow.domain.persistent.SurveyAssignment;
import org.waterforpeople.mapping.app.web.dto.SurveyInstanceDto;
import org.waterforpeople.mapping.app.web.dto.SurveyedLocaleDto;
import org.waterforpeople.mapping.dao.QuestionAnswerStoreDao;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.QuestionAnswerStore;
import org.waterforpeople.mapping.domain.SurveyInstance;
import org.waterforpeople.mapping.serialization.response.MediaResponse;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.gallatinsystems.common.Constants.ALL_DATAPOINTS;


public class FormInstanceUtil {
    private static final Logger log = Logger.getLogger(FormInstanceUtil.class.getName());
    SurveyedLocaleDao surveyedLocaleDao = new SurveyedLocaleDao();
    DataPointAssignmentDao dataPointAssignmentDao = new DataPointAssignmentDao();

    public List<SurveyInstance> getFormInstances(String androidId, long dataPointId, String cursor) throws Exception {
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
            throw new Exception("No assignments found");
        }

        SurveyInstanceDAO siDAO = new SurveyInstanceDAO();

        return siDAO.listInstancesByLocale(dataPoint.getKey().getId(), null, null, 30,cursor);
    }
}
