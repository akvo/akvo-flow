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
import java.util.Date;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.tools.remoteapi.RemoteApiInstaller;
import com.google.appengine.tools.remoteapi.RemoteApiOptions;

public class SatStatUpdater {
    private static final int GAE_USERNAME = 0;
    private static final int GAE_PASSWORD = 1;
    private static final int INSTANCE_ID = 2;
    private static final int APP_URL = 3;
    private static final int VERSION = 4;

    public static void main(String[] args) {
        if (args.length != 5) {
            System.err.println("Missing argument, please provide GAE username ,GAE password, "
                    + "instanceId, appUrl and version");
            return;
        }

        final String username = args[GAE_USERNAME];
        final String password = args[GAE_PASSWORD];
        final String instance = args[INSTANCE_ID] + ".appspot.com";
        final String appUrl = args[APP_URL];
        final String version = args[VERSION];

        try {
            updateVersion(instance, username, password, appUrl, version);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void updateVersion(String host, String username, String password, String url,
            String version) throws IOException {
        RemoteApiOptions options = new RemoteApiOptions().server(host, 443)
                .credentials(username, password);
        RemoteApiInstaller installer = new RemoteApiInstaller();
        installer.install(options);
        try {
            DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

            Entity e = new Entity("DeviceApplication");
            e.setProperty("appCode", "satStat");
            e.setProperty("deviceType", "androidPhone");
            e.setProperty("version", version);
            e.setProperty("fileName", url);

            final Date date = new Date();// use the same timestamp
            e.setProperty("createdDateTime", date);
            e.setProperty("lastUpdateDateTime", date);
            ds.put(e);
        } finally {
            installer.uninstall();
        }
        System.out.println("New SatStat APK version successfully stored in GAE");
    }
}
