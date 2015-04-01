/*
 *  Copyright (C) 2015 Stichting Akvo (Akvo Foundation)
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

package org.akvo.gae.remoteapi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

/**
 * Data script to assign a new permission to a role
 */
public class AddPermission implements Process {

    private final static String USER_ROLE = "user";
    private final static String ADMIN_ROLE = "admin";
    private final static Set<String> PERMISSIONS = new HashSet<String>(Arrays.asList(
            "PROJECT_FOLDER_CREATE",
            "PROJECT_FOLDER_READ",
            "PROJECT_FOLDER_UPDATE",
            "PROJECT_FOLDER_DELETE",
            "FORM_CREATE",
            "FORM_READ",
            "FORM_UPDATE",
            "FORM_DELETE",
            "DATA_CLEANING",
            "DATA_DELETE"));

    @SuppressWarnings("unchecked")
    @Override
    public void execute(DatastoreService ds, String[] args) throws Exception {
        if (args.length < 1) {
            System.err
                    .println("Usage: "
                            + RemoteAPI.class.getName()
                            + "AddPermission <appid> <username> <password> [--role=<comma separated list of roles>] --permission=<comma separated list of permissions>");
            System.exit(1);
        }

        List<String> rolesList = null;
        List<String> permissionsList = null;

        boolean useDefaultRoles = args.length < 2;
        if (useDefaultRoles) {
            rolesList = new ArrayList<String>(Arrays.asList(USER_ROLE, ADMIN_ROLE));
            permissionsList = splitArgumentList(args[0]);
        } else {
            rolesList = splitArgumentList(args[0]);
            permissionsList = splitArgumentList(args[1]);

            if (rolesList.isEmpty()) {
                System.err.println("No roles specified");
                System.exit(1);
            }
        }

        if (!isValidPermissionsList(permissionsList)) {
            System.exit(1);
        }

        // update roles
        Filter roleNameFilter = new FilterPredicate("name",
                FilterOperator.IN, rolesList);
        Query roleQuery = new Query("UserRole").setFilter(roleNameFilter);

        List<Entity> roles = new ArrayList<Entity>();
        for (Entity role : ds.prepare(roleQuery).asIterable()) {
            roles.add(role);
            List<String> permissions = (List<String>) role.getProperty("permissions");
            if (permissions == null) {
                role.setProperty("permissions", new HashSet<String>(permissionsList));
            } else {
                Set<String> permissionSet = new HashSet<String>(permissions);
                permissionSet.addAll(permissionsList);
                role.setProperty("permissions", permissionSet);
            }
        }
        ds.put(roles);
    }

    private List<String> splitArgumentList(String argument) {
        if (argument == null || argument.indexOf("=") == -1) {
            return new ArrayList<String>();
        }

        String[] parts = argument.split("=");
        String[] entities = parts.length == 2 ? parts[1].split(",") : null;

        if (entities == null) {
            return new ArrayList<String>();
        }

        List<String> argumentList = new ArrayList<String>();
        for (String entity : entities) {
            if (entity != null) {
                argumentList.add(entity);
            }
        }
        return argumentList;
    }

    private boolean isValidPermissionsList(List<String> permissionsList) {
        boolean valid = true;
        for (String permission : permissionsList) {
            if (!PERMISSIONS.contains(permission)) {
                System.err.println("Invalid permission: " + permission);
                valid = false;
            }
        }
        return valid;
    }
}
