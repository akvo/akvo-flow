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

package org.akvo.flow.xml;

import com.gallatinsystems.survey.domain.CascadeResource;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.QuestionGroup;
import com.gallatinsystems.survey.domain.QuestionOption;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.survey.domain.SurveyGroup;
import com.gallatinsystems.survey.domain.Translation;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


class FlowXmlObjectWriterTests {
    private static final String EXPECTED_CASCADE_QUESTION = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><survey name=\"This is a form\" defaultLanguageCode=\"en\" version=\"12.0\" app=\"akvoflowsandbox\" surveyGroupId=\"123\" surveyGroupName=\"Name of containing survey\" surveyId=\"17\" alias=\"akvoflowsandbox.appspot.com\" webFormPassword=\"webform\"><questionGroup><question id=\"1001\" order=\"1\" type=\"cascade\" mandatory=\"false\" localeNameFlag=\"false\" cascadeResource=\"cascade-123456789-v1.sqlite\"><text>This is question one</text></question><heading>This is a group</heading></questionGroup></survey>";

    private static final String EXPECTED_QUESTIONLESS_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><survey name=\"This is a form\" defaultLanguageCode=\"en\" version=\"11.0\" app=\"akvoflowsandbox\" surveyGroupId=\"123\" surveyGroupName=\"Name of containing survey\" surveyId=\"17\" alias=\"akvoflowsandbox.appspot.com\" webFormPassword=\"webform\"><questionGroup><heading>This is a group</heading></questionGroup></survey>";
    private static final String EXPECTED_QUESTIONLESS_XML_WITH_TRANSLATIONS = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><survey name=\"This is a form\" defaultLanguageCode=\"en\" version=\"11.0\" app=\"akvoflowsandbox\" surveyGroupId=\"123\" surveyGroupName=\"Name of containing survey\" surveyId=\"17\" alias=\"akvoflowsandbox.appspot.com\" webFormPassword=\"webform\"><altText type=\"translation\" language=\"es\">Formulario</altText><questionGroup><altText type=\"translation\" language=\"es\">Grupo</altText><heading>This is a group</heading></questionGroup></survey>";

    private static final String EXPECTED_MINIMAL_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
            "<survey name=\"This is a form\" defaultLanguageCode=\"en\" version=\"12.0\" app=\"akvoflowsandbox\" " +
            "surveyGroupId=\"123\" surveyGroupName=\"Name of containing survey\" surveyId=\"17\" alias=\"akvoflowsandbox.appspot.com\" webFormPassword=\"webform\">" +
            "<questionGroup>" +
            "<question id=\"1001\" order=\"1\" type=\"free\" mandatory=\"false\" localeNameFlag=\"false\">" +
            "<text>This is question one</text>" +
            "</question><question variableName=\"questionTwo\" id=\"1002\" order=\"2\" type=\"free\" mandatory=\"true\" localeNameFlag=\"false\">" +
            "<validationRule validationType=\"numeric\" allowDecimal=\"false\" signed=\"false\"/>" +
            "<text>This is question two</text></question>" +
            "<question id=\"1003\" order=\"3\" personalData=\"true\" answerStats=\"false\" type=\"geoshape\" mandatory=\"false\" " +
            "localeNameFlag=\"false\" allowPoints=\"false\" allowLine=\"false\" allowPolygon=\"false\">" +
            "<text>This is question three</text></question>" +
            "<heading>This is a group</heading>" +
            "</questionGroup></survey>";

    private static final String EXPECTED_REPEATABLE_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
            "<survey name=\"This is a form\" defaultLanguageCode=\"en\" version=\"12.0\" app=\"akvoflowsandbox\" " +
            "surveyGroupId=\"123\" surveyGroupName=\"Name of containing survey\" surveyId=\"17\" alias=\"akvoflowsandbox.appspot.com\" webFormPassword=\"webform\">" +
            "<questionGroup repeatable=\"true\">" +
            "<heading>This is a group</heading>" +
            "</questionGroup></survey>";

    private static final String EXPECTED_OPTIONS_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
            "<survey name=\"This is a form\" defaultLanguageCode=\"en\" version=\"11.0\" app=\"akvoflowsandbox\" " +
            "surveyGroupId=\"123\" surveyGroupName=\"Name of containing survey\" surveyId=\"17\" alias=\"akvoflowsandbox.appspot.com\" webFormPassword=\"webform\">" +
            "<questionGroup>" +
            "<question id=\"1001\" order=\"1\" type=\"option\" mandatory=\"false\" localeNameFlag=\"false\">" +
            "<options allowOther=\"true\" allowMultiple=\"true\"><option value=\"1\" code=\"1\"><text>1</text></option><option value=\"2\" code=\"2\"><text>2</text></option></options>" +
            "<text>This is question one</text>" +
            "</question><question id=\"1002\" order=\"2\" type=\"option\" mandatory=\"false\" localeNameFlag=\"false\">" +
            "<options allowOther=\"false\" allowMultiple=\"false\" renderType=\"radio\"><option value=\"3\" code=\"3\"><text>3</text></option><option value=\"4\" code=\"4\"><text>4</text></option></options>" +
            "<text>This is question two</text></question>" +
            "<heading>This is a group</heading>" +
            "</questionGroup></survey>";

    private static final String EXPECTED_BARCODE_QUESTION = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
            "<survey name=\"This is a form\" defaultLanguageCode=\"en\" version=\"12.0\" app=\"akvoflowsandbox\" " +
            "surveyGroupId=\"123\" surveyGroupName=\"Name of containing survey\" surveyId=\"17\" alias=\"akvoflowsandbox.appspot.com\" webFormPassword=\"webform\"><questionGroup>" +
            "<question id=\"1001\" order=\"1\" locked=\"true\" allowMultiple=\"true\" type=\"scan\" mandatory=\"false\" localeNameFlag=\"false\">" +
            "<text>This is question one</text></question><heading>This is a group</heading></questionGroup></survey>";

    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    @BeforeEach
    public void setUp() {
        helper.setUp();
    }

    @AfterEach
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    void testSerialiseQuestionlessForm() throws IOException {

        //Mock up a form tree
        Survey form1 = new Survey();
        form1.setKey(KeyFactory.createKey("Survey", 17L));
        form1.setName("This is a form");
        form1.setVersion(11.0);
        form1.setSurveyGroupId(123L);
        form1.setWebFormPassword("webform");

        //Add a QuestionGroup
        QuestionGroup qg = new QuestionGroup();
        qg.setKey(KeyFactory.createKey("QuestionGroup", 18L));
        qg.setSurveyId(17L);
        qg.setName("This is a group");
        qg.setOrder(1);
        TreeMap<Integer, QuestionGroup> gl = new TreeMap<>();
        gl.put(1, qg);
        form1.setQuestionGroupMap(gl);
        //No questions

        SurveyGroup survey = new SurveyGroup();
        survey.setCode("Name of containing survey");

        //Convert domain tree to Jackson tree
        XmlForm form = new XmlForm(form1, survey, "akvoflowsandbox", "akvoflowsandbox.appspot.com");
        //...and test it
        assertNotEquals(null, form);
        assertNotEquals(null, form.getQuestionGroup());
        List<XmlQuestionGroup> ga = form.getQuestionGroup();
        assertEquals(1, ga.size());
        assertEquals("This is a group", ga.get(0).getHeading());
        assertEquals(1, ga.get(0).getOrder());
        assertNull(ga.get(0).getRepeatable());

        //Convert Jackson tree into an XML string
        String xml = PublishedForm.generate(form);
        assertEquals(EXPECTED_QUESTIONLESS_XML, xml);

        //And finally parse to DTO to see that it is valid
        SurveyDto dto = PublishedForm.parse(xml, true).toDto(); //be strict

        assertNotEquals(null, dto);
        assertEquals(17L, dto.getKeyId());
        assertEquals("This is a form", dto.getName());
        assertEquals("11.0", dto.getVersion());
        assertEquals("This is a form", dto.getName());
    }

    @Test
    void testSerialiseMinimalForm() throws IOException {

        //Mock up a DTO tree
        Survey form1 = new Survey();
        form1.setKey(KeyFactory.createKey("Survey", 17L));
        form1.setName("This is a form");
        form1.setVersion(12.0);
        form1.setWebFormPassword("webform");

        //Add a QuestionGroup
        QuestionGroup qg = new QuestionGroup();
        qg.setKey(KeyFactory.createKey("QuestionGroup", 18L));
        qg.setSurveyId(17L);
        qg.setName("This is a group");
        qg.setOrder(1);
        TreeMap<Integer, QuestionGroup> gm = new TreeMap<>();
        gm.put(1, qg);
        form1.setQuestionGroupMap(gm);
        TreeMap<Integer, Question> qm = new TreeMap<>();
        qg.setQuestionMap(qm);

        //Add some questions
        //Intentionally do not set mandatory; it should be null
        Question q1 = new Question();
        q1.setKey(KeyFactory.createKey("Question", 1001L)); //Must have a key
        q1.setOrder(1);
        q1.setText("This is question one");
        q1.setType(Question.Type.FREE_TEXT);
        qm.put(1, q1);

        Question q2 = new Question();
        q2.setKey(KeyFactory.createKey("Question", 1002L)); //Must have a key
        q2.setOrder(2);
        q2.setText("This is question two");
        q2.setType(Question.Type.NUMBER);
        q2.setMandatoryFlag(true);
        q2.setVariableName("questionTwo");
        qm.put(2,q2);

        Question q3 = new Question();
        q3.setKey(KeyFactory.createKey("Question", 1003L)); //Must have a key
        q3.setOrder(3);
        q3.setText("This is question three");
        q3.setType(Question.Type.GEOSHAPE);
        q3.setMandatoryFlag(false);
        q3.setPersonalData(true);
        q3.setAnswerStats(false);
        qm.put(3, q3);

        int questionCount = qm.size();

        SurveyGroup survey = new SurveyGroup();
        survey.setCode("Name of containing survey");
        survey.setKey(KeyFactory.createKey("SurveyGroup", 123L));
        form1.setSurveyGroupId(survey.getKey().getId());

        //Convert domain tree to Jackson tree
        XmlForm form = new XmlForm(form1, survey, "akvoflowsandbox", "akvoflowsandbox.appspot.com");
        //...and test it
        assertNotEquals(null, form);
        assertNotEquals(null, form.getQuestionGroup());
        XmlQuestionGroup xqg = form.getQuestionGroup().get(0);
        assertNotEquals(null, xqg);
        assertNotEquals(null, xqg.getQuestion());
        assertEquals(questionCount, xqg.getQuestion().size());

        XmlQuestion xq1 = xqg.getQuestion().get(0);
        assertNotEquals(null, xq1);
        assertEquals(1001L, xq1.getId());
        assertEquals("This is question one", xq1.getText());
        assertEquals(Boolean.FALSE, xq1.getMandatory());
        assertEquals("free", xq1.getType());

        XmlQuestion xq2 = xqg.getQuestion().get(1);
        assertNotEquals(null, xq2);
        assertEquals(1002L, xq2.getId());
        assertEquals("This is question two", xq2.getText());
        assertEquals(Boolean.TRUE, xq2.getMandatory());
        assertEquals("free", xq2.getType());
        assertEquals("questionTwo", xq2.getVariableName());

        XmlQuestion xq3 = xqg.getQuestion().get(2);
        assertNotEquals(null, xq3);
        assertEquals(1003L, xq3.getId());
        assertEquals("This is question three", xq3.getText());
        assertEquals(Boolean.FALSE, xq3.getMandatory());
        assertEquals(Boolean.TRUE, xq3.getPersonalData());
        assertEquals(Boolean.FALSE, xq3.getAnswerStats());
        assertEquals("geoshape", xq3.getType());

        //Convert Jackson tree into an XML string
        String xml = PublishedForm.generate(form);
        assertEquals(EXPECTED_MINIMAL_XML, xml);

        //And finally parse it to a DTO
        SurveyDto dto = PublishedForm.parse(xml, true).toDto(); //be strict

        assertNotEquals(null, dto);
        assertEquals(17L, dto.getKeyId());
        assertEquals("This is a form", dto.getName());
        assertEquals("12.0", dto.getVersion());
        assertEquals("This is a form", dto.getName());
    }

    @Test
    void testSerialiseRepeatableGroupForm() throws IOException {

        //Mock up a DTO tree
        Survey form1 = new Survey();
        form1.setKey(KeyFactory.createKey("Survey", 17L));
        form1.setName("This is a form");
        form1.setVersion(12.0);
        form1.setWebFormPassword("webform");

        //Add a QuestionGroup
        QuestionGroup qg = new QuestionGroup();
        qg.setKey(KeyFactory.createKey("QuestionGroup", 18L));
        qg.setSurveyId(17L);
        qg.setName("This is a group");
        qg.setOrder(1);
        qg.setRepeatable(true);
        TreeMap<Integer, QuestionGroup> gm = new TreeMap<>();
        gm.put(1, qg);
        form1.setQuestionGroupMap(gm);
        TreeMap<Integer, Question> qm = new TreeMap<>();
        qg.setQuestionMap(qm);
        int questionCount = qm.size();

        SurveyGroup survey = new SurveyGroup();
        survey.setCode("Name of containing survey");
        survey.setKey(KeyFactory.createKey("SurveyGroup", 123L));
        form1.setSurveyGroupId(survey.getKey().getId());

        //Convert domain tree to Jackson tree
        XmlForm form = new XmlForm(form1, survey, "akvoflowsandbox", "akvoflowsandbox.appspot.com");
        //...and test it
        assertNotEquals(null, form);
        assertNotEquals(null, form.getQuestionGroup());
        XmlQuestionGroup xqg = form.getQuestionGroup().get(0);
        assertNotEquals(null, xqg);
        assertNotEquals(null, xqg.getQuestion());
        assertEquals(questionCount, xqg.getQuestion().size());
        assertTrue(xqg.getRepeatable());

        //Convert Jackson tree into an XML string
        String xml = PublishedForm.generate(form);
        assertEquals(EXPECTED_REPEATABLE_XML, xml);

        //And finally parse it to a DTO
        SurveyDto dto = PublishedForm.parse(xml, true).toDto(); //be strict

        assertNotEquals(null, dto);
        assertEquals(17L, dto.getKeyId());
        assertEquals("This is a form", dto.getName());
        assertEquals("12.0", dto.getVersion());
        assertEquals("This is a form", dto.getName());
    }

    @Test
    void testSerialiseFormWithCascade() throws IOException {

        //Mock up a DTO tree
        Survey form1 = new Survey();
        form1.setKey(KeyFactory.createKey("Survey", 17L));
        form1.setName("This is a form");
        form1.setVersion(12.0);
        form1.setSurveyGroupId(123L);
        form1.setWebFormPassword("webform");

        //Add a QuestionGroup
        QuestionGroup qg = new QuestionGroup();
        qg.setKey(KeyFactory.createKey("QuestionGroup", 18L));
        qg.setSurveyId(17L);
        qg.setName("This is a group");
        qg.setOrder(1);
        TreeMap<Integer, QuestionGroup> gm = new TreeMap<>();
        gm.put(1, qg);
        form1.setQuestionGroupMap(gm);
        TreeMap<Integer, Question> qm = new TreeMap<>();
        qg.setQuestionMap(qm);

        //Add some questions
        //Intentionally do not set mandatory; it should be null
        Question q1 = new Question();
        q1.setKey(KeyFactory.createKey("Question", 1001L)); //Must have a key
        q1.setOrder(1);
        q1.setText("This is question one");
        q1.setType(Question.Type.CASCADE);
        CascadeResource cr = new CascadeResource();
        cr.setKey(KeyFactory.createKey("CascadeResource", 123456789L));
        cr.setVersion(1);
        q1.setCascadeResource(cr.getResourceId());
        qm.put(1, q1);

        int questionCount = qm.size();
        SurveyGroup survey = new SurveyGroup();
        survey.setCode("Name of containing survey");

        //Convert domain tree to Jackson tree
        XmlForm form = new XmlForm(form1, survey, "akvoflowsandbox", "akvoflowsandbox.appspot.com");
        //...and test it
        assertNotEquals(null, form);
        assertNotEquals(null, form.getQuestionGroup());
        XmlQuestionGroup xqg = form.getQuestionGroup().get(0);
        assertNotEquals(null, xqg);
        assertNotEquals(null, xqg.getQuestion());
        assertEquals(questionCount, xqg.getQuestion().size());

        XmlQuestion xq1 = xqg.getQuestion().get(0);
        assertNotEquals(null, xq1);
        assertEquals(1001L, xq1.getId());
        assertEquals("This is question one", xq1.getText());
        assertEquals(Boolean.FALSE, xq1.getMandatory());
        assertEquals("cascade", xq1.getType());
        assertEquals("cascade-123456789-v1.sqlite", xq1.getCascadeResource());

        //Convert Jackson tree into an XML string
        String xml = PublishedForm.generate(form);
        assertEquals(EXPECTED_CASCADE_QUESTION, xml);

        //And finally parse it to a DTO
        SurveyDto dto = PublishedForm.parse(xml, true).toDto(); //be strict

        assertNotEquals(null, dto);
        assertEquals(17L, dto.getKeyId());
        assertEquals("This is a form", dto.getName());
        assertEquals("12.0", dto.getVersion());
        assertEquals("This is a form", dto.getName());
    }

    @Test
    void testOptionQuestionForm() throws IOException {

        //Mock up a form tree
        Survey form1 = new Survey();
        form1.setKey(KeyFactory.createKey("Survey", 17L));
        form1.setName("This is a form");
        form1.setVersion(11.0);
        form1.setSurveyGroupId(123L);
        form1.setWebFormPassword("webform");

        //Add a QuestionGroup
        QuestionGroup qg = new QuestionGroup();
        qg.setKey(KeyFactory.createKey("QuestionGroup", 18L));
        qg.setSurveyId(17L);
        qg.setName("This is a group");
        qg.setOrder(1);
        TreeMap<Integer, QuestionGroup> gl = new TreeMap<>();
        gl.put(1, qg);
        form1.setQuestionGroupMap(gl);
        TreeMap<Integer, Question> qm = new TreeMap<>();
        qg.setQuestionMap(qm);

        Question q1 = new Question();
        q1.setKey(KeyFactory.createKey("Question", 1001L));
        q1.setOrder(1);
        q1.setText("This is question one");
        q1.setType(Question.Type.OPTION);
        q1.setAllowOtherFlag(true);
        q1.setAllowMultipleFlag(true);

        TreeMap<Integer, QuestionOption> optionMap = new TreeMap<>();
        QuestionOption opt1 = new QuestionOption();
        opt1.setCode("1");
        opt1.setText("1");
        opt1.setKey(KeyFactory.createKey("QuestionOption", 11111L));
        opt1.setTranslationMap(new HashMap<>());
        optionMap.put(1, opt1);

        QuestionOption opt2 = new QuestionOption();
        opt2.setCode("2");
        opt2.setText("2");
        opt2.setKey(KeyFactory.createKey("QuestionOption", 222222L));
        opt2.setTranslationMap(new HashMap<>());
        optionMap.put(2, opt2);

        q1.setQuestionOptionMap(optionMap);
        qm.put(1, q1);

        Question q2 = new Question();
        q2.setKey(KeyFactory.createKey("Question", 1002L));
        q2.setOrder(2);
        q2.setText("This is question two");
        q2.setType(Question.Type.OPTION);
        q2.setAllowOtherFlag(false);
        q2.setAllowMultipleFlag(false);

        TreeMap<Integer, QuestionOption> optionMap2 = new TreeMap<>();
        QuestionOption opt3 = new QuestionOption();
        opt3.setCode("3");
        opt3.setText("3");
        opt3.setKey(KeyFactory.createKey("QuestionOption", 333333L));
        opt3.setTranslationMap(new HashMap<>());
        optionMap2.put(1, opt3);

        QuestionOption opt4 = new QuestionOption();
        opt4.setCode("4");
        opt4.setText("4");
        opt4.setKey(KeyFactory.createKey("QuestionOption", 44444L));
        opt4.setTranslationMap(new HashMap<>());
        optionMap2.put(2, opt4);

        q2.setQuestionOptionMap(optionMap2);
        qm.put(2, q2);

        SurveyGroup survey = new SurveyGroup();
        survey.setCode("Name of containing survey");

        //Convert domain tree to Jackson tree
        XmlForm form = new XmlForm(form1, survey, "akvoflowsandbox", "akvoflowsandbox.appspot.com");

        //Convert Jackson tree into an XML string
        String xml = PublishedForm.generate(form);
        assertEquals(EXPECTED_OPTIONS_XML, xml);

        //And finally parse to DTO to see that it is valid
        SurveyDto dto = PublishedForm.parse(xml, true).toDto(); //be strict

        assertNotEquals(null, dto);
        assertEquals(17L, dto.getKeyId());
        assertEquals("This is a form", dto.getName());
        assertEquals("11.0", dto.getVersion());
        assertEquals("This is a form", dto.getName());
    }

    @Test
    void testSerialiseFormWithBarcode() throws IOException {

        //Mock up a DTO tree
        Survey form1 = new Survey();
        form1.setKey(KeyFactory.createKey("Survey", 17L));
        form1.setName("This is a form");
        form1.setVersion(12.0);
        form1.setSurveyGroupId(123L);
        form1.setWebFormPassword("webform");

        //Add a QuestionGroup
        QuestionGroup qg = new QuestionGroup();
        qg.setKey(KeyFactory.createKey("QuestionGroup", 18L));
        qg.setSurveyId(17L);
        qg.setName("This is a group");
        qg.setOrder(1);
        TreeMap<Integer, QuestionGroup> gm = new TreeMap<>();
        gm.put(1, qg);
        form1.setQuestionGroupMap(gm);
        TreeMap<Integer, Question> qm = new TreeMap<>();
        qg.setQuestionMap(qm);

        //Add some questions
        //Intentionally do not set mandatory; it should be null
        Question q1 = new Question();
        q1.setKey(KeyFactory.createKey("Question", 1001L)); //Must have a key
        q1.setOrder(1);
        q1.setText("This is question one");
        q1.setType(Question.Type.SCAN);
        q1.setAllowMultipleFlag(true);
        q1.setGeoLocked(true); //this is used for geolocation and barcodes
        qm.put(1, q1);

        SurveyGroup survey = new SurveyGroup();
        survey.setCode("Name of containing survey");

        //Convert domain tree to Jackson tree
        XmlForm form = new XmlForm(form1, survey, "akvoflowsandbox", "akvoflowsandbox.appspot.com");

        //Convert Jackson tree into an XML string
        String xml = PublishedForm.generate(form);
        assertEquals(EXPECTED_BARCODE_QUESTION, xml);

        //And finally parse it to a DTO
        SurveyDto dto = PublishedForm.parse(xml, true).toDto(); //be strict
    }

    @Test
    void testSerialiseQuestionLessFormWithTranslations() throws IOException {

        //Mock up a form tree
        Survey form1 = new Survey();
        form1.setKey(KeyFactory.createKey("Survey", 17L));
        form1.setName("This is a form");
        form1.setVersion(11.0);
        form1.setSurveyGroupId(123L);
        form1.setWebFormPassword("webform");

        HashMap<String, Translation> translationHashMap = createTranslationMap("Formulario", "es", form1.getObjectId(),
                form1.getObjectId(), Translation.ParentType.SURVEY_NAME);
        form1.setTranslationMap(translationHashMap);

        //Add a QuestionGroup
        QuestionGroup qg = new QuestionGroup();
        qg.setKey(KeyFactory.createKey("QuestionGroup", 18L));
        qg.setSurveyId(17L);
        qg.setName("This is a group");
        qg.setOrder(1);
        TreeMap<Integer, QuestionGroup> gl = new TreeMap<>();
        HashMap<String, Translation> translationHashMap2 = createTranslationMap("Grupo", "es", form1.getObjectId(),
                qg.getKey().getId(), Translation.ParentType.QUESTION_GROUP_NAME);
        qg.setTranslations(translationHashMap2);
        gl.put(1, qg);
        form1.setQuestionGroupMap(gl);
        //No questions

        SurveyGroup survey = new SurveyGroup();
        survey.setCode("Name of containing survey");

        //Convert domain tree to Jackson tree
        XmlForm form = new XmlForm(form1, survey, "akvoflowsandbox", "akvoflowsandbox.appspot.com");
        //...and test it
        assertNotEquals(null, form);
        assertNotEquals(null, form.getQuestionGroup());
        List<XmlQuestionGroup> ga = form.getQuestionGroup();
        assertEquals(1, ga.size());
        assertEquals(1, form.getAltText().size());
        assertEquals("This is a group", ga.get(0).getHeading());
        assertEquals(1, ga.get(0).getOrder());
        assertEquals(1, ga.get(0).getAltText().size());
        assertNull(ga.get(0).getRepeatable());

        //Convert Jackson tree into an XML string
        String xml = PublishedForm.generate(form);
        assertEquals(EXPECTED_QUESTIONLESS_XML_WITH_TRANSLATIONS, xml);

        //And finally parse to DTO to see that it is valid
        SurveyDto dto = PublishedForm.parse(xml, true).toDto(); //be strict

        assertNotEquals(null, dto);
        assertEquals(17L, dto.getKeyId());
        assertEquals("This is a form", dto.getName());
        assertEquals("11.0", dto.getVersion());
        assertEquals("This is a form", dto.getName());
    }

    private HashMap<String, Translation> createTranslationMap(String text, String languageCode, Long surveyId,
                                                              long parentId, Translation.ParentType parentType) {
        HashMap<String, Translation> translationHashMap = new HashMap<>();
        Translation translation = new Translation();
        translation.setText(text);
        translation.setLanguageCode(languageCode);
        translation.setSurveyId(surveyId);
        translation.setParentId(parentId);
        translation.setParentType(parentType);
        translationHashMap.put(translation.getLanguageCode(), translation);
        return translationHashMap;
    }

}
