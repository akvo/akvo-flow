/*
 *  Copyright (C) 2018 Stichting Akvo (Akvo Foundation)
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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.repackaged.org.apache.commons.io.FileUtils;

import static org.akvo.gae.remoteapi.DataUtils.*;

/**
 * Delete users from the datastore. Takes in the path of a CSV file containing a list
 * of users to be removed (if present). The CSV file format is
 * emailAddress[,permissionList,superAdmin] e.g. test@example.com,10,False
 * only the email part is used here
 */
public class DeleteUsers implements Process {

    @Override
    public void execute(DatastoreService ds, String[] args) throws Exception {
        if (args.length == 0 || args[0].length() == 0) {
            System.err.println("Usage: " + RemoteAPI.class.getName()
                    + "DeleteUsers <appid> <username> <password> <user-file path>");
            throw new IllegalArgumentException("Missing params");
        }

        final File f = new File(args[0]);
        List<String> userLines = FileUtils.readLines(f);
        if (userLines.isEmpty()) {
            System.err.println("Empty file");
            return;
        }

        Map<String, Entity> existingUsers = retrieveExistingUsers(ds);
        List<Key> users = selectUserEntities(existingUsers, userLines);
        deleteSelectedUsers(ds, users);
        listRemainingSupers(existingUsers);
    }

    private void listRemainingSupers(Map<String, Entity> existingUsers) {
        System.out.println("Remaining superAdmins: ");
        for (Entity e: existingUsers.values()) {
            if (Boolean.TRUE.equals((Boolean)e.getProperty(USER_ROLE_FIELD))
                    || "0".equals((String)e.getProperty("permissionList"))) {
                System.out.println(e.getProperty(USER_EMAIL_FIELD));
            }
        }
    }

    private List<Key> selectUserEntities(Map<String, Entity> existing, List<String> userLines) {
        List<Key> users = new ArrayList<Key>();

        for (String line : userLines) {
            String[] userParts = line.split(",", 3); //lets us accept AddUser files
            String email = userParts[0].trim();
            if (email.length() == 0) {
                System.out.println("Skipping user: " + line);
                continue;
            }
            Entity user = existing.get(email);
            if (user != null) {
                boolean sa = Boolean.TRUE.equals((Boolean)user.getProperty(USER_ROLE_FIELD))
                        || "0".equals((String)user.getProperty("permissionList"));
                System.out.println("Found user: " + email +
                        " id " + user.getKey().getId() +
                        " superAdmin " + sa);
                if (sa) {
                    users.add(user.getKey());
                    existing.remove(email);
                }
            }
        }
        return users;
    }

    private Map<String, Entity> retrieveExistingUsers(DatastoreService ds) {
        Map<String, Entity> existingUsers = new HashMap<String, Entity>();

        Query userQuery = new Query(USER_KIND);
        for (Entity user : ds.prepare(userQuery).asList(FetchOptions.Builder.withDefaults())) {
            String email = (String) user.getProperty(USER_EMAIL_FIELD);
            existingUsers.put(email, user);
        }

        return existingUsers;
    }

    private static void deleteSelectedUsers(DatastoreService ds, List<Key> keys) {
        System.out.printf("Deleting %d user entities\n", keys.size());
        ds.delete(keys);
    }


}
