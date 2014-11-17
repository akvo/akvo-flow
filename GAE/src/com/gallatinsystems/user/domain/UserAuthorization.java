/*
 *  Copyright (C) 2014 Stichting Akvo (Akvo Foundation)
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
 * Represents user authorization that is the coupling of a role template (defined by @{link
 * com.gallatinsystems.user.domain.UserRole UserRole}) with a set of objects / resources to which
 * the role template is applied. The permissions defined in the role are applied when the user
 * attempts to access any object in the set.
 *
 * @author emmanuel
 */
@PersistenceCapable
public class UserAuthorization extends BaseDomain {

    private static final long serialVersionUID = 9089472356087486015L;

    private Long userId;

    private Long roleId;

    /*
     * A path defining a set of objects to which the permissions defined in the role are applied for
     * a specific user. The path is in the form /set/of/paths[/*] and may define a single object or
     * contain a star that specified all subordinate objects
     */
    private String objectPath;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getObjectPath() {
        return objectPath;
    }

    public void setObjectPath(String objectPath) {
        this.objectPath = objectPath;
    }
}
