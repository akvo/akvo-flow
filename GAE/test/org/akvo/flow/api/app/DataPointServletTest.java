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

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.domain.BaseDomain;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DataPointServletTest {

    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
    private final List<Long> ALL_DATA_POINTS = Collections.singletonList(0L);


    @BeforeEach
    public void setUp() {
        helper.setUp();
    }

    @AfterEach
    public void tearDown() {
        helper.tearDown();
    }


    private DataPointAssignment createDataPointAssignment(Long assignmentId, Long deviceId, List<Long> dataPointIds, Long surveyId) {
        final DataPointAssignment dpa = new DataPointAssignment();
        final DataPointAssignmentDao dao = new DataPointAssignmentDao();

        dpa.setDeviceId(deviceId);
        dpa.setDataPointIds(dataPointIds);
        dpa.setSurveyAssignmentId(assignmentId);
        dpa.setSurveyId(surveyId);

        return dao.save(dpa);
    }

    private SurveyAssignment createAssignment(Long surveyId, List<Long> deviceIds, List<Long> formIds) {
        final SurveyAssignment sa = new SurveyAssignment();
        final SurveyAssignmentDao dao = new SurveyAssignmentDao();

        sa.setSurveyId(surveyId);
        sa.setDeviceIds(deviceIds);
        sa.setFormIds(formIds);

        return dao.save(sa);
    }

    private List<SurveyedLocale> createDataPoints(Long surveyId, int howMany) {
        final SurveyedLocaleDao dpDao = new SurveyedLocaleDao();
        final List<SurveyedLocale> datapoints = new ArrayList<>();
        for (int i = 0; i < howMany; i++) {
            SurveyedLocale dataPoint = new SurveyedLocale();
            dataPoint.setIdentifier(String.valueOf(i));
            dataPoint.setSurveyGroupId(surveyId);
            dataPoint.setDisplayName("dataPoint: " + i);
            datapoints.add(dataPoint);
        }
        return new ArrayList<>(dpDao.save(datapoints));
    }

    private Set<Long> getEntityIds(List<? extends BaseDomain> entities) {
        Set<Long> entityIds = new HashSet<>();
        for (BaseDomain o : entities) {
            entityIds.add(o.getKey().getId());
        }
        return entityIds;
    }

    private Long randomId() {
        Random rnd = new Random();
        return rnd.nextLong();
    }

    @Test
    public void datastoreReadWriteTest() {
        final Long assignmentId = randomId();
        final Long deviceId = randomId();

        final Long surveyId = randomId();

        createDataPointAssignment(assignmentId, deviceId, ALL_DATA_POINTS, surveyId);

        final DataPointAssignmentDao dataPointAssignmentDao = new DataPointAssignmentDao();
        final List<DataPointAssignment> dataPointAssignments = dataPointAssignmentDao.listByDeviceAndSurvey(deviceId, surveyId);
        assertFalse(dataPointAssignments.isEmpty());

        final DataPointAssignment assignment = dataPointAssignments.get(0);
        assertEquals(ALL_DATA_POINTS, assignment.getDataPointIds());
        assertEquals(deviceId, assignment.getDeviceId());
        assertEquals(surveyId, assignment.getSurveyId());


        final List<Long> deviceIds = Arrays.asList(randomId(), randomId());
        final List<Long> formIds = Arrays.asList(randomId(), randomId());

        createAssignment(surveyId, deviceIds, formIds);

        final SurveyAssignmentDao saDao = new SurveyAssignmentDao();
        final List<SurveyAssignment> surveyAssignments = saDao.listAllContainingDevice(deviceIds.get(1));
        assertFalse(surveyAssignments.isEmpty());

        final SurveyAssignment sa = surveyAssignments.get(0);
        assertEquals(surveyId, sa.getSurveyId());
        assertTrue(sa.getDeviceIds().contains(deviceIds.get(1)));
        assertEquals(formIds, sa.getFormIds());
    }

    @Test
    public void someDataPointsTest() {

        final Long surveyId = randomId();
        final List<SurveyedLocale> dataPoints = createDataPoints(surveyId, 10);

        final List<Long> selectedDataPointIds = dataPoints.stream()
                .filter(surveyedLocale -> surveyedLocale.getKey().getId() % 2 == 0)
                .map(surveyedLocale -> surveyedLocale.getKey().getId())
                .collect(Collectors.toList());

        final Long assignmentId = randomId();
        final Long deviceId = randomId();

        createDataPointAssignment(assignmentId, deviceId, selectedDataPointIds, surveyId);

        final DataPointServlet servlet = new DataPointServlet();

        final List<SurveyedLocale> someDataPoints = servlet.getDataPointList(surveyId, deviceId);

        final Set<Long> selectedIds = new HashSet<>(selectedDataPointIds);
        final Set<Long> foundIds = getEntityIds(someDataPoints);

        assertEquals(selectedIds, foundIds);
    }


    /**
     * New schema: `[0]` as data point list represent all data points
     **/
    @Test
    public void allDataPointsNewSchemaTest() {

        final Long surveyId = randomId();
        final List<SurveyedLocale> allDataPoints = createDataPoints(surveyId, 15);
        final Long assignmentId = randomId();
        final Long deviceId = randomId();
        final List<Long> deviceIds = Arrays.asList(deviceId);
        final List<Long> formIds = Arrays.asList(randomId());

        createAssignment(surveyId, deviceIds, formIds);
        createDataPointAssignment(assignmentId, deviceId, ALL_DATA_POINTS, surveyId);

        final DataPointServlet servlet = new DataPointServlet();
        final List<SurveyedLocale> foundDataPoints = servlet.getDataPointList(surveyId, deviceId);

        final Set<Long> allDataPointIds = getEntityIds(allDataPoints);
        final Set<Long> foundDataPointIds = getEntityIds(foundDataPoints);

        assertEquals(allDataPointIds, foundDataPointIds);
    }


    /**
     * Old schema: We have a `SurveyAssingment` but no `DataPointAssignment`
     */
    @Test
    public void allDataPointsOldSchemaTest() {
        final Long surveyId = randomId();
        final List<SurveyedLocale> allDataPoints = createDataPoints(surveyId, 20);
        final List<Long> deviceIds = Arrays.asList(randomId(), randomId());
        final List<Long> formIds = Arrays.asList(randomId(), randomId());

        final SurveyAssignment sa = createAssignment(surveyId, deviceIds, formIds);

        final DataPointServlet servlet = new DataPointServlet();
        final List<SurveyedLocale> foundDataPoints = servlet.getDataPointList(surveyId, deviceIds.get(0));

        final Set<Long> allDataPointIds = getEntityIds(allDataPoints);
        final Set<Long> foundDataPointIds = getEntityIds(foundDataPoints);

        assertEquals(allDataPointIds, foundDataPointIds);
    }

    @Test
    public void noAssignmentTest() {
        final Long surveyId = randomId();
        final DataPointServlet servlet = new DataPointServlet();
        final List<SurveyedLocale> foundDataPoints = servlet.getDataPointList(surveyId, randomId());
        assertTrue(foundDataPoints.isEmpty());
    }

    @Test
    public void testRetrieveDataPointsWithCursor() {
        final Long surveyId = randomId();
        final List<SurveyedLocale> dataPoints = createDataPoints(surveyId, 35);
        final Long assignmentId = randomId();
        final Long deviceId = randomId();
        final List<Long> deviceIds = Arrays.asList(deviceId);
        final List<Long> formIds = Arrays.asList(randomId());

        createAssignment(surveyId, deviceIds, formIds);
        createDataPointAssignment(assignmentId, deviceId, ALL_DATA_POINTS, surveyId);

        final DataPointServlet servlet = new DataPointServlet();

        // first batch
        final List<SurveyedLocale> firstBatchDataPoints = servlet.getDataPointList(surveyId, deviceId, null);
        assertEquals(30, firstBatchDataPoints.size());

        // remaining points retrieved based on cursor
        String cursorMarkEndOfFirstBatch = BaseDAO.getCursor(firstBatchDataPoints);
        final List<SurveyedLocale> secondBatchDataPoints = servlet.getDataPointList(surveyId, deviceId, cursorMarkEndOfFirstBatch);
        assertEquals(5, secondBatchDataPoints.size());

        // record cursor and update datapoints lastUpdateDateTime for 10 datapoints.
        String cursorMarkEndOfSecondBatch = BaseDAO.getCursor(secondBatchDataPoints);
        final SurveyedLocaleDao dpDao = new SurveyedLocaleDao();
        dpDao.save(dataPoints.subList(0,10));
        final List<SurveyedLocale> thirdBatchDataPoints = servlet.getDataPointList(surveyId, deviceId, cursorMarkEndOfSecondBatch);
        assertEquals(10, thirdBatchDataPoints.size(), "The cursor should retrieve updated datapoints");


        String finalCursor = BaseDAO.getCursor(thirdBatchDataPoints);
        final List<SurveyedLocale> finalBatchDataPoints = servlet.getDataPointList(surveyId, deviceId, finalCursor);

        assertEquals(0, finalBatchDataPoints.size(), "There should not be any more datapoints to retrieve");
    }

    @Test
    void testDataPointsRetrievalWithNoDatapointsPresent() {
        final Long surveyId = randomId();
        final Long assignmentId = randomId();
        final Long deviceId = randomId();
        final List<Long> deviceIds = Arrays.asList(deviceId);
        final List<Long> formIds = Arrays.asList(randomId());

        createAssignment(surveyId, deviceIds, formIds);
        createDataPointAssignment(assignmentId, deviceId, ALL_DATA_POINTS, surveyId);

        final DataPointServlet servlet = new DataPointServlet();
        final List<SurveyedLocale> noDataPointsRetrieved = servlet.getDataPointList(surveyId, deviceId, null);
        assertEquals(0, noDataPointsRetrieved.size());
        assertNull(BaseDAO.getCursor(noDataPointsRetrieved), "There should not be any cursor");
    }
}
