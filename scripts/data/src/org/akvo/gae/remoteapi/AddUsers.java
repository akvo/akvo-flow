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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.repackaged.org.apache.commons.io.FileUtils;

import static org.akvo.gae.remoteapi.DataUtils.*;

/**
 * Add new users to the datastore configuration. Takes in the path of a CSV file containing a list
 * of users to be added to the relevant instance. The CSV file format is
 * emailAddress,permissionList,superAdmin e.g. test@example.com,10,False
 */
public class AddUsers implements Process {

    @Override
    public void execute(DatastoreService ds, String[] args) throws Exception {
        if (args.length == 0 || args[0].length() == 0) {
            System.err.println("Usage: " + RemoteAPI.class.getName()
                    + "AddUsers <appid> <username> <password> <users file path>");
            System.exit(1);
        }

        final File f = new File(args[0]);
        List<String> userLines = FileUtils.readLines(f);
        if (userLines.isEmpty()) {
            System.err.println("Empty file");
            return;
        }

        Map<String, Entity> users = createUserEntites(userLines);
        Map<String, Entity> existingUsers = retrieveExistingUsers(ds, users.keySet());
        mergeUserProperties(users, existingUsers);

        System.out.println("Upserting " + users.size() + " users");
        ds.put(users.values());
    }

    private Map<String, Entity> createUserEntites(List<String> userLines) {
        Map<String, Entity> users = new HashMap<String, Entity>();

        for (String line : userLines) {
            String[] userParts = line.split(",", 3);
            if (userParts.length < 3 || userParts[0].trim().length() == 0) {
                System.out.println("Skipping user: " + line);
            }
            Entity user = new Entity(USER_KIND);
            user.setProperty(USER_EMAIL_FIELD, userParts[0].trim());
            user.setProperty(USER_PERMISSION_FIELD, userParts[1].trim());
            user.setProperty(USER_ROLE_FIELD, Boolean.valueOf(userParts[2].trim()));
            users.put(userParts[0].trim(), user);

        }
        return users;
    }

    private Map<String, Entity> retrieveExistingUsers(DatastoreService ds,
            Set<String> emailAddresses) {
        Map<String, Entity> existingUsers = new HashMap<String, Entity>();

        Filter emailAddressFilter = new Query.FilterPredicate(USER_EMAIL_FIELD, FilterOperator.IN,
                new ArrayList<String>(emailAddresses));

        Query userQuery = new Query(USER_KIND).setFilter(emailAddressFilter);
        for (Entity user : ds.prepare(userQuery).asList(FetchOptions.Builder.withDefaults())) {
            String email = (String) user.getProperty(USER_EMAIL_FIELD);
            existingUsers.put(email, user);
        }

        return existingUsers;
    }

    private void mergeUserProperties(Map<String, Entity> users, Map<String, Entity> existingUsers) {
        for (String email : users.keySet()) {
            if (existingUsers.containsKey(email)) {
                Entity existingUser = existingUsers.get(email);
                Entity newUser = users.get(email);

                existingUser.setProperty(USER_PERMISSION_FIELD,
                        newUser.getProperty(USER_PERMISSION_FIELD));
                existingUser.setProperty(USER_ROLE_FIELD, newUser.getProperty(USER_ROLE_FIELD));

                users.put(email, existingUser);
            }
        }
    }
}
