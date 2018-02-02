/*
 *  Copyright (C) 2010-2014 Stichting Akvo (Akvo Foundation)
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

package com.gallatinsystems.surveyal.domain;

import java.util.Date;

import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.framework.domain.BaseDomain;
import com.google.appengine.api.datastore.Text;

/**
 * domain structure to represent the value of an surveyed metric for one surveyed locale at a given
 * point in time. If the survey question used to gather this value was mapped to a metric, the
 * metricId and metricName will be populated (otherwise, they are null). Similarly, the valueType
 * can be either NUMERIC or STRING and the numericValue or stringValue will be populated
 * accordingly.
 * 
 * @author Christopher Fagiani
 */
@PersistenceCapable
public class SurveyalValue extends BaseDomain {
    private static final long serialVersionUID = 861240917705953798L;
    public static final String STRING_VAL_TYPE = "STRING";
    public static final String NUM_VAL_TYPE = "NUMERIC";

    private Integer year;
    private Integer day;
    private Integer month;
    private Date collectionDate;
    private Long surveyedLocaleId;
    private Long surveyInstanceId;
    private Long surveyId;
    private String questionText;
    private Long surveyQuestionId;
    private Integer questionGroupOrder;
    private Integer questionOrder;
    private String metricName;
    private String metricGroup;
    private Long metricId;
    private Double score;
    private String valueType;
    private String stringValue;
    private Text stringValueText;
    private Double numericValue;
    private String organization;
    private String countryCode;
    private String localeType;
    private String systemIdentifier;
    private String questionType;

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public Long getSurveyedLocaleId() {
        return surveyedLocaleId;
    }

    public void setSurveyedLocaleId(Long surveyedLocaleId) {
        this.surveyedLocaleId = surveyedLocaleId;
    }

    public String getMetricGroup() {
        return metricGroup;
    }

    public void setMetricGroup(String metricGroup) {
        this.metricGroup = metricGroup;
    }

    public String getSystemIdentifier() {
        return systemIdentifier;
    }

    public void setSystemIdentifier(String systemIdentifier) {
        this.systemIdentifier = systemIdentifier;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getLocaleType() {
        return localeType;
    }

    public void setLocaleType(String localeType) {
        this.localeType = localeType;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Date getCollectionDate() {
        return collectionDate;
    }

    public void setCollectionDate(Date collectionDate) {
        this.collectionDate = collectionDate;
    }

    public Long getSurveyInstanceId() {
        return surveyInstanceId;
    }

    public void setSurveyInstanceId(Long surveyInstanceId) {
        this.surveyInstanceId = surveyInstanceId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public Long getSurveyQuestionId() {
        return surveyQuestionId;
    }

    public void setSurveyQuestionId(Long surveyQuestionId) {
        this.surveyQuestionId = surveyQuestionId;
    }

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public Long getMetricId() {
        return metricId;
    }

    public void setMetricId(Long metricId) {
        this.metricId = metricId;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public String getStringValue() {
        if (stringValue != null) {
            return stringValue;
        }

        if (stringValueText != null) {
            return stringValueText.getValue();
        }

        return null;
    }

    public void setStringValue(String stringValue) {
        if (stringValue != null && stringValue.length() > Constants.MAX_LENGTH) {
            this.stringValue = null;
            this.stringValueText = new Text(stringValue);
        } else {
            this.stringValue = stringValue;
            this.stringValueText = null;
        }
    }

    public Double getNumericValue() {
        return numericValue;
    }

    public void setNumericValue(Double numericValue) {
        this.numericValue = numericValue;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public Integer getQuestionGroupOrder() {
        return questionGroupOrder;
    }

    public void setQuestionGroupOrder(Integer questionGroupOrder) {
        this.questionGroupOrder = questionGroupOrder;
    }

    public Integer getQuestionOrder() {
        return questionOrder;
    }

    public void setQuestionOrder(Integer questionOrder) {
        this.questionOrder = questionOrder;
    }

    public Long getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Long surveyId) {
        this.surveyId = surveyId;
    }

}
