package org.akvo.flow.xml;

import java.util.ArrayList;
import java.util.List;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto.QuestionType;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/*
 * Class for working with a form XML file like this:
 * <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
 * <survey name="Brand new form four" defaultLanguageCode="en" version='4.0' app="akvoflowsandbox" surveyGroupId="41213002" surveyGroupName="Brand new" surveyId="43993002">
 * <questionGroup><heading>Foo</heading>
 * <question order="1" type="option" mandatory="true" localeNameFlag="false" id="46843002">
 * <options allowOther="false" allowMultiple="false" renderType="radio">
 * <option value="Yes" code="Y"><text>Yes</text></option>
 * <option value="No" code="N"><text>No</text></option>
 * </options>
 * <text>Brand new option question</text></question>
 * <question order="2" type="free" mandatory="true" localeNameFlag="false" id="40533002">
 * <dependency answer-value="Yes" question="46843002"/><text>Name of optimist</text></question>
 * <question order="3" type="free" mandatory="true" localeNameFlag="false" id="45993002">
 * <dependency answer-value="No" question="46843002"/><text>Name of pessimist</text></question>
 * <question order="4" type="free" mandatory="true" localeNameFlag="false" id="47563002"><text>Victim</text></question>
 * <question order="5" type="free" mandatory="true" localeNameFlag="false" id="65813002"><text>New question - please change name</text></question>
 * </questionGroup>
 * <questionGroup><heading>Bar</heading>
 * <question order="1" type="free" mandatory="true" localeNameFlag="false" id="46673002"><text>AA</text></question>
 * <question order="2" type="free" mandatory="true" localeNameFlag="false" id="77813002"><text>BB</text></question>
 * <question order="3" type="free" mandatory="true" localeNameFlag="false" id="48523002"><text>CC</text></question>
 * <question order="4" type="free" mandatory="true" localeNameFlag="false" id="56093002"><text>DD</text></question>
 * </questionGroup>
 * </survey>
 */

public class XmlQuestion {

    @JacksonXmlProperty(localName = "options", isAttribute = false)
    private XmlOptions options;
    @JacksonXmlProperty(localName = "validationRule", isAttribute = false)
    private XmlValidationRule validationRule; //TODO: only one, ever?
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
    @JacksonXmlProperty(localName = "localeNameFlag", isAttribute = true)
    private boolean localeNameFlag;
    @JacksonXmlProperty(localName = "locked", isAttribute = true)
    private boolean locked;
    @JacksonXmlProperty(localName = "localeLocationFlag", isAttribute = true)
    private boolean localeLocationFlag;
    @JacksonXmlProperty(localName = "caddisflyResourceUuid", isAttribute = true)
    private String caddisflyResourceUuid;
    @JacksonXmlProperty(localName = "cascadeResource", isAttribute = true)
    private long cascadeResource; //or String??
    @JacksonXmlProperty(localName = "allowPoints", isAttribute = true)
    private boolean allowPoints;
    @JacksonXmlProperty(localName = "allowLine", isAttribute = true)
    private boolean allowLine;
    @JacksonXmlProperty(localName = "allowPolygon", isAttribute = true)
    private boolean allowPolygon;


    public XmlQuestion() {
    }

    public XmlQuestion(QuestionDto dto) {
        text = dto.getText();
        mandatory = dto.getMandatoryFlag();
        id = dto.getKeyId();
        order = dto.getOrder();
        type = dto.getType().toString(); //??
        localeNameFlag = dto.getLocaleNameFlag();
        localeLocationFlag = dto.getLocaleLocationFlag();
        locked = dto.getGeoLocked();
        allowPoints = dto.getAllowPoints();
        allowLine = dto.getAllowLine();
        allowPolygon = dto.getAllowPolygon();
        caddisflyResourceUuid = dto.getCaddisflyResourceUuid();
        cascadeResource = dto.getCascadeResourceId();
        //= dto.get;
        //= dto.get;
        //Todo: lots

        //Now copy the option container
        options = new XmlOptions(dto.getOptionContainerDto());
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

    public long getCascadeResource() {
        return cascadeResource;
    }

    public void setCascadeResource(long cascadeResource) {
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
        //TODO: lots
        dto.setKeyId(id);
        dto.setText(text);
        dto.setOrder(order);
//      public enum QuestionType {
//      FREE_TEXT, OPTION, NUMBER, GEO, PHOTO, VIDEO, SCAN, TRACK, STRENGTH, DATE, CASCADE, GEOSHAPE, SIGNATURE, CADDISFLY
//  }
        QuestionType t;
        if ("free".equalsIgnoreCase(type)) {
            t = QuestionType.FREE_TEXT;
        } else
        try {
            t = QuestionType.valueOf(type.toUpperCase());//UC necessary?
        } catch (IllegalArgumentException e) {
            t = QuestionType.FREE_TEXT;
        }
        dto.setType(t);

        if (options != null) {
            dto.setOptionContainerDto(options.toDto());
        }

        //return cascade levels as a List<String>
        if (level != null) {
            List<String> cl = new ArrayList<>();
            for (XmlLevel lvl :level) {
                cl.add(lvl.getText());
            }
            dto.setLevelNames(cl);
        }

        return dto;
    }

    @Override public String toString() {
        return "question{" +
                "id='" + id + '\'' +
                "order='" + order + '\'' +
                "type='" + type + '\'' +
                "mandatory='" + mandatory + '\'' +
                "localeNameFlag='" + localeNameFlag + '\'' +
                "options=" + ((options != null) ? options.toString() : "(null)") +
                "level='" + level + '\'' +
                '}';
    }

}