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
import org.waterforpeople.mapping.app.gwt.client.surveyinstance.QuestionAnswerStoreDto;
import org.waterforpeople.mapping.app.gwt.client.surveyinstance.SurveyInstanceDto;
import org.waterforpeople.mapping.dao.QuestionAnswerStoreDao;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.QuestionAnswerStore;
import org.waterforpeople.mapping.domain.SurveyInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


public class FormInstanceUtil {
    private static final Logger log = Logger.getLogger(FormInstanceUtil.class.getName());
    SurveyedLocaleDao surveyedLocaleDao = new SurveyedLocaleDao();
    DataPointAssignmentDao dataPointAssignmentDao = new DataPointAssignmentDao();
    QuestionAnswerStoreDao qasDAO = new QuestionAnswerStoreDao();

    public List<SurveyInstance> getFormInstances(String androidId, String dataPointIdentifier, Integer pageSize, String cursor) throws Exception {
        DeviceDAO deviceDao = new DeviceDAO();
        Device device = deviceDao.getDevice(androidId, null, null);
        if (device == null) {
            throw new Exception("Device not found");
        }
        log.fine("Found device: " + device);


        SurveyedLocale dataPoint = surveyedLocaleDao.getByIdentifier(dataPointIdentifier);
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

    public List<SurveyInstanceDto> getFormInstancesDtoList(List<SurveyInstance> formInstances) {
        List<SurveyInstanceDto> formInstancesDTO = new ArrayList<>();
        for(SurveyInstance si : formInstances){
            SurveyInstanceDto siDTO = new SurveyInstanceDto();
            siDTO.setUuid(si.getUuid());
            siDTO.setSubmitter(si.getSubmitterName());
            siDTO.setSurveyId(si.getSurveyId());
            siDTO.setCollectionDate(si.getCollectionDate());
            List<QuestionAnswerStoreDto> qasDTOList = new ArrayList<>();
            List<QuestionAnswerStore> questionAnswerStoreList = qasDAO.listBySurveyInstance(si.getKey().getId());
            for (QuestionAnswerStore qas : questionAnswerStoreList){
                QuestionAnswerStoreDto qasDTO = new QuestionAnswerStoreDto();
                qasDTO.setT(qas.getType());
                qasDTO.setQ(qas.getQuestionID());
                qasDTO.setA(qas.getValue());
                qasDTOList.add(qasDTO);
            }
            siDTO.setQasList(qasDTOList);
            formInstancesDTO.add(siDTO);
        }
        return formInstancesDTO;
    }
}
