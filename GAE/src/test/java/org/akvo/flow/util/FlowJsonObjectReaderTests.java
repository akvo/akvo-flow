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
import static org.junit.jupiter.api.Assertions.assertNotEquals;


import com.fasterxml.jackson.core.type.TypeReference;
import org.akvo.flow.util.FlowJsonObjectReader;
import org.junit.jupiter.api.Test;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.domain.CaddisflyResource;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class FlowJsonObjectReaderTests {

    private String QUESTION_DTO_JSON_STRING = "{ \"type\": \"FREE_TEXT\",\n" +
                                                "\"text\": \"How many toilets are present?\",\n" +
                                                "\"dependentFlag\": false,\n" +
                                                "\"questionGroupId\": 12345678,\n" +
                                                "\"surveyId\": 910111213,\n" +
                                                "\"order\": 0 }";

    private String COMPLEX_JSON_OBJECT = "{\n" +
                                        "  \"tests\": [\n" +
                                        "    {\n" +
                                        "      \"name\": \"Soil - Electrical Conductivity\",\n" +
                                        "      \"subtype\": \"sensor\",\n" +
                                        "      \"uuid\": \"80697cd1-acc9-4a15-8358-f32b4257dfaf\",\n" +
                                        "      \"deviceId\": \"SoilEC\",\n" +
                                        "      \"brand\": \"Caddisfly\",\n" +
                                        "      \"image\": \"Caddisfly-Soil-EC\",\n" +
                                        "      \"imageScale\": \"centerCrop\",\n" +
                                        "      \"ranges\": \"50,12800\",\n" +
                                        "      \"responseFormat\": \"$2,$1\",\n" +
                                        "      \"instructions\": [],\n" +
                                        "      \"results\": [\n" +
                                        "        {\n" +
                                        "          \"id\": 1,\n" +
                                        "          \"name\": \"Soil Electrical Conductivity\",\n" +
                                        "          \"unit\": \"μS/cm\"\n" +
                                        "        },\n" +
                                        "        {\n" +
                                        "          \"id\": 2,\n" +
                                        "          \"name\": \"Temperature\",\n" +
                                        "          \"unit\": \"°Celsius\"\n" +
                                        "        }\n" +
                                        "      ]\n" +
                                        "    },\n" +
                                        "    {\n" +
                                        "      \"name\": \"Soil - Moisture\",\n" +
                                        "      \"subtype\": \"sensor\",\n" +
                                        "      \"uuid\": \"0b4a0aaa-f556-4c11-a539-c4626582cca6\",\n" +
                                        "      \"deviceId\": \"Soil Moisture\",\n" +
                                        "      \"brand\": \"Caddisfly\",\n" +
                                        "      \"image\": \"Caddisfly-Soil-Moisture\",\n" +
                                        "      \"imageScale\": \"centerCrop\",\n" +
                                        "      \"ranges\": \"0,100\",\n" +
                                        "      \"responseFormat\": \"$1\",\n" +
                                        "      \"instructions\": [],\n" +
                                        "      \"results\": [\n" +
                                        "        {\n" +
                                        "          \"id\": 1,\n" +
                                        "          \"name\": \"Soil Moisture\",\n" +
                                        "          \"unit\": \"% VWC\"\n" +
                                        "        }\n" +
                                        "      ]\n" +
                                        "    }\n" +
                                        "]}";

    @Test
    void testReadSimpleJsonObject() {
        FlowJsonObjectReader reader = new FlowJsonObjectReader();
        TypeReference<QuestionDto> typeReference = new TypeReference<QuestionDto>() {};
        QuestionDto testQuestionDto = null;
        try {
            testQuestionDto = reader.readObject(QUESTION_DTO_JSON_STRING, typeReference);
        } catch (IOException e) {
            //
        }
        assertEquals(testQuestionDto.getType(), QuestionDto.QuestionType.FREE_TEXT);
        assertEquals(testQuestionDto.getText(),"How many toilets are present?");
        assertFalse(testQuestionDto.getDependentFlag());
        assertEquals(testQuestionDto.getQuestionGroupId(), 12345678L);
        assertEquals(testQuestionDto.getType(), QuestionDto.QuestionType.FREE_TEXT);
        assertEquals(testQuestionDto.getOrder(), 0);
    }

    @Test
    void testReadComplexJsonObject() {
        FlowJsonObjectReader reader = new FlowJsonObjectReader();
        TypeReference<Map<String, List<CaddisflyResource>>> typeReference = new TypeReference<Map<String, List<CaddisflyResource>>>() {};
        Map<String, List<CaddisflyResource>> resourcesMap = new HashMap<>();
        try {
            resourcesMap = reader.readObject(COMPLEX_JSON_OBJECT, typeReference);
        } catch (IOException e) {
            //
        }

        List<CaddisflyResource> resourcesList = resourcesMap.get("tests");
        assertNotEquals(resourcesList, null);
        assertEquals(resourcesList.size(), 2);
        assertEquals(resourcesList.get(0).getResults().size(), 2);
        assertEquals(resourcesList.get(0).getName(), "Soil - Electrical Conductivity");
        assertEquals(resourcesList.get(1).getResults().size(), 1);
        assertEquals(resourcesList.get(1).getUuid(), "0b4a0aaa-f556-4c11-a539-c4626582cca6");

    }
}
