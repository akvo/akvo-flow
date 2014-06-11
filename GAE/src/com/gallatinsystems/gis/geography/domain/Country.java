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

package com.gallatinsystems.gis.geography.domain;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;

/**
 * persistent object to store countries
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Country extends BaseDomain {

    private static final long serialVersionUID = 8933871165504597175L;
    private String displayName = null;
    private String name = null;
    private String isoAlpha2Code = null;
    private String isoAlpha3Code = null;
    private Integer isoNumeric3Code = null;
    private Boolean includeInKMZ = false;
    private Boolean includeInExternal = false;
    private Double centroidLat = 0.0d;
    private Double centroidLon = 0.0d;
    private Integer zoomLevel = 7;

    public Boolean getIncludeInKMZ() {
        return includeInKMZ;
    }

    public void setIncludeInKMZ(Boolean includeInKMZ) {
        this.includeInKMZ = includeInKMZ;
    }

    public Boolean getIncludeInExternal() {
        return includeInExternal;
    }

    public void setIncludeInExternal(Boolean includeInExternal) {
        this.includeInExternal = includeInExternal;
    }

    public Double getCentroidLat() {
        return centroidLat;
    }

    public void setCentroidLat(Double centroidLat) {
        this.centroidLat = centroidLat;
    }

    public Double getCentroidLon() {
        return centroidLon;
    }

    public void setCentroidLon(Double centroidLon) {
        this.centroidLon = centroidLon;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIsoAlpha2Code() {
        return isoAlpha2Code;
    }

    public void setIsoAlpha2Code(String isoAlpha2Code) {
        this.isoAlpha2Code = isoAlpha2Code;
    }

    public String getIsoAlpha3Code() {
        return isoAlpha3Code;
    }

    public void setIsoAlpha3Code(String isoAlpha3Code) {
        this.isoAlpha3Code = isoAlpha3Code;
    }

    public Integer getIsoNumeric3Code() {
        return isoNumeric3Code;
    }

    public void setIsoNumeric3Code(Integer isoNumeric3Code) {
        this.isoNumeric3Code = isoNumeric3Code;
    }

    public Integer getZoomLevel() {
        return zoomLevel;
    }

    public void setZoomLevel(Integer zoomLevel) {
        this.zoomLevel = zoomLevel;
    }

}
