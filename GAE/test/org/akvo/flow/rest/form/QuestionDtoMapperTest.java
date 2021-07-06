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

import com.gallatinsystems.survey.domain.Question;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;

class QuestionDtoMapperTest {

    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    @BeforeEach
    public void setUp() {
        helper.setUp();
    }

    @AfterEach
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void mapQuestionsShouldReturnEmptyMapForNullList() {
        QuestionDtoMapper mapper = new QuestionDtoMapper(new QuestionOptionDtoMapper());

        TreeMap<Integer, Question> result = mapper.mapQuestions(new QuestionGroupDto());

        assertEquals(0, result.size());
    }

    @Test
    public void mapQuestionsShouldReturnOrderedQuestions() {
        QuestionDtoMapper mapper = new QuestionDtoMapper(new QuestionOptionDtoMapper());
        QuestionGroupDto groupDto = new QuestionGroupDto();
        List<QuestionDto> questionList = new ArrayList<>(2);
        QuestionDto questionDto = new QuestionDto();
        questionDto.setType(QuestionDto.QuestionType.DATE);
        questionDto.setKeyId(12345L);
        questionDto.setOrder(2);
        questionList.add(questionDto);
        QuestionDto questionDto1 = new QuestionDto();
        questionDto1.setKeyId(123456L);
        questionDto1.setOrder(1);
        questionDto1.setType(QuestionDto.QuestionType.DATE);
        questionList.add(questionDto1);
        groupDto.setQuestionList(questionList);

        TreeMap<Integer, Question> result = mapper.mapQuestions(groupDto);

        assertEquals(2, result.size());
        assertEquals(2, new ArrayList<>(result.values()).get(1).getOrder());
        assertEquals(Question.Type.DATE, new ArrayList<>(result.values()).get(1).getType());
    }
}
