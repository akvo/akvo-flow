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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;

/*
 * - Find duplicated SurveyedLocales
 */
public class DuplicatedSurveyedLocale implements Process {

    boolean deleteDuplicates = false;
    private Map<Long, String> surveyGroups = new HashMap<>();
    final List<Entity> toBeRemoved = new ArrayList<>();

    @Override
    public void execute(DatastoreService ds, String[] args) throws Exception {

        for (int i = 0; i < args.length; i++) {
            //System.out.printf("#Argument %d: %s\n", i, args[i]);
            if (args[i].equalsIgnoreCase("--doit")) {
                deleteDuplicates = true;
            }
        }
        
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        df.setTimeZone(TimeZone.getTimeZone("UT"));

        processSurveyGroups(ds);
        
        final Query q = new Query("SurveyedLocale").addSort("identifier", SortDirection.ASCENDING);
        final PreparedQuery pq = ds.prepare(q);

        
        System.out.println("#Processing SurveyedLocales");
        if (!deleteDuplicates) {
            System.out.println(" (report only)");
        }

        String lastIdentifier = "";
        Long lastId = Long.valueOf(0);
        Date lastCre = null;
        int count = 0;
        List<Long> lastContrib = null;
        String lastContribStr = "";
        
        int slCount = 0;
        
        for (Entity sl : pq.asIterable(FetchOptions.Builder.withChunkSize(500))) {
            slCount++;
            
            Long id = (Long) sl.getKey().getId();
            String identifier = (String) sl.getProperty("identifier");
            if (identifier == null) identifier = "";
            Date cre = (Date) sl.getProperty("createdDateTime");
            @SuppressWarnings("unchecked")
            List<Long> contrib = (List<Long>) sl.getProperty("surveyInstanceContrib");
            String contribStr = "";
            if (contrib != null) {
                contribStr = contrib.toString();
            }            

            if (identifier.equals(lastIdentifier)) { //Duplicate!
                //Show what this is for

                if (contrib != null) {                    
                    contribStr = "["; //get more details
                    for (Long i : contrib) {
                        contribStr += "\n  *si"  + i + qasForInstance(ds,  i);
                    }
                    contribStr += "]";
                }            
                if (lastContrib != null) {                    
                    lastContribStr = "["; //get more details
                    for (Long i : lastContrib) {
                        lastContribStr += "\n  *si" + i + qasForInstance(ds,  i);
                    }
                    lastContribStr += "]";
                }            
                
                System.err.println(String.format("**SurveyedLocale #%d created %s identifier %s SG %s contributors %s", lastId, df.format(lastCre), lastIdentifier, " '" + surveyGroups.get(lastId) + "'", lastContribStr));
                System.err.println(String.format("**SurveyedLocale #%d created %s identifier %s SG %s contributors %s", id, df.format(cre), identifier, " '" + surveyGroups.get(id) + "'", contribStr));
                System.err.println("");
                if (contrib == null || (
                        contrib.size() <= 1
                        && !contribStr.equals(lastContribStr))) { //one contrib and not the same as the first one
                    toBeRemoved.add(sl);
                }
                count++;
            }

            lastIdentifier = identifier;
            lastId = id;
            lastCre = cre;
            lastContrib = contrib;
            lastContribStr = contribStr;
        }
        System.out.println("Found " + count);
        if (!toBeRemoved.isEmpty()) {
            final List<Key> terminate = new ArrayList<>();
            for (Entity sl: toBeRemoved) {
                
                Long id = sl.getKey().getId();
                System.out.println("Deleting SL #" + id);
                terminate.add(sl.getKey()); //SL itself
                
                //Now get any surveyalValues
                final Query qv = new Query("SurveyalValue").setFilter(new Query.FilterPredicate("surveyedLocaleId", FilterOperator.EQUAL, id));
                final PreparedQuery pqv = ds.prepare(qv);
                for (Entity sv : pqv.asIterable(FetchOptions.Builder.withChunkSize(500))) {
                    System.out.print(" SV #" + sv.getKey().getId());
                    terminate.add(sv.getKey());
                }
                System.out.println("");
                                
                //Now get any contributing surveyInstances
                final Query qi = new Query("SurveyInstance").setFilter(new Query.FilterPredicate("surveyedLocaleId", FilterOperator.EQUAL, id));
                final PreparedQuery pqi = ds.prepare(qi);
                for (Entity si : pqi.asIterable(FetchOptions.Builder.withChunkSize(500))) {
                    System.out.println("  Deleting SI # and QAS" + si.getKey().getId());
                    terminate.add(si.getKey());
                    //Now get the QASs for this SI
                    terminate.addAll(qasForInstanceK(ds, si.getKey().getId()));
                }
                
            }
            System.out.println("Scanned " + slCount + " SLs");
            if (deleteDuplicates) {
                System.out.println("Deleting " + terminate.size() + " entities ( " + toBeRemoved.size() + ")" + count + " pairs");
                ds.delete(terminate);
            } else {
                System.out.println("Not deleting " + terminate.size() + " entities ( " + toBeRemoved.size() + ")" + count + " pairs");
            }
        }
    }
    
    private List<Long> qasForInstance(DatastoreService ds, Long siId) {
        final Query qasq = new Query("QuestionAnswerStore").setFilter(new Query.FilterPredicate("surveyInstanceId", FilterOperator.EQUAL, siId));
        final PreparedQuery pqasq = ds.prepare(qasq);
        List<Long> result = new ArrayList<>();
        for (Entity qa : pqasq.asIterable(FetchOptions.Builder.withChunkSize(500))) {
            result.add(qa.getKey().getId());
        }
        return result;
    }
    
    private List<Key> qasForInstanceK(DatastoreService ds, Long siId) {
        final Query qasq = new Query("QuestionAnswerStore").setFilter(new Query.FilterPredicate("surveyInstanceId", FilterOperator.EQUAL, siId));
        final PreparedQuery pqasq = ds.prepare(qasq);
        List<Key> result = new ArrayList<>();
        for (Entity qa : pqasq.asIterable(FetchOptions.Builder.withChunkSize(500))) {
            result.add(qa.getKey());
        }
        return result;
    }
    
      
    private void processSurveyGroups(DatastoreService ds) {

        System.out.println("#Processing SurveyGroups");

        final Query survey_q = new Query("SurveyGroups");
        final PreparedQuery survey_pq = ds.prepare(survey_q);

        for (Entity s : survey_pq.asIterable(FetchOptions.Builder.withChunkSize(500))) {

            Long surveyId = s.getKey().getId();
            String surveyGroupName = (String) s.getProperty("name");
            String type = (String) s.getProperty("projectType");
            if (type != null && type.contentEquals("PROJECT")) {
                surveyGroups.put(surveyId,surveyGroupName);
            }
        }
    }

}
