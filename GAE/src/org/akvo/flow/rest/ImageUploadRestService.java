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
import com.gallatinsystems.survey.dao.QuestionDao;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.Survey;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.waterforpeople.mapping.dao.QuestionAnswerStoreDao;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.QuestionAnswerStore;
import org.waterforpeople.mapping.domain.SurveyInstance;

@Controller
@RequestMapping("/image_upload")
public class ImageUploadRestService {

    private static final Logger log = Logger
            .getLogger(ImageUploadRestService.class.getName());

    @RequestMapping(method = RequestMethod.POST, value = "/question/{questionId}/instance/{instanceId}")
    @ResponseBody
    public Response uploadImage(@PathVariable("questionId") Long questionId,
                                @PathVariable("instanceId") Long formInstanceId,
                                @RequestParam(value = "image") MultipartFile file) {
        int responseCode = 200;
        String errorMessage = "";
        SurveyInstance formInstance = getFormInstance(formInstanceId);
        if (formInstance == null) {
            return new Response(400, "FormInstance not found");
        }
        long formId = formInstance.getSurveyId();
        Survey form = getForm(formId);
        if (form == null) {
            return new Response(400, "Form not found");
        }
        Question question = getQuestion(questionId);
        if (question == null) {
            return new Response(400, "Question not found");
        }
        if (question.getSurveyId() == null || !question.getSurveyId().equals(form.getObjectId())) {
            return new Response(400, "Question does not belong to that form");
        }

        if (file == null || file.isEmpty()) {
            return new Response(400, "File is not valid");
        }
        String fileExtension = getFileType(file);
        if (fileExtension == null) {
            return new Response(400, "File type is not valid: only jpg and png are accepted");
        }
        String originalFileName = file.getName();
        String filename = generateFileName(fileExtension);
        String resultFilename = uploadImageToS3(file, filename);
        if (resultFilename == null) {
            return new Response(400, "Upload to s3 failed for: " + originalFileName);
        }
        saveQuestionAnswer(question, formInstance, resultFilename);
        return new Response(responseCode, errorMessage);
    }

    @Nullable
    private String getFileType(MultipartFile file) {
        String contentType = file.getContentType() != null? file.getContentType(): "";
        switch (contentType) {
            case "image/jpeg":
                return ".jpg";
            case "image/png":
                return ".png";
        }
        return null;
    }

    @Nullable
    private String uploadImageToS3(MultipartFile file, String fileName) {
        try {
            Properties props = System.getProperties();
            String bucketName = props.getProperty("s3bucket");
            String directory = props.getProperty("images");
            S3Util.put(bucketName, directory + "/" + fileName, file.getBytes(), file.getContentType(), true);
            return fileName;
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error uploading image", e);
            return null;
        }
    }

    private void saveQuestionAnswer(Question question, SurveyInstance formInstance, String fileName) {
        QuestionAnswerStoreDao questionAnswerStoreDao = new QuestionAnswerStoreDao();
        QuestionAnswerStore existingStore = questionAnswerStoreDao.getByQuestionAndSurveyInstance(question.getKey().getId(), formInstance.getKey().getId());
        if (existingStore == null) {
            createAndSaveQuestionAnswer(question, formInstance, fileName, questionAnswerStoreDao, 0);
        } else {
            existingStore.setValue(fileName);
            questionAnswerStoreDao.save(existingStore);
        }
    }

    private void createAndSaveQuestionAnswer(Question question, SurveyInstance formInstance, String fileName, QuestionAnswerStoreDao questionAnswerStoreDao, int iteration) {
        QuestionAnswerStore store = new QuestionAnswerStore();
        store.setSurveyId(formInstance.getSurveyId());
        store.setSurveyInstanceId(formInstance.getKey().getId());
        store.setQuestionID(question.getKey().getId() + "");

        //TODO: should the date be same as collection date on the form Instance??
        store.setCollectionDate(formInstance.getCollectionDate());
        store.setType("IMAGE");

        store.setValue(fileName);

        store.setIteration(iteration);
        //TODO: where do we put the geolocation data?
        questionAnswerStoreDao.save(store);
    }

    private String generateFileName(String fileExtension) {
        //in the app this is how we create the filename
        return UUID.randomUUID().toString() + fileExtension;
    }

    private SurveyInstance getFormInstance(Long formInstanceId) {
        if (formInstanceId == null) {
            return null;
        }
        return new SurveyInstanceDAO().getByKey(formInstanceId);
    }

    private Question getQuestion(Long questionId) {
        if (questionId == null) {
            return null;
        }
        return new QuestionDao().getByKey(questionId);
    }

    private Survey getForm(long formId) {
        return new SurveyDAO().getByKey(formId);
    }

    public static class Response {
        private final int code;
        private final String message;

        public Response(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }
}
