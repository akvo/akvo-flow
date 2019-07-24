package org.akvo.flow.xml;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionOptionDto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/*
 * Class for working with form XML tags like this:
 *
    <help type="tip">
        <text>Outline</text>
        <altText type="translation" language="sv">Ungefärlig</altText>
    </help>

 */

public class XmlHelp {

    @JacksonXmlProperty(localName = "type", isAttribute = true)
    private String type; //Should be "tip"
    @JacksonXmlProperty(localName = "text", isAttribute = false)
    private String text;
    @JacksonXmlElementWrapper(localName = "altText", useWrapping = false)
    private XmlAltText[] altText;

    public XmlHelp() {
    }

    public XmlHelp(QuestionOptionDto dto) {
        //TODO
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


    @Override public String toString() {
        return "help{" +
                "type='" + type +
                "',text='" + text +
                "'}";
    }

}