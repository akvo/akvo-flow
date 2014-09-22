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

package com.gallatinsystems.weightsmeasures.domain;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class UnitOfMeasure extends BaseDomain {

    private static final long serialVersionUID = -713244155250701779L;
    private String name;
    private String code;
    private String description;
    private UnitOfMeasureType type = null;
    private String notation = null;
    private UnitOfMeasureSystem system = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UnitOfMeasureType getType() {
        return type;
    }

    public void setType(UnitOfMeasureType type) {
        this.type = type;
    }

    public String getNotation() {
        return notation;
    }

    public void setNotation(String notation) {
        this.notation = notation;
    }

    public UnitOfMeasureSystem getSystem() {
        return system;
    }

    public void setSystem(UnitOfMeasureSystem system) {
        this.system = system;
    }

    public enum UnitOfMeasureSystem {
        IMPERIAL, METRIC, OTHER
    }

    public enum UnitOfMeasureType {
        LENGTH, VOLUME, MASS, TIME, CURRENT, TEMPERATURE, AMOUNT_OF_SUBSTANCE
    }
}
