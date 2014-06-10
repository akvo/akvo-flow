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

import java.io.Serializable;
import java.util.TreeMap;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;

public class QuestionOptionDto extends BaseDto implements Serializable {

    private static final long serialVersionUID = 6237222655812167675L;

    private String text;
    private String code;
    private Integer order;
    private Long questionId;
    private TreeMap<String, TranslationDto> translationMap;

    public TreeMap<String, TranslationDto> getTranslationMap() {
        return translationMap;
    }

    public void setTranslationMap(TreeMap<String, TranslationDto> translationMap) {
        this.translationMap = translationMap;
    }

    /**
     * adds the translation to the translation map. If a translation already exists (based on
     * language code), it will be replaced
     * 
     * @param trans
     */
    public void addTranslation(TranslationDto trans) {
        if (translationMap == null) {
            translationMap = new TreeMap<String, TranslationDto>();
        }
        translationMap.put(trans.getLangCode(), trans);
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getText() {
        return text;
    }

    /**
     * returns the translated version of the text for the locale specified (if present). If no
     * translation exists, it will return the default text.
     * 
     * @param locale
     * @return
     */
    public String getLocalizedText(String locale) {
        if (locale != null && translationMap != null) {
            TranslationDto trans = translationMap.get(locale);
            String txt = null;
            if (trans != null) {
                txt = trans.getText();
            }
            if (txt != null && txt.trim().length() > 0) {
                return txt;
            } else {
                return this.text;
            }
        } else {
            return this.text;
        }
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

}
