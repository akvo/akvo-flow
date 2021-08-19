/*
 *  Copyright (C) 2021 Stichting Akvo (Akvo Foundation)
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
package org.waterforpeople.mapping.app.web.dto;


import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.surveyal.dao.SurveyedLocaleDao;
import com.gallatinsystems.surveyal.domain.SurveyedLocale;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.akvo.flow.api.app.DataStoreTestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.waterforpeople.mapping.domain.SurveyInstance;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class RawDataImportRequestTest {
    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
    private DataStoreTestUtil dataStoreTestUtil;

    @BeforeEach
    public void setUp() {
        helper.setUp();
        dataStoreTestUtil = new DataStoreTestUtil();
    }

    @AfterEach
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    void testFormInstanceMissingDataPoint() {
        Long surveyId = dataStoreTestUtil.randomId();

        List<SurveyedLocale> dataPoints = dataStoreTestUtil.createDataPoints(surveyId, 1);
        List<SurveyInstance> formInstances = dataStoreTestUtil.createFormInstances(dataPoints, 1);

        SurveyInstance instance = formInstances.get(0);
        new SurveyedLocaleDao().delete(dataPoints.get(0));

        RawDataImportRequest importRequest = new RawDataImportRequest();
        importRequest.setSurveyInstanceId(instance.getKey().getId());

        List<String> validateMissingDataPoint = importRequest.validateRequest();
        assertEquals("Associated datapoint is missing [ datapoint id = " + instance.getSurveyedLocaleId() + "]", validateMissingDataPoint.get(0));
    }

    @Test
    void testMissingFormAndSurvey() {
        Long surveyId = dataStoreTestUtil.randomId();

        List<SurveyedLocale> dataPoints = dataStoreTestUtil.createDataPoints(surveyId, 1);
        List<SurveyInstance> formInstances = dataStoreTestUtil.createFormInstances(dataPoints, 1);

        SurveyInstance instance = formInstances.get(0);

        RawDataImportRequest importRequest = new RawDataImportRequest();
        importRequest.setAction(RawDataImportRequest.SAVE_SURVEY_INSTANCE_ACTION);
        importRequest.setSurveyInstanceId(instance.getKey().getId());
        importRequest.setSurveyId(DataStoreTestUtil.DEFAULT_REGISTRATION_FORM_ID);

        List<String> validateMissingForm = importRequest.validateRequest();
        assertTrue(validateMissingForm.size() > 0, "There should be an error");
        assertEquals("Form [id=" + instance.getSurveyId() + "] not found", validateMissingForm.get(0));

        Survey form = dataStoreTestUtil.createDefaultForm();
        form.setSurveyGroupId(surveyId);

        List<String> validateMissingSurvey = importRequest.validateRequest();
        assertTrue(validateMissingSurvey.size() > 0, "There should be an error");
        assertEquals("Survey [id=" + surveyId + "] not found", validateMissingSurvey.get(0));
    }

    @Test
    void testCannotImportMonitoringForm() {
        Long surveyId = dataStoreTestUtil.randomId();

        List<SurveyedLocale> dataPoints = dataStoreTestUtil.createDataPoints(surveyId, 1);
        List<SurveyInstance> formInstances = dataStoreTestUtil.createFormInstances(dataPoints, 2);

        SurveyInstance monitoringFormInstance = formInstances.get(1);

        RawDataImportRequest importRequest = new RawDataImportRequest();
        importRequest.setSurveyInstanceId(monitoringFormInstance.getKey().getId());

        List<String> validateMissingDataPoint = importRequest.validateRequest();
        assertEquals("Importing new data into a monitoring form is not supported at the moment", validateMissingDataPoint.get(0));
    }

    @Test
    void testImportingFormInstanceWrongForm() {
        Long surveyId = dataStoreTestUtil.randomId();

        List<SurveyedLocale> dataPoints = dataStoreTestUtil.createDataPoints(surveyId, 1);
        List<SurveyInstance> formInstances = dataStoreTestUtil.createFormInstances(dataPoints, 1);

        SurveyInstance monitoringFormInstance = formInstances.get(1);

        RawDataImportRequest importRequest = new RawDataImportRequest();
        importRequest.setSurveyInstanceId(monitoringFormInstance.getKey().getId());

        List<String> validateMissingDataPoint = importRequest.validateRequest();
        assertEquals("Wrong survey selected when importing instance id [" + importRequest.getSurveyInstanceId() + "]", validateMissingDataPoint.get(0));
    }

    @Test
    void testMissingFormInstance() {
        RawDataImportRequest importRequest = new RawDataImportRequest();
        importRequest.setAction(RawDataImportRequest.SAVE_SURVEY_INSTANCE_ACTION);
        importRequest.setSurveyInstanceId(dataStoreTestUtil.randomId());
        // note that we only validate that the form instance should exist
        // if the form instance id is present. its a valid use case to have no
        // form instance id. this is when importing new data

        List<String> validationErrors = importRequest.validateRequest();
        assertEquals("Form instance [id=" + importRequest.getSurveyInstanceId() + "] not found", validationErrors.get(0));
    }

    @Test
    void testFormInstanceValidationOk() {
        Long surveyId = dataStoreTestUtil.randomId();

        List<SurveyedLocale> dataPoints = dataStoreTestUtil.createDataPoints(surveyId, 1);
        List<SurveyInstance> formInstances = dataStoreTestUtil.createFormInstances(dataPoints, 1);

        SurveyInstance instance = formInstances.get(0);

        RawDataImportRequest importRequest = new RawDataImportRequest();
        importRequest.setSurveyInstanceId(instance.getKey().getId());

        List<String> validateImportErrors = importRequest.validateRequest();
        assertTrue(validateImportErrors.isEmpty());
    }
}
