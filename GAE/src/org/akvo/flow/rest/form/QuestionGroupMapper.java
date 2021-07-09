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

/**
 * Maps a questionGroup from a questionGroupDto
 */
public class QuestionGroupMapper {
    private final QuestionMapper questionMapper;

    public QuestionGroupMapper(QuestionMapper questionMapper) {
        this.questionMapper = questionMapper;
    }

    @Nonnull
    TreeMap<Integer, QuestionGroup> mapGroups(List<QuestionGroupDto> groupDtos) {
        TreeMap<Integer, QuestionGroup> groupMap = new TreeMap<>();
        if (groupDtos != null) {
            int i = 1;
            for (QuestionGroupDto groupDto : groupDtos) {
                QuestionGroup group = mapGroup(groupDto);
                Integer groupDtoOrder = groupDto.getOrder();
                if (groupDtoOrder != null && groupMap.containsKey(groupDtoOrder)) {
                    group.setOrder(i);
                    groupMap.put(i, group);
                } else {
                    int order = groupDtoOrder != null ? groupDtoOrder : i;
                    group.setOrder(order);
                    groupMap.put(order, group);
                }
                i++;
            }
        }
        return groupMap;
    }

    private QuestionGroup mapGroup(QuestionGroupDto groupDto) {
        QuestionGroup group = new QuestionGroup();
        group.setKey(KeyFactory.createKey("QuestionGroup", groupDto.getKeyId()));
        group.setCode(groupDto.getCode());
        group.setSurveyId(groupDto.getSurveyId());
        group.setOrder(groupDto.getOrder());
        group.setPath(groupDto.getPath());
        group.setName(groupDto.getName());
        group.setRepeatable(groupDto.getRepeatable());
        String status = groupDto.getStatus();
        if (status != null) {
            group.setStatus(QuestionGroup.Status.valueOf(status));
        }
        group.setImmutable(groupDto.getImmutable());
        group.setQuestionMap(questionMapper.mapQuestions(groupDto.getQuestionList()));
        return group;
    }
}
