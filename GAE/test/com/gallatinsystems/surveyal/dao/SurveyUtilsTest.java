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
import com.gallatinsystems.survey.dao.SurveyGroupDAO;
import com.gallatinsystems.survey.dao.SurveyUtils;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.QuestionGroup;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.survey.domain.SurveyGroup;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.akvo.flow.api.app.DataStoreTestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SurveyUtilsTest {
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
    public void testCopyTemplateSurvey() throws Exception {

        Survey sourceSurvey = createSurvey();
        Survey copiedSurvey = copySurvey(sourceSurvey);

        SurveyUtils.copySurvey(copiedSurvey.getKey().getId(), sourceSurvey.getKey().getId(), true);

        List<QuestionGroup> qgs = new QuestionGroupDao().listQuestionGroupBySurvey(copiedSurvey.getKey().getId());
        assertEquals(1, qgs.size());
        assertTrue(qgs.get(0).getImmutable());

        List<Question> questions = new QuestionDao().listQuestionsBySurvey(copiedSurvey.getKey().getId());
        assertEquals(1, questions.size());
        assertTrue(questions.get(0).getImmutable());
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

    private Survey createSurvey() {

        SurveyGroup sg = new SurveyGroup();
        SurveyGroup newSg = new SurveyGroupDAO().save(sg);

        Survey survey = new Survey();
        survey.setName("Simple survey");
        survey.setSurveyGroupId(newSg.getKey().getId());
        Survey newSurvey = new SurveyDAO().save(survey);

        QuestionGroup qg = new QuestionGroup();
        qg.setName("quesitongroup");
        qg.setSurveyId(newSurvey.getKey().getId());
        QuestionGroup newQg = new QuestionGroupDao().save(qg);

        Question q = new Question();
        q.setType(Question.Type.FREE_TEXT);
        q.setQuestionGroupId(newQg.getKey().getId());
        q.setSurveyId(newSurvey.getKey().getId());
        new QuestionDao().save(q);

        return newSurvey;
    }
}
