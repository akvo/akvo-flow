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

import com.gallatinsystems.survey.domain.Question;
import org.akvo.flow.util.FlowJsonObjectWriter;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FlowJsonObjectWriterTests {

    @Test
    void testComplexJsonObject() {
        Map<String, Object> complexJsonObject = new LinkedHashMap<>();
        complexJsonObject.put("nullList", null);
        complexJsonObject.put("emptyList", new ArrayList<>());

        List<Integer> listOfNumbers = new ArrayList<>();
        listOfNumbers.add(1);
        listOfNumbers.add(2);
        listOfNumbers.add(3);
        complexJsonObject.put("listOfNumbers",listOfNumbers);

        List<Object> firstList = new ArrayList<>();
        firstList.add(5345);
        firstList.add(6587);
        firstList.add(9987);

        List<Object> secondList = new ArrayList<>();
        secondList.add("tea");
        secondList.add("coffee");

        Map<String, List<Object>> mapOfLists = new LinkedHashMap<>();
        mapOfLists.put("firstList", firstList);
        mapOfLists.put("secondList", secondList);

        complexJsonObject.put("mapOfLists", mapOfLists);

        FlowJsonObjectWriter writer = new FlowJsonObjectWriter();
        String jsonString = null;
        try {
            jsonString = writer.writeAsString(complexJsonObject);
        } catch (IOException e) {
            // ignoring exception
        }
        String jsonStringExpected = "{\"nullList\":null,\"emptyList\":[],\"listOfNumbers\":[1,2,3],\"mapOfLists\":{\"firstList\":[5345,6587,9987],\"secondList\":[\"tea\",\"coffee\"]}}";
        assertEquals(jsonStringExpected, jsonString);
    }

    @Test
    void testWriteSimpleJsonObjectWithExcludeNonNull() {

        Question question = new Question();
        question.setText("First Question");
        question.setOrder(0);
        question.setType(Question.Type.FREE_TEXT);
        question.setAllowOtherFlag(true);

        FlowJsonObjectWriter writer = new FlowJsonObjectWriter().withExcludeNullValues();
        String jsonString = null;
        try {
            jsonString = writer.writeAsString(question);
        } catch (IOException e) {
            // ignoring exception
        }

        String jsonStringExpected = "{\"type\":\"FREE_TEXT\",\"text\":\"First Question\",\"allowOtherFlag\":true,\"collapseable\":false,\"immutable\":false,\"order\":0}";
        assertEquals(jsonStringExpected, jsonString);
    }
}
