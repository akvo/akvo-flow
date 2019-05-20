/*
 *  Copyright (C) 2019 Stichting Akvo (Akvo Foundation)
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

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

public class LetsEncryptAppUrl implements Process {

    private static final String DEVAPPKIND = "DeviceApplication";
    private static final String DEVURL_PROP = "fileName";
    private static final String APPCODE_PROP = "appCode";
    private static final String APPCODE_VALUE = "flowapp";
    private List<Entity> updatedEntities = new ArrayList<Entity>();

    @Override
    public void execute(DatastoreService ds, String[] args) throws Exception {

        System.out.println("Fetching device apps.");

        PreparedQuery pq = ds.prepare(new Query(DEVAPPKIND));
        for (Entity da : pq.asIterable(FetchOptions.Builder.withChunkSize(500))) {
            String oldurl = (String) da.getProperty(DEVURL_PROP);
            String newurl = oldurl.replaceFirst("http:", "https:");
            String appcode  = (String) da.getProperty(APPCODE_PROP);
            if (!newurl.equals(oldurl)
//                    && appcode.equals(APPCODE_VALUE) //Only modern apps?
                    ) {
                System.out.println(oldurl + " -> " + newurl);
                da.setProperty(DEVURL_PROP, newurl);
                updatedEntities.add(da);
            }
        }

        if (args.length == 1 && args[0].equals("--doit")) {
            ds.put(updatedEntities);
        } else {
            System.out.println("This was a dry run. " + updatedEntities.size() + " changes not saved to datastore");
        }
    }

}
