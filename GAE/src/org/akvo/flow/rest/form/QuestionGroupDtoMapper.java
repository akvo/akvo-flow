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

import com.gallatinsystems.survey.domain.QuestionGroup;
import com.google.appengine.api.datastore.KeyFactory;
import java.util.List;
import java.util.TreeMap;
import javax.annotation.Nonnull;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;

public class QuestionGroupDtoMapper {
    private final QuestionDtoMapper questionDtoMapper;

    public QuestionGroupDtoMapper(QuestionDtoMapper questionDtoMapper) {
        this.questionDtoMapper = questionDtoMapper;
    }

    @Nonnull
    TreeMap<Integer, QuestionGroup> getGroupMap(SurveyDto surveyDto) {
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

    QuestionGroup mapToGroup(QuestionGroupDto groupDto) {
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
        group.setQuestionMap(questionDtoMapper.mapQuestions(groupDto));
        return group;
    }
}
