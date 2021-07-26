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
import com.gallatinsystems.survey.domain.QuestionOption;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.survey.domain.SurveyGroup;
import com.gallatinsystems.survey.domain.Translation;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import java.util.TreeMap;
import org.akvo.flow.api.app.DataStoreTestUtil;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TranslationsAppenderTest {

    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
    private DataStoreTestUtil dataStoreTestUtil;

    @BeforeEach
    public void setUp() {
        helper.setUp();
        dataStoreTestUtil = new DataStoreTestUtil();
    }

    @AfterEach
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void attachTranslationsCorrectlyForForm() {
        SurveyGroup survey = dataStoreTestUtil.createSurveyGroup();
        Survey form = dataStoreTestUtil.createSurvey(survey);
        dataStoreTestUtil.createTranslation(form.getObjectId(), form.getObjectId(), Translation.ParentType.SURVEY_NAME, "uno", "es");
        dataStoreTestUtil.createTranslation(form.getObjectId(), form.getObjectId(), Translation.ParentType.SURVEY_NAME, "un", "fr");

        TranslationsAppender mapper = new TranslationsAppender();

        mapper.attachFormTranslations(form);

        assertEquals(2, form.getTranslationMap().values().size());
    }

    @Test
    public void attachTranslationsCorrectlyForGroups() {
        SurveyGroup survey = dataStoreTestUtil.createSurveyGroup();
        Survey form = dataStoreTestUtil.createSurvey(survey);

        QuestionGroup group = dataStoreTestUtil.createQuestionGroup(form, 1, false);
        TreeMap<Integer, QuestionGroup> questionGroupMap = new TreeMap<>();
        questionGroupMap.put(1, group);
        form.setQuestionGroupMap(questionGroupMap);
        dataStoreTestUtil.createTranslation(form.getObjectId(), group.getKey().getId(), Translation.ParentType.QUESTION_GROUP_NAME, "uno", "es");

        TranslationsAppender mapper = new TranslationsAppender();

        mapper.attachFormTranslations(form);

        assertEquals(1, form.getQuestionGroupMap().get(1).getTranslations().size());
    }

    @Test
    public void attachTranslationsCorrectlyForQuestions() {
        SurveyGroup survey = dataStoreTestUtil.createSurveyGroup();
        Survey form = dataStoreTestUtil.createSurvey(survey);

        QuestionGroup group = dataStoreTestUtil.createQuestionGroup(form, 1, false);
        Question question = dataStoreTestUtil.createQuestion(form, group.getKey().getId(), Question.Type.DATE, false);
        TreeMap<Integer, Question> questionMap = new TreeMap<>();
        questionMap.put(question.getOrder(), question);
        group.setQuestionMap(questionMap);
        TreeMap<Integer, QuestionGroup> questionGroupMap = new TreeMap<>();
        questionGroupMap.put(1, group);
        form.setQuestionGroupMap(questionGroupMap);
        dataStoreTestUtil.createTranslation(form.getObjectId(), question.getKey().getId(), Translation.ParentType.QUESTION_TEXT, "uno", "es");
        dataStoreTestUtil.createTranslation(form.getObjectId(), question.getKey().getId(), Translation.ParentType.QUESTION_TIP, "tipo", "es");

        TranslationsAppender mapper = new TranslationsAppender();

        mapper.attachFormTranslations(form);

        assertEquals(2, form.getQuestionGroupMap().get(1).getQuestionMap().get(0).getTranslations().size());
    }

    @Test
    public void attachTranslationsCorrectlyForOptions() {
        SurveyGroup survey = dataStoreTestUtil.createSurveyGroup();
        Survey form = dataStoreTestUtil.createSurvey(survey);

        QuestionGroup group = dataStoreTestUtil.createQuestionGroup(form, 1, false);
        Question question = dataStoreTestUtil.createQuestion(form, group.getKey().getId(), Question.Type.DATE, false);
        QuestionOption option1 = dataStoreTestUtil.createQuestionOption(question, "1", "one", 1);
        QuestionOption option2 = dataStoreTestUtil.createQuestionOption(question, "2", "two", 2);
        TreeMap<Integer, QuestionOption> questionOptionMap = new TreeMap<>();
        questionOptionMap.put(option1.getOrder(), option1);
        questionOptionMap.put(option2.getOrder(), option2);
        question.setQuestionOptionMap(questionOptionMap);
        TreeMap<Integer, Question> questionMap = new TreeMap<>();
        questionMap.put(question.getOrder(), question);
        group.setQuestionMap(questionMap);
        TreeMap<Integer, QuestionGroup> questionGroupMap = new TreeMap<>();
        questionGroupMap.put(1, group);
        form.setQuestionGroupMap(questionGroupMap);
        dataStoreTestUtil.createTranslation(form.getObjectId(), option1.getKey().getId(), Translation.ParentType.QUESTION_OPTION, "uno", "es");
        dataStoreTestUtil.createTranslation(form.getObjectId(), option2.getKey().getId(), Translation.ParentType.QUESTION_OPTION, "dos", "es");

        TranslationsAppender mapper = new TranslationsAppender();

        mapper.attachFormTranslations(form);

        assertEquals(1, form.getQuestionGroupMap().get(1).getQuestionMap().get(0).getQuestionOptionMap().get(1).getTranslationMap().size());
    }
}
