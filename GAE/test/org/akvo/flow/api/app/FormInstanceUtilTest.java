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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FormInstanceUtilTest {

    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
    private final List<Long> ALL_DATA_POINTS = Collections.singletonList(0L);
    private DataUtil dataUtil;
    private FormInstanceUtil formInstanceUtil;

    @BeforeEach
    public void setUp() {
        helper.setUp();
        dataUtil = new DataUtil();
        formInstanceUtil = new FormInstanceUtil();
    }

    @AfterEach
    public void tearDown() {
        helper.tearDown();
    }


    @Test
    public void getFormInstancesByDeviceAndDataPoint() throws Exception {
        final Long assignmentId = dataUtil.randomId();
        final Long deviceId = dataUtil.randomId();
        final String androidId = "12345";
        final Long surveyId = dataUtil.randomId();
        dataUtil.createDevice(deviceId, androidId);


        dataUtil.createDataPointAssignment(assignmentId, deviceId, ALL_DATA_POINTS, surveyId);
        dataUtil.createAssignment(surveyId, Arrays.asList(deviceId), Arrays.asList(dataUtil.randomId()));
        List<SurveyedLocale> dataPoints = dataUtil.createDataPoints(surveyId, 1);
        List<SurveyInstance> formInstances = dataUtil.createFormInstances(dataPoints, 5);
        dataUtil.createAnswers(formInstances);
        List<SurveyInstance> formInstances1 = formInstanceUtil.getFormInstances(androidId, dataPoints.get(0).getKey().getId(), null);

        assertEquals(dataUtil.getEntityIds(formInstances), dataUtil.getEntityIds(formInstances1));
/*


        List<SurveyInstance> result = formInstanceUtil.getFormInstances(androidId, dataPointId);


        final DataPointAssignmentDao dataPointAssignmentDao = new DataPointAssignmentDao();
        final List<DataPointAssignment> dataPointAssignments = dataPointAssignmentDao.listByDeviceAndSurvey(deviceId, surveyId);
        assertFalse(dataPointAssignments.isEmpty());

        final DataPointAssignment assignment = dataPointAssignments.get(0);
        assertEquals(ALL_DATA_POINTS, assignment.getDataPointIds());
        assertEquals(deviceId, assignment.getDeviceId());
        assertEquals(surveyId, assignment.getSurveyId());


        final List<Long> deviceIds = Arrays.asList(dataUtil.randomId(), dataUtil.randomId());
        final List<Long> formIds = Arrays.asList(dataUtil.randomId(), dataUtil.randomId());

        dataUtil.createAssignment(surveyId, deviceIds, formIds);

        final SurveyAssignmentDao saDao = new SurveyAssignmentDao();
        final List<SurveyAssignment> surveyAssignments = saDao.listAllContainingDevice(deviceIds.get(1));
        assertFalse(surveyAssignments.isEmpty());

        final SurveyAssignment sa = surveyAssignments.get(0);
        assertEquals(surveyId, sa.getSurveyId());
        assertTrue(sa.getDeviceIds().contains(deviceIds.get(1)));
        assertEquals(formIds, sa.getFormIds());*/
    }

}
