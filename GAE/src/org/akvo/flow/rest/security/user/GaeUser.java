/*
 * Copyright (C) 2019 Stichting Akvo (Akvo Foundation)
 *
 * This file is part of Akvo FLOW.
 *
 * Akvo FLOW is free software: you can redistribute it and modify it under the terms of
 * the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 * either version 3 of the License or any later version.
 *
 * Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License included below for more details.
 *
 * The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */

package org.akvo.flow.rest.security.user;

import java.io.Serializable;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

import org.akvo.flow.rest.security.AppRole;

/**
 * Custom user object for the application.
 *
 * @author Luke Taylor
 */
public class GaeUser implements Serializable {
    private static final long serialVersionUID = -381882633758542764L;
    private final String email;
    private final String userName;
    private final Set<AppRole> authorities;
    private final boolean enabled;
    private Long userId;
    private boolean authByGAE;

    /**
     * Pre-registration constructor. Assigns the user the "ROLE_NEW_USER" role only.
     */
    public GaeUser(boolean authByGAE, String userName, String email) {
        this.authorities = EnumSet.of(AppRole.ROLE_NEW_USER);
        this.userName = userName;
        this.email = email;
        this.enabled = true;
        this.authByGAE = authByGAE;
    }

    /**
     * Post-registration constructor
     */
    public GaeUser(String userName, String email, Long userId, Set<AppRole> authorities,
            boolean enabled, boolean authByGAE) {
        this.userName = userName;
        this.email = email;
        this.authorities = authorities;
        this.enabled = enabled;
        this.userId = userId;
        this.authByGAE = authByGAE;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getEmail() {
        return email;
    }

    public String getUserName() {
        return userName;
    }

    public Long getUserId() {
        return userId;
    }

    public boolean isAuthByGAE() {return authByGAE;}

    public Collection<AppRole> getAuthorities() {
        return authorities;
    }

    @Override
    public String toString() {
        return "GaeUser{email = '" + email + "' " +
                ", userName='" + userName + '\'' +
                ", authorities=" + authorities +
                '}';
    }
}
