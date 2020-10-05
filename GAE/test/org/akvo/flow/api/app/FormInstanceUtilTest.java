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

import com.gallatinsystems.surveyal.domain.SurveyedLocale;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.waterforpeople.mapping.domain.SurveyInstance;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FormInstanceUtilTest {

    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
    private final List<Long> ALL_DATA_POINTS = Collections.singletonList(0L);
    private DataStoreTestUtil dataStoreTestUtil;
    private FormInstanceUtil formInstanceUtil;

    @BeforeEach
    public void setUp() {
        helper.setUp();
        dataStoreTestUtil = new DataStoreTestUtil();
        formInstanceUtil = new FormInstanceUtil();
    }

    @AfterEach
    public void tearDown() {
        helper.tearDown();
    }


    @Test
    public void anAssigmentWithAllDataPointsReturnsAllFormInstances() throws Exception {
        final Long assignmentId = dataStoreTestUtil.randomId();
        final Long deviceId = dataStoreTestUtil.randomId();
        final String androidId = "12345";
        final Long surveyId = dataStoreTestUtil.randomId();

        dataStoreTestUtil.createDevice(deviceId, androidId);
        dataStoreTestUtil.createDataPointAssignment(assignmentId, deviceId, ALL_DATA_POINTS, surveyId);
        List<SurveyedLocale> dataPoints = dataStoreTestUtil.createDataPoints(surveyId, 1);
        List<SurveyInstance> formInstances = dataStoreTestUtil.createFormInstances(dataPoints, 5);
        dataStoreTestUtil.createAnswers(formInstances);

        List<SurveyInstance> formInstances1 = formInstanceUtil.getFormInstances(androidId, dataPoints.get(0).getKey().getId(), null);

        assertEquals(dataStoreTestUtil.getEntityIds(formInstances), dataStoreTestUtil.getEntityIds(formInstances1));
    }

    @Test
    public void anAssignmentWithSomeDataPointsReturnOnlySelectedFormInstances() throws Exception {
        final Long assignmentId = dataStoreTestUtil.randomId();
        final Long deviceId = dataStoreTestUtil.randomId();
        final String androidId = "12345";
        final Long surveyId = dataStoreTestUtil.randomId();

        dataStoreTestUtil.createDevice(deviceId, androidId);
        List<SurveyedLocale> dataPoints = dataStoreTestUtil.createDataPoints(surveyId, 3);
        List<SurveyInstance> formInstances = dataStoreTestUtil.createFormInstances(dataPoints, 5);
        dataStoreTestUtil.createAnswers(formInstances);

        long selectedDataPointId = dataPoints.get(0).getKey().getId();
        dataStoreTestUtil.createDataPointAssignment(assignmentId, deviceId, Arrays.asList(selectedDataPointId), surveyId);

        List<SurveyInstance> formInstances1 = formInstanceUtil.getFormInstances(androidId, selectedDataPointId, null);

        assertEquals(5, formInstances1.size()); // 1 Datapoint assigned = 5 Form instances generated

        Set<Long> expectedDataPointId = new HashSet<>();
        expectedDataPointId.add(selectedDataPointId);
        Set<Long> formInstancesDataPointId = formInstances1.stream().map(SurveyInstance::getSurveyedLocaleId).collect(Collectors.toSet());

        assertEquals(expectedDataPointId, formInstancesDataPointId);

    }
}
