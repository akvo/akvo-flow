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
 * Class for working with form XML like this:
 * <validationRule allowDecimal="false" validationType="numeric" signed="true"/>
 */

public class XmlValidationRule {

    @JacksonXmlProperty(localName = "validationType", isAttribute = true)
    private String validationType;
    @JacksonXmlProperty(localName = "allowDecimal", isAttribute = true)
    private boolean allowDecimal;
    @JacksonXmlProperty(localName = "signed", isAttribute = true)
    private boolean signed;
    @JacksonXmlProperty(localName = "minVal", isAttribute = true)
    private double minVal;
    @JacksonXmlProperty(localName = "maxVal", isAttribute = true)
    private double maxVal;
    @JacksonXmlProperty(localName = "maxLength", isAttribute = true)
    private int maxLength;

    public XmlValidationRule() {
    }

    @Override public String toString() {
        return "validationRule{" +
                "validationType='" + validationType +
                "',allowDecimal='" + allowDecimal +
                "', signed='" + signed +
                "', maxVal='" + maxVal +
                "', minVal='" + minVal +
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

    public double getMinVal() {
        return minVal;
    }

    public void setMinVal(double minVal) {
        this.minVal = minVal;
    }

    public double getMaxVal() {
        return maxVal;
    }

    public void setMaxVal(double maxVal) {
        this.maxVal = maxVal;
    }

}
