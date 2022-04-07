/*
 *  Copyright (C) 2022 Stichting Akvo (Akvo Foundation)
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

 *  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */

package org.akvo.flow.domain;

import com.gallatinsystems.user.dao.UserAuthorizationDAO;
import com.gallatinsystems.user.dao.UserRoleDao;
import com.gallatinsystems.user.domain.Permission;
import com.gallatinsystems.user.domain.UserAuthorization;
import com.gallatinsystems.user.domain.UserRole;

import java.util.Arrays;
import java.util.HashSet;

public class DefaultUserAuthorization {

    private static final String DEFAULT_USER_ROLE_NAME = "DefaultUserRole";

    public static UserAuthorization getOrCreateDefaultAuthorization(Long newUserId, Long folderId) {
        UserRole role = getOrCreateDefaultUserRole();
        UserAuthorization auth = new UserAuthorizationDAO().findUserAuthorization(newUserId, role.getKey().getId(), folderId);
        if (auth != null) {
            return auth;
        }

        auth = new UserAuthorization();
        auth.setUserId(newUserId);
        auth.setRoleId(role.getKey().getId());
        auth.setSecuredObjectId(folderId);

        return auth;
    }

    private static UserRole getOrCreateDefaultUserRole() {

        UserRole role = new UserRoleDao().findUserRoleByName(DEFAULT_USER_ROLE_NAME);
        if (role == null) {
            role = new UserRole();
            role.setName(DEFAULT_USER_ROLE_NAME);
            role.setPermissions(new HashSet<>(Arrays.asList(Permission.values())));
            return new UserRoleDao().save(role);
        }
        return role;
    }
}
