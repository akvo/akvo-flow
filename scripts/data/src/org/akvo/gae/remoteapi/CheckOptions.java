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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;

public class CheckOptions implements Process {

    @Override
    public void execute(DatastoreService ds, String[] args) throws Exception {

        try {
            int optionsNull = 0;
            int optionsJson = 0;
            int optionsOther = 0;
            int optionsLong = 0;
            int optionsLegacy = 0;
            
            Filter f = new FilterPredicate("type", FilterOperator.EQUAL, "OPTION");
            Query q = new Query("QuestionAnswerStore").setFilter(f).addSort("createdDateTime",
                    SortDirection.DESCENDING);
            PreparedQuery pq = ds.prepare(q);

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            df.setTimeZone(TimeZone.getTimeZone("GMT"));

            for (Entity e : pq.asIterable(FetchOptions.Builder.withChunkSize(1000))) {
                String val = (String) e.getProperty("value");
                if (val == null) {
                    val = ((com.google.appengine.api.datastore.Text) e.getProperty("valueText")).getValue(); //it was too big for string
                    optionsLong++;
                }
                if (val == null) {
                    optionsNull++;
                } else if (val.startsWith("[")) {
                        optionsJson++;
                        if (val.contains("\"code\":\"OTHER\"") || val.contains("isOther")) {
                            optionsOther++;
                        }
                } else {
                    optionsLegacy++;
                }
                
            }
            System.out.println("JSON:" + optionsJson +  "  Legacy:" + optionsLegacy + "  null:" + optionsNull+ "  Long:" + optionsLong+ "  Other:" + optionsOther);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
