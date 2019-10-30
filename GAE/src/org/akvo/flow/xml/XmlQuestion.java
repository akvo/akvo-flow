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
import java.util.HashMap;
import java.util.List;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto.QuestionType;
import org.waterforpeople.mapping.app.gwt.client.survey.TranslationDto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;


public class XmlQuestion {

    private static String FREE_TYPE = "free";
    private static String NUMERIC_VALIDATION_TYPE = "numeric";

    @JacksonXmlProperty(localName = "options", isAttribute = false)
    private XmlOptions options;
    @JacksonXmlProperty(localName = "validationRule", isAttribute = false)
    private XmlValidationRule validationRule; //TODO: only one, ever?
    @JacksonXmlProperty(localName = "dependency", isAttribute = false)
    private XmlDependency dependency; //only one
    @JacksonXmlProperty(localName = "help", isAttribute = false)
    private XmlHelp help;
    @JacksonXmlElementWrapper(localName = "altText", useWrapping = false)
    private XmlAltText[] altText;
    @JacksonXmlProperty(localName = "text", isAttribute = false)
    private String text;
    @JacksonXmlElementWrapper(localName = "levels", useWrapping = true)
    private XmlLevel[] level;

    @JacksonXmlProperty(localName = "id", isAttribute = true)
    private long id;
    @JacksonXmlProperty(localName = "order", isAttribute = true)
    private int order;
    @JacksonXmlProperty(localName = "type", isAttribute = true)
    private String type;
    @JacksonXmlProperty(localName = "mandatory", isAttribute = true)
    private boolean mandatory;
    @JacksonXmlProperty(localName = "requireDoubleEntry", isAttribute = true)
    private boolean requireDoubleEntry;
    @JacksonXmlProperty(localName = "localeNameFlag", isAttribute = true)
    private boolean localeNameFlag;
    @JacksonXmlProperty(localName = "locked", isAttribute = true)
    private boolean locked;
    @JacksonXmlProperty(localName = "localeLocationFlag", isAttribute = true)
    private boolean localeLocationFlag;
    @JacksonXmlProperty(localName = "caddisflyResourceUuid", isAttribute = true)
    private String caddisflyResourceUuid;
    @JacksonXmlProperty(localName = "cascadeResource", isAttribute = true)
    private String cascadeResource;
    @JacksonXmlProperty(localName = "allowPoints", isAttribute = true)
    private boolean allowPoints;
    @JacksonXmlProperty(localName = "allowLine", isAttribute = true)
    private boolean allowLine;
    @JacksonXmlProperty(localName = "allowPolygon", isAttribute = true)
    private boolean allowPolygon;


    public XmlQuestion() {
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean getMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public boolean getLocaleNameFlag() {
        return localeNameFlag;
    }

    public void setLocaleNameFlag(boolean localeNameFlag) {
        this.localeNameFlag = localeNameFlag;
    }

    public XmlAltText[] getAltText() {
        return altText;
    }

    public void setAltText(XmlAltText[] altText) {
        this.altText = altText;
    }

    public XmlOptions getOptions() {
        return options;
    }

    public void setOptions(XmlOptions options) {
        this.options = options;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isLocaleLocationFlag() {
        return localeLocationFlag;
    }

    public void setLocaleLocationFlag(boolean localeLocationFlag) {
        this.localeLocationFlag = localeLocationFlag;
    }

    public String getCaddisflyResourceUuid() {
        return caddisflyResourceUuid;
    }

    public void setCaddisflyResourceUuid(String caddisflyResourceUuid) {
        this.caddisflyResourceUuid = caddisflyResourceUuid;
    }

    public String getCascadeResource() {
        return cascadeResource;
    }

    public void setCascadeResource(String cascadeResource) {
        this.cascadeResource = cascadeResource;
    }

    public XmlValidationRule getValidationRule() {
        return validationRule;
    }

    public void setValidationRule(XmlValidationRule validationRule) {
        this.validationRule = validationRule;
    }

    public boolean getLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean getAllowPoints() {
        return allowPoints;
    }

    public void setAllowPoints(boolean allowPoints) {
        this.allowPoints = allowPoints;
    }

    public boolean getAllowLine() {
        return allowLine;
    }

    public void setAllowLine(boolean allowLine) {
        this.allowLine = allowLine;
    }

    public boolean getAllowPolygon() {
        return allowPolygon;
    }

    public void setAllowPolygon(boolean allowPolygon) {
        this.allowPolygon = allowPolygon;
    }

    public XmlHelp getHelp() {
        return help;
    }

    public void setHelp(XmlHelp help) {
        this.help = help;
    }

    public XmlLevel[] getLevel() {
        return level;
    }

    public void setLevel(XmlLevel[] level) {
        this.level = level;
    }

    /**
     * @return a Dto object with relevant fields copied
     */
    public QuestionDto toDto() {
        QuestionDto dto = new QuestionDto();
        dto.setKeyId(id);
        dto.setText(text);
        dto.setOrder(order);
        dto.setMandatoryFlag(mandatory);
        dto.setLocaleNameFlag(localeNameFlag);
        //Type is more complicated:
        QuestionType t; //FREE_TEXT, OPTION, NUMBER, GEO, PHOTO, VIDEO, SCAN, TRACK, STRENGTH, DATE, CASCADE, GEOSHAPE, SIGNATURE, CADDISFLY
        if (FREE_TYPE.equalsIgnoreCase(type)) { //Text OR number
            if (validationRule != null && NUMERIC_VALIDATION_TYPE.equals(validationRule.getValidationType())) {
                t = QuestionType.NUMBER;
            } else {
                t = QuestionType.FREE_TEXT;
            }
        } else {
            try {
                t = QuestionType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                t = QuestionType.FREE_TEXT;
            }
        }
        dto.setType(t);

        if (options != null) {
            dto.setOptionContainerDto(options.toDto());
            //exporter code expects these in the QuestionDto:
            dto.setAllowMultipleFlag(options.getAllowMultiple());
            dto.setAllowOtherFlag(options.getAllowOther());
        }
        //Translations
        if (altText != null) {
            HashMap<String,TranslationDto> qMap = new HashMap<>();
            for (XmlAltText alt : altText) {
                qMap.put(alt.getLanguage(), alt.toDto());
            }
            dto.setTranslationMap(qMap);
        }

        //return cascade levels as a List<String>
        if (level != null) {
            List<String> cl = new ArrayList<>();
            for (XmlLevel lvl : level) {
                cl.add(lvl.getText());
            }
            dto.setLevelNames(cl);
        }

        if (caddisflyResourceUuid != null) {
            dto.setCaddisflyResourceUuid(caddisflyResourceUuid);
        }
        return dto;
    }

    @Override public String toString() {
        return "question{" +
                "id='" + id +
                "',order='" + order +
                "',type='" + type +
                "',mandatory='" + mandatory +
                "',locked='" + locked +
                "',localeNameFlag='" + localeNameFlag +
                "',options=" + ((options != null) ? options.toString() : "(null)") +
                ",level='" + level +
                "'}";
    }

}
