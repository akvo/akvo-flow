/*
 *  Copyright (C) 2021 Stichting Akvo (Akvo Foundation)
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

package org.waterforpeople.mapping.serialization;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.waterforpeople.mapping.domain.QuestionAnswerStore;
import org.waterforpeople.mapping.domain.SurveyInstance;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SurveyInstanceHandlerTests {
    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    private final String DATA_JSON = "{\"dataPointId\":\"cjd4-18p7-xtqu\",\"deviceId\":\"valeria_pixel3\",\"duration\":5,\"formId\":\"291513002\",\"formVersion\":1.0,\"responses\":[{\"answerType\":\"META_NAME\",\"iteration\":0,\"questionId\":\"-1\",\"value\":\"test\"},{\"answerType\":\"VALUE\",\"iteration\":0,\"questionId\":\"275613002\",\"value\":\"test\"}],\"submissionDate\":1623940101098,\"username\":\"valeria_pixel3\",\"uuid\":\"5e7d9f3b-54bf-4766-a9ae-c71fcd85abfc\"}";

    @BeforeEach
    public void setUp() {
        helper.setUp();
    }

    @AfterEach
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    void testSimpleSubmission() {
        SurveyInstance formInstance = SurveyInstanceHandler.fromJSON(DATA_JSON);
        assertEquals("cjd4-18p7-xtqu", formInstance.getSurveyedLocaleIdentifier());
        assertEquals("valeria_pixel3", formInstance.getDeviceIdentifier());
        assertEquals(291513002l, formInstance.getSurveyId());

        List<QuestionAnswerStore> responses = formInstance.getQuestionAnswersStore();
        assertEquals(1, responses.size());

    }
}
