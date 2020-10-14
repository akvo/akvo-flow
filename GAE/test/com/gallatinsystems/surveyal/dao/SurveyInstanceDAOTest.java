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
    public void getMonitoringDataShouldLimitTheNumberOfFormInstances() {
        Long surveyId = dataStoreTestUtil.randomId();
        Long creationSurveyId = dataStoreTestUtil.randomId();
        int numberOfDataPoints = 1;
        int totalFormInstancesPerDataPoint = 100;

        List<SurveyedLocale> dataPoints = dataStoreTestUtil.createDataPoints(surveyId, numberOfDataPoints);
        dataPoints.stream().forEach(dataPoint -> dataPoint.setCreationSurveyId(creationSurveyId));
        dataStoreTestUtil.saveDataPoints(dataPoints);

        List<SurveyInstance> formInstances = dataStoreTestUtil.createFormInstances(dataPoints, totalFormInstancesPerDataPoint);
        formInstances
                .stream()
                .collect(Collectors.groupingBy(SurveyInstance::getSurveyedLocaleId))
                .forEach((dataPointId, instances) -> instances.get(0).setSurveyId(creationSurveyId));

        dataStoreTestUtil.saveFormInstances(formInstances);

        int numberOfInstances = 3;
        List<SurveyInstance> monitoringData = new SurveyInstanceDAO().getMonitoringData(dataPoints, numberOfInstances);

        assertNotNull(monitoringData);
        assertFalse(monitoringData.isEmpty());
        assertEquals(numberOfInstances + 1, monitoringData.size());
    }

    // https://github.com/akvo/akvo-flow/issues/3652
    @Test
    public void getMonitoringDataShouldNotIncludeNullFormInstances() {
        Long surveyId = dataStoreTestUtil.randomId();
        Long creationSurveyId = dataStoreTestUtil.randomId();
        int numberOfDataPoints = 5;
        int totalFormInstancesPerDataPoint = 10;
        int numberOfInstances = 3;

        // We're explicitly not setting up a registration form instance
        List<SurveyedLocale> dataPoints = dataStoreTestUtil.createDataPoints(surveyId, numberOfDataPoints);
        dataPoints.stream().forEach(dataPoint -> dataPoint.setCreationSurveyId(creationSurveyId));
        dataStoreTestUtil.saveDataPoints(dataPoints);
        dataStoreTestUtil.createFormInstances(dataPoints, totalFormInstancesPerDataPoint);


        // Calling getMonitoringData included a null value in the list if the data point has no registration form instance
        List<SurveyInstance> monitoringData = new SurveyInstanceDAO().getMonitoringData(dataPoints, numberOfInstances);

        assertEquals(0, monitoringData.stream().filter(surveyInstance -> surveyInstance == null).count());
        assertTrue(monitoringData.isEmpty());
    }
}
