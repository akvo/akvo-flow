/*
 *  Copyright (C) 2012-2014,2019 Stichting Akvo (Akvo Foundation)
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

package org.akvo.flow.rest.security;

import org.springframework.security.core.GrantedAuthority;

/**
 * @author Luke Taylor
 */
public enum AppRole implements GrantedAuthority {
    ROLE_NEW_USER(-1),
    ROLE_SUPER_ADMIN(0),
    ROLE_ADMIN(10),
    ROLE_USER(20);

    private final int level;

    /**
     * Creates an authority with a specific bit representation. It's important that this doesn't
     * change as it will be used in the database. The enum ordinal is less reliable as the enum may
     * be reordered or have new roles inserted which would change the ordinal values.
     *
     * @param bit the permission bit which will represent this authority in the datastore.
     */
    AppRole(int bit) {
        this.level = bit;
    }

    public int getLevel() {
        return level;
    }

    public String getAuthority() {
        return toString();
    }
}
