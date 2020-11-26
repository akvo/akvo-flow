/*
 *  Copyright (C) 2019,2020 Stichting Akvo (Akvo Foundation)
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

package org.akvo.flow.util;

import com.gallatinsystems.survey.domain.Translation;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.gallatinsystems.survey.domain.Question;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FlowJsonObjectWriterTests {

    @Test
    void testComplexJsonObject() {
        Map<String, Object> complexJsonObject = new LinkedHashMap<>();

        Date date = new Date();
        date.setTime(1601983649l);
        complexJsonObject.put("date", date);

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
        String jsonStringExpected = "{\"date\":1601983649,\"nullList\":null,\"emptyList\":[],\"listOfNumbers\":[1,2,3],\"mapOfLists\":{\"firstList\":[5345,6587,9987],\"secondList\":[\"tea\",\"coffee\"]}}";
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

        String jsonStringExpected = "{\"type\":\"FREE_TEXT\",\"text\":\"First Question\",\"allowOtherFlag\":true,\"collapseable\":false,\"immutable\":false,\"personalData\":false,\"order\":0}";
        assertEquals(jsonStringExpected, jsonString);
    }

    @Test
    void testWriteSimpleJsonObjectWithTranslations() {

        Question question = new Question();
        question.setText("First Question");
        question.setOrder(0);
        question.setType(Question.Type.FREE_TEXT);
        question.setAllowOtherFlag(true);
        question.setTip("Help");
        List<Translation> translations = new ArrayList<>(2);
        Translation translation = new Translation();
        translation.setParentType(Translation.ParentType.QUESTION_TEXT);
        translation.setText("Primera pregunta");
        translation.setLanguageCode("es");
        Translation translation2 = new Translation();
        translations.add(translation);
        translation2.setParentType(Translation.ParentType.QUESTION_TIP);
        translation2.setText("Ayuda");
        translation2.setLanguageCode("es");
        translations.add(translation2);
        question.setTranslations(translations);

        FlowJsonObjectWriter writer = new FlowJsonObjectWriter().withExcludeNullValues();
        String jsonString = null;
        try {
            jsonString = writer.writeAsString(question);
        } catch (IOException e) {
            // ignoring exception
        }

        String jsonStringExpected = "{\"type\":\"FREE_TEXT\",\"tip\":\"Help\",\"text\":\"First Question\",\"translations\":[{\"languageCode\":\"es\",\"text\":\"Primera pregunta\",\"parentType\":\"QUESTION_TEXT\"},{\"languageCode\":\"es\",\"text\":\"Ayuda\",\"parentType\":\"QUESTION_TIP\"}],\"allowOtherFlag\":true,\"collapseable\":false,\"immutable\":false,\"personalData\":false,\"order\":0}";
        assertEquals(jsonStringExpected, jsonString);
    }
}
