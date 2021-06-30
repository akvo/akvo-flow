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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
    private final XmlFormAssembler xmlFormAssembler = new XmlFormAssembler();

    @PostMapping(consumes = "application/json")
    @ResponseBody
    public String publishForm(@RequestBody SurveyDto surveyDto) {
        SurveyGroup survey = new SurveyGroupDAO().getByKey(surveyDto.getSurveyGroupId());
        long formId = surveyDto.getKeyId();
        Survey form = assembleForm(surveyDto);
        FormUploadXml formUploadXml = xmlFormAssembler.assembleXmlForm(survey, form);

        if (!formUploadXml.getXmlContent().isEmpty()) {
            log.info("Uploading " + formId);
            UploadStatusContainer uc = uploadFormXML(
                    formUploadXml.getFormIdFilename(),
                    formUploadXml.getFormIdVersionFilename(),
                    formUploadXml.getXmlContent());
            if (uc.getUploadedZip1() && uc.getUploadedZip2()) {
                formUploadSuccess(survey, form);
                log.info("Completed form assembly for " + formId);
                return "OK";
            } else {
                log.severe("Failed to upload assembled form, id " + formId + "\n" + uc.getMessage());
                return "Error";
            }
        }
        return "Error";
    }

    private Survey assembleForm(SurveyDto surveyDto) {
        Survey form = getSurveyFromDto(surveyDto);
        attachFormTranslations(form);
        List<Question> questions = getQuestionList(form.getQuestionGroupMap());
        attachCascadeResources(questions);
        return form;
    }

    private void formUploadSuccess(SurveyGroup survey, Survey form) {
        List<Question> questions = getQuestionList(form.getQuestionGroupMap());
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

    private Survey getSurveyFromDto(SurveyDto surveyDto) {
        Survey form = new Survey();
        form.setKey(KeyFactory.createKey("Survey", surveyDto.getKeyId()));
        form.setCode(surveyDto.getCode());
        form.setName(surveyDto.getName());
        form.setVersion(Double.parseDouble(surveyDto.getVersion()));
        form.setDesc(surveyDto.getDescription());
        form.setStatus(Survey.Status.NOT_PUBLISHED);
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

    @Nonnull
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

    @Nonnull
    private TreeMap<Integer, Question> mapQuestions(QuestionGroupDto groupDto) {
        TreeMap<Integer, Question> mappedQuestions = new TreeMap<>();
        List<QuestionDto> questionList = groupDto.getQuestionList();
        if (questionList != null) {
            for (QuestionDto questionDto : questionList) {
                mappedQuestions.put(questionDto.getOrder(), mapToQuestion(questionDto));
            }
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

    @Nonnull
    private TreeMap<Integer, QuestionOption> mapToOptions(QuestionDto questionDto) {
        TreeMap<Integer, QuestionOption> mappedOptions = new TreeMap<>();
        List<QuestionOptionDto> dtoList = questionDto.getOptionList();
        if (dtoList != null) {
            for (QuestionOptionDto questionOptionDto: dtoList) {
                mappedOptions.put(questionOptionDto.getOrder(), mapToQuestionOption(questionOptionDto));
            }
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
        TreeMap<Integer, QuestionGroup> questionGroupMap = form.getQuestionGroupMap();
        if (questionGroupMap != null) {
            List<QuestionGroup> groups = new ArrayList<>(questionGroupMap.values());
            for (QuestionGroup group: groups) {
                HashMap<String, Translation> map = getGroupTranslationsMap(translations, group.getKey().getId());
                if (map.size() > 0) {
                    group.setTranslations(map);
                }
                TreeMap<Integer, Question> questionMap = group.getQuestionMap();
                if (questionMap != null) {
                    List<Question> questions = new ArrayList<>(questionMap.values());
                    for (Question question: questions) {
                        List<Translation> questionTranslations = getQuestionTranslations(translations, question.getKey().getId());
                        if (questionTranslations.size() > 0) {
                            question.setTranslations(questionTranslations);
                        }
                        TreeMap<Integer, QuestionOption> questionOptionMap = question.getQuestionOptionMap();
                        if (questionOptionMap != null) {
                            List<QuestionOption> options = new ArrayList<>(questionOptionMap.values());
                            for (QuestionOption option : options) {
                                HashMap<String, Translation> optionTranslationsMap = getOptionTranslations(translations, option.getKey().getId());
                                if (optionTranslationsMap.size() > 0) {
                                    option.setTranslationMap(optionTranslationsMap);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private HashMap<String, Translation> getOptionTranslations(List<Translation> translations, long id) {
        List<Translation> translationsForForm = getTranslationsForParent(translations, id);
        return mapTranslations(translationsForForm);
    }

    @Nonnull
    private List<Translation> getTranslationsForParent(@Nullable List<Translation> translations, long id) {
        if (translations == null) {
            return Collections.emptyList();
        }
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

    private HashMap<String, Translation> mapTranslations(@Nullable List<Translation> translations) {
        HashMap<String, Translation> mappedTranslations = new HashMap<>();
        if (translations != null) {
            for (Translation t : translations) {
                mappedTranslations.put(t.getLanguageCode(), t);
            }
        }
        return mappedTranslations;
    }

    private HashMap<String, Translation> getFormTranslationMap(List<Translation> translations) {
        List<Translation> translationsForForm = translations
                .stream()
                .filter(it -> SURVEY_DESC.equals(it.getParentType()) || SURVEY_NAME.equals(it.getParentType()))
                .collect(Collectors.toList());
        return mapTranslations(translationsForForm);
    }

    @Nonnull
    private List<Question> getQuestionList(@Nullable TreeMap<Integer, QuestionGroup> questionGroupTreeMap) {
        if (questionGroupTreeMap != null) {
            List<QuestionGroup> groups = new ArrayList<>(questionGroupTreeMap.values());
            return groups.stream().filter(it -> it.getQuestionMap() != null).map(this::getValues).collect(Collectors.toList())
                    .stream().flatMap(Collection::stream).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private List<Question> getValues(QuestionGroup questionGroup) {
        TreeMap<Integer, Question> questionMap = questionGroup.getQuestionMap();
        if (questionMap == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(questionMap.values());
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
            log.severe("Error uploading zip file: " + e.toString());
            return false;
        }
    }

}
