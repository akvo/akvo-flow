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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DataPointServletTest {

    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

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

    @Test
    public void datastoreReadWriteTest() {
        final Long assignmentId = 1L;
        final Long deviceId = 1L;
        final List<Long> dataPointIds = Collections.singletonList(0L);
        final Long surveyId = 1L;

        createDataPointAssignment(assignmentId, deviceId, dataPointIds, surveyId);

        final DataPointAssignmentDao dataPointAssignmentDao = new DataPointAssignmentDao();
        final List<DataPointAssignment> dataPointAssignments = dataPointAssignmentDao.listByDeviceAndSurvey(deviceId, surveyId);
        assertFalse(dataPointAssignments.isEmpty());

        final DataPointAssignment assignment = dataPointAssignments.get(0);
        assertEquals(dataPointIds, assignment.getDataPointIds());
        assertEquals(deviceId, assignment.getDeviceId());
        assertEquals(surveyId, assignment.getSurveyId());


        final List<Long> deviceIds = Arrays.asList(1L, 2L);
        final List<Long> formIds = Arrays.asList(100L, 200L);

        createAssignment(surveyId, deviceIds, formIds);

        final SurveyAssignmentDao saDao = new SurveyAssignmentDao();
        final List<SurveyAssignment> surveyAssignments = saDao.listAllContainingDevice(2L);
        assertFalse(surveyAssignments.isEmpty());

        final SurveyAssignment sa = surveyAssignments.get(0);
        assertEquals(surveyId, sa.getSurveyId());
        assertTrue(sa.getDeviceIds().contains(2L));
        assertEquals(formIds, sa.getFormIds());
    }

    @Test
    public void someDataPointsTest() {

        final Long surveyId = 100L;
        final List<SurveyedLocale> dataPoints = createDataPoints(surveyId, 10);

        final SurveyedLocale dataPoint = dataPoints.get(7);
        assertEquals("7", dataPoint.getIdentifier());

        final List<Long> selectedDataPointIds = Arrays.asList(dataPoints.get(5).getKey().getId(), dataPoints.get(6).getKey().getId());
        final Long assignmentId = 20L;
        final Long deviceId = 1L;

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

        final Long surveyId = 200L;
        final List<SurveyedLocale> allDataPoints = createDataPoints(surveyId, 15);
        final Long assignmentId = 30L;
        final Long deviceId = 2L;

        createDataPointAssignment(assignmentId, deviceId, Collections.singletonList(0L), surveyId);

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
        final Long surveyId = 300L;
        final List<SurveyedLocale> allDataPoints = createDataPoints(surveyId, 20);
        final List<Long> deviceIds = Arrays.asList(5L, 6L);
        final List<Long> formIds = Arrays.asList(10L, 11L);

        final SurveyAssignment sa = createAssignment(surveyId, deviceIds, formIds);

        final DataPointServlet servlet = new DataPointServlet();
        final List<SurveyedLocale> foundDataPoints = servlet.getDataPointList(surveyId, 6L);

        final Set<Long> allDataPointIds = getEntityIds(allDataPoints);
        final Set<Long> foundDataPointIds = getEntityIds(foundDataPoints);

        assertEquals(allDataPointIds, foundDataPointIds);
    }
}
