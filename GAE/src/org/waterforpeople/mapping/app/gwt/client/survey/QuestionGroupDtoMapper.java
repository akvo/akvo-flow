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
package org.waterforpeople.mapping.app.gwt.client.survey;

import com.gallatinsystems.survey.domain.QuestionGroup;
import org.springframework.beans.BeanUtils;

import javax.annotation.Nullable;

public class QuestionGroupDtoMapper {

    @Nullable
    public QuestionGroupDto transform(QuestionGroup questionGroup) {
        QuestionGroupDto qgDto = new QuestionGroupDto();
        BeanUtils.copyProperties(questionGroup, qgDto, new String[] {
                "questionMap",
                "translationMap"
        });
        qgDto.setKeyId(questionGroup.getKey().getId());
        return qgDto;
    }
}