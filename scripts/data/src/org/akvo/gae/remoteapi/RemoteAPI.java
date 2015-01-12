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

import com.google.appengine.tools.remoteapi.RemoteApiInstaller;
import com.google.appengine.tools.remoteapi.RemoteApiOptions;

public class RemoteAPI {

    public static void main(String[] args) {

        if (args.length < 4) {
            System.err.println("Usage: " + RemoteAPI.class.getName()
                    + " <class> <appid> <username> <password> [args ...]");
            System.exit(1);
        }

        final String className = args[0];
        final String instanceUrl = "localhost".equals(args[1]) ? "localhost" : args[1]
                + ".appspot.com";
        final String userEmail = args[2];
        final String passwd = args[3];
        final int port = "localhost".equals(args[1]) ? 8888 : 443;
        final RemoteApiOptions options = new RemoteApiOptions().server(instanceUrl, port)
                .credentials(
                        userEmail, passwd);
        final RemoteApiInstaller installer = new RemoteApiInstaller();

        try {
            installer.install(options);
            Process p = (Process) Class.forName(className).newInstance();
            p.execute(Arrays.copyOfRange(args, 4, args.length));
            System.out.println("Done");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            installer.uninstall();
        }
        
    }
}
