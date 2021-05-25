package org.akvo.flow.rest;

import com.gallatinsystems.common.util.S3Util;
import com.gallatinsystems.survey.dao.QuestionDao;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.Survey;
import java.io.IOException;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
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
    public Pair<Integer, String> uploadImage(@RequestParam(value = "formInstanceId", defaultValue = "") String formInstanceId,
                                             @RequestParam(value = "questionId", defaultValue = "") String questionId,
                                             @RequestParam(value = "formId", defaultValue = "") String formId,
                                             @RequestParam(value = "image") MultipartFile file) {
        int responseCode = 200;
        String errorMessage = "";
        Survey form = getForm(formId);
        if (form == null) {
            return new Pair<>(400, "Form not found");
        }
        Question question = getQuestion(questionId);
        if (question == null) {
            return new Pair<>(400, "Question not found");
        }
        if (question.getSurveyId() == null || !question.getSurveyId().equals(form.getObjectId())) {
            return new Pair<>(400, "Question does not belong to that form");
        }
        SurveyInstance formInstance = getFormInstance(formInstanceId);
        if (formInstance == null) {
            return new Pair<>(400, "FormInstance not found");
        }
        if (formInstance.getSurveyId()== null || !formInstance.getSurveyId().equals(form.getObjectId())) {
            return new Pair<>(400, "FormInstance does not belong to that form");
        }
        if (file == null || file.isEmpty()) {
            return new Pair<>(400, "File is not valid");
        }
        String fileExtension = getFileType(file);
        if (fileExtension == null) {
            return new Pair<>(400, "File type is not valid: only jpg and png are accepted"); //TODO: shall we accept other formats?
        }
        String filename = uploadImageToS3(file, generateFileName(fileExtension));
        if (filename == null) {
            return new Pair<>(400, "Upload to s3 failed for: " + filename);
        }
        saveQuestionAnswer(question, formInstance, filename);
        return new Pair<>(responseCode, errorMessage);
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
        } catch (IOException e) {
            log.log(Level.SEVERE, "Error uploading image", e);
            return null;
        }
    }

    private void saveQuestionAnswer(Question question, SurveyInstance formInstance, String fileName) {
        //TODO: what if the question is from repeatable group?
        QuestionAnswerStore store = new QuestionAnswerStore();
        store.setSurveyId(formInstance.getSurveyId());
        store.setSurveyInstanceId(formInstance.getKey().getId());
        store.setQuestionID(question.getKey().getId() + "");

        //TODO: should the date be same as collection date on the form Instance??
        store.setCollectionDate(formInstance.getCollectionDate());
        store.setType("IMAGE");

        store.setValue(fileName);

        //store.setIteration(iteration); ????
        //TODO: fetch other answers for that question and add one iteration if needed
        //TODO: where do we put the geolocation data?
        new QuestionAnswerStoreDao().save(store);
    }

    private String generateFileName(String fileExtension) {
        //in the app this is how we create the filename
        //TODO: what if image is not JPG but PNG
        //maybe pass this as param?
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
}
