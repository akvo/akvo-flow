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

    public XmlOption[] getOption() {
        return option;
    }

    public void setOption(XmlOption[] option) {
        this.option = option;
    }

    public boolean getAllowOther() {
        return allowOther;
    }

    public void setAllowOther(boolean allowOther) {
        this.allowOther = allowOther;
    }

    public boolean getAllowMultiple() {
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
        if (option != null) {
            ArrayList<QuestionOptionDto> oList = new ArrayList<>();
            for (XmlOption o : option) {
                oList.add(o.toDto());
            }
            dto.setOptionsList(oList);
        }

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
