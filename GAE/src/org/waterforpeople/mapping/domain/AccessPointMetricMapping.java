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

package org.waterforpeople.mapping.domain;

import java.util.List;

import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;

/**
 * domain for mapping fields within AccessPoint to Metric values
 * 
 * @author Christopher Fagiani
 */
@PersistenceCapable
public class AccessPointMetricMapping extends BaseDomain {

    private static final long serialVersionUID = -4247381034949232233L;

    public static final String UNKOWN_BUCKET = "UNKNOWN";
    public static final String POSITIVE_BUCKET = "POSITIVE";
    public static final String NEGATIVE_BUCKET = "NEGATIVE";
    public static final String NEUTRAL_BUCKET = "NEUTRAL";

    private String organization;
    private String metricName;
    private String metricGroup;
    private String fieldName;
    private List<String> positiveValues;
    private List<String> neutralValues;
    private List<String> negativeValues;

    public List<String> getPositiveValues() {
        return positiveValues;
    }

    public void setPositiveValues(List<String> positiveValues) {
        this.positiveValues = positiveValues;
    }

    public List<String> getNeutralValues() {
        return neutralValues;
    }

    public void setNeutralValues(List<String> neutralValues) {
        this.neutralValues = neutralValues;
    }

    public List<String> getNegativeValues() {
        return negativeValues;
    }

    public void setNegativeValues(List<String> negativeValues) {
        this.negativeValues = negativeValues;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public String getMetricGroup() {
        return metricGroup;
    }

    public void setMetricGroup(String metricGroup) {
        this.metricGroup = metricGroup;
    }
}
