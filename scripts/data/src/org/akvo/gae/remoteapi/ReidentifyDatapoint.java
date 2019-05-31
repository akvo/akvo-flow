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
import java.util.UUID;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;

/*
 * - Give a datapoint a new (unique!) identifier, and propagate it to dependent form instances
 */
public class ReidentifyDatapoint implements Process {

    boolean doChange = false;
    final List<Entity> toBeSaved = new ArrayList<>();
    Long dpId;

    @Override
    public void execute(DatastoreService ds, String[] args) throws Exception {

        for (int i = 0; i < args.length; i++) {
            if (args[i].equalsIgnoreCase("--doit")) {
                doChange = true;
            } else {
                dpId = Long.parseLong(args[i]);
            }
        }

        Key k = KeyFactory.createKey("SurveyedLocale", dpId);
        Entity datapoint = ds.get(k);
        String oldIdentifier = (String) datapoint.getProperty("identifier");
        String newIdentifier = "";
        int tries = 0;
        do {
            if (++tries >= 100) { //stop if hopeless
                System.out.println("Giving up after 100 tries!");
                return;
            }
            newIdentifier = generateBase32Uuid();  //Unique?
        } while (datapointsWithIdentifier(ds, newIdentifier).size() > 0
                || formInstancesWithIdentifier(ds, newIdentifier).size() > 0 );

        System.out.println(String.format("Reidentifying DP %d from %s to %s",
                dpId,
                oldIdentifier,
                newIdentifier));

        datapoint.setProperty("identifier", newIdentifier);
        toBeSaved.add(datapoint); //The datapoint itself

        //Now change any contributing surveyInstances, based on their parent pointer
        final Query qi = new Query("SurveyInstance").setFilter(new Query.FilterPredicate("surveyedLocaleId", FilterOperator.EQUAL, dpId));
        final PreparedQuery pqi = ds.prepare(qi);
        for (Entity si : pqi.asIterable(FetchOptions.Builder.withChunkSize(500))) {
            System.out.println("  Reindentifying SI #" + si.getKey().getId());
            si.setProperty("surveyedLocaleIdentifier", newIdentifier);
            toBeSaved.add(si);
        }

        if (doChange) {
            System.out.println("Changing " + toBeSaved.size() + " entities");
            DataUtils.batchSaveEntities(ds, toBeSaved);
        } else {
            System.out.println("Not changing " + toBeSaved.size() + " entities");
        }
    }


    private List<Key> datapointsWithIdentifier(DatastoreService ds, String identitifier) {
        final Query qasq = new Query("SurveyedLocale")
                .setFilter(new Query.FilterPredicate("identifier", FilterOperator.EQUAL, identitifier))
                .setKeysOnly();
        final PreparedQuery pqasq = ds.prepare(qasq);
        List<Key> result = new ArrayList<>();
        for (Entity qa : pqasq.asIterable(FetchOptions.Builder.withChunkSize(500))) {
            result.add(qa.getKey());
        }
        return result;
    }

    private List<Key> formInstancesWithIdentifier(DatastoreService ds, String identitifier) {
        final Query qasq = new Query("SurveyInstance")
                .setFilter(new Query.FilterPredicate("surveyedLocaleIdentifier", FilterOperator.EQUAL, identitifier))
                .setKeysOnly();
        final PreparedQuery pqasq = ds.prepare(qasq);
        List<Key> result = new ArrayList<>();
        for (Entity qa : pqasq.asIterable(FetchOptions.Builder.withChunkSize(500))) {
            result.add(qa.getKey());
        }
        return result;
    }

    //Copied from DataProcessorRestServlet
    public static String generateBase32Uuid() {
        final String uuid = UUID.randomUUID().toString();
        String strippedUUID = (uuid.substring(0, 13) + uuid.substring(24, 27)).replace("-", "");
        String result = null;
        try {
            Long id = Long.parseLong(strippedUUID, 16);
            result = Long.toString(id, 32).replace("l", "w").replace("o", "x").replace("i", "y");
        } catch (NumberFormatException e) {
            // if we can't create the base32 UUID string, return the original uuid.
            result = uuid;
        }

        // insert dashes for readability
        return String.format("%s-%s-%s", result.substring(0, 4), result.substring(4, 8), result.substring(8));
    }

}
