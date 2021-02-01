package com.gallatinsystems.survey.dao;

import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.QuestionGroup;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.survey.domain.SurveyGroup;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.akvo.flow.api.app.DataStoreTestUtil;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SurveyDAOTest {

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
    public void questionsFromGroupsOutsideSurveyShouldNotBeIncluded() {
        SurveyGroup newSg = dataStoreTestUtil.createSurveyGroup();
        Survey newSurvey = dataStoreTestUtil.createSurvey(newSg);
        QuestionGroup newQg = dataStoreTestUtil.createQuestionGroup(newSurvey, 0, false);
        dataStoreTestUtil.createQuestion(newSurvey, newQg.getKey().getId(), Question.Type.FREE_TEXT, false);
        dataStoreTestUtil.createQuestion(newSurvey, 123123123, Question.Type.FREE_TEXT, false);

        SurveyDAO surveyDAO = new SurveyDAO();
        Survey survey = surveyDAO.loadFullFormIncludingQuestionOptions(newSurvey.getObjectId(), true);
        assertEquals(1, survey.getQuestionGroupMap().values().size());
        assertEquals(1, survey.getQuestionGroupMap().get(survey.getQuestionGroupMap().firstKey()).getQuestionMap().values().size());
    }

}