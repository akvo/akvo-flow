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

package com.gallatinsystems.survey.domain;

import com.gallatinsystems.framework.domain.BaseDomain;

import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import java.util.HashMap;

/**
 * Option for multiple choice questions.
 */
@PersistenceCapable
public class QuestionOption extends BaseDomain {

    private static final long serialVersionUID = 2794521663923141747L;
    private String code = null;
    private String text;
    @NotPersistent
    private HashMap<String, Translation> translationMap;
    private Long questionId;
    private Integer order;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public HashMap<String, Translation> getTranslationMap() {
        return translationMap;
    }

    public void setTranslationMap(HashMap<String, Translation> translationMap) {
        this.translationMap = translationMap;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void addTranslation(Translation t) {
        if (translationMap == null) {
            translationMap = new HashMap<String, Translation>();
        }
        translationMap.put(t.getLanguageCode(), t);
    }

    public long getKeyId() {
        return getKey().getId();
    }
}
