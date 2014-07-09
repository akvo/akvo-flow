/*
 *  Copyright (C) 2010-2012 Stichting Akvo (Akvo Foundation)
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

package org.waterforpeople.mapping.app.gwt.client.survey;

import java.util.List;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;

/**
 * transfer object to map survey questions to object fields
 * 
 * @author Christopher Fagiani
 */
public class SurveyAttributeMappingDto extends BaseDto {

    private static final long serialVersionUID = -1318176575596948441L;

    private Long surveyId;
    private String surveyQuestionId;
    private String objectName;
    private String attributeName;
    private Long questionGroupId;
    private List<String> apTypes;

    public List<String> getApTypes() {
        return apTypes;
    }

    public void setApTypes(List<String> apTypes) {
        this.apTypes = apTypes;
    }

    public Long getQuestionGroupId() {
        return questionGroupId;
    }

    public void setQuestionGroupId(Long questionGroupId) {
        this.questionGroupId = questionGroupId;
    }

    public Long getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Long surveyKeyId) {
        this.surveyId = surveyKeyId;
    }

    public String getSurveyQuestionId() {
        return surveyQuestionId;
    }

    public void setSurveyQuestionId(String surveyQuestionId) {
        this.surveyQuestionId = surveyQuestionId;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String targetObjectName) {
        this.objectName = targetObjectName;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String targetAttributeName) {
        this.attributeName = targetAttributeName;
    }
}
