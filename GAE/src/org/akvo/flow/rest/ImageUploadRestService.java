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
import java.util.Date;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;
import org.akvo.flow.util.ExifTagExtractor;
import org.akvo.flow.util.ExifTagInfo;
import org.akvo.flow.util.FileResponse;
import org.akvo.flow.util.FlowJsonObjectWriter;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.waterforpeople.mapping.dao.QuestionAnswerStoreDao;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.QuestionAnswerStore;
import org.waterforpeople.mapping.domain.SurveyInstance;

@Controller
@RequestMapping("/image_upload")
public class ImageUploadRestService {

    private static final Logger log = Logger.getLogger(ImageUploadRestService.class.getName());

    public static final String IMAGES_DIRECTORY = "images";

    @RequestMapping(method = RequestMethod.POST, headers = "content-type=multipart/*", value = "/question/{questionId}/instance/{instanceId}")
    @ResponseBody
    public Response uploadImage(@PathVariable("questionId") Long questionId,
                                @PathVariable("instanceId") Long formInstanceId,
                                @RequestParam(value = "image") MultipartFile file) {
        int responseCode = 200;
        String errorMessage = "";
        SurveyInstance formInstance = getFormInstance(formInstanceId);
        if (formInstance == null) {
            throwError("FormInstance not found");
        }
        long formId = formInstance.getSurveyId();
        Survey form = getForm(formId);
        if (form == null) {
            throwError("Form not found");
        }
        Question question = getQuestion(questionId);
        if (question == null) {
            throwError("Question not found");
        }
        if (question.getSurveyId() == null || !question.getSurveyId().equals(form.getObjectId())) {
            throwError("Question does not belong to that form");
        }

        if (file == null || file.isEmpty()) {
            throwError("File is not valid");
        }
        String fileExtension = getFileType(file);
        if (fileExtension == null) {
            throwError("File type is not valid: only jpg and png are accepted");
        }
        String originalFileName = file.getName();
        String filename = generateFileName(fileExtension);
        String resultFilename = uploadImageToS3(file, filename);
        if (resultFilename == null) {
            throwError("Upload to s3 failed for: " + originalFileName);
        }
        ExifTagExtractor exifTagExtractor = new ExifTagExtractor();
        ExifTagInfo exifTag = exifTagExtractor.fetchExifTags(file);
        createOrUpdateQuestionAnswer(question, formInstance, resultFilename, exifTag);
        return new Response(responseCode, errorMessage);
    }

    private void throwError(String errorMessage) {
        throw new ResponseStatusException(BAD_REQUEST, errorMessage, new Exception(errorMessage));
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
            S3Util.put(bucketName, IMAGES_DIRECTORY + "/" + fileName, file.getBytes(), file.getContentType(), true);
            return fileName;
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error uploading image", e);
            return null;
        }
    }

    private void createOrUpdateQuestionAnswer(Question question, SurveyInstance formInstance, String fileName, ExifTagInfo exifTag) {
        QuestionAnswerStoreDao questionAnswerStoreDao = new QuestionAnswerStoreDao();
        QuestionAnswerStore questionAnswerStore = questionAnswerStoreDao.getByQuestionAndSurveyInstance(question.getKey().getId(), formInstance.getKey().getId());
        if (questionAnswerStore == null) {
            questionAnswerStore = createQuestionAnswer(question, formInstance);
        }
        saveQuestionAnswer(formInstance, fileName, exifTag, questionAnswerStoreDao, questionAnswerStore);
    }

    private void saveQuestionAnswer(SurveyInstance formInstance, String fileName, ExifTagInfo exifTag, QuestionAnswerStoreDao questionAnswerStoreDao, QuestionAnswerStore store) {
        try {
            FileResponse fileResponse = new FileResponse(fileName, exifTag.getLocation());
            store.setValue(new FlowJsonObjectWriter().writeAsString(fileResponse));
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error generating filename json", e);
            store.setValue(fileName);
        }
        Date collectionDate = exifTag != null? exifTag.getCollectionDate(): null;
        if (collectionDate != null) {
            store.setCollectionDate(collectionDate);
        } else {
            store.setCollectionDate(formInstance.getCollectionDate());
        }
        questionAnswerStoreDao.save(store);
    }

    private QuestionAnswerStore createQuestionAnswer(Question question, SurveyInstance formInstance) {
        QuestionAnswerStore store = new QuestionAnswerStore();
        store.setSurveyId(formInstance.getSurveyId());
        store.setSurveyInstanceId(formInstance.getKey().getId());
        store.setQuestionID(question.getKey().getId() + "");
        store.setType("IMAGE");
        store.setIteration(0);
        return store;
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
