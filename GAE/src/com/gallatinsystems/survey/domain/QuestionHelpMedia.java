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

import java.util.HashMap;
import java.util.Map;

import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;

/**
 * represents help media for a question. Help media is text and/or digital media like photos or
 * videos. In the case of digital media, this object only retains a URL pointing to the resource.
 * Help can optionally have translation objects for the text.
 * 
 * @author Christopher Fagiani
 */
@PersistenceCapable
public class QuestionHelpMedia extends BaseDomain {

    private static final long serialVersionUID = 7035250558880867571L;
    private String resourceUrl = null;
    private Type type = null;
    private String text = null;
    private Long questionId;
    @NotPersistent
    private Map<String, Translation> translationMap;

    public enum Type {
        PHOTO, VIDEO, TEXT, ACTIVITY
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Map<String, Translation> getTranslationMap() {
        return translationMap;
    }

    public void setTranslationMap(Map<String, Translation> translationMap) {
        this.translationMap = translationMap;
    }

    public String getResourceUrl() {
        return resourceUrl;
    }

    public void setResourceUrl(String url) {
        this.resourceUrl = url;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void addTranslation(Translation t) {
        if (translationMap == null) {
            translationMap = new HashMap<String, Translation>();
        }
        translationMap.put(t.getLanguageCode(), t);
    }

}
