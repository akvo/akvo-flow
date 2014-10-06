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

package com.gallatinsystems.user.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import com.gallatinsystems.user.domain.Permission;
import com.gallatinsystems.user.domain.User;
import com.gallatinsystems.user.domain.UserRole;

/**
 * Dao for User objects
 * 
 * @author Christopher Fagiani
 */
public class UserDao extends BaseDAO<User> {

    public UserDao() {
        super(User.class);
    }

    /**
     * finds a single user by email address.
     * 
     * @param email
     * @return
     */
    public User findUserByEmail(String email) {
        return findByProperty("emailAddress", email, STRING_TYPE);
    }

    /**
     * searches for users that match the non-null params
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<User> searchUser(String username, String emailAddress,
            String orderByField, String orderByDir, String cursorString) {
        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(User.class);
        StringBuilder filterString = new StringBuilder();
        StringBuilder paramString = new StringBuilder();
        Map<String, Object> paramMap = null;
        paramMap = new HashMap<String, Object>();

        appendNonNullParam("userName", filterString, paramString, "String",
                username, paramMap);
        appendNonNullParam("emailAddress", filterString, paramString, "String",
                emailAddress, paramMap);

        if (orderByField != null) {
            String ordering = orderByDir;
            if (ordering == null) {
                ordering = "asc";
            }
            query.setOrdering(orderByField + " " + ordering);
        }
        if (filterString.length() > 0) {
            query.setFilter(filterString.toString());
            query.declareParameters(paramString.toString());
        }

        prepareCursor(cursorString, query);
        List<User> results = (List<User>) query.executeWithMap(paramMap);
        return results;
    }

    /**
     * lists all permissions
     * 
     * @return
     */
    public List<Permission> listPermissions() {
        return list(Permission.class, null);
    }
    
    /**
     * finds a single user by accessKey
     * 
     * @param accessKey
     * @return
     */
    public User findByAccessKey(String accessKey) {
    	return findByProperty("accessKey", accessKey, STRING_TYPE);
    }

    /**
     * Retrieve a list of users who have been assigned certain role.
     *
     * @param roleName
     * @return returns a list of users who have been assigned the named role. Returns an empty list
     *         if no users have been assigned the role
     */
    public List<User> listUsersByRole(String roleName) {
        List<User> userList = list(Constants.ALL_RESULTS);
        for (User user : userList) {
            for (UserRole role : user.getRoles()) {
                if (role.getName().equals(roleName)) {
                    userList.add(user);
                }
            }
        }
        return userList;
    }
}
