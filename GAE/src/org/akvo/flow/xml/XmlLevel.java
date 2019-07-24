package org.akvo.flow.xml;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionOptionDto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/*
 * Class for working with form XML tags like this:

 <levels>
     <level><text>Level 1</text></level>
     <level><text>Level 2</text></level>
 </levels>

 */

public class XmlLevel {

    @JacksonXmlProperty(localName = "text", isAttribute = false)
    private String text;

    public XmlLevel() {
    }

    public XmlLevel(QuestionOptionDto dto) {
        //TODO
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    @Override public String toString() {
        return "level{" +
                "text='" + text +
                "'}";
    }

}