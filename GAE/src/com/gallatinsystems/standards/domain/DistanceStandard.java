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

package com.gallatinsystems.standards.domain;

import java.util.Date;

import javax.jdo.annotations.PersistenceCapable;

import org.waterforpeople.mapping.domain.AccessPoint;
import org.waterforpeople.mapping.domain.AccessPoint.AccessPointType;
import org.waterforpeople.mapping.domain.AccessPoint.LocationType;

import com.gallatinsystems.framework.domain.BaseDomain;
import com.gallatinsystems.standards.domain.Standard.StandardComparisons;
import com.gallatinsystems.standards.domain.Standard.StandardScope;
import com.gallatinsystems.standards.domain.Standard.StandardType;

@PersistenceCapable
public class DistanceStandard extends BaseDomain implements StandardDef {

    /**
     * ds.setAccessPointType(AccessPointType.WATER_POINT);
     * ds.setStandardType(StandardType.WaterPointLevelOfService);
     * ds.setStandardScope(StandardScope.Local); ds.setCountry("BO");
     */
    private static final long serialVersionUID = -9111026583324258277L;

    private AccessPoint.LocationType locationType = null;
    private Integer maxDistance = null;
    private AccessPointType accessPointType = null;
    private StandardType standardType = null;
    private String countryCode = null;
    private StandardScope standardScope = null;
    private String standardDescription = null;
    private Date effectiveStartDate = null;
    private Date effectiveEndDate = null;
    private Boolean partOfCompoundRule = null;

    public Date getEffectiveStartDate() {
        return effectiveStartDate;
    }

    public void setEffectiveStartDate(Date effectiveStartDate) {
        this.effectiveStartDate = effectiveStartDate;
    }

    public Date getEffectiveEndDate() {
        return effectiveEndDate;
    }

    public void setEffectiveEndDate(Date effectiveEndDate) {
        this.effectiveEndDate = effectiveEndDate;
    }

    public String getStandardDescription() {
        return standardDescription;
    }

    public void setStandardDescription(String standardDescription) {
        this.standardDescription = standardDescription;
    }

    public AccessPointType getAccessPointType() {
        return accessPointType;
    }

    public void setAccessPointType(AccessPointType accessPointType) {
        this.accessPointType = accessPointType;
    }

    public StandardType getStandardType() {
        return standardType;
    }

    public void setStandardType(StandardType standardType) {
        this.standardType = standardType;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public StandardScope getStandardScope() {
        return standardScope;
    }

    public void setStandardScope(StandardScope standardScope) {
        this.standardScope = standardScope;
    }

    public LocationType getLocationType() {
        return locationType;
    }

    public void setLocationType(LocationType locationType) {
        this.locationType = locationType;
    }

    public Integer getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(Integer maxDistance) {
        this.maxDistance = maxDistance;
    }

    @Override
    public void setPartOfCompoundRule(Boolean partOfCompoundRule) {
        this.partOfCompoundRule = partOfCompoundRule;
    }

    @Override
    public Boolean getPartOfCompoundRule() {
        // TODO Auto-generated method stub
        return partOfCompoundRule;
    }

    @Override
    public void setStandardComparisons(StandardComparisons sc) {
    }

    @Override
    public StandardComparisons getStandardComparisons() {
        // TODO Auto-generated method stub
        return null;
    }
}
