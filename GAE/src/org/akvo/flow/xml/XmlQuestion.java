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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.Translation;


@JsonInclude(JsonInclude.Include.NON_NULL)
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
    private List<XmlAltText> altText;
    @JacksonXmlProperty(localName = "text", isAttribute = false)
    private String text;
    @JacksonXmlElementWrapper(localName = "levels", useWrapping = true)
    private List<XmlLevel> level;

    @JacksonXmlProperty(localName = "id", isAttribute = true)
    private long id;
    @JacksonXmlProperty(localName = "order", isAttribute = true)
    private int order;
    @JacksonXmlProperty(localName = "type", isAttribute = true)
    private String type;
    @JacksonXmlProperty(localName = "mandatory", isAttribute = true)
    private Boolean mandatory;
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
    private Boolean allowPoints;
    @JacksonXmlProperty(localName = "allowLine", isAttribute = true)
    private Boolean allowLine;
    @JacksonXmlProperty(localName = "allowPolygon", isAttribute = true)
    private Boolean allowPolygon;


    public XmlQuestion() {
    }

    public XmlQuestion(Question q) {
        text = q.getText();
        id = q.getKey().getId();
        order = q.getOrder();
        mandatory = q.getMandatoryFlag();
        localeNameFlag = Boolean.TRUE == q.getLocaleNameFlag();
        localeLocationFlag = Boolean.TRUE == q.getLocaleLocationFlag();
        locked = Boolean.TRUE == q.getGeoLocked();
        allowPoints = Boolean.TRUE == q.getAllowPoints();
        allowLine = Boolean.TRUE == q.getAllowLine();
        allowPolygon = Boolean.TRUE == q.getAllowPolygon();
        caddisflyResourceUuid = q.getCaddisflyResourceUuid();
        if (q.getCascadeResourceId() != null) {
            cascadeResource = q.getCascadeResourceId().toString();
        }
        if (q.getTip() != null) {
            help = new XmlHelp(q.getTip());
        }

        switch (q.getType()) {
            case NUMBER:
                validationRule = new XmlValidationRule(q);
                type = FREE_TYPE;
                break; //Could have done a fall-through here ;)
            case FREE_TEXT:
                type = FREE_TYPE;
                break;
            default:
                type = q.getType().toString();
        }
        if (Boolean.TRUE == q.getDependentFlag()) {
            dependency = new XmlDependency(q.getDependentQuestionId(), q.getDependentQuestionAnswer());
        }
        //Translations, if any
        if (q.getTranslationMap() != null) {
            altText = new ArrayList<>();
            for (Translation t: q.getTranslationMap().values()) {
                altText.add(new XmlAltText(t));
            }
        }
        //Cascade level names, if any
        if (q.getLevelNames() != null) {
            level = new ArrayList<>();
            for (String text: q.getLevelNames()) {
                level.add(new XmlLevel(text));
            }
        }
        //Now copy any options into the transfer container
        if (q.getQuestionOptionMap() != null) {
            options = new XmlOptions(q);
        }
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
                "',allowPoints='" + allowPoints +
                "',allowLines='" + allowLine +
                "',allowPolygon='" + allowPolygon +
                "',options=" + ((options != null) ? options.toString() : "(null)") +
//                ",level='" + level +
                "'}";
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

    public Boolean getMandatory() {
        return mandatory;
    }

    public void setMandatory(Boolean mandatory) {
        this.mandatory = mandatory;
    }

    public boolean getLocaleNameFlag() {
        return localeNameFlag;
    }

    public void setLocaleNameFlag(boolean localeNameFlag) {
        this.localeNameFlag = localeNameFlag;
    }

    public List<XmlAltText> getAltText() {
        return altText;
    }

    public void setAltText(List<XmlAltText> altText) {
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

    public Boolean getAllowPoints() {
        return allowPoints;
    }

    public void setAllowPoints(Boolean allowPoints) {
        this.allowPoints = allowPoints;
    }

    public Boolean getAllowLine() {
        return allowLine;
    }

    public void setAllowLine(Boolean allowLine) {
        this.allowLine = allowLine;
    }

    public Boolean getAllowPolygon() {
        return allowPolygon;
    }

    public void setAllowPolygon(Boolean allowPolygon) {
        this.allowPolygon = allowPolygon;
    }

    public XmlHelp getHelp() {
        return help;
    }

    public void setHelp(XmlHelp help) {
        this.help = help;
    }

    public List<XmlLevel> getLevel() {
        return level;
    }

    public void setLevel(List<XmlLevel> level) {
        this.level = level;
    }

}
