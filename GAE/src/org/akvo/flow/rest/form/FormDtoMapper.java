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
import com.google.appengine.api.datastore.KeyFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;

public class FormDtoMapper {
    private final QuestionGroupDtoMapper questionGroupDtoMapper;
    private final TranslationsDtoMapper translationsDtoMapper;

    public FormDtoMapper(QuestionGroupDtoMapper questionGroupDtoMapper, TranslationsDtoMapper translationsDtoMapper) {
        this.questionGroupDtoMapper = questionGroupDtoMapper;
        this.translationsDtoMapper = translationsDtoMapper;
    }

    Survey assembleForm(SurveyDto surveyDto) {
        Survey form = mapFormFromDto(surveyDto);
        translationsDtoMapper.attachFormTranslations(form);
        List<Question> questions = getQuestionList(form.getQuestionGroupMap());
        attachCascadeResources(questions);
        return form;
    }

    Survey mapFormFromDto(SurveyDto surveyDto) {
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
        TreeMap<Integer, QuestionGroup> groupMap = questionGroupDtoMapper.mapGroups(surveyDto);
        form.setQuestionGroupMap(groupMap);
        return form;
    }

    void attachCascadeResources(List<Question> questions) {
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

    List<Question> getListOfQuestions(QuestionGroup questionGroup) {
        TreeMap<Integer, Question> questionMap = questionGroup.getQuestionMap();
        if (questionMap == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(questionMap.values());
    }
}
