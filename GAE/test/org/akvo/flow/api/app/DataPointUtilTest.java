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

import com.gallatinsystems.device.domain.Device;
import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.surveyal.dao.SurveyedLocaleDao;
import com.gallatinsystems.surveyal.domain.SurveyedLocale;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.akvo.flow.dao.DataPointAssignmentDao;
import org.akvo.flow.dao.SurveyAssignmentDao;
import org.akvo.flow.domain.persistent.DataPointAssignment;
import org.akvo.flow.domain.persistent.SurveyAssignment;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import static org.akvo.flow.api.app.DataPointServlet.LIMIT_DATAPOINTS_30;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;



public class DataPointUtilTest {

    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
    private final List<Long> ALL_DATA_POINTS = Collections.singletonList(0L);
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
    public void datastoreReadWriteTest() {
        final Long assignmentId = dataStoreTestUtil.randomId();
        final Long deviceId = dataStoreTestUtil.randomId();

        final Long surveyId = dataStoreTestUtil.randomId();

        dataStoreTestUtil.createDataPointAssignment(assignmentId, deviceId, ALL_DATA_POINTS, surveyId);

        final DataPointAssignmentDao dataPointAssignmentDao = new DataPointAssignmentDao();
        final List<DataPointAssignment> dataPointAssignments = dataPointAssignmentDao.listByDeviceAndSurvey(deviceId, surveyId);
        assertFalse(dataPointAssignments.isEmpty());

        final DataPointAssignment assignment = dataPointAssignments.get(0);
        assertEquals(ALL_DATA_POINTS, assignment.getDataPointIds());
        assertEquals(deviceId, assignment.getDeviceId());
        assertEquals(surveyId, assignment.getSurveyId());


        final List<Long> deviceIds = Arrays.asList(dataStoreTestUtil.randomId(), dataStoreTestUtil.randomId());
        final List<Long> formIds = Arrays.asList(dataStoreTestUtil.randomId(), dataStoreTestUtil.randomId());

        dataStoreTestUtil.createAssignment(surveyId, deviceIds, formIds);

        final SurveyAssignmentDao saDao = new SurveyAssignmentDao();
        final List<SurveyAssignment> surveyAssignments = saDao.listAllContainingDevice(deviceIds.get(1));
        assertFalse(surveyAssignments.isEmpty());

        final SurveyAssignment sa = surveyAssignments.get(0);
        assertEquals(surveyId, sa.getSurveyId());
        assertTrue(sa.getDeviceIds().contains(deviceIds.get(1)));
        assertEquals(formIds, sa.getFormIds());
    }

    @Test
    public void someDataPointsTest() throws Exception {

        final Long surveyId = dataStoreTestUtil.randomId();
        final List<SurveyedLocale> dataPoints = dataStoreTestUtil.createDataPoints(surveyId, 10);

        final List<Long> selectedDataPointIds = dataPoints.stream()
                .filter(surveyedLocale -> surveyedLocale.getKey().getId() % 2 == 0)
                .map(surveyedLocale -> surveyedLocale.getKey().getId())
                .collect(Collectors.toList());

        final Long assignmentId = dataStoreTestUtil.randomId();
        final Long deviceId = dataStoreTestUtil.randomId();
        final String androidId = "ABCD";
        Device device = dataStoreTestUtil.createDevice(deviceId, androidId);

        dataStoreTestUtil.createDataPointAssignment(assignmentId, device.getKey().getId(), selectedDataPointIds, surveyId);

        final DataPointUtil dpu = new DataPointUtil();

        final List<SurveyedLocale> someDataPoints = dpu.getAssignedDataPoints(androidId, surveyId, null,LIMIT_DATAPOINTS_30);

        final Set<Long> selectedIds = new HashSet<>(selectedDataPointIds);
        final Set<Long> foundIds = dataStoreTestUtil.getEntityIds(someDataPoints);

        assertEquals(selectedIds, foundIds);
    }


    /**
     * New schema: `[0]` as data point list represent all data points
     **/
    @Test
    public void allDataPointsNewSchemaTest() throws Exception {

        final Long surveyId = dataStoreTestUtil.randomId();
        final List<SurveyedLocale> allDataPoints = dataStoreTestUtil.createDataPoints(surveyId, 15);
        final Long assignmentId = dataStoreTestUtil.randomId();
        final Long deviceId = dataStoreTestUtil.randomId();
        final List<Long> deviceIds = Arrays.asList(deviceId);
        final List<Long> formIds = Arrays.asList(dataStoreTestUtil.randomId());

        dataStoreTestUtil.createAssignment(surveyId, deviceIds, formIds);
        dataStoreTestUtil.createDataPointAssignment(assignmentId, deviceId, ALL_DATA_POINTS, surveyId);
        final String androidId = "ABCD";
        dataStoreTestUtil.createDevice(deviceId, androidId);

        final DataPointUtil dpu = new DataPointUtil();

        final List<SurveyedLocale> foundDataPoints = dpu.getAssignedDataPoints(androidId, surveyId, null, LIMIT_DATAPOINTS_30);

        final Set<Long> allDataPointIds = dataStoreTestUtil.getEntityIds(allDataPoints);
        final Set<Long> foundDataPointIds = dataStoreTestUtil.getEntityIds(foundDataPoints);

        assertEquals(allDataPointIds, foundDataPointIds);
    }

    @Test
    public void noAssignmentTest() throws Exception {
        final Long surveyId = dataStoreTestUtil.randomId();
        final DataPointUtil dpu = new DataPointUtil();
        final String androidId = "ABCD";
        dataStoreTestUtil.createDevice(dataStoreTestUtil.randomId(), androidId);
        assertThrows(NoDataPointsAssignedException.class, () -> dpu.getAssignedDataPoints(androidId, surveyId, null, LIMIT_DATAPOINTS_30));
    }

    @Test
    public void testRetrieveDataPointsWithCursor() throws Exception {
        final Long surveyId = dataStoreTestUtil.randomId();
        final List<SurveyedLocale> dataPoints = dataStoreTestUtil.createDataPoints(surveyId, 35);
        final Long assignmentId = dataStoreTestUtil.randomId();
        final Long deviceId = dataStoreTestUtil.randomId();
        final String androidId = "ABCD";
        dataStoreTestUtil.createDevice(deviceId, androidId);

        final List<Long> deviceIds = Arrays.asList(deviceId);
        final List<Long> formIds = Arrays.asList(dataStoreTestUtil.randomId());

        dataStoreTestUtil.createAssignment(surveyId, deviceIds, formIds);
        DataPointAssignment assignment = dataStoreTestUtil.createDataPointAssignment(assignmentId, deviceId, ALL_DATA_POINTS, surveyId);

        final DataPointUtil dpu = new DataPointUtil();

        // first batch
        final List<SurveyedLocale> firstBatchDataPoints = dpu.getAssignedDataPoints(androidId, surveyId, null, LIMIT_DATAPOINTS_30);
        assertEquals(30, firstBatchDataPoints.size());

        // remaining points retrieved based on cursor
        String cursorMarkEndOfFirstBatch = BaseDAO.getCursor(firstBatchDataPoints);
        final List<SurveyedLocale> secondBatchDataPoints = dpu.getAssignedDataPoints(androidId, surveyId, cursorMarkEndOfFirstBatch, LIMIT_DATAPOINTS_30);
        assertEquals(5, secondBatchDataPoints.size());

        // record cursor and update datapoints lastUpdateDateTime for 10 datapoints.
        String cursorMarkEndOfSecondBatch = BaseDAO.getCursor(secondBatchDataPoints);
        final SurveyedLocaleDao dpDao = new SurveyedLocaleDao();
        dpDao.save(dataPoints.subList(0, 10));
        final List<SurveyedLocale> thirdBatchDataPoints = dpu.getAssignedDataPoints(androidId, surveyId, cursorMarkEndOfSecondBatch, LIMIT_DATAPOINTS_30);
        assertEquals(10, thirdBatchDataPoints.size(), "The cursor should retrieve updated datapoints");


        String finalCursor = BaseDAO.getCursor(thirdBatchDataPoints);
        final List<SurveyedLocale> finalBatchDataPoints = dpu.getAssignedDataPoints(androidId, surveyId, finalCursor, LIMIT_DATAPOINTS_30);

        assertEquals(0, finalBatchDataPoints.size(), "There should not be any more datapoints to retrieve");
    }

    @Test
    void testDataPointsRetrievalWithNoDatapointsPresent() throws Exception {
        final Long surveyId = dataStoreTestUtil.randomId();
        final Long assignmentId = dataStoreTestUtil.randomId();
        final Long deviceId = dataStoreTestUtil.randomId();
        final String androidId = "ABCD";
        dataStoreTestUtil.createDevice(deviceId, androidId);

        final List<Long> deviceIds = Arrays.asList(deviceId);
        final List<Long> formIds = Arrays.asList(dataStoreTestUtil.randomId());

        dataStoreTestUtil.createAssignment(surveyId, deviceIds, formIds);
        DataPointAssignment assignment = dataStoreTestUtil.createDataPointAssignment(assignmentId, deviceId, ALL_DATA_POINTS, surveyId);

        final DataPointUtil dpu = new DataPointUtil();
        final List<SurveyedLocale> noDataPointsRetrieved = dpu.getAssignedDataPoints(androidId, surveyId, null, LIMIT_DATAPOINTS_30);
        assertEquals(0, noDataPointsRetrieved.size());
        assertNull(BaseDAO.getCursor(noDataPointsRetrieved), "There should not be any cursor");
    }
}
