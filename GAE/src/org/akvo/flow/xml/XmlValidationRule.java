package org.akvo.flow.xml;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionOptionDto;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/*
 * Class for working with form XML like this:
 * <validationRule allowDecimal="false" validationType="numeric" signed="true"/>
 */

public class XmlValidationRule {

    @JacksonXmlProperty(localName = "validationType", isAttribute = true)
    private String validationType;
    @JacksonXmlProperty(localName = "allowDecimal", isAttribute = true)
    private boolean allowDecimal;
    @JacksonXmlProperty(localName = "signed", isAttribute = true)
    private boolean signed;

    public XmlValidationRule() {
    }

    public XmlValidationRule(QuestionOptionDto dto) {
        //TODO
    }

    @Override public String toString() {
        return "validationRule{" +
                "validationType='" + validationType +
                "',allowDecimal='" + allowDecimal +
                "', signed='" + signed +
                "'}";
    }

}