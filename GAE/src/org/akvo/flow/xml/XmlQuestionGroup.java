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

import java.util.Arrays;
import java.util.TreeMap;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/* Class for working with XML like this:
<questionGroup repeatable = "false">
    <heading>G2</heading>
    <question>
    </question>
</questionGroup>
 */
public class XmlQuestionGroup {

    @JacksonXmlElementWrapper(useWrapping = false)
    private XmlQuestion[] question;

    @JacksonXmlProperty(localName = "heading", isAttribute = false)
    private String heading;

    @JacksonXmlProperty(localName = "repeatable", isAttribute = true)
    private boolean repeatable;

    private int order; //Not in XML

    public XmlQuestionGroup() {
    }

    //Create a form XML object from a DTO
    public XmlQuestionGroup(QuestionGroupDto dto) {
        heading = dto.getCode();
        if (heading == null){
            heading = dto.getName();
        }
        repeatable = dto.getRepeatable();
        //Now copy the q tree
        question = new XmlQuestion[dto.getQuestionMap().size()];
        int i = 0;
        for (QuestionDto q: dto.getQuestionMap().values()) {
            question[i++] = new XmlQuestion(q);
        }
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String h) {
        this.heading = h;
    }

    public XmlQuestion[] getQuestion() {
        return question;
    }

    public void setQuestion(XmlQuestion[] qs) {
        this.question = qs;
    }

    public boolean getRepeatable() {
        return repeatable;
    }

    public void setRepeatable(boolean repeatable) {
        this.repeatable = repeatable;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    /**
     * @return a Dto object with relevant fields copied
     */
    public QuestionGroupDto toDto() {
        QuestionGroupDto dto = new QuestionGroupDto();
        dto.setName(heading);
        dto.setCode(heading);
        dto.setOrder(order);
        dto.setRepeatable(repeatable);
        TreeMap<Integer,QuestionDto> qMap = new TreeMap<>();
        for (XmlQuestion q : question) {
            qMap.put(q.getOrder(), q.toDto());
        }
        dto.setQuestionMap(qMap);

        return dto;
    }

    @Override public String toString() {

        return "questionGroup{" +
                "order='" + order +
                "',heading='" + heading +
                "',questions=" + Arrays.toString(question) +
                '}';
    }
}
