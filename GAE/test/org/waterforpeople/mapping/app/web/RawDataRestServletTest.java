package org.waterforpeople.mapping.app.web;

import com.gallatinsystems.surveyal.dao.SurveyedLocaleDao;
import com.gallatinsystems.surveyal.domain.SurveyedLocale;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.akvo.flow.api.app.DataStoreTestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.waterforpeople.mapping.app.web.dto.RawDataImportRequest;
import org.waterforpeople.mapping.domain.SurveyInstance;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RawDataRestServletTest {
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
        Long surveyId = dataStoreTestUtil.randomId();

        List<SurveyedLocale> dataPoints = dataStoreTestUtil.createDataPoints(surveyId, 1);
        List<SurveyInstance> formInstances = dataStoreTestUtil.createFormInstances(dataPoints, 1);

        SurveyInstance instance = formInstances.get(0);
        new SurveyedLocaleDao().delete(dataPoints.get(0));

        RawDataImportRequest importRequest = new RawDataImportRequest();
        importRequest.setSurveyInstanceId(instance.getKey().getId());

        RawDataRestServlet servlet = new RawDataRestServlet();
        Map<Long, String> validateMissingDataPoint = servlet.validateImportRequest(importRequest);
        assertEquals("Associated datapoint is missing [ datapoint id = " + instance.getSurveyedLocaleId() + "]", validateMissingDataPoint.get(instance.getKey().getId()));
    }

    @Test
    void testFormInstanceValidationOk() {
        Long surveyId = dataStoreTestUtil.randomId();

        List<SurveyedLocale> dataPoints = dataStoreTestUtil.createDataPoints(surveyId, 1);
        List<SurveyInstance> formInstances = dataStoreTestUtil.createFormInstances(dataPoints, 1);

        SurveyInstance instance = formInstances.get(0);

        RawDataImportRequest importRequest = new RawDataImportRequest();
        importRequest.setSurveyInstanceId(instance.getKey().getId());

        RawDataRestServlet servlet = new RawDataRestServlet();
        Map<Long, String> validateImportErrors = servlet.validateImportRequest(importRequest);
        assertTrue(validateImportErrors.isEmpty());
    }
}
