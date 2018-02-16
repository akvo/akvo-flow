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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.repackaged.org.apache.commons.io.FileUtils;
import com.google.appengine.tools.remoteapi.RemoteApiInstaller;
import com.google.appengine.tools.remoteapi.RemoteApiOptions;

public class CheckOptions {

    public static void main(String[] args) {
        final String usr = args[0];
        final String pwd = args[1];

        final RemoteApiOptions options = new RemoteApiOptions().server(
                "instance.appspot.com", 443)
                .credentials(usr, pwd);
        final RemoteApiInstaller installer = new RemoteApiInstaller();
//        File out = new File("/tmp/s3/qas.txt");

        try {
            int optionsJson = 0;
            int optionsOther = 0;
            
            installer.install(options);
            DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
            Filter f = new FilterPredicate("type", FilterOperator.EQUAL, "OPTION");
            Query q = new Query("QuestionAnswerStore").setFilter(f).addSort("createdDateTime",
                    SortDirection.DESCENDING);
            PreparedQuery pq = ds.prepare(q);

            List<String> data = new ArrayList<String>();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            df.setTimeZone(TimeZone.getTimeZone("GMT"));

            for (Entity e : pq.asIterable(FetchOptions.Builder.withChunkSize(100))) {
                String val = (String) e.getProperty("value");
                //Date created = (Date) e.getProperty("createdDateTime");
                if (val.startsWith("[")) {
                    optionsJson++;
                } else {
                    optionsOther++;
                }
                
//                data.add(df.format(created) + "\t" + val.substring(pos + 1));
                System.out.println(".");
            }
            System.out.println("JSON:" + optionsJson);
            System.out.println("Other:" + optionsOther);
//            System.out.println(data.subList(0, 10));
//            FileUtils.writeLines(out, data, "\n");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            installer.uninstall();
        }
    }
}
