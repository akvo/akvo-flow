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

import com.gallatinsystems.survey.domain.QuestionOption;
import com.google.appengine.api.datastore.KeyFactory;
import java.util.List;
import java.util.TreeMap;
import javax.annotation.Nonnull;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionOptionDto;

public class QuestionOptionDtoMapper {

    @Nonnull
    TreeMap<Integer, QuestionOption> mapToOptions(QuestionDto questionDto) {
        TreeMap<Integer, QuestionOption> mappedOptions = new TreeMap<>();
        List<QuestionOptionDto> dtoList = questionDto.getOptionList();
        if (dtoList != null) {
            for (QuestionOptionDto questionOptionDto : dtoList) {
                mappedOptions.put(questionOptionDto.getOrder(), mapToQuestionOption(questionOptionDto));
            }
        }
        return mappedOptions;
    }

    QuestionOption mapToQuestionOption(QuestionOptionDto questionOptionDto) {
        QuestionOption questionOption = new QuestionOption();
        questionOption.setKey(KeyFactory.createKey("QuestionOption", questionOptionDto.getKeyId()));
        questionOption.setText(questionOptionDto.getText());
        questionOption.setCode(questionOptionDto.getCode());
        questionOption.setOrder(questionOptionDto.getOrder());
        questionOption.setQuestionId(questionOptionDto.getQuestionId());
        return questionOption;
    }
}
