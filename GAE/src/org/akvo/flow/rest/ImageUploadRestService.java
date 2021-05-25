package org.akvo.flow.rest;

import com.gallatinsystems.common.util.S3Util;
import com.gallatinsystems.survey.dao.QuestionDao;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.Survey;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.springframework.stereotype.Controller;
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

    @RequestMapping(method = RequestMethod.POST, value = "")
    @ResponseBody
    public Response uploadImage(@RequestParam(value = "formInstanceId", defaultValue = "") String formInstanceId,
                                             @RequestParam(value = "questionId", defaultValue = "") String questionId,
                                             @RequestParam(value = "formId", defaultValue = "") String formId,
                                             @RequestParam(value = "image") MultipartFile file) {
        int responseCode = 200;
        String errorMessage = "";
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
        SurveyInstance formInstance = getFormInstance(formInstanceId);
        if (formInstance == null) {
            return new Response(400, "FormInstance not found");
        }
        if (formInstance.getSurveyId()== null || !formInstance.getSurveyId().equals(form.getObjectId())) {
            return new Response(400, "FormInstance does not belong to that form");
        }
        if (file == null || file.isEmpty()) {
            return new Response(400, "File is not valid");
        }
        String fileExtension = getFileType(file);
        if (fileExtension == null) {
            return new Response(400, "File type is not valid: only jpg and png are accepted");
        }
        String filename = uploadImageToS3(file, generateFileName(fileExtension));
        if (filename == null) {
            return new Response(400, "Upload to s3 failed for: " + filename);
        }
        saveQuestionAnswer(question, formInstance, filename);
        return new Response(responseCode, errorMessage);
    }

    @Nullable
    private String getFileType(MultipartFile file) {
        //TODO: shall we accept other formats?
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
        } catch (IOException e) {
            log.log(Level.SEVERE, "Error uploading image", e);
            return null;
        }
    }

    private void saveQuestionAnswer(Question question, SurveyInstance formInstance, String fileName) {
        QuestionAnswerStoreDao questionAnswerStoreDao = new QuestionAnswerStoreDao();
        int iteration = 0;
        List<QuestionAnswerStore> existingStores = questionAnswerStoreDao.listByQuestionAndSurveyInstance(question.getKey().getId(), formInstance.getKey().getId());
        if (existingStores != null && !existingStores.isEmpty()) {
            List<QuestionAnswerStore> sorted = existingStores.stream().sorted(Comparator.comparing(QuestionAnswerStore::getIteration)).collect(Collectors.toList());
            Collections.reverse(sorted);
            iteration = sorted.get(0).getIteration();
        }
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

    private SurveyInstance getFormInstance(String formInstanceId) {
        if (formInstanceId == null || formInstanceId.isEmpty()) {
            return null;
        }
        return new SurveyInstanceDAO().getByKey(formInstanceId);
    }

    private Question getQuestion(String questionId) {
        if (questionId == null || questionId.isEmpty()) {
            return null;
        }
        return new QuestionDao().getByKey(questionId);
    }

    private Survey getForm(String formId) {
        if (formId == null || formId.isEmpty()) {
            return null;
        }
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
