/*
 *  Copyright (C) 2019 Stichting Akvo (Akvo Foundation)
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

package test.java.org.akvo.flow.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;


import com.fasterxml.jackson.core.type.TypeReference;
import org.akvo.flow.util.FlowJsonObjectReader;
import org.junit.jupiter.api.Test;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;

import java.io.IOException;

class FlowJsonObjectReaderTests {

    private String QUESTION_DTO_JSON_STRING = "{ \"type\": \"FREE_TEXT\",\n" +
                                                "\"text\": \"How many toilets are present?\",\n" +
                                                "\"dependentFlag\": false,\n" +
                                                "\"questionGroupId\": 12345678,\n" +
                                                "\"surveyId\": 910111213,\n" +
                                                "\"order\": 0 }";

    @Test
    void testReadSimpleJsonObject() {
        FlowJsonObjectReader reader = new FlowJsonObjectReader();
        TypeReference<QuestionDto> typeReference = new TypeReference<QuestionDto>() {};
        QuestionDto testQuestionDto = null;
        try {
            testQuestionDto = reader.readObject(QUESTION_DTO_JSON_STRING, typeReference);
        } catch (IOException e) {
            System.out.println("Reading error: " + e.getMessage());
        }
        assertEquals(testQuestionDto.getType(), QuestionDto.QuestionType.FREE_TEXT);
        assertEquals(testQuestionDto.getText(),"How many toilets are present?");
        assertFalse(testQuestionDto.getDependentFlag());
        assertEquals(testQuestionDto.getQuestionGroupId(), 12345678L);
        assertEquals(testQuestionDto.getType(), QuestionDto.QuestionType.FREE_TEXT);
        assertEquals(testQuestionDto.getOrder(), 0);
    }
}
