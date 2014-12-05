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

package com.gallatinsystems.user.dao;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.user.domain.UserRole;

public class UserRoleDao extends BaseDAO<UserRole> {

    public UserRoleDao() {
        super(UserRole.class);
    }

    /**
     * Retrieve all the UserRole objects
     *
     * @return
     */
    public List<UserRole> listAllRoles() {
        List<UserRole> roleList = list(Constants.ALL_RESULTS);
        if (roleList == null) {
            return Collections.emptyList();
        }
        return roleList;
    }

    /**
     * Retrieve a UserRole by its name
     *
     * @param name
     * @return
     */
    public UserRole findUserRoleByName(String name) {
        return findByProperty("name", name, STRING_TYPE);
    }

    /**
     * Save a single UserRole object
     *
     * @param role
     * @return
     */
    public UserRole save(UserRole role) {
        return super.save(role);
    }

    /**
     * Save a collection of UserRole objects
     *
     * @param role
     * @return
     */
    public Collection<UserRole> save(List<UserRole> roleList) {
        return super.save(roleList);
    }
}
