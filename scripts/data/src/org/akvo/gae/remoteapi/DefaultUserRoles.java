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

package org.akvo.gae.remoteapi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.tools.remoteapi.RemoteApiInstaller;
import com.google.appengine.tools.remoteapi.RemoteApiOptions;

/**
 * Run this application to create two default user roles (admin and user), and to grant existing
 * users the authorization to view, modify and delete all surveys groups and surveys that exist on
 * an instance
 *
 * @author emmanuel
 */
public class DefaultUserRoles {

    private static String ADMIN_ROLE_NAME = "admin";

    private static String USER_ROLE_NAME = "user";

    private static List<String> allPermissionsList = Arrays.asList("PROJECT_FOLDER_CREATE",
            "PROJECT_FOLDER_READ", "PROJECT_FOLDER_UPDATE", "PROJECT_FOLDER_DELETE", "FORM_CREATE",
            "FORM_READ", "FORM_UPDATE", "FORM_DELETE");

    private static Entity createAuthorizationEntity(Key userKey, Key roleKey) {
        Entity authorizationEntity = new Entity("UserAuthorization");
        authorizationEntity.setProperty("userId", userKey.getId());
        authorizationEntity.setProperty("roleId", roleKey.getId());
        authorizationEntity.setProperty("objectPath", "/");
        return authorizationEntity;
    }

    public static String anonymizeEmail(String email) {
        return email.replaceAll(".{4}@.{2}", "**@**");
    }

    public static void main(String[] args) {
        if (args.length < 3 || args.length > 4) {
            System.out
                    .println(
                    "Usage: java org.akvo.gae.remoteapi.DefaultUserRoles <appid> <user> '<passwd>' [port]");
            System.exit(1);
        }

        final String instanceUrl = "localhost".equals(args[0]) ? "localhost" : args[0]
                + ".appspot.com";
        final String userEmail = args[1];
        final String passwd = args[2];
        final int port = args.length == 4 ? Integer.parseInt(args[3]) : 443;

        final RemoteApiOptions options = new RemoteApiOptions().server(instanceUrl, port)
                .credentials(
                        userEmail, passwd);

        final RemoteApiInstaller installer = new RemoteApiInstaller();

        try {
            installer.install(options);

            DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

            // create / retrieve defaultroles + retrieve key ids

            Query rolesQuery = new Query("UserRole").setFilter(new Query.FilterPredicate("name",
                    FilterOperator.IN, Arrays.asList(ADMIN_ROLE_NAME, USER_ROLE_NAME)));
            PreparedQuery rolesPq = ds.prepare(rolesQuery);
            Entity defaultAdminRole = null;
            Entity defaultUserRole = null;

            for (Entity role : rolesPq.asIterable()) {
                String roleName = (String) role.getProperty("name");
                if (ADMIN_ROLE_NAME.equals(roleName)) {
                    defaultAdminRole = role;
                } else {
                    defaultUserRole = role;
                }
            }

            Key adminRoleKey = null;
            if (defaultAdminRole == null) {
                defaultAdminRole = new Entity("UserRole");
                defaultAdminRole.setProperty("name", "admin");
                defaultAdminRole.setProperty("permissions", allPermissionsList);
                adminRoleKey = ds.put(defaultAdminRole);
                System.out.println(ADMIN_ROLE_NAME + " role created");
            } else {
                adminRoleKey = defaultAdminRole.getKey();
                System.out.println(ADMIN_ROLE_NAME + " role already exists");
            }

            Key userRoleKey = null;
            if (defaultUserRole == null) {
                defaultUserRole = new Entity("UserRole");
                defaultUserRole.setProperty("name", "user");
                defaultUserRole.setProperty("permissions", allPermissionsList);
                userRoleKey = ds.put(defaultUserRole);
                System.out.println(USER_ROLE_NAME + " role created");
            } else {
                userRoleKey = defaultUserRole.getKey();
                System.out.println(USER_ROLE_NAME + " role already exists");
            }

            // for each user create authorization

            // retrieve existing authorization
            Query userAuthQuery = new Query("UserAuthorization");
            userAuthQuery.setFilter(new Query.FilterPredicate(
                    "objectPath", FilterOperator.EQUAL, "/"));

            PreparedQuery userAuthPq = ds.prepare(userAuthQuery);
            Map<Long, Long> userAuthMap = new HashMap<Long, Long>();
            for (Entity authorization : userAuthPq.asIterable()) {
                Long userId = (Long) authorization.getProperty("userId");
                Long roleId = (Long) authorization.getProperty("roleId");
                userAuthMap.put(userId, roleId);
            }

            List<Entity> userAuthorizationList = new ArrayList<Entity>();
            Query userQuery = new Query("User");
            // ignore superadmin users
            userQuery.setFilter(new Query.FilterPredicate(
                    "permissionList", FilterOperator.IN, Arrays.asList("10", "20")));
            PreparedQuery userPq = ds.prepare(userQuery);

            for (Entity user : userPq.asIterable()) {
                String permissionList = (String) user.getProperty("permissionList");
                String email = (String) user.getProperty("emailAddress");
                Long userId = user.getKey().getId();

                if (permissionList.trim().equals("10")) {
                    if (userAuthMap.containsKey(userId)
                            && userAuthMap.get(userId).equals(adminRoleKey.getId())) {
                        System.out.println("Admin authorization for user " + anonymizeEmail(email)
                                + " already exists");
                        continue;
                    }
                    userAuthorizationList
                            .add(createAuthorizationEntity(user.getKey(), adminRoleKey));
                    System.out.println("Creating Admin user authorization for: "
                            + anonymizeEmail(email));
                } else {
                    if (userAuthMap.containsKey(userId)
                            && userAuthMap.get(userId).equals(userRoleKey.getId())) {
                        System.out.println("User authorization for user " + anonymizeEmail(email)
                                + " already exists");
                        continue;
                    }
                    userAuthorizationList
                            .add(createAuthorizationEntity(user.getKey(), userRoleKey));
                    System.out.println("Creating user authorization for: " + anonymizeEmail(email));
                }
            }

            ds.put(userAuthorizationList);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            installer.uninstall();
        }
    }
}
