package com.gallatinsystems.survey.device.xml;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.gallatinsystems.survey.device.domain.Dependency;
import com.gallatinsystems.survey.device.domain.Option;
import com.gallatinsystems.survey.device.domain.Question;
import com.gallatinsystems.survey.device.domain.QuestionGroup;
import com.gallatinsystems.survey.device.domain.Survey;

/**
 * Handler for sax-based xml parser for Survey files
 * 
 * @author Christopher Fagiani
 **/
public class SurveyHandler extends DefaultHandler {

    private static final String QUESTION_GROUP = "questionGroup";
    private static final String HEADING = "heading";
    private static final String QUESTION = "question";
    private static final String ORDER = "order";
    private static final String MANDATORY = "mandatory";
    private static final String TYPE = "type";
    private static final String ID = "id";
    private static final String DEPENDENCY = "dependency";
    private static final String ANSWER = "answer-value";
    private static final String TEXT = "text";
    private static final String OPTION = "option";
    private static final String VALUE = "value";
    private static final String OPTIONS = "options";
    private static final String ALLOW_OTHER = "allowOther";
    private static final String TIP = "tip";

    private Survey survey;
    private QuestionGroup currentQuestionGroup;
    private Question currentQuestion;
    private Option currentOption;
    private Dependency currentDependency;
    private List<Option> currentOptions;

    private StringBuilder builder;

    public Survey getSurvey() {
        return survey;
    }

    public void characters(char[] ch, int start, int length)
            throws SAXException {
        super.characters(ch, start, length);
        builder.append(ch, start, length);
    }

    /**
     * processes elements after the end tag is encountered
     */
    public void endElement(String uri, String localName, String name)
            throws SAXException {
        super.endElement(uri, localName, name);
        if (this.currentQuestionGroup != null) {
            if (localName.equalsIgnoreCase(HEADING)) {
                currentQuestionGroup.setHeading(builder.toString().trim());
            } else if (localName.equalsIgnoreCase(QUESTION)) {
                currentQuestionGroup.addQuestion(currentQuestion);
                currentQuestion = null;
            } else if (localName.equalsIgnoreCase(QUESTION_GROUP)) {
                survey.addQuestionGroup(currentQuestionGroup);
                currentQuestionGroup = null;
            }
        }
        if (currentQuestion != null) {
            if (localName.equalsIgnoreCase(TEXT)) {
                currentQuestion.setText(builder.toString().trim());
            } else if (localName.equalsIgnoreCase(OPTIONS)) {
                currentQuestion.setOptions(currentOptions);
                currentOptions = null;
            } else if (localName.equalsIgnoreCase(TIP)) {
                currentQuestion.setTip(builder.toString().trim());
            }
        }
        if (currentOption != null) {
            if (localName.equalsIgnoreCase(OPTION)) {
                currentOption.setText(builder.toString().trim());
                if (currentOptions != null) {
                    currentOptions.add(currentOption);
                }
                currentOption = null;
            }
        }
        builder.setLength(0);
    }

    /**
     * construct a new survey object and store as a member
     */
    public void startDocument() throws SAXException {
        super.startDocument();
        survey = new Survey();
        builder = new StringBuilder();
    }

    /**
     * read in the attributes of the new xml element and set the appropriate
     * values on the object(s) being hydrated.
     */
    public void startElement(String uri, String localName, String name,
            Attributes attributes) throws SAXException {
        super.startElement(uri, localName, name, attributes);
        if (localName.equalsIgnoreCase(QUESTION_GROUP)) {
            currentQuestionGroup = new QuestionGroup();
            currentQuestionGroup.setOrder(Integer.parseInt(attributes
                    .getValue(ORDER)));
        } else if (localName.equalsIgnoreCase(QUESTION)) {
            currentQuestion = new Question();
            currentQuestion.setOrder(Integer.parseInt(attributes
                    .getValue(ORDER)));
            currentQuestion.setMandatory(Boolean.parseBoolean(attributes
                    .getValue(MANDATORY)));
            currentQuestion.setType(attributes.getValue(TYPE));
            currentQuestion.setId(attributes.getValue(ID));
        } else if (localName.equalsIgnoreCase(OPTIONS)) {
            currentOptions = new ArrayList<Option>();
            if (currentQuestion != null) {
                currentQuestion.setAllowOther(Boolean.parseBoolean(attributes
                        .getValue(ALLOW_OTHER)));
            }
        } else if (localName.equalsIgnoreCase(OPTION)) {
            currentOption = new Option();
            currentOption.setValue(attributes.getValue(VALUE));
        } else if (localName.equalsIgnoreCase(DEPENDENCY)) {
            currentDependency = new Dependency();
            currentDependency.setQuestion(attributes.getValue(QUESTION));
            currentDependency.setAnswer(attributes.getValue(ANSWER));
            if (currentQuestion != null) {
                currentQuestion.addDependency(currentDependency);
            }
            currentDependency = null;
        }
    }
}
