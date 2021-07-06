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

import com.gallatinsystems.survey.dao.TranslationDao;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.QuestionGroup;
import com.gallatinsystems.survey.domain.QuestionOption;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.survey.domain.Translation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.annotation.Nullable;

public class TranslationsDtoMapper {

    public TranslationsDtoMapper() {
    }

    void attachFormTranslations(Survey form) {
        Long formId = form.getObjectId();
        Map<Long, List<Translation>> translations = new TranslationDao().mappedTranslationsByParentId(formId);
        HashMap<String, Translation> formTranslationMap = getMappedTranslationsForParent(translations, formId);
        if (formTranslationMap.size() > 0) {
            form.setTranslationMap(formTranslationMap);
        }
        TreeMap<Integer, QuestionGroup> questionGroupMap = form.getQuestionGroupMap();
        if (questionGroupMap != null) {
            List<QuestionGroup> groups = new ArrayList<>(questionGroupMap.values());
            for (QuestionGroup group : groups) {
                HashMap<String, Translation> map = getMappedTranslationsForParent(translations, group.getKey().getId());
                if (map.size() > 0) {
                    group.setTranslations(map);
                }
                TreeMap<Integer, Question> questionMap = group.getQuestionMap();
                if (questionMap != null) {
                    List<Question> questions = new ArrayList<>(questionMap.values());
                    for (Question question : questions) {
                        List<Translation> questionTranslations = translations.get(question.getKey().getId());
                        if (questionTranslations != null && questionTranslations.size() > 0) {
                            question.setTranslations(questionTranslations);
                        }
                        TreeMap<Integer, QuestionOption> questionOptionMap = question.getQuestionOptionMap();
                        if (questionOptionMap != null) {
                            List<QuestionOption> options = new ArrayList<>(questionOptionMap.values());
                            for (QuestionOption option : options) {
                                HashMap<String, Translation> optionTranslationsMap = getMappedTranslationsForParent(translations, option.getKey().getId());
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

    HashMap<String, Translation> getMappedTranslationsForParent(Map<Long, List<Translation>> translations, Long parentId) {
        List<Translation> translationsForForm = translations.get(parentId);
        return mapTranslations(translationsForForm);
    }

    HashMap<String, Translation> mapTranslations(@Nullable List<Translation> translations) {
        HashMap<String, Translation> mappedTranslations = new HashMap<>();
        if (translations != null) {
            for (Translation t : translations) {
                mappedTranslations.put(t.getLanguageCode(), t);
            }
        }
        return mappedTranslations;
    }
}
