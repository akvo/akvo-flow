package com.gallatinsystems.survey.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.akvo.flow.api.app.DataStoreTestUtil;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.gallatinsystems.framework.domain.BaseDomain;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.QuestionGroup;
import com.gallatinsystems.survey.domain.QuestionOption;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.survey.domain.SurveyGroup;
import com.gallatinsystems.survey.domain.Translation;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.tools.remoteapi.RemoteApiInstaller;
import com.google.appengine.tools.remoteapi.RemoteApiOptions;

import static org.junit.jupiter.api.Assertions.*;


// This is more of an end to end tests that requires the app to be up and running.
// Leaving it disabled until we can conditionally run it in CI/locally
// To run:
// Remove the @Disabled annotation
// docker-compose exec akvo-flow bash -c 'cd GAE && mvn test -Dtest=CopySurveyFromAnotherInstanceTest#testing'
public class CopySurveyFromAnotherInstanceTest {

    private final DataStoreTestUtil dataStoreTestUtil = new DataStoreTestUtil();

    @Test
//    @Disabled
    public void testing() throws IOException, InterruptedException {
        installRemoteApi();
        cleanupDatabase();

        Survey newSurvey = createSurveyWithTranslations();
        assertTranslations("health check", newSurvey.getKey().getId());

        List<Key> surveysBeforeCopy = new SurveyDAO().listSurveyIds();
        makeCopy(newSurvey);
        ArrayList<Key> newSurveysAfterCopy = new ArrayList<>(new SurveyDAO().listSurveyIds());
        newSurveysAfterCopy.removeAll(surveysBeforeCopy);

        assertEquals(1, newSurveysAfterCopy.size(), "Expected only one new survey in " + newSurveysAfterCopy + ", before it was: " + surveysBeforeCopy);

        assertTranslations("copied survey check", newSurveysAfterCopy.get(0).getId());

        assertTranslations("original survey is still ok", newSurvey.getKey().getId()); // Ensure that the keys are not copied from the original survey

    }

    private void makeCopy(Survey newSurvey) throws IOException {
        DefaultHttpClient client = new DefaultHttpClient();
        client.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(0, false));

        System.out.println("newSurvey = " + newSurvey.getKey().getId());
        HttpGet get = new HttpGet("http://localhost:8888/app_worker/dataprocessor?action=importRemoteSurvey&apiKey=very%20private&surveyId=" + newSurvey.getKey().getId() + "&source=http://localhost:8888");

        HttpResponse httpResponse = client.execute(get);
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        assertEquals(200, statusCode);
    }

    private void installRemoteApi() throws IOException {
        RemoteApiOptions options = new RemoteApiOptions();
        options.server("localhost", 8888);
        options.useDevelopmentServerCredential();
        RemoteApiInstaller installer = new RemoteApiInstaller();
        installer.install(options);
    }

    private Survey createSurveyWithTranslations() {
        SurveyGroup newSurveyGroup = createSurveyGroup();

        Survey survey = dataStoreTestUtil.createSurvey(newSurveyGroup);
        addTranslation(survey, survey, Translation.ParentType.SURVEY_NAME, "azul", "es");
        addTranslation(survey, survey, Translation.ParentType.SURVEY_NAME, "blu", "it");

        QuestionGroup questionGroup = dataStoreTestUtil.createQuestionGroup(survey, 0, false);
        addTranslation(survey, questionGroup, Translation.ParentType.QUESTION_GROUP_NAME, "nombre", "es");
        addTranslation(survey, questionGroup, Translation.ParentType.QUESTION_GROUP_NAME, "Nome", "it");

        Question question = dataStoreTestUtil.createQuestion(survey, questionGroup.getKey().getId(), Question.Type.OPTION, false);
        addTranslation(survey, question, Translation.ParentType.QUESTION_TIP, "hola tip", "es");
        addTranslation(survey, question, Translation.ParentType.QUESTION_TIP, "ciao tip", "it");
        addTranslation(survey, question, Translation.ParentType.QUESTION_TEXT, "hola", "es");
        addTranslation(survey, question, Translation.ParentType.QUESTION_TEXT, "ciao", "it");

        QuestionOption questionOption = dataStoreTestUtil.createQuestionOption(question, "1", "1");
        addTranslation(survey, questionOption, Translation.ParentType.QUESTION_OPTION, "primero", "es");
        addTranslation(survey, questionOption, Translation.ParentType.QUESTION_OPTION, "primo", "it");

        return survey;
    }

    private void addTranslation(Survey survey, BaseDomain baseDomain, Translation.ParentType surveyName, String azul, String es) {
        dataStoreTestUtil.createTranslation(survey.getObjectId(), baseDomain.getKey().getId(), surveyName, azul, es);
    }

    private SurveyGroup createSurveyGroup() {
        SurveyGroup surveyGroup = new SurveyGroup();
        surveyGroup.setProjectType(SurveyGroup.ProjectType.PROJECT);
        return new SurveyGroupDAO().save(surveyGroup);
    }

    private void assertTranslations(String stage, long surveyId) {
        Survey survey = new SurveyDAO().loadFullForm(surveyId);
        assertTranslations(stage + " survey",
                survey.getTranslationMap().values(),
                expect(Translation.ParentType.SURVEY_NAME, "azul", "es"),
                expect(Translation.ParentType.SURVEY_NAME, "blu", "it"));

        QuestionGroup firstQuestionGroup = survey.getQuestionGroupMap().firstEntry().getValue();
        assertTranslations(stage + " question group",
                firstQuestionGroup.getTranslations().values(),
                expect(Translation.ParentType.QUESTION_GROUP_NAME, "nombre", "es"),
                expect(Translation.ParentType.QUESTION_GROUP_NAME, "Nome", "it"));

        // We cannot rely on the map as it is indexing by order, but order is changed if it does not start with 0.
        // We rely now on the fact that the map is sorted by order.
        List<Question> questions = new ArrayList<>(firstQuestionGroup.getQuestionMap().values());
        Question optionQuestion = questions.get(0);
        assertNotNull(optionQuestion, stage + " option question is null");
        assertTranslations(stage + " question",
                firstQuestion.getTranslations(),
                expect(Translation.ParentType.QUESTION_TIP, "hola tip", "es"),
                expect(Translation.ParentType.QUESTION_TIP, "ciao tip", "it"),
                expect(Translation.ParentType.QUESTION_TEXT, "hola", "es"),
                expect(Translation.ParentType.QUESTION_TEXT, "ciao", "it"));
        assertEquals(optionQuestion.getQuestionGroupId(), firstQuestionGroup.getKey().getId(), stage + " option question has the incorrect question group");

        Question dependantQuestion = questions.get(1);
        assertNotNull(dependantQuestion, stage + " dependant question is null");
        assertTranslations(stage + " dependant question",
                dependantQuestion.getTranslations(),
                expect(Translation.ParentType.QUESTION_TEXT, "dependant hola", "es"),
                expect(Translation.ParentType.QUESTION_TEXT, "dependant ciao", "it"));
        assertEquals(dependantQuestion.getQuestionGroupId(), firstQuestionGroup.getKey().getId(), stage + " dependant question has the incorrect question group");
        assertEquals(optionQuestion.getKey().getId(), dependantQuestion.getDependentQuestionId(), stage + " dependant question is not the expected one");

        Map.Entry<Integer, QuestionOption> firstQuestionOption = optionQuestion.getQuestionOptionMap().firstEntry();
        assertNotNull(firstQuestionOption, stage + " question group is null");
        assertTranslations(stage + " question group",
                firstQuestionOption.getValue().getTranslationMap().values(),
                expect(Translation.ParentType.QUESTION_OPTION, "primero", "es"),
                expect(Translation.ParentType.QUESTION_OPTION, "primo", "it"));
    }

    private TranslationExpectation expect(Translation.ParentType questionTip, String s, String es) {
        return new TranslationExpectation(questionTip, s, es);
    }

    private void assertTranslations(String stage, Collection<Translation> actual, TranslationExpectation... translationExpectations) {
        assertEquals(
                new HashSet<>(Arrays.asList(translationExpectations)),
                actual.stream()
                        .map(t -> expect(t.getParentType(), t.getText(), t.getLanguageCode()))
                        .collect(Collectors.toSet()),
                stage);
    }

    private void cleanupDatabase() {
        for (int i = 0; i < 5; i++) {
            List<Key> keys = new SurveyDAO().listSurveyIds();
            for (Key key : keys) {
                new SurveyDAO().deleteByKey(key);
            }
            List<SurveyGroup> list = new SurveyGroupDAO().list("");
            for (SurveyGroup surveyGroup : list) {
                new SurveyGroupDAO().deleteByKey(surveyGroup.getKey());
            }
            List<Translation> list1 = new TranslationDao().list("");
            for (Translation translation : list1) {
                new TranslationDao().deleteByKey(translation.getKey());
            }
            List<QuestionOption> list2 = new QuestionOptionDao().list("");
            for (QuestionOption translation : list2) {
                new QuestionOptionDao().deleteByKey(translation.getKey());
            }
            List<Question> list3 = new QuestionDao().list("");
            for (Question translation : list3) {
                new QuestionDao().deleteByKey(translation.getKey());
            }

        }

    }

    private static class TranslationExpectation {
        private final Translation.ParentType parentType;
        private final String text;
        private final String lang;

        public TranslationExpectation(Translation.ParentType parentType, String text, String lang) {
            this.parentType = parentType;
            this.text = text;
            this.lang = lang;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TranslationExpectation that = (TranslationExpectation) o;
            return parentType == that.parentType && Objects.equals(text, that.text) && Objects.equals(lang, that.lang);
        }

        @Override
        public int hashCode() {
            return Objects.hash(parentType, text, lang);
        }

        @Override
        public String toString() {
            return "TranslationExpectation{" +
                    "parentType=" + parentType +
                    ", text='" + text + '\'' +
                    ", lang='" + lang + '\'' +
                    '}';
        }
    }

    @Test
    @Disabled
    public void nothing(){

    }
}
