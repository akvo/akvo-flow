/*
 *  Copyright (C) 2010-2017 Stichting Akvo (Akvo Foundation)
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

package com.gallatinsystems.survey.domain;

import java.util.HashMap;
import java.util.TreeMap;

import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;

/**
 * Grouping of questions within a survey. A questionGroup belongs to exactly 1 survey. Within a
 * group, each question has a unique order.
 */
@PersistenceCapable
public class QuestionGroup extends BaseDomain {
    /**
	 * 
	 */
    private static final long serialVersionUID = -6831602386813027856L;
    private String name = null;

    @NotPersistent
    private TreeMap<Integer, Question> questionMap;
    @NotPersistent
    private HashMap<String, Translation> translationMap;
    private String code = null;
    private String path = null;
    private Long surveyId;
    private Integer order;
    private Boolean repeatable;
    private Status status = null;

    public enum Status {
        READY, COPYING
    };

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Long getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Long surveyId) {
        this.surveyId = surveyId;
    }

    public HashMap<String, Translation> getTranslationMap() {
        return translationMap;
    }

    public void setTranslationMap(HashMap<String, Translation> translationMap) {
        this.translationMap = translationMap;
    }

    public void addQuestion(Integer order, Question question) {
        if (getQuestionMap() == null)
            setQuestionMap(new TreeMap<Integer, Question>());
        getQuestionMap().put(order, question);
    }

    public void setQuestionMap(TreeMap<Integer, Question> questionMap) {
        this.questionMap = questionMap;
    }

    public TreeMap<Integer, Question> getQuestionMap() {
        return questionMap;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addTranslation(Translation t) {
        if (translationMap == null) {
            translationMap = new HashMap<String, Translation>();
        }
        translationMap.put(t.getLanguageCode(), t);
    }
    
    public Status getStatus() {
        return status;
    }
    
    public void setStatus(Status status) {
        this.status = status;
    }
    
    public void setRepeatable(Boolean repeatable) {
        this.repeatable = repeatable;
    }
    
    public Boolean getRepeatable() {
        return repeatable;
    }
    
}
