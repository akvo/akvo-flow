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

import java.util.ArrayList;
import java.util.List;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionOptionDto;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.gallatinsystems.survey.domain.QuestionOption;
import com.gallatinsystems.survey.domain.Translation;

/*
 * Class for working with form XML like this:
 *
 * <option value="Yes" code="Y"><text>Yes</text></option>
 * <option value="No" code="N"><text>No</text><altText type="translation" language="fr">Non</altText></option>
 */

public class XmlOption {

    @JacksonXmlProperty(localName = "code", isAttribute = true)
    private String code;
    @JacksonXmlProperty(localName = "value", isAttribute = true)
    private String value;
    @JacksonXmlProperty(localName = "text", isAttribute = false)
    private String text;
    @JacksonXmlElementWrapper(localName = "altText", useWrapping = false)
    private List<XmlAltText> altText;

    public XmlOption() {
    }

    public XmlOption(QuestionOption o) {
        setCode(o.getCode());
        setText(o.getText());
        setValue(o.getText());
        //Translations
        altText = new ArrayList<>();
        for (Translation t: o.getTranslationMap().values()) {
            altText.add(new XmlAltText(t));
        }
    }

    /**
     * @return a DTO object with relevant fields copied
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
                "code='" + code +
                "',value='" + value +
                "',text='" + text +
                "'}";
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

    public List<XmlAltText> getAltText() {
        return altText;
    }

    public void setAltText(List<XmlAltText> altText) {
        this.altText = altText;
    }


}
