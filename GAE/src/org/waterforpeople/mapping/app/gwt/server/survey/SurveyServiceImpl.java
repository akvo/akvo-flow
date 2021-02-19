/*
 *  Copyright (C) 2010-2019 Stichting Akvo (Akvo Foundation)
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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import java.util.stream.Collectors;
import org.waterforpeople.mapping.app.gwt.client.survey.OptionContainerDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDependencyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionHelpDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionOptionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.TranslationDto;
import org.waterforpeople.mapping.app.util.DtoMarshaller;
import com.gallatinsystems.survey.dao.CascadeResourceDao;
import com.gallatinsystems.survey.domain.CascadeResource;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.QuestionHelpMedia;
import com.gallatinsystems.survey.domain.QuestionOption;
import com.gallatinsystems.survey.domain.Translation;
import com.gallatinsystems.survey.domain.Translation.ParentType;
import com.google.appengine.api.datastore.KeyFactory;

@Deprecated
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

        qDto.setTranslationMap(marshalTranslations(q.translationsAsMap()));

        if (Question.Type.CASCADE.equals(q.getType()) && q.getCascadeResourceId() != null) {
            qDto.setLevelNames(getCascadeResourceLevelNames(q.getCascadeResourceId()));
        }

        return qDto;
    }

    public static QuestionDto marshalQuestionDtoWithTipTranslation(Question q) {
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

        Map<String, Translation> translationMap = q.translationsAsMap();
        qDto.setTranslationMap(marshalTranslations(translationMap));

        if (q.getTip() != null && q.getTip() != "") {
            List<Translation> translations = q.getTranslations();
            Map<String, TranslationDto> tipTranslations = new TreeMap<>();
            for (Translation t: translations) {
                if (t.getParentType().equals(ParentType.QUESTION_TIP)) {
                    TranslationDto tDto = new TranslationDto();
                    tDto.setKeyId(t.getKey().getId());
                    tDto.setLangCode(t.getLanguageCode());
                    tDto.setText(t.getText());
                    tDto.setParentId(t.getParentId());
                    tDto.setParentType(t.getParentType().toString());
                    tipTranslations.put(t.getLanguageCode(), tDto);
                }
            }
            if (!tipTranslations.isEmpty()) {
                QuestionHelpDto dto = new QuestionHelpDto();
                dto.setText(q.getTip());
                dto.setTranslationMap(tipTranslations);
                qDto.addQuestionHelp(dto);
            }
        }

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

    private static TreeMap<String, Translation> marshalFromDtoTranslations(Map<String, TranslationDto> translationMap) {
        TreeMap<String, Translation> transMap = null;
        if (translationMap != null && translationMap.size() > 0) {
            transMap = new TreeMap<>();
            for (TranslationDto trans : translationMap.values()) {
                Translation t = new Translation();
                if (trans.getKeyId() != null)
                    t.setKey((KeyFactory.createKey(Translation.class.getSimpleName(), trans.getKeyId())));
                t.setLanguageCode(trans.getLangCode());
                t.setText(trans.getText());
                t.setParentId(trans.getParentId());
                if (trans.getParentType().equals(Translation.ParentType.QUESTION_TEXT.toString())) {
                    t.setParentType(ParentType.QUESTION_TEXT);
                } else if (trans.getParentType().equals(Translation.ParentType.QUESTION_OPTION.toString())) {
                    t.setParentType(ParentType.QUESTION_OPTION);
                } else if (Translation.ParentType.QUESTION_HELP_MEDIA_TEXT.toString().equals(trans.getParentType())) {
                    t.setParentType(ParentType.QUESTION_HELP_MEDIA_TEXT);
                } else if (Translation.ParentType.QUESTION_TIP.toString().equals(trans.getParentType())) {
                    t.setParentType(ParentType.QUESTION_TIP);
                }
                transMap.put(t.getLanguageCode(), t);
            }
        }
        return transMap;
    }

    public Question marshalQuestion(QuestionDto qdto) {
        Question q = new Question();

        DtoMarshaller.copyToCanonical(q, qdto);

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

        if (qdto.getTranslationMap() != null) {
            TreeMap<String, Translation> transMap = marshalFromDtoTranslations(qdto.getTranslationMap());
            List<Translation> values = transMap.values().stream().collect(Collectors.toList());
            q.setTranslations(values);
        }

        if (qdto.getQuestionHelpList() != null) {
            int count = 0;
            for (QuestionHelpDto help : qdto.getQuestionHelpList()) {
                QuestionHelpMedia helpDomain = new QuestionHelpMedia();
                Map<String, TranslationDto> transMap = help.getTranslationMap();
                help.setTranslationMap(null);
                DtoMarshaller.copyToCanonical(helpDomain, help);
                if (transMap != null) {
                    helpDomain.setTranslationMap(marshalFromDtoTranslations(transMap));
                }
                q.addHelpMedia(count++, helpDomain);
            }
        }

        return q;
    }

}
