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
import com.gallatinsystems.survey.domain.SurveyGroup;
import com.gallatinsystems.surveyal.dao.SurveyedLocaleDao;
import com.gallatinsystems.surveyal.domain.SurveyedLocale;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.akvo.flow.api.app.DataStoreTestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.SurveyInstance;

import java.util.List;

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
        SurveyGroup survey = dataStoreTestUtil.createSurveyGroup();
        Long surveyId = survey.getKey().getId();
        survey.setMonitoringGroup(true);
        survey.setNewLocaleSurveyId(DataStoreTestUtil.DEFAULT_REGISTRATION_FORM_ID);
        Survey form = dataStoreTestUtil.createDefaultRegistrationForm(survey.getKey().getId());

        List<SurveyedLocale> dataPoints = dataStoreTestUtil.createDataPoints(surveyId, 1);
        List<SurveyInstance> formInstances = dataStoreTestUtil.createFormInstances(dataPoints, 1);

        SurveyInstance instance = formInstances.get(0);
        instance.setSurveyedLocaleId(null);
        new SurveyInstanceDAO().save(instance);

        RawDataImportRequest importRequest = new RawDataImportRequest();
        importRequest.setAction(RawDataImportRequest.SAVE_SURVEY_INSTANCE_ACTION);
        importRequest.setSurveyInstanceId(instance.getKey().getId());
        importRequest.setSurveyId(form.getKey().getId());

        List<String> validateDataPointNotAssociated = importRequest.validateRequest();
        assertTrue(validateDataPointNotAssociated.size() > 0, "There should be an error");
        assertEquals("Form instance [id=" + importRequest.getSurveyInstanceId() + "] does not have an associated datapoint", validateDataPointNotAssociated.get(0));

        instance.setSurveyedLocaleId(dataPoints.get(0).getKey().getId());
        new SurveyInstanceDAO().save(instance);
        new SurveyedLocaleDao().delete(dataPoints.get(0));

        List<String> validateMissingDataPoint = importRequest.validateRequest();
        assertTrue(validateMissingDataPoint.size() > 0, "There should be an error");
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

        dataStoreTestUtil.createDefaultRegistrationForm(surveyId);

        List<String> validateMissingSurvey = importRequest.validateRequest();
        assertTrue(validateMissingSurvey.size() > 0, "There should be an error");
        assertEquals("Survey [id=" + surveyId + "] not found", validateMissingSurvey.get(0));
    }

    @Test
    void testCannotImportMonitoringForm() {
        SurveyGroup survey = dataStoreTestUtil.createSurveyGroup();
        survey.setMonitoringGroup(true);
        survey.setNewLocaleSurveyId(DataStoreTestUtil.DEFAULT_REGISTRATION_FORM_ID);
        dataStoreTestUtil.createDefaultRegistrationForm(survey.getKey().getId());
        dataStoreTestUtil.createDefaultMonitoringForm(survey.getKey().getId());

        RawDataImportRequest importRequest = new RawDataImportRequest();
        importRequest.setAction(RawDataImportRequest.SAVE_SURVEY_INSTANCE_ACTION);
        importRequest.setSurveyId(DataStoreTestUtil.DEFAULT_MONITORING_FORM_ID);

        List<String> validateMonitoringFormImport = importRequest.validateRequest();
        assertTrue(validateMonitoringFormImport.size() > 0, "There should be an error");
        assertEquals("Importing new data into a monitoring form is not supported at the moment", validateMonitoringFormImport.get(0));
    }

    @Test
    void testImportMonitoringFormExistingInstances() {
        SurveyGroup survey = dataStoreTestUtil.createSurveyGroup();
        survey.setMonitoringGroup(true);
        survey.setNewLocaleSurveyId(DataStoreTestUtil.DEFAULT_REGISTRATION_FORM_ID);
        dataStoreTestUtil.createDefaultRegistrationForm(survey.getKey().getId());
        dataStoreTestUtil.createDefaultMonitoringForm(survey.getKey().getId());

        List<SurveyedLocale> dataPoints = dataStoreTestUtil.createDataPoints(survey.getKey().getId(), 1);
        List<SurveyInstance> formInstances = dataStoreTestUtil.createFormInstances(dataPoints, 2);

        SurveyInstance monitoringFormInstance = formInstances.get(1);

        RawDataImportRequest importRequest = new RawDataImportRequest();
        importRequest.setAction(RawDataImportRequest.SAVE_SURVEY_INSTANCE_ACTION);
        importRequest.setSurveyInstanceId(monitoringFormInstance.getKey().getId());
        importRequest.setSurveyId(DataStoreTestUtil.DEFAULT_MONITORING_FORM_ID);

        List<String> validateImportErrors = importRequest.validateRequest();
        assertTrue(validateImportErrors.isEmpty());
    }

    @Test
    void testImportingFormInstanceWrongForm() {
        SurveyGroup survey = dataStoreTestUtil.createSurveyGroup();
        long surveyId = survey.getKey().getId();
        List<SurveyedLocale> dataPoints = dataStoreTestUtil.createDataPoints(surveyId, 1);
        List<SurveyInstance> formInstances = dataStoreTestUtil.createFormInstances(dataPoints, 1);
        Survey monitoringForm = dataStoreTestUtil.createDefaultMonitoringForm(surveyId);

        SurveyInstance registrationFormInstance = formInstances.get(0);

        RawDataImportRequest importRequest = new RawDataImportRequest();
        importRequest.setAction(RawDataImportRequest.SAVE_SURVEY_INSTANCE_ACTION);
        importRequest.setSurveyInstanceId(registrationFormInstance.getKey().getId());
        importRequest.setSurveyId(monitoringForm.getKey().getId());

        List<String> validateImportWrongForm = importRequest.validateRequest();
        assertTrue(validateImportWrongForm.size() > 0, "There should be an error");
        assertEquals("Wrong survey selected when importing instance [id=" + importRequest.getSurveyInstanceId() + "]", validateImportWrongForm.get(0));
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
        SurveyGroup survey = dataStoreTestUtil.createSurveyGroup();
        survey.setMonitoringGroup(true);
        survey.setNewLocaleSurveyId(DataStoreTestUtil.DEFAULT_REGISTRATION_FORM_ID);
        SurveyGroup savedSurvey = new SurveyedLocaleDao().save(survey);
        Long surveyId = savedSurvey.getKey().getId();
        Survey form = dataStoreTestUtil.createDefaultRegistrationForm(surveyId);

        List<SurveyedLocale> dataPoints = dataStoreTestUtil.createDataPoints(surveyId, 1);
        List<SurveyInstance> formInstances = dataStoreTestUtil.createFormInstances(dataPoints, 1);

        SurveyInstance instance = formInstances.get(0);

        RawDataImportRequest importRequest = new RawDataImportRequest();
        importRequest.setAction(RawDataImportRequest.SAVE_SURVEY_INSTANCE_ACTION);
        importRequest.setSurveyInstanceId(instance.getKey().getId());
        importRequest.setSurveyId(form.getKey().getId());

        List<String> validateImportErrors = importRequest.validateRequest();
        assertTrue(validateImportErrors.isEmpty());
    }
}
