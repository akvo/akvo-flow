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
package org.akvo.flow.rest;

import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.survey.domain.SurveyGroup;
import com.gallatinsystems.surveyal.dao.SurveyedLocaleDao;
import com.gallatinsystems.surveyal.domain.SurveyedLocale;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import java.io.IOException;
import org.akvo.flow.api.app.DataStoreTestUtil;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.SurveyInstance;

class ImageUploadRestServiceTest {

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
    void testUploadImageWhenFormNotFound() throws IOException {
        ImageUploadRestService service = new ImageUploadRestService();

        ImageUploadRestService.Response response = service.uploadImage("123", "123", "123", null);

        assertEquals(400, response.getCode());
        assertEquals("Form not found", response.getMessage());
    }

    @Test
    void testUploadImageWhenQuestionNotFound() throws IOException {
        Survey form = dataStoreTestUtil.createSurvey(dataStoreTestUtil.createSurveyGroup());
        String formId = form.getObjectId() + "";

        ImageUploadRestService service = new ImageUploadRestService();
        ImageUploadRestService.Response response = service.uploadImage("123", "123", formId, null);

        assertEquals(400, response.getCode());
        assertEquals("Question not found", response.getMessage());
    }

    @Test
    void testUploadImageWhenQuestionFromAnotherSurvey() throws IOException {
        SurveyGroup surveyGroup = dataStoreTestUtil.createSurveyGroup();
        Survey form = dataStoreTestUtil.createSurvey(surveyGroup);
        Survey form2 = dataStoreTestUtil.createSurvey(surveyGroup);
        String formId = form.getObjectId() + "";
        String questionId = dataStoreTestUtil.createQuestion(form2, dataStoreTestUtil.createQuestionGroup(form2, 0, false).getKey().getId(), Question.Type.DATE, false).getKey().getId() + "";

        ImageUploadRestService service = new ImageUploadRestService();
        ImageUploadRestService.Response response = service.uploadImage("123", questionId, formId, null);

        assertEquals(400, response.getCode());
        assertEquals("Question does not belong to that form", response.getMessage());
    }

    @Test
    void testUploadImageWhenFormInstanceNotFound() throws IOException {
        SurveyGroup surveyGroup = dataStoreTestUtil.createSurveyGroup();
        Survey form = dataStoreTestUtil.createSurvey(surveyGroup);
        String formId = form.getObjectId() + "";
        Survey form2 = dataStoreTestUtil.createSurvey(surveyGroup);
        String questionId = dataStoreTestUtil.createQuestion(form, dataStoreTestUtil.createQuestionGroup(form, 0, false).getKey().getId(), Question.Type.DATE, false).getKey().getId() + "";
        SurveyedLocale dataPoint = dataStoreTestUtil.createDataPoint(surveyGroup.getObjectId(), form2.getObjectId(), 0);
        new SurveyedLocaleDao().save(dataPoint);
        SurveyInstance surveyInstance = dataStoreTestUtil.createSurveyInstance(dataPoint, 0);
        new SurveyInstanceDAO().save(surveyInstance);
        String formInstanceId = surveyInstance.getKey().getId() + "";

        ImageUploadRestService service = new ImageUploadRestService();
        ImageUploadRestService.Response response = service.uploadImage(formInstanceId, questionId, formId, null);

        assertEquals(400, response.getCode());
        assertEquals("FormInstance does not belong to that form", response.getMessage());
    }

    @Test
    void testUploadImageWhenFileIsNull() throws IOException {
        SurveyGroup surveyGroup = dataStoreTestUtil.createSurveyGroup();
        Survey form = dataStoreTestUtil.createSurvey(surveyGroup);
        String formId = form.getObjectId() + "";
        String questionId = dataStoreTestUtil.createQuestion(form, dataStoreTestUtil.createQuestionGroup(form, 0, false).getKey().getId(), Question.Type.DATE, false).getKey().getId() + "";
        SurveyedLocale dataPoint = dataStoreTestUtil.createDataPoint(surveyGroup.getObjectId(), form.getObjectId(), 0);
        new SurveyedLocaleDao().save(dataPoint);
        SurveyInstance surveyInstance = dataStoreTestUtil.createSurveyInstance(dataPoint, 0);
        new SurveyInstanceDAO().save(surveyInstance);
        String formInstanceId = surveyInstance.getKey().getId() + "";

        ImageUploadRestService service = new ImageUploadRestService();
        ImageUploadRestService.Response response = service.uploadImage(formInstanceId, questionId, formId, null);

        assertEquals(400, response.getCode());
        assertEquals("File is not valid", response.getMessage());
    }
}
