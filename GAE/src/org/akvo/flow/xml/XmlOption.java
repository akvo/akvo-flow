package org.akvo.flow.xml;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionOptionDto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/*
 * Class for working with a form XML file like this:
 * <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
 * <survey name="Brand new form four" defaultLanguageCode="en" version='4.0' app="akvoflowsandbox" surveyGroupId="41213002" surveyGroupName="Brand new" surveyId="43993002">
 * <questionGroup><heading>Foo</heading>
 * <question order="1" type="option" mandatory="true" localeNameFlag="false" id="46843002">
 * <options allowOther="false" allowMultiple="false" renderType="radio">
 * <option value="Yes" code="Y"><text>Yes</text></option>
 * <option value="No" code="N"><text>No</text></option>
 * </options>
 * <text>Brand new option question</text></question>
 * <question order="2" type="free" mandatory="true" localeNameFlag="false" id="40533002">
 * <dependency answer-value="Yes" question="46843002"/><text>Name of optimist</text></question>
 * <question order="3" type="free" mandatory="true" localeNameFlag="false" id="45993002">
 * <dependency answer-value="No" question="46843002"/><text>Name of pessimist</text></question>
 * <question order="4" type="free" mandatory="true" localeNameFlag="false" id="47563002"><text>Victim</text></question>
 * <question order="5" type="free" mandatory="true" localeNameFlag="false" id="65813002"><text>New question - please change name</text></question>
 * </questionGroup>
 * <questionGroup><heading>Bar</heading>
 * <question order="1" type="free" mandatory="true" localeNameFlag="false" id="46673002"><text>AA</text></question>
 * <question order="2" type="free" mandatory="true" localeNameFlag="false" id="77813002"><text>BB</text></question>
 * <question order="3" type="free" mandatory="true" localeNameFlag="false" id="48523002"><text>CC</text></question>
 * <question order="4" type="free" mandatory="true" localeNameFlag="false" id="56093002"><text>DD</text></question>
 * </questionGroup>
 * </survey>
 */

public class XmlOption {

    @JacksonXmlProperty(localName = "code", isAttribute = true)
    private String code;
    @JacksonXmlProperty(localName = "value", isAttribute = true)
    private String value;
    @JacksonXmlProperty(localName = "text", isAttribute = false)
    private String text;
    @JacksonXmlElementWrapper(localName = "altText", useWrapping = false)
    private XmlAltText[] altText;

    public XmlOption() {
    }

    public XmlOption(QuestionOptionDto dto) {
        //TODO
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public XmlAltText[] getAltText() {
        return altText;
    }

    public void setAltText(XmlAltText[] altText) {
        this.altText = altText;
    }

    /**
     * @return a Dto object with relevant fields copied
     */
    public QuestionOptionDto toDto() {
        QuestionOptionDto dto = new QuestionOptionDto();
        //TODO translations
        dto.setCode(code);
        dto.setText(text);
        return dto;
    }

    @Override public String toString() {
        return "option{" +
                "code='" + code + '\'' +
                "value='" + value + '\'' +
                "text='" + text + '\'' +
                '}';
    }

}