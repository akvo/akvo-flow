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
import com.gallatinsystems.survey.domain.QuestionGroup;
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
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;

class QuestionGroupDtoMapperTest {

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
    public void mapGroupsShouldReturnEmptyMapForNullList() {
        QuestionGroupDtoMapper mapper = new QuestionGroupDtoMapper(new QuestionDtoMapper(new QuestionOptionDtoMapper()));

        TreeMap<Integer, QuestionGroup> result = mapper.mapGroups(new SurveyDto());

        assertEquals(0, result.size());
    }

    @Test
    public void mapGroupsShouldReturnOrderedGroups() {
        QuestionGroupDtoMapper mapper = new QuestionGroupDtoMapper(new QuestionDtoMapper(new QuestionOptionDtoMapper()));
        SurveyDto groupDto = new SurveyDto();
        List<QuestionGroupDto> groupDtos = new ArrayList<>(2);
        QuestionGroupDto groupDto1 = new QuestionGroupDto();
        groupDto1.setKeyId(12345L);
        groupDto1.setOrder(2);
        groupDtos.add(groupDto1);
        QuestionGroupDto groupDto2 = new QuestionGroupDto();
        groupDto2.setKeyId(123456L);
        groupDto2.setOrder(1);
        groupDtos.add(groupDto2);
        groupDto.setQuestionGroupList(groupDtos);

        TreeMap<Integer, QuestionGroup> result = mapper.mapGroups(groupDto);

        assertEquals(2, result.size());
        assertEquals(2, new ArrayList<>(result.values()).get(1).getOrder());
    }

    @Test
    public void mapGroupsShouldReturnOrderedGroupsEvenIfMissingOrder() {
        QuestionGroupDtoMapper mapper = new QuestionGroupDtoMapper(new QuestionDtoMapper(new QuestionOptionDtoMapper()));
        SurveyDto groupDto = new SurveyDto();
        List<QuestionGroupDto> groupDtos = new ArrayList<>(2);
        QuestionGroupDto groupDto1 = new QuestionGroupDto();
        groupDto1.setKeyId(12345L);
        groupDtos.add(groupDto1);
        QuestionGroupDto groupDto2 = new QuestionGroupDto();
        groupDto2.setKeyId(123456L);
        groupDtos.add(groupDto2);
        groupDto.setQuestionGroupList(groupDtos);

        TreeMap<Integer, QuestionGroup> result = mapper.mapGroups(groupDto);

        assertEquals(2, result.size());
        assertEquals(2, new ArrayList<>(result.values()).get(1).getOrder());
    }
}
