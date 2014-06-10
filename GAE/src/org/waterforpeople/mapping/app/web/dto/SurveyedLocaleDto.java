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

package org.waterforpeople.mapping.app.web.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;

/**
 * dto that can hold surveyedLocale data
 * 
 * @author Mark Westra
 */
public class SurveyedLocaleDto extends BaseDto {

    private static final long serialVersionUID = -850583183416882347L;

    private String id;
    private Long surveyGroupId;
    private String displayName;
    private Double lat;
    private Double lon;
    private List<SurveyInstanceDto> surveyInstances;
    private Long lastUpdateDateTime;
    private String identifier;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public SurveyedLocaleDto() {
        surveyInstances = new ArrayList<SurveyInstanceDto>();
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public List<SurveyInstanceDto> getSurveyInstances() {
        return surveyInstances;
    }

    public void setSurveyInstances(List<SurveyInstanceDto> surveyInstances) {
        this.surveyInstances = surveyInstances;
    }

    public void addInstance(SurveyInstanceDto siDto) {
        surveyInstances.add(siDto);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getSurveyGroupId() {
        return surveyGroupId;
    }

    public void setSurveyGroupId(Long surveyGroupId) {
        this.surveyGroupId = surveyGroupId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Long getLastUpdateDateTime() {
        return lastUpdateDateTime;
    }

    public void setLastUpdateDateTime(Date lastUpdateDateTime) {
        this.lastUpdateDateTime = lastUpdateDateTime.getTime();
    }

    public void setLastUpdateDateTime(Long lastUpdateDateTime) {
        this.lastUpdateDateTime = lastUpdateDateTime;
    }
}
