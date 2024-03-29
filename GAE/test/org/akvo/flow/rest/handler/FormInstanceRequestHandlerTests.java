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

package org.akvo.flow.rest.handler;

import com.gallatinsystems.surveyal.dao.SurveyedLocaleDao;
import com.gallatinsystems.surveyal.domain.SurveyedLocale;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.akvo.flow.api.app.DataStoreTestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.waterforpeople.mapping.app.gwt.client.surveyinstance.SurveyInstanceDto;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.SurveyInstance;

import java.util.List;

import static org.akvo.flow.api.app.DataStoreTestUtil.DEFAULT_REGISTRATION_FORM_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class FormInstanceRequestHandlerTests {

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
    public void testDeleteMonitoringFormInstance() {
        Long surveyId = dataStoreTestUtil.randomId();
        List<SurveyedLocale> singleDataPointList = dataStoreTestUtil.createDataPoints(surveyId, 1);
        List<SurveyInstance> formInstances = dataStoreTestUtil.createFormInstances(singleDataPointList, 2);

        FormInstanceRequestHandler requestHandler = new FormInstanceRequestHandler();
        requestHandler.deleteFormInstance(formInstances.get(1));

        SurveyInstanceDAO siDao = new SurveyInstanceDAO();
        List<SurveyInstance> remainingFormInstances = siDao.listInstancesByLocale(singleDataPointList.get(0).getKey().getId(), null, null, null);
        assertEquals(1, remainingFormInstances.size(), "Expecting one form instance to remain");
        assertEquals(DEFAULT_REGISTRATION_FORM_ID, remainingFormInstances.get(0).getSurveyId());
    }

    @Test
    public void testDeleteRegistrationFormInstance() {
        Long surveyId = dataStoreTestUtil.randomId();
        Long registrationFormId = dataStoreTestUtil.randomId();
        List<SurveyedLocale> singleDataPointList = dataStoreTestUtil.createDataPoints(surveyId, registrationFormId, 1);
        List<SurveyInstance> formInstances = dataStoreTestUtil.createFormInstances(singleDataPointList, 3);

        long dataPointId = singleDataPointList.get(0).getKey().getId();
        FormInstanceRequestHandler requestHandler = new FormInstanceRequestHandler();
        SurveyInstance registrationFormInstance = formInstances.get(0);
        requestHandler.deleteFormInstance(registrationFormInstance);

        SurveyInstanceDAO siDao = new SurveyInstanceDAO();
        List<SurveyInstance> remainingFormInstance = siDao.listInstancesByLocale(dataPointId, null, null, null);
        assertEquals(0, remainingFormInstance.size(), "Expecting no form instance to remain");
        assertNull(new SurveyedLocaleDao().getByKey(dataPointId), "Expect datapoint to have been deleted");
    }

    @Test
    public void testDeleteOrphanedInstance() {
        Long surveyId = dataStoreTestUtil.randomId();
        List<SurveyedLocale> singleDataPointList = dataStoreTestUtil.createDataPoints(surveyId, 1);
        List<SurveyInstance> formInstances = dataStoreTestUtil.createFormInstances(singleDataPointList, 1);

        SurveyedLocaleDao slDao = new SurveyedLocaleDao();
        slDao.deleteSurveyedLocale(singleDataPointList.get(0));
        formInstances.get(0).setSurveyedLocaleId(null);

        FormInstanceRequestHandler requestHandler = new FormInstanceRequestHandler();
        requestHandler.deleteFormInstance(formInstances.get(0));

        SurveyInstanceDAO siDao = new SurveyInstanceDAO();
        List<SurveyInstance> remainingFormInstances = siDao.listSurveyInstanceBySurvey(DEFAULT_REGISTRATION_FORM_ID, null);
        assertEquals(0, remainingFormInstances.size(), "Expecting no form instance to remain");
    }
}
