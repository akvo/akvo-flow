package org.akvo.flow.xml;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionOptionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.TranslationDto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

/*
 * Class for working with form XML tags like this:
 * <altText type="translation" language="sv">Ja</altText>
 */

public class XmlAltText {

    @JacksonXmlProperty(localName = "type", isAttribute = true)
    private String type;
    @JacksonXmlProperty(localName = "language", isAttribute = true)
    private String language;
    @JacksonXmlText
    private String text;

    public XmlAltText() {
    }

    public XmlAltText(QuestionOptionDto dto) {
        //TODO
    }

    /**
     * @return a Dto object with relevant fields copied
     */
    public TranslationDto toDto() {
        TranslationDto dto = new TranslationDto();
        dto.setLangCode(language);
        dto.setText(text);
        return dto;
    }

    @Override public String toString() {
        return "translation{" +
                "langCode='" + language +
                "',type='" + type +
                "',text='" + text +
                "'}";
    }

}