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
import java.util.Arrays;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;


@JacksonXmlRootElement(localName = "survey")
public final class XmlForm {

    @JacksonXmlElementWrapper(localName = "questionGroup", useWrapping = false)
    private XmlQuestionGroup[] questionGroup;

    @JacksonXmlProperty(localName = "version", isAttribute = true)
    private String version;

    @JacksonXmlProperty(localName = "name", isAttribute = true)
    private String name;

    @JacksonXmlProperty(localName = "defaultLanguageCode", isAttribute = true)
    private String defaultLanguageCode;

    @JacksonXmlProperty(localName = "app", isAttribute = true)
    private String app;

    @JacksonXmlProperty(localName = "surveyGroupId", isAttribute = true)
    private String surveyGroupId;

    @JacksonXmlProperty(localName = "surveyGroupName", isAttribute = true)
    private String surveyGroupName;

    @JacksonXmlProperty(localName = "surveyId", isAttribute = true)
    private long surveyId;

    public XmlForm() {
    }


    /**
     * @return a Dto object with relevant fields copied
     */
    public SurveyDto toDto() {
        SurveyDto dto = new SurveyDto();
        //TODO: need to test against many form files
        dto.setKeyId(surveyId);
        dto.setName(name);
        dto.setCode(name);
        dto.setVersion(version);
        ArrayList<QuestionGroupDto> gList = new ArrayList<>();
        int i = 1;
        for (XmlQuestionGroup g : questionGroup) {
            g.setOrder(i++);
            gList.add(g.toDto());
        }
        dto.setQuestionGroupList(gList);

        return dto;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public XmlForm(XmlQuestionGroup[] questionGroups) {
        this.questionGroup = questionGroups;
    }

    public XmlQuestionGroup[] getQuestionGroup() {
        return questionGroup;
    }

    public void setQuestionGroup(XmlQuestionGroup[] qgs) {
        this.questionGroup = qgs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDefaultLanguageCode() {
        return defaultLanguageCode;
    }

    public void setDefaultLanguageCode(String defaultLanguageCode) {
        this.defaultLanguageCode = defaultLanguageCode;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getSurveyGroupId() {
        return surveyGroupId;
    }

    public void setSurveyGroupId(String surveyGroupId) {
        this.surveyGroupId = surveyGroupId;
    }

    public String getSurveyGroupName() {
        return surveyGroupName;
    }

    public void setSurveyGroupName(String surveyGroupName) {
        this.surveyGroupName = surveyGroupName;
    }

    public long getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(String surveyId) {
        this.surveyId = Long.parseLong(surveyId);
    }

    @Override public String toString() {
        return "Form{" +
                "questionGroups=" + Arrays.toString(questionGroup) +
                '}';
    }
}
