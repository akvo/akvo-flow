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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.gallatinsystems.survey.domain.Question;

/*
 * Class for working with form XML like this:
 * <validationRule allowDecimal="false" validationType="numeric" signed="true"/>
 *
 * It serves as an extension of XmlQuestion for NUMBER questions.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class XmlValidationRule {

    private static String NUMERIC_TYPE = "numeric";

    @JacksonXmlProperty(localName = "validationType", isAttribute = true)
    private String validationType;
    @JacksonXmlProperty(localName = "allowDecimal", isAttribute = true)
    private boolean allowDecimal;
    @JacksonXmlProperty(localName = "signed", isAttribute = true)
    private boolean signed;
    @JacksonXmlProperty(localName = "minVal", isAttribute = true)
    private Double minVal;
    @JacksonXmlProperty(localName = "maxVal", isAttribute = true)
    private Double maxVal;
    @JacksonXmlProperty(localName = "maxLength", isAttribute = true)
    private int maxLength;

    public XmlValidationRule() {
    }

    public XmlValidationRule(Question q) {
        validationType = NUMERIC_TYPE; //The only type supported so far
        signed = Boolean.TRUE.equals(q.getAllowSign());
        allowDecimal = Boolean.TRUE.equals(q.getAllowDecimal());
        maxVal = q.getMaxVal();
        minVal = q.getMinVal();
        //TODO: max length not in DTO, and would not apply to NUMERIC anyway
    }

    @Override public String toString() {
        return "validationRule{" +
                "validationType='" + validationType +
                "',allowDecimal='" + allowDecimal +
                "',signed='" + signed +
                "',maxVal='" + maxVal +
                "',minVal='" + minVal +
                "'}";
    }

    public String getValidationType() {
        return validationType;
    }

    public void setValidationType(String validationType) {
        this.validationType = validationType;
    }

    public boolean getAllowDecimal() {
        return allowDecimal;
    }

    public void setAllowDecimal(boolean allowDecimal) {
        this.allowDecimal = allowDecimal;
    }

    public boolean getSigned() {
        return signed;
    }

    public void setSigned(boolean signed) {
        this.signed = signed;
    }

    public Double getMinVal() {
        return minVal;
    }

    public void setMinVal(Double minVal) {
        this.minVal = minVal;
    }

    public Double getMaxVal() {
        return maxVal;
    }

    public void setMaxVal(Double maxVal) {
        this.maxVal = maxVal;
    }

}
