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

import org.waterforpeople.mapping.domain.AccessPoint.AccessPointType;

import com.gallatinsystems.standards.domain.Standard.StandardComparisons;
import com.gallatinsystems.standards.domain.Standard.StandardScope;
import com.gallatinsystems.standards.domain.Standard.StandardType;
import com.google.appengine.api.datastore.Key;

public interface StandardDef {
    public void setKey(Key key);

    public Date getEffectiveStartDate();

    public void setEffectiveStartDate(Date effectiveStartDate);

    public Date getEffectiveEndDate();

    public void setEffectiveEndDate(Date effectiveEndDate);

    public String getStandardDescription();

    public void setStandardDescription(String standardDescription);

    public AccessPointType getAccessPointType();

    public void setAccessPointType(AccessPointType accessPointType);

    public StandardType getStandardType();

    public void setStandardType(StandardType standardType);

    public String getCountryCode();

    public void setCountryCode(String countryCode);

    public StandardScope getStandardScope();

    public void setStandardScope(StandardScope standardScope);

    public void setPartOfCompoundRule(Boolean partOfCompoundRule);

    public Boolean getPartOfCompoundRule();

    public void setStandardComparisons(StandardComparisons sc);

    public StandardComparisons getStandardComparisons();
}
