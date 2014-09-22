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

import java.util.TreeMap;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;
import com.gallatinsystems.framework.gwt.dto.client.NamedObject;

public class QuestionGroupDto extends BaseDto implements NamedObject {

    private static final long serialVersionUID = -7253934961271624253L;

    private TreeMap<Integer, QuestionDto> questionMap = null;

    private String code;
    private String description;
    private Long surveyId;
    private Integer order;
    private String path;
    private String name;
    private Long sourceId;

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Long surveyId) {
        this.surveyId = surveyId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setQuestionMap(TreeMap<Integer, QuestionDto> questionMap) {
        this.questionMap = questionMap;
    }

    public TreeMap<Integer, QuestionDto> getQuestionMap() {
        return questionMap;
    }

    public void addQuestion(QuestionDto item, Integer position) {
        if (questionMap == null) {
            questionMap = new TreeMap<Integer, QuestionDto>();
            questionMap.put(position, item);
        } else {
            questionMap.put(position, item);

        }
    }

    @Override
    public String getDisplayName() {
        return getCode();
    }

    @Override
    public Long getKeyId() {
        return super.getKeyId();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }
}
