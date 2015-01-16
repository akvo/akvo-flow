//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2010.08.05 at 05:55:36 PM MDT
//

package com.gallatinsystems.survey.domain.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * <p>
 * Java class for anonymous complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element ref="{}altText"/>
 *         &lt;element ref="{}dependency"/>
 *         &lt;element ref="{}help"/>
 *         &lt;element ref="{}options"/>
 * 	   &lt;element ref="{}levels"/>
 *         &lt;element ref="{}text"/>
 *         &lt;element ref="{}validationRule"/>
 *       &lt;/choice>
 *       &lt;attribute name="order" use="required" type="{http://www.w3.org/2001/XMLSchema}NMTOKEN" />
 *       &lt;attribute name="locked" type="{http://www.w3.org/2001/XMLSchema}NMTOKEN" />
 *       &lt;attribute name="requireDoubleEntry" type="{http://www.w3.org/2001/XMLSchema}NMTOKEN" />
 *       &lt;attribute name="type" use="required" type="{http://www.w3.org/2001/XMLSchema}NMTOKEN" />
 *       &lt;attribute name="cascadeResource" type="{http://www.w3.org/2001/XMLSchema}NMTOKEN" />
 *       &lt;attribute name="cascadeVersion" type="{http://www.w3.org/2001/XMLSchema}NMTOKEN" />
 *       &lt;attribute name="mandatory" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="false"/>
 *             &lt;enumeration value="true"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}NMTOKEN" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "altText", "dependency", "help", "options", "levels",
        "text", "validationRule", "scoring"
})
@XmlRootElement(name = "question")
public class Question {

    @XmlElement(required = false)
    protected List<AltText> altText;
    protected Dependency dependency;
    @XmlElement(required = false)
    protected List<Help> help;
    protected Options options;
    @XmlElement(required = false)
    protected Levels levels;
    protected Text text;
    protected ValidationRule validationRule;
    @XmlElement(required = false)
    protected Scoring scoring;

    @XmlAttribute(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    protected String order;

    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    protected String locked;

    @XmlAttribute(required = false)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String allowMultiple;

    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    protected String requireDoubleEntry;
    @XmlAttribute(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    protected String type;

    @XmlAttribute(required = false)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    protected String cascadeResource;

    @XmlAttribute(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String mandatory;
    @XmlAttribute(required = false)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String allowExternalSources;

    @XmlAttribute(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String localeNameFlag;

    @XmlAttribute(required = false)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String localeLocationFlag;

    @XmlAttribute(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    protected String id;

    @XmlAttribute(required = false)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String allowPoints;

    @XmlAttribute(required = false)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String allowLine;

    @XmlAttribute(required = false)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String allowPolygon;

    public List<AltText> getAltText() {
        if (altText == null) {
            altText = new ArrayList<AltText>();
        }
        return altText;
    }

    public void setAltText(List<AltText> altText) {
        this.altText = altText;
    }

    /**
     * Gets the value of the dependency property.
     *
     * @return possible object is {@link Dependency }
     */
    public Dependency getDependency() {
        return dependency;
    }

    /**
     * Sets the value of the dependency property.
     *
     * @param value
     *            allowed object is {@link Dependency }
     */
    public void setDependency(Dependency value) {
        this.dependency = value;
    }

    /**
     * Gets the value of the help property.
     *
     * @return possible object is {@link Help }
     */
    public List<Help> getHelp() {
        return help;
    }

    /**
     * Sets the value of the help property.
     *
     * @param value
     *            allowed object is {@link Help }
     */
    public void setHelp(List<Help> value) {
        this.help = value;
    }

    /**
     * Gets the value of the options property.
     *
     * @return possible object is {@link Options }
     */
    public Options getOptions() {
        return options;
    }

    /**
     * Sets the value of the options property.
     *
     * @param value
     *            allowed object is {@link Options }
     */
    public void setOptions(Options value) {
        this.options = value;
    }

    /**
     * Gets the value of the levels property.
     *
     * @return possible object is {@link Levels }
     */
    public Levels getLevels() {
        return levels;
    }

    /**
     * Sets the value of the levels property.
     *
     * @param value
     *            allowed object is {@link Levels }
     */
    public void setLevels(Levels value) {
        this.levels = value;
    }

    /**
     * Gets the value of the text property.
     *
     * @return possible object is {@link Text }
     */
    public Text getText() {
        return text;
    }

    /**
     * Sets the value of the text property.
     *
     * @param value
     *            allowed object is {@link Text }
     */
    public void setText(Text value) {
        this.text = value;
    }

    /**
     * Gets the value of the validationRule property.
     *
     * @return possible object is {@link ValidationRule }
     */
    public ValidationRule getValidationRule() {
        return validationRule;
    }

    /**
     * Sets the value of the validationRule property.
     *
     * @param value
     *            allowed object is {@link ValidationRule }
     */
    public void setValidationRule(ValidationRule value) {
        this.validationRule = value;
    }

    /**
     * Gets the value of the order property.
     *
     * @return possible object is {@link String }
     */
    public String getOrder() {
        return order;
    }

    /**
     * Sets the value of the order property.
     *
     * @param value
     *            allowed object is {@link String }
     */
    public void setOrder(String value) {
        this.order = value;
    }

    /**
     * Gets the value of the allowMultiple property.
     *
     * @return possible object is {@link String }
     */
    public String getAllowMultiple() {
        return allowMultiple;
    }

    /**
     * Sets the value of the allowMultiple property.
     *
     * @param value
     *            allowed object is {@link String }
     */
    public void setAllowMultiple(String value) {
        this.allowMultiple = value;
    }

    /**
     * Gets the value of the locked property.
     *
     * @return possible object is {@link String }
     */
    public String getLocked() {
        return locked;
    }

    /**
     * Sets the value of the locked property.
     *
     * @param value
     *            allowed object is {@link String }
     */
    public void setLocked(String value) {
        this.locked = value;
    }

    /**
     * Sets the value of the requireDoubleEntry property.
     *
     * @param value
     *            allowed object is {@link String }
     */
    public void setRequireDoubleEntry(String value) {
        this.requireDoubleEntry = value;
    }

    /**
     * Gets the value of the requireDoubleEntry property.
     *
     * @return possible object is {@link String }
     */
    public String getRequireDoubleEntry() {
        return requireDoubleEntry;
    }

    /**
     * Gets the value of the cascadeResource property.
     *
     * @return possible object is {@link String }
     */
    public String getCascadeResource() {
        return cascadeResource;
    }

    /**
     * Sets the value of the cascadeResource property.
     *
     * @param value
     *            allowed object is {@link String }
     */
    public void setCascadeResource(String value) {
        this.cascadeResource = value;
    }

    /**
     * Gets the value of the type property.
     *
     * @return possible object is {@link String }
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     *
     * @param value
     *            allowed object is {@link String }
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the mandatory property.
     *
     * @return possible object is {@link String }
     */
    public String getMandatory() {
        return mandatory;
    }

    /**
     * Sets the value of the mandatory property.
     *
     * @param value
     *            allowed object is {@link String }
     */
    public void setMandatory(String value) {
        this.mandatory = value;
    }

    /**
     * Gets the value of the localeName property.
     *
     * @return possible object is {@link String }
     */
    public String getLocaleNameFlag() {
        return localeNameFlag;
    }

    /**
     * Sets the value of the localeName property.
     *
     * @param value
     *            allowed object is {@link String }
     */
    public void setLocaleNameFlag(String value) {
        this.localeNameFlag = value;
    }

    /**
     * Gets the value of the localeLocation property.
     *
     * @return possible object is {@link String }
     */
    public String getLocaleLocationFlag() {
        return localeLocationFlag;
    }

    /**
     * Sets the value of the localeLocation property.
     *
     * @param value
     *            allowed object is {@link String }
     */
    public void setLocaleLocationFlag(String value) {
        this.localeLocationFlag = value;
    }

    /**
     * Gets the value of the id property.
     *
     * @return possible object is {@link String }
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     *
     * @param value
     *            allowed object is {@link String }
     */
    public void setId(String value) {
        this.id = value;
    }

    public String getAllowExternalSources() {
        return allowExternalSources;
    }

    public void setAllowExternalSources(String allowExternalSources) {
        this.allowExternalSources = allowExternalSources;
    }

    public Scoring getScoring() {
        return scoring;
    }

    public void setScoring(Scoring scoring) {
        this.scoring = scoring;
    }

    public void setAllowPoints(String allowPoints) {
        this.allowPoints = allowPoints;
    }

    public String getAllowPoints() {
        return allowPoints;
    }

    public void setAllowLine(String allowLine) {
        this.allowLine = allowLine;
    }

    public String getAllowLine() {
        return allowLine;
    }

    public void setAllowPolygon(String allowPolygon) {
        this.allowPolygon = allowPolygon;
    }

    public String getAllowPolygon() {
        return allowPolygon;
    }

}
