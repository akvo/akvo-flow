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

import org.waterforpeople.mapping.app.gwt.client.survey.TranslationDto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import com.gallatinsystems.survey.domain.Translation;

/*
 * Class for working with form XML tags like this:
 * <altText type="translation" language="sv">Ja</altText>
 */

public class XmlAltText {

    private static String TRANSLATION_TYPE = "translation";

    @JacksonXmlProperty(localName = "type", isAttribute = true)
    private String type;
    @JacksonXmlProperty(localName = "language", isAttribute = true)
    private String language;
    @JacksonXmlText
    private String text;

    public XmlAltText() {
    }

    public XmlAltText(Translation t) {
        language = t.getLanguageCode();
        text = t.getText();
        type = TRANSLATION_TYPE;
    }

    /**
     * @return a DTO object with relevant fields copied
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
