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
import java.util.List;

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

    public static List<String> allPermissionsList = Arrays.asList("PROJECT_FOLDER_CREATE",
            "PROJECT_FOLDER_READ", "PROJECT_FOLDER_UPDATE", "PROJECT_FOLDER_DELETE", "FORM_CREATE",
            "FORM_READ", "FORM_UPDATE", "FORM_DELETE");

    public static Entity createAuthorizationEntity(Key userKey, Key roleKey) {
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
        if (args.length > 4) {
            throw new IllegalArgumentException(
                    "Usage: java org.akvo.gae.remoteapi.DefaultUserRoles <appid> <user> '<passwd>' [port]");
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

            // create defaultroles + retrieve key ids
            Entity defaultAdminRole = new Entity("UserRole");
            defaultAdminRole.setProperty("name", "admin");
            defaultAdminRole.setProperty("permissions", allPermissionsList);
            Key adminRoleKey = ds.put(defaultAdminRole);

            Entity defaultUserRole = new Entity("UserRole");
            defaultUserRole.setProperty("name", "user");
            defaultUserRole.setProperty("permissions", allPermissionsList);
            Key userRoleKey = ds.put(defaultUserRole);

            // for each user create authorization
            List<Entity> userAuthorizationList = new ArrayList<Entity>();

            Query userQuery = new Query("User");

            // ignore superadmin users
            userQuery.setFilter(new Query.FilterPredicate(
                    "permissionList", FilterOperator.IN, Arrays.asList("10", "20")));

            PreparedQuery userPq = ds.prepare(userQuery);
            for (Entity user : userPq.asIterable()) {
                String permissionList = (String) user.getProperty("permissionList");
                String email = (String) user.getProperty("emailAddress");

                if (permissionList.trim().equals("10")) {
                    userAuthorizationList
                            .add(createAuthorizationEntity(user.getKey(), adminRoleKey));
                    System.out.println("Creating Admin user authorization for: "
                            + anonymizeEmail(email));
                } else {
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
