/*
 *  Copyright (C) 2015 Stichting Akvo (Akvo Foundation)
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
package org.akvo.flow.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import org.junit.Test;
import org.waterforpeople.mapping.domain.QuestionAnswerStore;
import org.waterforpeople.mapping.domain.SurveyInstance;
import org.waterforpeople.mapping.serialization.SurveyInstanceHandler;

public class DataSerializationTest {
    private static final String TSV_12_SURVEY_INSTANCE = "555\t\t1111\tVALUE\tquestionvalue\ttestuser\t\t1234\t\t\t\tabc";
    private static final String TSV_13_SURVEY_INSTANCE = "555\t\t1111\tVALUE\tquestionvalue\ttestuser\t\t1234\t\t\t\tabc\t10";
    private static final String TSV_14_SURVEY_INSTANCE = "555\t\t1111\tVALUE\tquestionvalue\ttestuser\t\t1234\t\t\t\tabc\t10\txyz";
    private static final String JSON_SURVEY_INSTANCE = "{"
            + "\"formId\": 555, \"submissionDate\": 1234,"
            + "\"duration\": 10, \"dataPointId\": \"xyz\","
            + "\"uuid\": \"abc\", \"deviceId\": \"test\","
            + "\"username\": \"testuser\","
            + "\"responses\": ["
            + "{\"questionId\": \"1111\", \"answerType\": \"VALUE\", \"value\":\"questionvalue\"}"
            + "]}";

    @Test
    public void JSONTest() {
        SurveyInstanceHandler handler = new SurveyInstanceHandler();
        SurveyInstance si = handler.fromJSON(JSON_SURVEY_INSTANCE);
        testSurveyInstance(si, true, true);
    }

    @Test
    public void TSVTest() {
        SurveyInstanceHandler handler = new SurveyInstanceHandler();
        // 12 token TSV
        SurveyInstance si = handler.fromTSV(new ArrayList<>(Arrays.asList(TSV_12_SURVEY_INSTANCE)));
        testSurveyInstance(si, false, false);
        
        // 13 token TSV
        si = handler.fromTSV(new ArrayList<>(Arrays.asList(TSV_13_SURVEY_INSTANCE)));
        testSurveyInstance(si, true, false);
        
        // 14 token TSV
        si = handler.fromTSV(new ArrayList<>(Arrays.asList(TSV_14_SURVEY_INSTANCE)));
        testSurveyInstance(si, true, true);
    }

    private void testSurveyInstance(SurveyInstance si, boolean hasDuration, boolean hasDataPointId) {
        assertNotNull(si);
        assertEquals(555, si.getSurveyId().longValue());
        assertEquals(new Date(1234), si.getCollectionDate());
        assertEquals("abc", si.getUuid());
        assertEquals("testuser", si.getSubmitterName());
        assertEquals(false, si.getQuestionAnswersStore().isEmpty());
        QuestionAnswerStore qas = si.getQuestionAnswersStore().get(0);
        assertEquals("1111", qas.getQuestionID());
        assertEquals("VALUE", qas.getType());
        assertEquals("questionvalue", qas.getValue());
        if (hasDuration) {
            assertEquals(10, si.getSurveyalTime().longValue());
        }
        if (hasDataPointId) {
            assertEquals("xyz", si.getSurveyedLocaleIdentifier());
        }
    }

}
