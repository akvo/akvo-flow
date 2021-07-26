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
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionOptionDto;

class QuestionOptionMapperTest {

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
    public void mapToOptionsShouldReturnEmptyMapForNullList() {
        QuestionOptionMapper mapper = new QuestionOptionMapper();

        final QuestionDto questionDto = new QuestionDto();
        TreeMap<Integer, QuestionOption> result = mapper.mapOptions(questionDto.getOptionList());

        assertEquals(0, result.size());
    }

    @Test
    public void mapToOptionsShouldReturnOrderedOptions() {
        QuestionOptionMapper mapper = new QuestionOptionMapper();
        QuestionDto questionDto = new QuestionDto();
        List<QuestionOptionDto> optionList = new ArrayList<>();
        QuestionOptionDto optionDto = new QuestionOptionDto();
        optionDto.setKeyId(12345L);
        optionDto.setOrder(2);
        optionList.add(optionDto);
        QuestionOptionDto optionDto2 = new QuestionOptionDto();
        optionDto2.setKeyId(123456L);
        optionDto2.setOrder(1);
        optionList.add(optionDto2);
        questionDto.setOptionList(optionList);

        TreeMap<Integer, QuestionOption> result = mapper.mapOptions(questionDto.getOptionList());

        assertEquals(1, new ArrayList<>(result.values()).get(0).getOrder());
        assertEquals(2, new ArrayList<>(result.values()).get(1).getOrder());
    }
}
