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