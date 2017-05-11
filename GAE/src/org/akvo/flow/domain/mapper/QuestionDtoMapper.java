/*
 *  Copyright (C) 2017 Stichting Akvo (Akvo Foundation)
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
package org.akvo.flow.domain.mapper;

import com.gallatinsystems.survey.domain.Question;

import org.springframework.beans.BeanUtils;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;

import javax.annotation.Nullable;

import java.util.*;

public class QuestionDtoMapper {

    @Nullable
    public static QuestionDto transform(Question question) {
        if (question != null) {
            QuestionDto questionDto = new QuestionDto();

            String[] questionPropertiesNonExistingInQuestionDto = {
                    "key",
                    "type",
                    "createdDateTime",
                    "lastUpdateDateTime",
                    "lastUpdateUserId",
                    "createUserId",
                    "ancestorIds",
                    "translationMap",
                    "questionOptionMap",
                    "questionHelpMediaMap",
                    "scoringRules",
                    "sourceQuestionId",
                    "referenceId"
            };
            BeanUtils.copyProperties(question, questionDto,
                    questionPropertiesNonExistingInQuestionDto);
            if (question.getType() != null) {
                questionDto.setType(QuestionDto.QuestionType.valueOf(question.getType()
                        .toString()));
            }
            questionDto.setKeyId(question.getKey().getId());
            return questionDto;
        } else {
            return null;
        }
    }

    public List<QuestionDto> transform(List<Question> questions) {
        int size = questions == null ? 0 : questions.size();
        List<QuestionDto> questionDtos = new ArrayList<>(size);
        if (questions != null) {
            for (Question question : questions) {
                QuestionDto questionDto = transform(question);
                if (questionDto != null) {
                    questionDtos.add(questionDto);
                }
            }
        }
        return questionDtos;
    }
}
