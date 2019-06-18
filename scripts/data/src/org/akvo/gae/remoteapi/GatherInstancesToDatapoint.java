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
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;

/*
 * - Gather all form instances under the datapoint with the same identifier
 */
public class GatherInstancesToDatapoint implements Process {

    boolean doChange = false;
    boolean ignoreSurvey = false;
    final List<Entity> toBeSaved = new ArrayList<>();
    Long dpId;

    @Override
    public void execute(DatastoreService ds, String[] args) throws Exception {

        for (int i = 0; i < args.length; i++) {
            if (args[i].equalsIgnoreCase("--doit")) {
                doChange = true;
            } else if (args[i].equalsIgnoreCase("--ignoresurvey")) {
                ignoreSurvey = true;
            } else {
                dpId = Long.parseLong(args[i]);
            }
        }

        Key k = KeyFactory.createKey("SurveyedLocale", dpId);
        Entity datapoint = ds.get(k);
        String identifier = (String) datapoint.getProperty("identifier");
        @SuppressWarnings("unchecked")
        List<Long> contrib = (List<Long>) datapoint.getProperty("surveyInstanceContrib");
        Long dpSurvey = (Long) datapoint.getProperty("surveyGroupId");


        //Now re-parent any form instances with the same identifier
        final Query qi = new Query("SurveyInstance").setFilter(new Query.FilterPredicate("surveyedLocaleIdentifier", FilterOperator.EQUAL, identifier));
        final PreparedQuery pqi = ds.prepare(qi);
        for (Entity fi : pqi.asIterable(FetchOptions.Builder.withChunkSize(500))) {
            Long oldParent = (Long) fi.getProperty("surveyedLocaleId");
            Long fiForm = (Long) fi.getProperty("surveyId");
            Long fiSurvey = surveyOfForm(ds, fiForm);

            if (!ignoreSurvey && fiSurvey != dpSurvey) {
                System.out.println("Wrong survey for FI #" + fi.getKey().getId());
                continue;
            }
            if (!dpId.equals(oldParent)) {
                System.out.println("Reparenting FI #" + fi.getKey().getId());
                fi.setProperty("surveyedLocaleId", dpId);
                toBeSaved.add(fi);
                if (contrib == null) {
                    contrib = new ArrayList<>();
                }
                contrib.add(fi.getKey().getId());
            }
        }

        if (toBeSaved.size() > 0) {
            datapoint.setProperty("surveyInstanceContrib", contrib);
            toBeSaved.add(datapoint);
        }

        if (doChange) {
            System.out.println("Changing " + toBeSaved.size() + " entities");
            DataUtils.batchSaveEntities(ds, toBeSaved);
        } else {
            System.out.println("Not changing " + toBeSaved.size() + " entities");
        }
    }

    private Long surveyOfForm(final DatastoreService ds, final Long formId) {
        if (formId == 0) {
            System.out.println(String.format(" ##Error! formId is 0"));
            return 0L;
        }
        Key k = KeyFactory.createKey("Survey", formId);
        Entity form;
        try {
            form = ds.get(k);
        } catch (EntityNotFoundException e) {
            return -1L;
        }
        Long surveyId = (Long) form.getProperty("surveyGroupId");
        //Check that it exists
        Key k2 = KeyFactory.createKey("SurveyGroup", surveyId);
        Entity survey;
        try {
            survey = ds.get(k2);
        } catch (EntityNotFoundException e) {
            return -2L;
        }
        return surveyId;
    }

}
