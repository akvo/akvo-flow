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
