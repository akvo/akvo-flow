
package org.akvo.gae.remoteapi;

import java.io.File;
import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.repackaged.org.apache.commons.io.FileUtils;
import com.google.appengine.tools.remoteapi.RemoteApiInstaller;
import com.google.appengine.tools.remoteapi.RemoteApiOptions;

public class CheckSurveyInstance {

    public static void main(String[] args) {

        if (args.length != 2) {
            throw new IllegalArgumentException("Missing params");
        }

        final String base = "/tmp";

        final String[] servers = {
                "ircflow"
        };

        final String usr = args[0];
        final String pwd = args[1];

        for (String i : servers) {

            final RemoteApiOptions options = new RemoteApiOptions().server(i + ".appspot.com", 443)
                    .credentials(usr, pwd);
            final RemoteApiInstaller installer = new RemoteApiInstaller();

            try {
                System.out.println("Processing: " + i);

                final List<String> uuids = FileUtils.readLines(new File(base + "/" + i
                        + "/uuid.txt"));

                installer.install(options);
                DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

                for (String j : uuids) {

                    try {
                        final Filter f = new FilterPredicate("uuid", FilterOperator.EQUAL, j);
                        final Query q = new Query("SurveyInstance").setFilter(f).setKeysOnly();
                        final Entity e = ds.prepare(q).asSingleEntity();

                        if (e == null) {
                            System.err.println(j);
                        }
                    } catch (Exception e) {
                        // no-op
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                installer.uninstall();
            }
        }

    }
}
