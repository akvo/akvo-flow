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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.QuestionGroup;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;

import java.util.ArrayList;
import java.util.TreeMap;

/* Class for working with XML like this:
<questionGroup repeatable = "false">
    <heading>G2</heading>
    <question>...</question>
    <question>...</question>
</questionGroup>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class XmlQuestionGroup {

    @JacksonXmlProperty(localName = "repeatable", isAttribute = true)
    private Boolean repeatable;

    @JacksonXmlProperty(localName = "heading", isAttribute = false)
    private String heading;

    @JacksonXmlElementWrapper(localName = "question", useWrapping = false)
    private ArrayList<XmlQuestion> question;

    //TODO if we want to generate it: @JacksonXmlProperty(localName = "order", isAttribute = true)
    @JsonIgnore
    private int order; //Not currently in XML; provided by parent

    public XmlQuestionGroup() {
    }

    //Create a form XML object from a DTO
    public XmlQuestionGroup(QuestionGroup group) {
        heading = group.getCode();
        if (heading == null){
            heading = group.getName();
        }
        order = group.getOrder();
        repeatable = Boolean.TRUE.equals(group.getRepeatable()) ? true : null;
        //Now copy the question tree, if any
        if (group.getQuestionMap() != null) {
            question = new ArrayList<XmlQuestion>();
            for (Question q: group.getQuestionMap().values()) {
                question.add(new XmlQuestion(q));
            }
        }
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
        if (question != null) {
            TreeMap<Integer,QuestionDto> qMap = new TreeMap<>();
            for (XmlQuestion q : question) {
                qMap.put(q.getOrder(), q.toDto());
            }
            dto.setQuestionMap(qMap);
        }

        return dto;
    }

    @Override public String toString() {

        return "questionGroup{" +
                "order='" + order +
                "',heading='" + heading +
                "',repeatable='" + repeatable +
//                "',questions=" + question==null?"(null)":question.toString() +
                "}";
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String h) {
        this.heading = h;
    }

    public Boolean getRepeatable() {
        return repeatable;
    }

    public void setRepeatable(Boolean repeatable) {
        this.repeatable = repeatable;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public ArrayList<XmlQuestion> getQuestion() {
        return question;
    }

    public void setQuestion(ArrayList<XmlQuestion> qs) {
        this.question = qs;
    }

}
