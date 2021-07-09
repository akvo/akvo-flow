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

import com.gallatinsystems.survey.dao.CascadeResourceDao;
import com.gallatinsystems.survey.domain.CascadeResource;
import com.gallatinsystems.survey.domain.Question;
import static com.gallatinsystems.survey.domain.Question.Type.CASCADE;
import com.gallatinsystems.survey.domain.QuestionGroup;
import com.gallatinsystems.survey.domain.Survey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;

/**
 * Assembles a form
 */
public class FormAssembler {

    private final FormMapper formMapper;
    private final TranslationsAppender translationsAppender;

    public FormAssembler(FormMapper formMapper, TranslationsAppender translationsAppender) {
        this.formMapper = formMapper;
        this.translationsAppender = translationsAppender;
    }

    Survey assembleForm(SurveyDto surveyDto) {
        Survey form = formMapper.mapFormFromDto(surveyDto);
        translationsAppender.attachFormTranslations(form);
        List<Question> questions = getQuestionList(form.getQuestionGroupMap());
        attachCascadeResources(questions);
        return form;
    }


    private void attachCascadeResources(List<Question> questions) {
        CascadeResourceDao cascadeResourceDao = new CascadeResourceDao();
        for (Question question : questions) {
            if (CASCADE.equals(question.getType())) {
                CascadeResource cascadeResource = cascadeResourceDao.getByKey(question.getCascadeResourceId());
                if (cascadeResource != null) {
                    question.setCascadeResource(cascadeResource.getResourceId());
                    question.setLevelNames(cascadeResource.getLevelNames());
                }
            }
        }
    }

    @Nonnull
    List<Question> getQuestionList(@Nullable TreeMap<Integer, QuestionGroup> questionGroupTreeMap) {
        if (questionGroupTreeMap != null) {
            List<QuestionGroup> groups = new ArrayList<>(questionGroupTreeMap.values());
            return groups.stream()
                    .filter(it -> it.getQuestionMap() != null)
                    .map(this::getListOfQuestions).collect(Collectors.toList())
                    .stream().flatMap(Collection::stream).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private List<Question> getListOfQuestions(QuestionGroup questionGroup) {
        TreeMap<Integer, Question> questionMap = questionGroup.getQuestionMap();
        if (questionMap == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(questionMap.values());
    }
}
