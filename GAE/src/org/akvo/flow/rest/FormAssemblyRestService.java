package org.akvo.flow.rest;

import com.gallatinsystems.common.domain.UploadStatusContainer;
import com.gallatinsystems.common.util.S3Util;
import com.gallatinsystems.common.util.ZipUtil;
import com.gallatinsystems.survey.dao.CascadeResourceDao;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.dao.SurveyGroupDAO;
import com.gallatinsystems.survey.dao.SurveyUtils;
import com.gallatinsystems.survey.dao.TranslationDao;
import com.gallatinsystems.survey.domain.CascadeResource;
import com.gallatinsystems.survey.domain.Question;
import static com.gallatinsystems.survey.domain.Question.Type.CASCADE;
import com.gallatinsystems.survey.domain.QuestionGroup;
import com.gallatinsystems.survey.domain.QuestionOption;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.survey.domain.SurveyGroup;
import com.gallatinsystems.survey.domain.Translation;
import static com.gallatinsystems.survey.domain.Translation.ParentType.SURVEY_DESC;
import static com.gallatinsystems.survey.domain.Translation.ParentType.SURVEY_NAME;
import com.gallatinsystems.survey.domain.WebForm;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.utils.SystemProperty;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.TreeMap;
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
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionOptionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;

@Controller
@RequestMapping("/form_assembly")
public class FormAssemblyRestService {

    private static final Logger log = Logger.getLogger(FormAssemblyRestService.class.getName());
    private static final String SURVEY_UPLOAD_DIR = "surveyuploaddir";
    private static final String SURVEY_UPLOAD_URL = "surveyuploadurl";
    public static final String BUCKET = "s3bucket";

    @PostMapping(consumes = "application/json")
    @ResponseBody
    public String assembleForm(@RequestBody SurveyDto surveyDto) {
        Properties props = System.getProperties();
        String alias = props.getProperty("alias");
        //TODO: do we need to fetch this?
        SurveyGroup survey = new SurveyGroupDAO().getByKey(surveyDto.getSurveyGroupId());
        Survey form = getSurveyFromDto(surveyDto);
        long formId = form.getObjectId();
        String xmlAppId = props.getProperty("xmlAppId");
        String appStr = (xmlAppId != null && !xmlAppId.isEmpty()) ? xmlAppId : SystemProperty.applicationId.get();
        attachFormTranslations(form);
        List<Question> questions = getQuestionList((List<QuestionGroup>) form.getQuestionGroupMap().values());
        attachCascadeResources(questions);
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
                    boolean webForm = WebForm.validWebForm(survey, form, questions);
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

    private Survey getSurveyFromDto(SurveyDto surveyDto) {
        Survey form = new Survey();
        form.setKey(KeyFactory.createKey("Survey", surveyDto.getKeyId()));
        form.setCode(surveyDto.getCode());
        form.setName(surveyDto.getName());
        form.setVersion(Double.parseDouble(surveyDto.getVersion()));
        form.setDesc(surveyDto.getDescription());
        form.setStatus(Survey.Status.NOT_PUBLISHED); //TODO: should it be not published?
        form.setPath(surveyDto.getPath());
        form.setSurveyGroupId(surveyDto.getSurveyGroupId());
        form.setDefaultLanguageCode(surveyDto.getDefaultLanguageCode());
        form.setRequireApproval(surveyDto.getRequireApproval());
        form.setCreatedDateTime(surveyDto.getCreatedDateTime());
        form.setLastUpdateDateTime(surveyDto.getLastUpdateDateTime());
        form.setAncestorIds(surveyDto.getAncestorIds());
        TreeMap<Integer, QuestionGroup> groupMap = getGroupMap(surveyDto);
        form.setQuestionGroupMap(groupMap);
        return form;
    }

    private TreeMap<Integer, QuestionGroup> getGroupMap(SurveyDto surveyDto) {
        TreeMap<Integer, QuestionGroup> groupMap = new TreeMap<>();
        List<QuestionGroupDto> groupDtos = surveyDto.getQuestionGroupList();
        if (groupDtos != null) {
            int i = 1;
            for (QuestionGroupDto groupDto : groupDtos) {
                QuestionGroup group = mapToGroup(groupDto);
                if (groupMap.containsKey(groupDto.getOrder())) {
                    groupMap.put(i, group);
                    groupDto.setOrder(i);
                } else {
                    int order = groupDto.getOrder() != null ? groupDto.getOrder() : i;
                    groupMap.put(order, group);
                }
                i++;
            }
        }
        return groupMap;
    }

    private QuestionGroup mapToGroup(QuestionGroupDto groupDto) {
        QuestionGroup group = new QuestionGroup();
        group.setKey(KeyFactory.createKey("QuestionGroup", groupDto.getKeyId()));
        group.setCode(groupDto.getCode());
        group.setSurveyId(groupDto.getSurveyId());
        group.setOrder(groupDto.getOrder());
        group.setPath(groupDto.getPath());
        group.setName(groupDto.getName());
        group.setRepeatable(groupDto.getRepeatable());
        group.setStatus(QuestionGroup.Status.valueOf(groupDto.getStatus()));
        group.setImmutable(groupDto.getImmutable());
        group.setQuestionMap(mapQuestions(groupDto));
        return group;
    }

    private TreeMap<Integer, Question> mapQuestions(QuestionGroupDto groupDto) {
        TreeMap<Integer, Question> mappedQuestions = new TreeMap<>();
        for (QuestionDto questionDto : groupDto.getQuestionList()) {
            mappedQuestions.put(questionDto.getOrder(), mapToQuestion(questionDto));
        }
        return mappedQuestions;
    }

    private Question mapToQuestion(QuestionDto questionDto) {
        Question question = new Question();
        question.setKey(KeyFactory.createKey("Question", questionDto.getKeyId()));
        question.setType(Question.Type.valueOf(questionDto.getType().toString()));
        question.setTip(questionDto.getTip());
        question.setText(questionDto.getText());
        question.setDependentFlag(questionDto.getDependentFlag());
        question.setAllowMultipleFlag(questionDto.getAllowMultipleFlag());
        question.setAllowOtherFlag(questionDto.getAllowOtherFlag());
        question.setCollapseable(questionDto.getCollapseable());
        question.setGeoLocked(questionDto.getGeoLocked());
        question.setRequireDoubleEntry(questionDto.getRequireDoubleEntry());
        question.setImmutable(questionDto.getImmutable());
        question.setDependentQuestionId(questionDto.getDependentQuestionId());
        question.setDependentQuestionAnswer(questionDto.getDependentQuestionAnswer());
        question.setCascadeResourceId(questionDto.getCascadeResourceId());
        question.setCaddisflyResourceUuid(questionDto.getCaddisflyResourceUuid());
        question.setQuestionGroupId(question.getQuestionGroupId());
        question.setSurveyId(questionDto.getSurveyId());
        question.setVariableName(questionDto.getVariableName());
        question.setOrder(questionDto.getOrder());
        question.setMandatoryFlag(questionDto.getMandatoryFlag());
        question.setPath(questionDto.getPath());
        question.setAllowDecimal(questionDto.getAllowDecimal());
        question.setAllowSign(questionDto.getAllowSign());
        question.setMinVal(questionDto.getMinVal());
        question.setMaxVal(question.getMaxVal());
        question.setAllowExternalSources(questionDto.getAllowExternalSources());
        question.setLocaleNameFlag(questionDto.getLocaleNameFlag());
        question.setLocaleLocationFlag(questionDto.getLocaleLocationFlag());
        question.setPersonalData(questionDto.getPersonalData());
        question.setAllowPoints(questionDto.getAllowPoints());
        question.setAllowLine(questionDto.getAllowLine());
        question.setAllowPolygon(questionDto.getAllowPolygon());
        question.setSourceQuestionId(questionDto.getSourceId());
        question.setQuestionOptionMap(mapToOptions(questionDto));
        return question;
    }

    private TreeMap<Integer, QuestionOption> mapToOptions(QuestionDto questionDto) {
        TreeMap<Integer, QuestionOption> mappedOptions = new TreeMap<>();
        List<QuestionOptionDto> dtoList = questionDto.getOptionList();
        for (QuestionOptionDto questionOptionDto: dtoList) {
            mappedOptions.put(questionOptionDto.getOrder(), mapToQuestionOption(questionOptionDto));
        }
        return mappedOptions;
    }

    private QuestionOption mapToQuestionOption(QuestionOptionDto questionOptionDto) {
        QuestionOption questionOption = new QuestionOption();
        questionOption.setKey(KeyFactory.createKey("QuestionOption", questionOptionDto.getKeyId()));
        questionOption.setText(questionOptionDto.getText());
        questionOption.setCode(questionOptionDto.getCode());
        questionOption.setOrder(questionOptionDto.getOrder());
        questionOption.setQuestionId(questionOptionDto.getQuestionId());
        return questionOption;
    }

    private void attachCascadeResources(List<Question> questions) {
        CascadeResourceDao cascadeResourceDao = new CascadeResourceDao();
        for (Question question: questions) {
            if (CASCADE.equals(question.getType())) {
                CascadeResource cascadeResource = cascadeResourceDao.getByKey(question.getCascadeResourceId());
                if (cascadeResource != null) {
                    question.setCascadeResource(cascadeResource.getResourceId());
                    question.setLevelNames(cascadeResource.getLevelNames());
                }
            }
        }
    }

    private void attachFormTranslations(Survey form) {
        List<Translation> translations = new TranslationDao().listByFormId(form.getObjectId());
        HashMap<String, Translation> formTranslationMap = getFormTranslationMap(translations);
        if (formTranslationMap.size() > 0) {
            form.setTranslationMap(formTranslationMap);
        }
        List<QuestionGroup> groups = (List<QuestionGroup>) form.getQuestionGroupMap().values();
        for (QuestionGroup group: groups) {
            HashMap<String, Translation> map = getGroupTranslationsMap(translations, group.getKey().getId());
            if (map.size() > 0) {
                group.setTranslations(map);
            }
            List<Question> questions = (List<Question>) group.getQuestionMap().values();
            for (Question question: questions) {
                List<Translation> questionTranslations = getQuestionTranslations(translations, question.getKey().getId());
                if (questionTranslations != null && questionTranslations.size() > 0) {
                    question.setTranslations(questionTranslations);
                }
                TreeMap<Integer, QuestionOption> questionOptionMap = question.getQuestionOptionMap();
                List<QuestionOption> options = questionOptionMap == null ? Collections.emptyList() : (List<QuestionOption>) questionOptionMap.values();
                for (QuestionOption option : options) {
                    HashMap<String, Translation> optionTranslationsMap = getOptionTranslations(translations, option.getKey().getId());
                    if (optionTranslationsMap.size() > 0) {
                        option.setTranslationMap(optionTranslationsMap);
                    }
                }
            }
        }
    }

    private HashMap<String, Translation> getOptionTranslations(List<Translation> translations, long id) {
        List<Translation> translationsForForm = getTranslationsForParent(translations, id);
        return mapTranslations(translationsForForm);
    }

    private List<Translation> getTranslationsForParent(List<Translation> translations, long id) {
        return translations
                .stream()
                .filter(it -> id == it.getParentId())
                .collect(Collectors.toList());
    }

    private List<Translation> getQuestionTranslations(List<Translation> translations, long id) {
        return getTranslationsForParent(translations, id);
    }

    private HashMap<String, Translation> getGroupTranslationsMap(List<Translation> translations, long id) {
        List<Translation> translationsForForm = getTranslationsForParent(translations, id);
        return mapTranslations(translationsForForm);
    }

    private HashMap<String, Translation> mapTranslations(List<Translation> translationsForForm) {
        HashMap<String, Translation> translationHashMap = new HashMap<>();
        for (Translation t : translationsForForm) {
            translationHashMap.put(t.getLanguageCode(), t);
        }
        return translationHashMap;
    }

    private HashMap<String, Translation> getFormTranslationMap(List<Translation> translations) {
        List<Translation> translationsForForm = translations
                .stream()
                .filter(it -> SURVEY_DESC.equals(it.getParentType()) || SURVEY_NAME.equals(it.getParentType()))
                .collect(Collectors.toList());
        return mapTranslations(translationsForForm);
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
