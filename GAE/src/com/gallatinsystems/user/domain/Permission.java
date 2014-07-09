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

package com.gallatinsystems.user.domain;

import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;

/**
 * permissions that can be assigned to a user. Code is mandatory and must be unique
 * 
 * @author Christopher Fagiani
 */
@PersistenceCapable
public class Permission extends BaseDomain {
    private static final long serialVersionUID = 3706308694153467750L;
    private String code;
    private String name;

    public Permission(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public Permission(String name) {
        this(name, name.toUpperCase());
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * equality defined as having the same code
     */
    public boolean equals(Object other) {
        if (other != null && other instanceof Permission) {
            Permission op = (Permission) other;
            if (getCode() != null && getCode().equals(op.getCode())) {
                return true;
            } else if (op.getCode() == null && getCode() == null) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public int hashCode() {
        if (code != null) {
            return code.hashCode();
        } else {
            return 0;
        }
    }
}
