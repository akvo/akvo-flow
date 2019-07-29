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
import org.waterforpeople.mapping.app.gwt.client.survey.OptionContainerDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionOptionDto;

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

public class XmlOptions {

    @JacksonXmlElementWrapper(localName = "option", useWrapping = false)
    private XmlOption[] option;
    @JacksonXmlProperty(localName = "allowOther", isAttribute = true)
    private boolean allowOther;
    @JacksonXmlProperty(localName = "allowMultiple", isAttribute = true)
    private boolean allowMultiple;
    @JacksonXmlProperty(localName = "renderType", isAttribute = true)
    private String renderType; //Ignore for now


    public XmlOptions() {
    }

    public XmlOptions(OptionContainerDto dto) {
        //TODO
    }

    public XmlOption[] getOption() {
        return option;
    }

    public void setOption(XmlOption[] option) {
        this.option = option;
    }

    public boolean isAllowOther() {
        return allowOther;
    }

    public void setAllowOther(boolean allowOther) {
        this.allowOther = allowOther;
    }

    public boolean isAllowMultiple() {
        return allowMultiple;
    }

    public void setAllowMultiple(boolean allowMultiple) {
        this.allowMultiple = allowMultiple;
    }

    public String getRenderType() {
        return renderType;
    }

    public void setRenderType(String renderType) {
        this.renderType = renderType;
    }

    /**
     * @return a Dto with relevant fields copied
     */
    public OptionContainerDto toDto() {
        OptionContainerDto dto = new OptionContainerDto();
        dto.setAllowOtherFlag(allowOther);
        dto.setAllowMultipleFlag(allowMultiple);
        ArrayList<QuestionOptionDto> oList = new ArrayList<>();
        for (XmlOption o : option) {
            oList.add(o.toDto());
        }
        dto.setOptionsList(oList);
        return dto;
    }

    @Override public String toString() {
        return "options{" +
                "allowOther='" + allowOther +
                "',allowMultiple='" + allowMultiple +
                "',options=" + option.toString() +
                '}';
    }

}
