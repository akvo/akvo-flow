package org.akvo.flow.rest;

import com.gallatinsystems.common.domain.UploadStatusContainer;
import com.gallatinsystems.common.util.S3Util;
import com.gallatinsystems.common.util.ZipUtil;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.dao.SurveyGroupDAO;
import com.gallatinsystems.survey.dao.SurveyUtils;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.QuestionGroup;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.survey.domain.SurveyGroup;
import com.gallatinsystems.survey.domain.WebForm;
import com.google.appengine.api.utils.SystemProperty;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.akvo.flow.xml.PublishedForm;
import org.akvo.flow.xml.XmlForm;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/form_assembly")
public class FormAssemblyRestService {

    private static final Logger log = Logger.getLogger(FormAssemblyRestService.class.getName());
    private static final String SURVEY_UPLOAD_DIR = "surveyuploaddir";
    private static final String SURVEY_UPLOAD_URL = "surveyuploadurl";
    public static final String BUCKET = "s3bucket";

    @PostMapping(consumes = "application/json")
    @ResponseBody
    public String assembleForm(@RequestBody Survey form) {
        Properties props = System.getProperties();
        String alias = props.getProperty("alias");
        //TODO: do we need to fetch this?
        SurveyGroup survey = new SurveyGroupDAO().getByKey(form.getSurveyGroupId());
        long formId = form.getObjectId();
        String xmlAppId = props.getProperty("xmlAppId");
        String appStr = (xmlAppId != null && !xmlAppId.isEmpty()) ? xmlAppId : SystemProperty.applicationId.get();
        //TODO: fetch translations
        //TODO: fetch cascades
        XmlForm jacksonForm = new XmlForm(form, survey, appStr, alias);
        String formXML;
        try {
            formXML = PublishedForm.generate(jacksonForm);
            log.info("Uploading " + formId);
            UploadStatusContainer uc = uploadFormXML(
                    Long.toString(formId), //latest version in plain filename
                    formId + "v" + form.getVersion(), //archive copy
                    formXML);
            if (uc.getUploadedZip1() && uc.getUploadedZip2()) {
                log.info("Finishing assembly of " + formId);
                form.setStatus(Survey.Status.PUBLISHED);
                if (form.getWebForm()) {
                    boolean webForm = WebForm.validWebForm(survey, form, getQuestionList((List<QuestionGroup>) form.getQuestionGroupMap().values()));
                    form.setWebForm(webForm);
                }
                new SurveyDAO().save(form); //remember PUBLISHED status
                //invalidate any cached reports in flow-services
                //TODO: shall this be done in another service?
                List<Long> ids = new ArrayList<>();
                ids.add(formId);
                SurveyUtils.notifyReportService(ids, "invalidate");
                log.info("Completed form assembly for " + formId);
                return "OK";
            } else {
                log.warning("Failed to upload assembled form, id " + formId + "\n" + uc.getMessage());
                return "Error";
            }
        } catch (IOException e) {
            log.log(Level.SEVERE, "Failed to convert form to XML: " + e.getMessage());
            return "Error";
        }
    }

    private List<Question> getQuestionList(List<QuestionGroup> values) {
        return values.stream().map(it -> (List<Question>) it.getQuestionMap().values()).collect(Collectors.toList())
                .stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    /**
     * Upload a zipped form file twice to S3 under different filenames.
     */
    public UploadStatusContainer uploadFormXML(String fileName1, String fileName2, String formXML) {
        Properties props = System.getProperties();
        String bucketName = props.getProperty(BUCKET);
        String directory = props.getProperty(SURVEY_UPLOAD_DIR);
        String uploadUrl = props.getProperty(SURVEY_UPLOAD_URL);

        UploadStatusContainer uc = new UploadStatusContainer();
        uc.setUploadedZip1(uploadZippedXml(formXML, bucketName, directory, fileName1));
        uc.setUploadedZip2(uploadZippedXml(formXML, bucketName, directory, fileName2));
        uc.setUrl(uploadUrl + directory + "/" + fileName1 + ".zip");
        return uc;
    }

    private boolean uploadZippedXml(String content, String bucketName, String directory, String fileName) {
        ByteArrayOutputStream os2 = ZipUtil.generateZip(content, fileName + ".xml");

        try {
            return S3Util.put(bucketName,
                    directory + "/" + fileName + ".zip",
                    os2.toByteArray(),
                    "application/zip",
                    true);
        } catch (IOException e) {
            log.severe("Error uploading zipfile: " + e.toString());
            return false;
        }
    }
}
