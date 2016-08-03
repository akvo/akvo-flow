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

import java.util.Arrays;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.tools.remoteapi.RemoteApiInstaller;
import com.google.appengine.tools.remoteapi.RemoteApiOptions;

public class RemoteAPI {

    public static void main(String[] args) {

        if (args.length < 2) {
            System.err
                    .println("Usage: "
                            + RemoteAPI.class.getName()
                            + "<class> <appid> [serviceAccount] [serviceAccountPrivatekey path] [args ...]\n"
                            + "<class> can be a fully qualified class or just a class name."
                            + " Defaults to package org.akvo.gae.remoteapi");
            System.exit(1);
        }

        final boolean isLocalDevelopmentServer = "localhost".equals(args[1]);
        final String className = args[0];
        final String instanceUrl = isLocalDevelopmentServer ? "localhost" : args[1]
                + ".appspot.com";
        final int port = isLocalDevelopmentServer ? 8888 : 443;
        String serviceAccount = null;
        String serviceAccountPvk = null;

        if (!isLocalDevelopmentServer) {
            serviceAccount = args[2];
            serviceAccountPvk = args[3];
        }

        final RemoteApiOptions options = new RemoteApiOptions().server(instanceUrl, port);

        if (isLocalDevelopmentServer) {
            options.useDevelopmentServerCredential();
        } else {
            options.useServiceAccountCredential(serviceAccount, serviceAccountPvk);
        }

        final RemoteApiInstaller installer = new RemoteApiInstaller();

        try {
            installer.install(options);
            String clz = className.indexOf(".") != -1 ? className : "org.akvo.gae.remoteapi."
                    + className;
            DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
            Process p = (Process) Class.forName(clz).newInstance();
            if (isLocalDevelopmentServer) {
                p.execute(ds, Arrays.copyOfRange(args, 2, args.length));
            } else {
                p.execute(ds, Arrays.copyOfRange(args, 4, args.length));
            }
            System.out.println("Done");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            installer.uninstall();
        }
    }
}
