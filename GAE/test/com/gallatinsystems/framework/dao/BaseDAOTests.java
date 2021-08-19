package com.gallatinsystems.framework.dao;

import com.gallatinsystems.surveyal.dao.SurveyedLocaleDao;
import com.gallatinsystems.surveyal.domain.SurveyedLocale;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.akvo.flow.api.app.DataStoreTestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BaseDAOTests {
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
    void testRetrieveMultipleEntities() {
        Long surveyId = dataStoreTestUtil.randomId();
        int numberOfDataPoints = 5;

        List<SurveyedLocale> dataPoints = dataStoreTestUtil.createDataPoints(surveyId, numberOfDataPoints);
        List<Long> ids = new ArrayList<>();
        for (SurveyedLocale datapoint : dataPoints) {
            ids.add(datapoint.getKey().getId());
        }

        SurveyedLocaleDao slDao = new SurveyedLocaleDao();
        List<SurveyedLocale> retrievedDataPoints = slDao.listByKeys(ids);

        assertEquals(5, retrievedDataPoints.size(), "Number of retrieved datapoints should be the same as created");
    }

    @Test
    void testRetrieveMultipleEntitiesWhenMissing() {
        Long surveyId = dataStoreTestUtil.randomId();
        int numberOfDataPoints = 5;

        List<SurveyedLocale> dataPoints = dataStoreTestUtil.createDataPoints(surveyId, numberOfDataPoints);
        List<Long> ids = new ArrayList<>();
        for (SurveyedLocale datapoint : dataPoints) {
            ids.add(datapoint.getKey().getId());
        }

        SurveyedLocaleDao slDao = new SurveyedLocaleDao();
        slDao.delete(dataPoints.get(0));

        List<SurveyedLocale> retrievedDataPoints = slDao.listByKeys(ids);

        assertEquals(4, retrievedDataPoints.size());
    }

    @Test
    void testRetrieveDuplicateEntries() {
        Long surveyId = dataStoreTestUtil.randomId();
        int numberOfDataPoints = 5;

        List<SurveyedLocale> dataPoints = dataStoreTestUtil.createDataPoints(surveyId, numberOfDataPoints);
        List<Long> ids = new ArrayList<>();
        for (SurveyedLocale datapoint : dataPoints) {
            ids.add(datapoint.getKey().getId());
        }

        // add duplicate
        ids.add(dataPoints.get(0).getKey().getId());

        SurveyedLocaleDao slDao = new SurveyedLocaleDao();
        List<SurveyedLocale> retrievedDataPoints = slDao.listByKeys(ids);

        assertEquals(5, retrievedDataPoints.size());
    }
}
