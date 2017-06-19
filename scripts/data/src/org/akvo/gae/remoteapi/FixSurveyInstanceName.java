/*
 *  Copyright (C) 2017 Stichting Akvo (Akvo Foundation)
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

import java.util.HashMap;
import java.util.Map;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

/*
 * - Adds surveyedLOcaleDisplayName property to SurveyInstance when missing
 *   from the associated SurveyedLocale
 */
public class FixSurveyInstanceName implements Process {

    private int slCount = 0;
    private int siCount = 0;
    private int siNonameCount = 0;
    private int siWrongnameCount = 0;
    private int siPtrErrCount = 0;
    private int siFixCount = 0;

    @Override
    public void execute(DatastoreService ds, String[] args) throws Exception {

        final Query q = new Query("SurveyedLocale");
        final PreparedQuery pq = ds.prepare(q);

        final Map<Long, String> slDisplayNames = new HashMap<>();

        System.out.println("Processing SurveyedLocales");

        for (Entity sl : pq.asIterable(FetchOptions.Builder.withChunkSize(500))) {
            slCount++;
            if (sl.getProperty("displayName") != null) {
                slDisplayNames.put(sl.getKey().getId(), (String) sl.getProperty("displayName"));
            }
        }


        final Query q2 = new Query("SurveyInstance");
        final PreparedQuery pq2 = ds.prepare(q2);

        System.out.println("Processing SurveyInstances");

        for (Entity si : pq2.asIterable(FetchOptions.Builder.withChunkSize(500))) {
            
            siCount++;
            Long sl = (Long) si.getProperty("surveyedLocaleId");
            if (sl == null) { 
                System.out.printf("SurveyInstance [%d] has null surveyedLocaleId.\n", si.getKey().getId());
                siPtrErrCount++;
                continue;
            }
            String slName = slDisplayNames.get(sl);
            String siName = (String) si.getProperty("surveyedLocaleDisplayName");
            if ( siName == null ) { //missing
                siNonameCount++;
                System.out.printf("Missing displayname for: [%d]\n", si.getKey().getId());
                if (slName != null) {
                    si.setProperty("surveyedLocaleDisplayName", slName);
                    ds.put(si);
                    siFixCount++;
                }      
            } else { //check if same
                if (!siName.equals(slName)) { 
                    siWrongnameCount++;
                    System.out.printf("Wrong displayname for [%d]: '%s' <> '%s'\n", si.getKey().getId(), siName, slName);
                    if (slName != null) {
                        si.setProperty("surveyedLocaleDisplayName", slName);
                        ds.put(si);
                        siFixCount++;
                    }      
                }       
                
            }
        }
        System.out.printf("#SurveyedLocales:                     %5d\n", slCount);
        System.out.printf("#SurveyedInstances:                   %5d\n", siCount);
        System.out.printf("#SurveyedInstances no name:           %5d\n", siNonameCount);
        System.out.printf("#SurveyedInstances wrong name:        %5d\n", siWrongnameCount);
        System.out.printf("#SurveyedInstances no SurveyedLocale: %5d\n", siPtrErrCount);
        System.out.printf("#SurveyedInstances fixed:             %5d\n", siFixCount);
    }
}
