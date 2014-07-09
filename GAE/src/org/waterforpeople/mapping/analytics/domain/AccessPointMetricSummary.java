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

package org.waterforpeople.mapping.analytics.domain;

import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;

/**
 * summary to record counts of access point metrics for roll up by multiple dimensions
 * 
 * @author Christopher Fagiani
 */
@PersistenceCapable
public class AccessPointMetricSummary extends BaseDomain {

    private static final long serialVersionUID = 5145606518665763854L;
    private String organization;
    private String country;
    private Integer subLevel;
    private String subLevelName;
    private String subValue;
    private String metricGroup;
    private String metricName;
    private String metricValue;
    private Long count;
    private Long year;
    private PeriodType periodType;
    private Integer periodValue;
    private String valueBucket;
    private Integer shardNum;
    private Double latitude;
    private Double longitude;
    private String parentSubName;

    public String getParentSubName() {
        return parentSubName;
    }

    public void setParentSubName(String parentSubName) {
        this.parentSubName = parentSubName;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getShardNum() {
        return shardNum;
    }

    public void setShardNum(Integer shardNum) {
        this.shardNum = shardNum;
    }

    public String getSubLevelName() {
        return subLevelName;
    }

    public void setSubLevelName(String subLevelName) {
        this.subLevelName = subLevelName;
    }

    public PeriodType getPeriodType() {
        return periodType;
    }

    public void setPeriodType(PeriodType periodType) {
        this.periodType = periodType;
    }

    public Integer getPeriodValue() {
        return periodValue;
    }

    public void setPeriodValue(Integer periodValue) {
        this.periodValue = periodValue;
    }

    public enum PeriodType {
        Day, Week, Month, Quarter, Year
    };

    public String getMetricGroup() {
        return metricGroup;
    }

    public void setMetricGroup(String metricGroup) {
        this.metricGroup = metricGroup;
    }

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public String getMetricValue() {
        return metricValue;
    }

    public void setMetricValue(String metricValue) {
        this.metricValue = metricValue;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Long getYear() {
        return year;
    }

    public void setYear(Long year) {
        this.year = year;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getValueBucket() {
        return valueBucket;
    }

    public void setValueBucket(String valueBucket) {
        this.valueBucket = valueBucket;
    }

    public void setSubLevel(Integer subLevel) {
        this.subLevel = subLevel;
    }

    public Integer getSubLevel() {
        return subLevel;
    }

    public void setSubValue(String subValue) {
        this.subValue = subValue;
    }

    public String getSubValue() {
        return subValue;
    }

    public String toString() {
        return "Country: " + this.country + " SubLevel: " + subLevel
                + " subValue: " + subValue + " MetricGroup: " + metricGroup
                + " MetricName: " + metricName + " MetricValue: " + metricValue + " count: "
                + count;
    }

    public String identifierString() {
        return country + subLevel + subValue + metricGroup + metricName + metricValue;
    }

}
