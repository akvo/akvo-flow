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

import com.gallatinsystems.survey.domain.QuestionOption;

import org.springframework.beans.BeanUtils;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionOptionDto;

import javax.annotation.Nullable;

public class QuestionOptionDtoMapper {

    @Nullable
    public static QuestionOptionDto transform(QuestionOption questionOption) {
        QuestionOptionDto qoDto = new QuestionOptionDto();
        BeanUtils.copyProperties(questionOption, qoDto, new String[] {
                "translationMap"
        });
        qoDto.setKeyId(questionOption.getKeyId());
        return qoDto;
    }
}
