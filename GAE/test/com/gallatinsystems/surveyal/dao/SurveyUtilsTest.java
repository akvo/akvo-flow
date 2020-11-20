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

package com.gallatinsystems.surveyal.dao;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.survey.dao.QuestionDao;
import com.gallatinsystems.survey.dao.QuestionGroupDao;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.dao.SurveyUtils;
import com.gallatinsystems.survey.dao.TranslationDao;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.QuestionGroup;
import com.gallatinsystems.survey.domain.QuestionOption;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.survey.domain.SurveyGroup;
import com.gallatinsystems.survey.domain.Translation;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.akvo.flow.api.app.DataStoreTestUtil;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SurveyUtilsTest {
    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
    private DataStoreTestUtil dataStoreTestUtil =  new DataStoreTestUtil();

    @BeforeEach
    public void setUp() {
        helper.setUp();
    }

    @AfterEach
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void testCopyTemplateSurvey() throws Exception {

        Survey sourceSurvey = createSurvey(1, 1);
        Survey copiedSurvey = copySurvey(sourceSurvey);

        SurveyUtils.copySurvey(copiedSurvey.getKey().getId(), sourceSurvey.getKey().getId(), true);

        List<QuestionGroup> qgs = new QuestionGroupDao().listQuestionGroupBySurvey(copiedSurvey.getKey().getId());
        assertEquals(1, qgs.size());
        assertTrue(qgs.get(0).getImmutable());

        List<Question> questions = new QuestionDao().listQuestionsBySurvey(copiedSurvey.getKey().getId());
        assertEquals(1, questions.size());
        assertTrue(questions.get(0).getImmutable());
    }

    @Test
    public void testCopyTranslationsOfTemplateSurvey() throws Exception {

        Survey sourceSurvey = createSurveyWithTranslations();
        Survey copiedSurvey = copySurvey(sourceSurvey);

        SurveyUtils.copySurvey(copiedSurvey.getKey().getId(), sourceSurvey.getKey().getId(), true);

        List<Translation> translations = new TranslationDao().listByFormId(copiedSurvey.getKey().getId());
        assertEquals(3, translations.size());

        List<QuestionGroup> qgs = new QuestionGroupDao().listQuestionGroupBySurvey(copiedSurvey.getKey().getId());
        assertEquals(qgs.get(0).getKey().getId(), translations.get(0).getParentId());
        assertEquals( "uno", translations.get(2).getText());
    }

    @Test
    public void testCopySurvey() throws Exception {

        Survey sourceSurvey = createSurvey(6, 4);
        Survey copiedSurvey = copySurvey(sourceSurvey);

        SurveyUtils.copySurvey(copiedSurvey.getKey().getId(), sourceSurvey.getKey().getId(), false);

        List<QuestionGroup> copiedQuestionGroups = new QuestionGroupDao().listQuestionGroupBySurvey(copiedSurvey.getKey().getId());
        assertEquals(6, copiedQuestionGroups.size());

        List<Question> copiedSurveyQuestions = new QuestionDao().listQuestionsBySurvey(copiedSurvey.getKey().getId());
        assertEquals(24, copiedSurveyQuestions.size());

        Map<Integer, Question> copiedQuestionsForOneGroup = new QuestionDao().listQuestionsByQuestionGroup(copiedQuestionGroups.get(0).getKey().getId(), false);
        assertEquals(4, copiedQuestionsForOneGroup.size());
    }

    @Test
    public void testCopyTranslations() throws Exception {

        Survey sourceSurvey = createSurveyWithTranslations();
        Survey copiedSurvey = copySurvey(sourceSurvey);

        SurveyUtils.copySurvey(copiedSurvey.getKey().getId(), sourceSurvey.getKey().getId(), false);

        List<Translation> translations = new TranslationDao().listByFormId(copiedSurvey.getKey().getId());
        assertEquals(3, translations.size());

        List<QuestionGroup> qgs = new QuestionGroupDao().listQuestionGroupBySurvey(copiedSurvey.getKey().getId());
        assertEquals(qgs.get(0).getKey().getId(), translations.get(0).getParentId());
        assertEquals( "uno", translations.get(2).getText());
    }

    private Survey copySurvey(Survey sourceSurvey) {
        SurveyDto dto = new SurveyDto();
        dto.setName(sourceSurvey.getName());
        dto.setSurveyGroupId(sourceSurvey.getSurveyGroupId());

        final Survey tmp = new Survey();
        BeanUtils.copyProperties(sourceSurvey, tmp, Constants.EXCLUDED_PROPERTIES);

        tmp.setName(dto.getName());
        tmp.setSurveyGroupId(dto.getSurveyGroupId());

        return new SurveyDAO().save(tmp);
    }

    private Survey createSurvey(int howManyQuestionGroups, int howManyQuestions) {

        SurveyGroup newSg = dataStoreTestUtil.createSurveyGroup();
        Survey newSurvey = dataStoreTestUtil.createSurvey(newSg);

        for (int i = 0; i < howManyQuestionGroups; i++) {
            QuestionGroup newQg = dataStoreTestUtil.createQuestionGroup(newSurvey);
            for (int j = 0; j < howManyQuestions; j++) {
                dataStoreTestUtil.createQuestion(newSurvey, newQg.getKey().getId(), Question.Type.FREE_TEXT);
            }
        }

        return newSurvey;
    }

    private Survey createSurveyWithTranslations() {

        SurveyGroup newSg = dataStoreTestUtil.createSurveyGroup();
        Survey newSurvey = dataStoreTestUtil.createSurvey(newSg);

        QuestionGroup newQg = dataStoreTestUtil.createQuestionGroup(newSurvey);
        long questionGroupId = newQg.getKey().getId();
        dataStoreTestUtil.createTranslation(newSurvey.getObjectId(), questionGroupId, Translation.ParentType.QUESTION_GROUP_NAME, "name", "es");

        Question question = dataStoreTestUtil.createQuestion(newSurvey, questionGroupId, Question.Type.OPTION);
        dataStoreTestUtil.createTranslation(newSurvey.getObjectId(), question.getKey().getId(), Translation.ParentType.QUESTION_TEXT, "hola", "es");

        QuestionOption saved = dataStoreTestUtil.createQuestionOption(question);
        dataStoreTestUtil.createTranslation(newSurvey.getObjectId(), saved.getKey().getId(), Translation.ParentType.QUESTION_OPTION, "uno", "es");
        return newSurvey;
    }
}
