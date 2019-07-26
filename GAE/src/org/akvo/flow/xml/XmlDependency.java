package org.akvo.flow.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/*
 * Class for working with form XML like this:
 * <dependency answer-value="Other" question="536189117"/>
 */

public class XmlDependency {

    @JacksonXmlProperty(localName = "question", isAttribute = true)
    private long question;
    @JacksonXmlProperty(localName = "answer-value", isAttribute = true)
    private String answerValue;

    public XmlDependency() {
    }

    public XmlDependency(long questionId, String answer) {
        question = questionId;
        answerValue = answer;
    }

    @Override public String toString() {
        return "dependency{" +
                "question='" + question + '\'' +
                "answerValue='" + answerValue + '\'' +
                '}';
    }

}