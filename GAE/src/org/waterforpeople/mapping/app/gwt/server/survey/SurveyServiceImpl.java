/*
 *  Copyright (C) 2010-2018 Stichting Akvo (Akvo Foundation)
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

package org.waterforpeople.mapping.app.gwt.server.survey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.waterforpeople.mapping.app.gwt.client.survey.OptionContainerDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDependencyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionHelpDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionOptionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.TranslationDto;
import org.waterforpeople.mapping.app.util.DtoMarshaller;
import org.waterforpeople.mapping.app.web.DataProcessorRestServlet;
import org.waterforpeople.mapping.app.web.dto.SurveyAssemblyRequest;

import com.gallatinsystems.survey.dao.CascadeResourceDao;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.dao.SurveyGroupDAO;
import com.gallatinsystems.survey.domain.CascadeResource;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.QuestionHelpMedia;
import com.gallatinsystems.survey.domain.QuestionOption;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.survey.domain.SurveyGroup;
import com.gallatinsystems.survey.domain.Translation;
import com.gallatinsystems.survey.domain.Translation.ParentType;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

public class SurveyServiceImpl {

    public static final String FREE_QUESTION_TYPE = "free";
    public static final String OPTION_QUESTION_TYPE = "option";
    public static final String GEO_QUESTION_TYPE = "geo";
    public static final String VIDEO_QUESTION_TYPE = "video";
    public static final String PHOTO_QUESTION_TYPE = "photo";
    public static final String SCAN_QUESTION_TYPE = "scan";
    public static final String STRENGTH_QUESTION_TYPE = "strength";

    private static final Logger log = Logger.getLogger(SurveyServiceImpl.class
            .getName());

    public static QuestionDto marshalQuestionDto(Question q) {
        QuestionDto qDto = new QuestionDto();

        DtoMarshaller.copyToDto(q, qDto);

        if (q.getQuestionHelpMediaMap() != null) {
            for (QuestionHelpMedia help : q.getQuestionHelpMediaMap().values()) {
                QuestionHelpDto dto = new QuestionHelpDto();
                Map<String, Translation> transMap = help.getTranslationMap();
                help.setTranslationMap(null);
                DtoMarshaller.copyToDto(help, dto);
                if (transMap != null) {
                    dto.setTranslationMap(marshalTranslations(transMap));
                }
                qDto.addQuestionHelp(dto);
            }
        }

        if (q.getQuestionOptionMap() != null) {
            OptionContainerDto ocDto = new OptionContainerDto();
            if (q.getAllowOtherFlag() != null)
                ocDto.setAllowOtherFlag(q.getAllowOtherFlag());
            if (q.getAllowMultipleFlag() != null)
                ocDto.setAllowMultipleFlag(q.getAllowMultipleFlag());
            for (QuestionOption qo : q.getQuestionOptionMap().values()) {
                QuestionOptionDto ooDto = new QuestionOptionDto();
                ooDto.setTranslationMap(marshalTranslations(qo
                        .getTranslationMap()));
                ooDto.setKeyId(qo.getKey().getId());
                if (qo.getCode() != null)
                    ooDto.setCode(qo.getCode());
                if (qo.getText() != null)
                    ooDto.setText(qo.getText());
                ooDto.setOrder(qo.getOrder());
                ocDto.addQuestionOption(ooDto);

            }
            qDto.setOptionContainerDto(ocDto);
        }

        if (q.getDependentQuestionId() != null) {
            QuestionDependencyDto qdDto = new QuestionDependencyDto();
            qdDto.setQuestionId(q.getDependentQuestionId());
            qdDto.setAnswerValue(q.getDependentQuestionAnswer());
            qDto.setQuestionDependency(qdDto);
        }

        qDto.setTranslationMap(marshalTranslations(q.getTranslationMap()));

        if (Question.Type.CASCADE.equals(q.getType()) && q.getCascadeResourceId() != null) {
            qDto.setLevelNames(getCascadeResourceLevelNames(q.getCascadeResourceId()));
        }

        return qDto;
    }

    private static List<String> getCascadeResourceLevelNames(Long id) {
        final CascadeResource cr = new CascadeResourceDao().getByKey(id);
        if (cr == null || cr.getLevelNames() == null || cr.getLevelNames().isEmpty()) {
            return null;
        }
        return cr.getLevelNames();
    }

    private static TreeMap<String, TranslationDto> marshalTranslations(
            Map<String, Translation> translationMap) {
        TreeMap<String, TranslationDto> transMap = null;
        if (translationMap != null && translationMap.size() > 0) {
            transMap = new TreeMap<String, TranslationDto>();
            for (Translation trans : translationMap.values()) {
                TranslationDto tDto = new TranslationDto();
                tDto.setKeyId(trans.getKey().getId());
                tDto.setLangCode(trans.getLanguageCode());
                tDto.setText(trans.getText());
                tDto.setParentId(trans.getParentId());
                tDto.setParentType(trans.getParentType().toString());
                transMap.put(tDto.getLangCode(), tDto);
            }
        }
        return transMap;
    }

    private static TreeMap<String, Translation> marshalFromDtoTranslations(
            Map<String, TranslationDto> translationMap) {
        TreeMap<String, Translation> transMap = null;
        if (translationMap != null && translationMap.size() > 0) {
            transMap = new TreeMap<String, Translation>();
            for (TranslationDto trans : translationMap.values()) {
                Translation t = new Translation();
                if (trans.getKeyId() != null)
                    t.setKey((KeyFactory.createKey(
                            Translation.class.getSimpleName(), trans.getKeyId())));
                t.setLanguageCode(trans.getLangCode());
                t.setText(trans.getText());
                t.setParentId(trans.getParentId());
                if (trans.getParentType().equals(
                        Translation.ParentType.QUESTION_TEXT.toString())) {
                    t.setParentType(ParentType.QUESTION_TEXT);
                } else if (trans.getParentType().equals(
                        Translation.ParentType.QUESTION_OPTION.toString())) {
                    t.setParentType(ParentType.QUESTION_OPTION);
                } else if (Translation.ParentType.QUESTION_HELP_MEDIA_TEXT
                        .toString().equals(trans.getParentType())) {
                    t.setParentType(ParentType.QUESTION_HELP_MEDIA_TEXT);
                }
                transMap.put(t.getLanguageCode(), t);
            }
        }
        return transMap;
    }

    public Question marshalQuestion(QuestionDto qdto) {
        Question q = new Question();

        DtoMarshaller.copyToCanonical(q, qdto);

        /*
         * TODO: remove as same code seems to be duplicated later in this method if
         * (qdto.getQuestionHelpList() != null) { List<QuestionHelpDto> qHListDto =
         * qdto.getQuestionHelpList(); for (QuestionHelpDto qhDto : qHListDto) { QuestionHelpMedia
         * qh = new QuestionHelpMedia(); DtoMarshaller.copyToCanonical(qh, qhDto); } }
         */

        if (qdto.getOptionContainerDto() != null) {
            OptionContainerDto ocDto = qdto.getOptionContainerDto();

            if (ocDto.getAllowOtherFlag() != null) {
                q.setAllowOtherFlag(ocDto.getAllowOtherFlag());
            }
            if (ocDto.getAllowMultipleFlag() != null) {
                q.setAllowMultipleFlag(ocDto.getAllowMultipleFlag());
            }

            if (ocDto.getOptionsList() != null) {
                ArrayList<QuestionOptionDto> optionDtoList = ocDto
                        .getOptionsList();
                for (QuestionOptionDto qoDto : optionDtoList) {
                    QuestionOption oo = new QuestionOption();
                    if (qoDto.getKeyId() != null)
                        oo.setKey((KeyFactory.createKey(
                                QuestionOption.class.getSimpleName(),
                                qoDto.getKeyId())));
                    if (qoDto.getCode() != null)
                        oo.setCode(qoDto.getCode());
                    if (qoDto.getText() != null)
                        oo.setText(qoDto.getText());
                    oo.setOrder(qoDto.getOrder());
                    // Hack
                    if (qoDto.getTranslationMap() != null) {
                        TreeMap<String, Translation> transTreeMap = SurveyServiceImpl
                                .marshalFromDtoTranslations(qoDto
                                        .getTranslationMap());

                        HashMap<String, Translation> transMap = new HashMap<String, Translation>();
                        for (Map.Entry<String, Translation> entry : transTreeMap
                                .entrySet()) {
                            transMap.put(entry.getKey(), entry.getValue());
                        }
                        oo.setTranslationMap(transMap);
                    }
                    q.addQuestionOption(oo);
                }
            }
        }

        /*
         * TODO: remove. probably not necessary as already covered by dependentFlag,
         * dependentQuestionId, and dependentQUestionAnswer members in schema if
         * (qdto.getQuestionDependency() != null) {
         * q.setDependentQuestionId(qdto.getQuestionDependency() .getQuestionId());
         * q.setDependentQuestionAnswer(qdto.getQuestionDependency() .getAnswerValue());
         * q.setDependentFlag(true); }
         */

        if (qdto.getTranslationMap() != null) {
            TreeMap<String, Translation> transMap = marshalFromDtoTranslations(qdto
                    .getTranslationMap());
            q.setTranslationMap(transMap);
        }

        if (qdto.getQuestionHelpList() != null) {
            int count = 0;
            for (QuestionHelpDto help : qdto.getQuestionHelpList()) {
                QuestionHelpMedia helpDomain = new QuestionHelpMedia();
                Map<String, TranslationDto> transMap = help.getTranslationMap();
                help.setTranslationMap(null);
                DtoMarshaller.copyToCanonical(helpDomain, help);
                if (transMap != null) {
                    helpDomain
                            .setTranslationMap(marshalFromDtoTranslations(transMap));
                }
                q.addHelpMedia(count++, helpDomain);
            }
        }

        return q;
    }

    public void publishSurveyAsync(Long surveyId) {
        TaskOptions options = TaskOptions.Builder
                .withUrl("/app_worker/surveyassembly")
                .param("action", SurveyAssemblyRequest.ASSEMBLE_SURVEY)
                .param("surveyId", surveyId.toString());
        com.google.appengine.api.taskqueue.Queue queue = com.google.appengine.api.taskqueue.QueueFactory
                .getQueue("surveyAssembly");
        queue.add(options);

        Survey s = new SurveyDAO().getById(surveyId);
        SurveyGroup sg = s != null ? new SurveyGroupDAO().getByKey(s.getSurveyGroupId()) : null;
        if (sg != null && sg.getNewLocaleSurveyId() != null &&
                sg.getNewLocaleSurveyId().longValue() == surveyId.longValue()) {
            // This is the registration form. Schedule datapoint name re-assembly
            DataProcessorRestServlet.scheduleDatapointNameAssembly(sg.getKey().getId(), null);
        }
    }
}
