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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;

/*
 * - Find SurveyedLocales and update their surveyInstanceContrib fields.
 */
public class FixSurveyInstanceContrib implements Process {

    boolean doIt = false;

    @Override
    public void execute(DatastoreService ds, String[] args) throws Exception {

        for (int i = 0; i < args.length; i++) {
            //System.out.printf("#Argument %d: %s\n", i, args[i]);
            if (args[i].equalsIgnoreCase("--doit")) {
                doIt = true;
            }
        }
        
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        df.setTimeZone(TimeZone.getTimeZone("UT"));
        final List<Entity> toBeSaved = new ArrayList<>();

        final Query q = new Query("SurveyedLocale");
        final PreparedQuery pq = ds.prepare(q);

        System.out.println("#Processing SurveyedLocales");

        for (Entity sl : pq.asIterable(FetchOptions.Builder.withChunkSize(500))) {

            Long id = (Long) sl.getKey().getId();
            @SuppressWarnings("unchecked")
            List<Long> contrib = (List<Long>) sl.getProperty("surveyInstanceContrib");
            String identifier = (String) sl.getProperty("identifier");
            if (identifier == null) {
                System.out.println("Null identifier for SL #" + id + "!");
                continue;
            }
            if (identifier == "") {
                System.out.println("Empty identifier for SL #" + id + "!");
                continue;
            }
            
            
            //Now get any contributing surveyInstances
            final Query qi = new Query("SurveyInstance").setFilter(new Query.FilterPredicate("surveyedLocaleId", FilterOperator.EQUAL, id));
            final PreparedQuery pqi = ds.prepare(qi);
            List<Long> actualContrib = new ArrayList<>();
            for (Entity si : pqi.asIterable(FetchOptions.Builder.withChunkSize(500))) {
                actualContrib.add(si.getKey().getId());
                String slIdentifier = (String) si.getProperty("surveyedLocaleIdentifier");
                if (slIdentifier == null || !slIdentifier.equals(identifier)) {
                    System.out.println("Different identifier for SI #" + si.getKey().getId() + ":" + slIdentifier + " <> " + identifier);
                    si.setProperty("surveyedLocaleIdentifier", identifier);
                    toBeSaved.add(si);
                }
            }
            if ((contrib == null && actualContrib.size() > 0)
                    || (contrib != null && !contrib.containsAll(actualContrib))
                    || (contrib != null && (contrib.size() != actualContrib.size()))
                    ) {
                System.out.println("Updating contrib for SL #" + id + " from " + contrib + " to " + actualContrib);
                sl.setProperty("surveyInstanceContrib", actualContrib);;
                toBeSaved.add(sl);
                
            }
            if (doIt && toBeSaved.size() >= 100) {
                System.out.println("Updating " + toBeSaved.size() + " entities.");
                ds.put(toBeSaved);
                toBeSaved.clear();
            }
       }

        System.out.println("#Updating " + toBeSaved.size() + " entities.");
        
        if (doIt) {
            ds.put(toBeSaved);
        } else {
            System.out.println("... just kidding!");
        }
    }
}
