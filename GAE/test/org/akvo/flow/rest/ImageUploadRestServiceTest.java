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

import com.gallatinsystems.common.util.S3Util;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.mockStatic;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.waterforpeople.mapping.dao.QuestionAnswerStoreDao;
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
    void testUploadImageWhenFormInstanceNotFound() throws IOException {
        ImageUploadRestService service = new ImageUploadRestService();

        assertThrows(ResponseStatusException.class, () -> service.uploadImage(123L, 123L, null), "FormInstance not found");
    }

    @Test
    void testUploadImageWhenFormNotFound() throws IOException {
        SurveyGroup surveyGroup = dataStoreTestUtil.createSurveyGroup();
        Survey form = dataStoreTestUtil.createSurvey(surveyGroup);
        SurveyedLocale dataPoint = dataStoreTestUtil.createDataPoint(surveyGroup.getObjectId(), form.getObjectId(), 0);
        new SurveyedLocaleDao().save(dataPoint);
        SurveyInstance surveyInstance = dataStoreTestUtil.createSurveyInstance(dataPoint, 0);
        surveyInstance.setSurveyId(123333L);
        new SurveyInstanceDAO().save(surveyInstance);
        Long formInstanceId = surveyInstance.getKey().getId();

        ImageUploadRestService service = new ImageUploadRestService();

        assertThrows(ResponseStatusException.class, () -> service.uploadImage(123L, formInstanceId, null), "Form not found");
    }

    @Test
    void testUploadImageWhenQuestionNotFound() throws IOException {
        SurveyGroup surveyGroup = dataStoreTestUtil.createSurveyGroup();
        Survey form = dataStoreTestUtil.createSurvey(surveyGroup);
        SurveyedLocale dataPoint = dataStoreTestUtil.createDataPoint(surveyGroup.getObjectId(), form.getObjectId(), 0);
        new SurveyedLocaleDao().save(dataPoint);
        SurveyInstance surveyInstance = dataStoreTestUtil.createSurveyInstance(dataPoint, 0);
        new SurveyInstanceDAO().save(surveyInstance);
        Long formInstanceId = surveyInstance.getKey().getId();

        ImageUploadRestService service = new ImageUploadRestService();

        assertThrows(ResponseStatusException.class, () -> service.uploadImage(123L, formInstanceId, null), "Question not found");
    }

    @Test
    void testUploadImageWhenQuestionFromAnotherSurvey() throws IOException {
        SurveyGroup surveyGroup = dataStoreTestUtil.createSurveyGroup();
        Survey form = dataStoreTestUtil.createSurvey(surveyGroup);
        Survey form2 = dataStoreTestUtil.createSurvey(surveyGroup);
        Long questionId = dataStoreTestUtil.createQuestion(form2, dataStoreTestUtil.createQuestionGroup(form2, 0, false).getKey().getId(), Question.Type.DATE, false).getKey().getId();
        SurveyedLocale dataPoint = dataStoreTestUtil.createDataPoint(surveyGroup.getObjectId(), form.getObjectId(), 0);
        new SurveyedLocaleDao().save(dataPoint);
        SurveyInstance surveyInstance = dataStoreTestUtil.createSurveyInstance(dataPoint, 0);
        new SurveyInstanceDAO().save(surveyInstance);
        Long formInstanceId = surveyInstance.getKey().getId();

        ImageUploadRestService service = new ImageUploadRestService();

        assertThrows(ResponseStatusException.class, () -> service.uploadImage(questionId, formInstanceId, null), "Question does not belong to that form");
    }

    @Test
    void testUploadImageWhenFileIsNull() throws IOException {
        SurveyGroup surveyGroup = dataStoreTestUtil.createSurveyGroup();
        Survey form = dataStoreTestUtil.createSurvey(surveyGroup);
        Long questionId = dataStoreTestUtil.createQuestion(form, dataStoreTestUtil.createQuestionGroup(form, 0, false).getKey().getId(), Question.Type.DATE, false).getKey().getId();
        SurveyedLocale dataPoint = dataStoreTestUtil.createDataPoint(surveyGroup.getObjectId(), form.getObjectId(), 0);
        new SurveyedLocaleDao().save(dataPoint);
        SurveyInstance surveyInstance = dataStoreTestUtil.createSurveyInstance(dataPoint, 0);
        new SurveyInstanceDAO().save(surveyInstance);
        Long formInstanceId = surveyInstance.getKey().getId();

        ImageUploadRestService service = new ImageUploadRestService();

        assertThrows(ResponseStatusException.class, () -> service.uploadImage(questionId, formInstanceId, null), "File is not valid");
    }

    @Test
    void testUploadImageWhenFileInvalid() throws IOException {
        SurveyGroup surveyGroup = dataStoreTestUtil.createSurveyGroup();
        Survey form = dataStoreTestUtil.createSurvey(surveyGroup);
        Long questionId = dataStoreTestUtil.createQuestion(form, dataStoreTestUtil.createQuestionGroup(form, 0, false).getKey().getId(), Question.Type.DATE, false).getKey().getId();
        SurveyedLocale dataPoint = dataStoreTestUtil.createDataPoint(surveyGroup.getObjectId(), form.getObjectId(), 0);
        new SurveyedLocaleDao().save(dataPoint);
        SurveyInstance surveyInstance = dataStoreTestUtil.createSurveyInstance(dataPoint, 0);
        new SurveyInstanceDAO().save(surveyInstance);
        Long formInstanceId = surveyInstance.getKey().getId();

        ImageUploadRestService service = new ImageUploadRestService();

        assertThrows(ResponseStatusException.class, () -> service.uploadImage(questionId, formInstanceId, new MockMultipartFile("file.txt", "new_file.txt", "image/text", new byte[2])), "File type is not valid: only jpg and png are accepted");
    }

    @Test
    void testUploadImageWhenS3UploadFails() throws IOException {
        SurveyGroup surveyGroup = dataStoreTestUtil.createSurveyGroup();
        Survey form = dataStoreTestUtil.createSurvey(surveyGroup);
        Long questionId = dataStoreTestUtil.createQuestion(form, dataStoreTestUtil.createQuestionGroup(form, 0, false).getKey().getId(), Question.Type.DATE, false).getKey().getId();
        SurveyedLocale dataPoint = dataStoreTestUtil.createDataPoint(surveyGroup.getObjectId(), form.getObjectId(), 0);
        new SurveyedLocaleDao().save(dataPoint);
        SurveyInstance surveyInstance = dataStoreTestUtil.createSurveyInstance(dataPoint, 0);
        new SurveyInstanceDAO().save(surveyInstance);
        long formInstanceId = surveyInstance.getKey().getId();

        ImageUploadRestService service = new ImageUploadRestService();

        //upload to s3 will fail because we do not have any keys setup
        assertThrows(ResponseStatusException.class, () -> service.uploadImage(questionId, formInstanceId, new MockMultipartFile("file.jpg", "new_image.jpg", "image/jpeg", new byte[2])), "Upload to s3 failed for: file.jpg");
    }

    @Test
    void testUploadImageWhenS3UploadSuccess() throws IOException {
        SurveyGroup surveyGroup = dataStoreTestUtil.createSurveyGroup();
        Survey form = dataStoreTestUtil.createSurvey(surveyGroup);
        long questionId = dataStoreTestUtil.createQuestion(form, dataStoreTestUtil.createQuestionGroup(form, 0, false).getKey().getId(), Question.Type.DATE, false).getKey().getId();
        SurveyedLocale dataPoint = dataStoreTestUtil.createDataPoint(surveyGroup.getObjectId(), form.getObjectId(), 0);
        new SurveyedLocaleDao().save(dataPoint);
        SurveyInstance surveyInstance = dataStoreTestUtil.createSurveyInstance(dataPoint, 0);
        new SurveyInstanceDAO().save(surveyInstance);
        Long formInstanceId = surveyInstance.getKey().getId();

        ImageUploadRestService service = new ImageUploadRestService();

        try (MockedStatic<S3Util> mockedS3 = mockStatic(S3Util.class)) {
            // Mocking put on S3
            mockedS3.when(() -> S3Util.put(anyString(), anyString(), any(), anyString(), anyBoolean())).thenReturn(true);

            ImageUploadRestService.Response response = service.uploadImage(questionId, formInstanceId, new MockMultipartFile("file.jpg", "new_image.jpg", "image/jpeg", new byte[2]));
            assertEquals(200, response.getCode());
            assertEquals("", response.getMessage());
            assertNotNull(new QuestionAnswerStoreDao().getByQuestionAndSurveyInstance(questionId, formInstanceId));
        }
    }

}
