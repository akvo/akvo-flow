/*
 *  Copyright (C) 2019-2020 Stichting Akvo (Akvo Foundation)
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

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.Translation;
import java.util.ArrayList;
import java.util.List;

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
    private String type; //Should always be "tip"
    @JacksonXmlProperty(localName = "text", isAttribute = false)
    private String text;
    @JacksonXmlElementWrapper(localName = "altText", useWrapping = false)
    private List<XmlAltText> altText;

    public XmlHelp(Question q) {
        this.text = q.getTip();
        List<Translation> translations = q.getTranslations();
        for (Translation t: translations) {
            if (Translation.ParentType.QUESTION_TIP.equals(t.getParentType())) {
                if (altText == null) {
                    altText = new ArrayList<>();
                }
                altText.add(new XmlAltText(t));
            }
        }
    }

    @Override public String toString() {
        return "help{" +
                "type='" + type +
                "',text='" + text +
                "'}";
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<XmlAltText> getAltText() {
        return altText;
    }

    public void setAltText(List<XmlAltText> altText) {
        this.altText = altText;
    }

}
