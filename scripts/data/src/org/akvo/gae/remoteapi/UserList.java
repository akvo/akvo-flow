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

import java.io.File;
import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.repackaged.org.apache.commons.io.FileUtils;
import com.google.appengine.tools.remoteapi.RemoteApiInstaller;
import com.google.appengine.tools.remoteapi.RemoteApiOptions;

public class UserList {

    public static void main(String[] args) throws Exception {

        if (args.length != 1) {
            throw new IllegalArgumentException("Missing params");
        }

        StringBuilder configFolder = new StringBuilder(args[0].trim());
        if (!configFolder.toString().endsWith("/")) {
            configFolder.append("/");
        }

        final List<String> instances = FileUtils
                .readLines(new File("/tmp/instances.txt"));
        final File f = new File("/tmp/device-list.csv");

        FileUtils.write(f, "Instance,Device,Version\n");

        for (String i : instances) {
            final String serviceAccount = "sa-" + i + "@" + i + ".iam.gserviceaccount.com";
            final String serviceAccountKey = configFolder + i + "/" + i + ".p12";
            final RemoteApiOptions options = new RemoteApiOptions().server(i + ".appspot.com", 443)
                    .useServiceAccountCredential(serviceAccount, serviceAccountKey);
            final RemoteApiInstaller installer = new RemoteApiInstaller();
            final StringBuffer sb = new StringBuffer();
            installer.install(options);
            try {
                System.out.println("Processing: " + i);
                DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
                Query q = new Query("User");
                PreparedQuery pq = ds.prepare(q);
                int j = 1;
                for (Entity e : pq.asIterable()) {

                    if (j % 100 == 0) {
                        System.out.println(".");
                    } else {
                        System.out.print(".");
                    }

                    sb.append(
                            String.format("%s,%s,%s", i, e.getProperty("emailAddress"),
                                    getRole((String) e.getProperty("permissionList"))))
                                    .append("\n");
                    j++;
                }
                FileUtils.write(f, sb.toString(), true);
            } finally {
                installer.uninstall();
            }
            System.out.println("\n");
        }

    }

    private static String getRole(String value) {
        if ("0".equals(value)) {
            return "SUPER_ADMIN";
        }
        if ("10".equals(value)) {
            return "ADMIN";
        }
        if ("20".equals(value)) {
            return "USER";
        }
        return value;
    }
}
