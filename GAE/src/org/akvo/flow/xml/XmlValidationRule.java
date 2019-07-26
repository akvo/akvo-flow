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
    @JacksonXmlProperty(localName = "minVal", isAttribute = true)
    private double minVal;
    @JacksonXmlProperty(localName = "maxVal", isAttribute = true)
    private double maxVal;
    @JacksonXmlProperty(localName = "maxLength", isAttribute = true)
    private int maxLength;

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
                "', maxVal='" + maxVal +
                "', minVal='" + minVal +
                "'}";
    }

    public String getValidationType() {
        return validationType;
    }

    public void setValidationType(String validationType) {
        this.validationType = validationType;
    }

    public boolean getAllowDecimal() {
        return allowDecimal;
    }

    public void setAllowDecimal(boolean allowDecimal) {
        this.allowDecimal = allowDecimal;
    }

    public boolean getSigned() {
        return signed;
    }

    public void setSigned(boolean signed) {
        this.signed = signed;
    }

    public double getMinVal() {
        return minVal;
    }

    public void setMinVal(double minVal) {
        this.minVal = minVal;
    }

    public double getMaxVal() {
        return maxVal;
    }

    public void setMaxVal(double maxVal) {
        this.maxVal = maxVal;
    }

}