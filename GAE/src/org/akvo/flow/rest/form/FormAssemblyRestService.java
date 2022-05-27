/*
 * Copyright (C) 2021 Stichting Akvo (Akvo Foundation)
 *
 * This file is part of Akvo Flow.
 *
 * Akvo Flow is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Akvo Flow is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Akvo Flow.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.akvo.flow.rest.form;

import com.gallatinsystems.common.domain.UploadStatusContainer;
import com.gallatinsystems.common.util.S3Util;
import com.gallatinsystems.common.util.ZipUtil;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.dao.SurveyGroupDAO;
import com.gallatinsystems.survey.dao.SurveyUtils;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.survey.domain.SurveyGroup;
import com.gallatinsystems.survey.domain.WebForm;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;

@Controller
@RequestMapping("/form_publish")
public class FormAssemblyRestService {

    private static final Logger log = Logger.getLogger(FormAssemblyRestService.class.getName());
    private static final String SURVEY_UPLOAD_DIR = "surveyuploaddir";
    private static final String SURVEY_UPLOAD_URL = "surveyuploadurl";
    private static final String BUCKET = "s3bucket";

    private final XmlFormAssembler xmlFormAssembler = new XmlFormAssembler();
    private final FormAssembler formAssembler = new FormAssembler(new FormMapper(new QuestionGroupMapper(new QuestionMapper(new QuestionOptionMapper()))),
            new TranslationsAppender());

    @PostMapping(consumes = "application/json")
    @ResponseBody
    public SurveyDto publishForm(@RequestBody SurveyDto surveyDto) {
        SurveyGroup survey = new SurveyGroupDAO().getByKey(surveyDto.getSurveyGroupId());
        long formId = surveyDto.getKeyId();
        Survey form = formAssembler.assembleForm(surveyDto);
        try {
            FormUploadXml formUploadXml = xmlFormAssembler.assembleXmlForm(survey, form);
            log.info("Uploading " + formId);
            UploadStatusContainer uc = uploadFormXML(
                    formUploadXml.getFormIdFilename(),
                    formUploadXml.getFormIdVersionFilename(),
                    formUploadXml.getXmlContent());
            if (uc.getUploadedZip1() && uc.getUploadedZip2()) {
                formUploadSuccess(survey, form);
                log.info("Completed form assembly for " + formId);
                surveyDto.setStatus(Survey.Status.PUBLISHED.toString());
                return surveyDto;
            } else {
                String message = "Failed to upload assembled form, id " + formId;
                log.severe(message + "\n" + uc.getMessage());
                throw new ResponseStatusException(INTERNAL_SERVER_ERROR, message, new Exception(uc.getMessage()));
            }
        } catch (IOException e) {
            log.log(Level.SEVERE, "Failed to convert form to XML: " + e.getMessage(), e);
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "Failed to assemble form, id " + formId, e);
        }
    }

    private void formUploadSuccess(SurveyGroup survey, Survey form) {
        List<Question> questions = formAssembler.getQuestionList(form.getQuestionGroupMap());
        form.setStatus(Survey.Status.PUBLISHED);
        if (form.getWebForm()) {
            boolean webForm = WebForm.validWebForm(survey, form, questions);
            form.setWebForm(webForm);
        }
        new SurveyDAO().save(form);
        //invalidate any cached reports in flow-services
        //TODO: shall this be done in another service?
        List<Long> ids = new ArrayList<>();
        ids.add(form.getObjectId());
        SurveyUtils.notifyReportService(ids, "invalidate");
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
            log.log(Level.SEVERE, "Error uploading zip file: " + e.toString(), e);
            return false;
        }
    }
}
