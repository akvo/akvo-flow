package org.akvo.flow.rest;

import com.gallatinsystems.survey.dao.QuestionDao;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.Survey;
import java.util.UUID;
import javafx.util.Pair;
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
        String fileName = generateFileName();
        uploadImageToS3(file, fileName);
        saveQuestionAnswer(question, formInstance, fileName);
        return new Pair<>(responseCode, errorMessage);
    }

    private void uploadImageToS3(MultipartFile file, String fileName) {
        //TODO:
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

    private String generateFileName() {
        //in the app this is how we create the filename
        //TODO: what if image is not JPG but PNG
        //maybe pass this as param?
        return UUID.randomUUID().toString()+".jpg";
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
