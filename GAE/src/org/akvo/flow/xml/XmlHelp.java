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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public XmlHelp() {

    }

    public XmlHelp(Question q) {
        this.text = q.getTip();
        // this removes the empty <altText/> tag from the xml
        if (altText == null) {
            altText = new ArrayList<>();
        }
        Map<String, Translation> translationMap = createTranslationsMap(q);
        for (Translation translation: translationMap.values()) {
            altText.add(new XmlAltText(translation));
        }
    }

    /**
     * This allows to filter out duplicated translations if any
     * @param q
     * @return
     */
    private Map<String, Translation> createTranslationsMap(Question q) {
        Map<String, Translation> translationMap = new HashMap<>();
        List<Translation> translations = q.getTranslations();
        if (translations != null) {
            for (Translation t: translations) {
                if (Translation.ParentType.QUESTION_TIP.equals(t.getParentType())) {
                    translationMap.put(t.getLanguageCode(), t);
                }
            }
        }
        return translationMap;
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
