/*
 *  Copyright (C) 2020 Stichting Akvo (Akvo Foundation)
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

package org.akvo.flow.xml;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.gallatinsystems.survey.dao.QuestionDao;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.QuestionGroup;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.survey.domain.SurveyGroup;
import com.gallatinsystems.survey.domain.Translation;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.akvo.flow.api.app.DataStoreTestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class XmlQuestionTest {
    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
    private final DataStoreTestUtil dsu =  new DataStoreTestUtil();

    @BeforeEach
    public void setUp() {
        helper.setUp();
    }

    @AfterEach
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void aQuestionWithDuplicatedTipsOnlySerializesOne() throws JsonProcessingException {

        SurveyGroup surveyGroup = dsu.createSurveyGroup();
        Survey survey = dsu.createSurvey(surveyGroup);
        QuestionGroup questionGroup = dsu.createQuestionGroup(survey, 1, false);
        Question question = dsu.createQuestion(survey, questionGroup.getKey().getId(), Question.Type.FREE_TEXT, false);
        QuestionDao dao = new QuestionDao();

        question.setOrder(1);
        question.setText("First question");
        question.setTip("One");
        dao.save(question);

        long surveyId = survey.getKey().getId();
        long questionId = question.getKey().getId();

        dsu.createTranslation(surveyId, questionId, Translation.ParentType.QUESTION_TIP, "uno", "es");
        dsu.createTranslation(surveyId, questionId, Translation.ParentType.QUESTION_TIP, "uno", "es");

        Question question1 = dao.getByKey(question.getKey().getId(), true);
        XmlQuestion toXml = new XmlQuestion(question1);
        XmlMapper objectMapper = new XmlMapper();

        String xmlTemplate = "<XmlQuestion id=\"%s\" order=\"1\" type=\"free\" mandatory=\"false\" localeNameFlag=\"false\"><help><altText type=\"translation\" language=\"es\">uno</altText><text>One</text></help><text>First question</text></XmlQuestion>";
        String expectedXml = String.format(xmlTemplate, questionId);

        assertEquals(expectedXml, objectMapper.writeValueAsString(toXml));
    }
}