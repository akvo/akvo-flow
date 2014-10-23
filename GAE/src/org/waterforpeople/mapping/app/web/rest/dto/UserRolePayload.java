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

package org.waterforpeople.mapping.app.web.rest.dto;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.gallatinsystems.user.domain.UserRole;

/**
 * Payload class to wrap the UserRole object. Enables hiding of the internal structure of the
 * UserRole object as well as aggregating data from multiple sources.
 *
 * @author emmanuel
 */
public class UserRolePayload implements Serializable {

    private static final long serialVersionUID = 2069008497215961962L;

    private UserRole userRole;

    public UserRolePayload() {
        userRole = new UserRole();
    }

    public UserRolePayload(UserRole role) {
        userRole = role;
    }

    public String getName() {
        return userRole.getName();
    }

    public void setName(String name) {
        userRole.setName(name);
    }

    public Long getKeyId() {
        return userRole.getKey().getId();
    }

    @JsonIgnore
    public UserRole getUserRole() {
        return userRole;
    }
}
