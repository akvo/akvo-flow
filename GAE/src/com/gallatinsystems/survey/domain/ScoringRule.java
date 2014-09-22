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

import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;

/**
 * Represents a rule used for scoring question responses (i.e. translating from a submitted value to
 * some other enumerated value).
 * 
 * @author Christopher Fagiani
 */
@PersistenceCapable
public class ScoringRule extends BaseDomain {

    private static final long serialVersionUID = -3935333809912657757L;
    private String type;
    private Long questionId;
    private String rangeMin;
    private String rangeMax;
    private String value;

    public ScoringRule(Long questionId, String type, String min, String max,
            String val) {
        this(type, min, max, val);
        this.questionId = questionId;
    }

    public ScoringRule(String type, String min, String max, String val) {
        setType(type);
        setRangeMin(min);
        setRangeMax(max);
        this.value = val;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        if (type != null) {
            this.type = type;
        } else {
            this.type = "NUMERIC";
        }
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getRangeMin() {
        return rangeMin;
    }

    public void setRangeMin(String rangeMin) {
        if (rangeMin != null && rangeMin.trim().length() > 0) {
            this.rangeMin = rangeMin;
        } else {
            this.rangeMin = null;
        }
    }

    public String getRangeMax() {
        return rangeMax;
    }

    public void setRangeMax(String rangeMax) {
        if (rangeMax != null && rangeMax.trim().length() > 0) {
            this.rangeMax = rangeMax;
        } else {
            this.rangeMax = null;
        }
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
