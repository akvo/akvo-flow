/*
 *  Copyright (C) 2020,2021 Stichting Akvo (Akvo Foundation)
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

package com.gallatinsystems.surveyal.dao;

import com.gallatinsystems.surveyal.domain.SurveyedLocale;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.akvo.flow.api.app.DataStoreTestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.SurveyInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SurveyInstanceDAOTest {
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
    public void getRegistrationFormDataShouldLimitTheNumberOfFormInstances() {
        Long surveyId = dataStoreTestUtil.randomId();
        Long creationFormId = dataStoreTestUtil.randomId();
        int numberOfDataPoints = 1;
        int totalFormInstancesPerDataPoint = 100;

        List<SurveyedLocale> dataPoints = dataStoreTestUtil.createDataPoints(surveyId, numberOfDataPoints);
        dataPoints.stream().forEach(dataPoint -> dataPoint.setCreationSurveyId(creationFormId));
        dataStoreTestUtil.saveDataPoints(dataPoints);

        List<SurveyInstance> formInstances = dataStoreTestUtil.createFormInstances(dataPoints, totalFormInstancesPerDataPoint);
        formInstances
                .stream()
                .collect(Collectors.groupingBy(SurveyInstance::getSurveyedLocaleId))
                .forEach((dataPointId, instances) -> instances.get(0).setSurveyId(creationFormId));

        dataStoreTestUtil.saveFormInstances(formInstances);

        List<SurveyInstance> registrationFormInstances = new SurveyInstanceDAO().getRegistrationFormData(dataPoints);

        assertNotNull(registrationFormInstances);
        assertFalse(registrationFormInstances.isEmpty());
        assertEquals(1, registrationFormInstances.size());
    }

    // https://github.com/akvo/akvo-flow/issues/3652
    // This test case is created specifically to address a fix for a data inconsistency
    // in the datastore where there exists a data point but no associated registration form
    // ideally we should clean up the datastore
    @Test
    public void getRegistrationFormDataShouldNotIncludeNullFormInstances() {
        Long surveyId = dataStoreTestUtil.randomId();
        Long registrationFormId = dataStoreTestUtil.randomId();
        int numberOfDataPoints = 5;
        int totalFormInstancesPerDataPoint = 10;

        List<SurveyedLocale> dataPoints = dataStoreTestUtil.createDataPoints(surveyId, registrationFormId, numberOfDataPoints);
        dataStoreTestUtil.createFormInstances(dataPoints, totalFormInstancesPerDataPoint);

        // we intentionally remove the registration form instances to simulate the data inconsistency
        List<SurveyInstance> registrationFormData = new SurveyInstanceDAO().getRegistrationFormData(dataPoints);
        new SurveyInstanceDAO().delete(registrationFormData);

        List<SurveyInstance> registrationFormDataAfterDeletion = new SurveyInstanceDAO().getRegistrationFormData(dataPoints);

        assertEquals(0, registrationFormDataAfterDeletion.stream().filter(surveyInstance -> surveyInstance == null).count());
        assertTrue(registrationFormDataAfterDeletion.isEmpty());
    }
}
