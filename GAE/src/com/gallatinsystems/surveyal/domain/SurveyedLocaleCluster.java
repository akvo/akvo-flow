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
import java.util.List;

import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;

/**
 * Domain structure to hold cluster info on clustered surveyedLocales we use the geohash as an index
 * to cluster on. the geocells field holds all the geocells up to but not including the
 * clusterGeocell field.
 */
@PersistenceCapable
public class SurveyedLocaleCluster extends BaseDomain {
    private static final long serialVersionUID = 86240917705953798L;

    private String clusterGeocell;
    private List<String> geocells;
    private Integer level;
    private Integer count;
    private Long firstSurveyedLocaleId;
    private Date firstCollectionDate;
    private Double latCenter;
    private Double lonCenter;
    private Boolean showOnPublicMap;

    public SurveyedLocaleCluster(Double lat, Double lon, List<String> geocells,
            String cGeocell, Integer level, Long firstSurveyedLocaleId, Boolean showOnPublicMap,
            Date firstCollectionDate) {
        setClusterGeocell(cGeocell);
        setGeocells(geocells);
        setLatCenter(lat);
        setLonCenter(lon);
        setLevel(level);
        setCount(1);
        setFirstSurveyedLocaleId(firstSurveyedLocaleId);
        setFirstCollectionDate(firstCollectionDate);
        setShowOnPublicMap(showOnPublicMap);
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Double getLatCenter() {
        return latCenter;
    }

    public void setLatCenter(Double latCenter) {
        this.latCenter = latCenter;
    }

    public Double getLonCenter() {
        return lonCenter;
    }

    public void setLonCenter(Double lonCenter) {
        this.lonCenter = lonCenter;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public List<String> getGeocells() {
        return geocells;
    }

    public void setGeocells(List<String> geocells) {
        this.geocells = geocells;
    }

    public String getClusterGeocell() {
        return clusterGeocell;
    }

    public void setClusterGeocell(String clusterGeocell) {
        this.clusterGeocell = clusterGeocell;
    }

    public Boolean getShowOnPublicMap() {
        return showOnPublicMap;
    }

    public void setShowOnPublicMap(Boolean showOnPublicMap) {
        this.showOnPublicMap = showOnPublicMap;
    }

    public Long getFirstSurveyedLocaleId() {
        return firstSurveyedLocaleId;
    }

    public void setFirstSurveyedLocaleId(Long firstSurveyedLocaleId) {
        this.firstSurveyedLocaleId = firstSurveyedLocaleId;
    }

    public Date getFirstCollectionDate() {
        return firstCollectionDate;
    }

    public void setFirstCollectionDate(Date firstCollectionDate) {
        this.firstCollectionDate = firstCollectionDate;
    }
}
