/*
 *  Copyright (C) 2017-2018 Stichting Akvo (Akvo Foundation)
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

import static org.akvo.gae.remoteapi.DataUtils.batchSaveEntities;
import static org.akvo.gae.remoteapi.DataUtils.batchDelete;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;

/*
 * - Checks that all surveys, groups, questions and options are consistent
 */
public class ReorderQuestionGroup implements Process {

    private int orphanSurveys = 0, orphanGroups = 0, orphanQuestions = 0, unreachableQuestions = 0, orphanOptions = 0;
    private int goodQuestions = 0, goodOptions = 0;

    @Override
    public void execute(DatastoreService ds, String[] args) throws Exception {

        System.out.printf("#Arguments: questionGroupId.\n");
        for (int i = 0; i < args.length; i++) {
            //System.out.printf("#Argument %d: %s\n", i, args[i]);
/*            if (args[i].equalsIgnoreCase("FIX")) {
                fixSurveyPointers = true;
            }
            if (args[i].equalsIgnoreCase("GC")) {
                deleteOrphans = true;
            }
            */
        }

    	Long qgid = Long.parseLong(args[0]);
        processQuestions(ds, qgid);

        System.out.printf("#Questions:       %5d good, %4d groupless, %4d unreachable\n", goodQuestions, orphanQuestions, unreachableQuestions);

    }

    private void processGroups(DatastoreService ds) {

        System.out.println("#Processing Question Groups");

        final Query group_q = new Query("QuestionGroup");
        final PreparedQuery group_pq = ds.prepare(group_q);

        for (Entity g : group_pq.asIterable(FetchOptions.Builder.withChunkSize(500))) {

            Long questionGroupId = g.getKey().getId();
            Long questionGroupSurvey = (Long) g.getProperty("surveyId");
            String questionGroupName = (String) g.getProperty("name");
            if (questionGroupSurvey == null) {
                System.out.printf("#ERR group %d '%s' is not in a survey\n",
                        questionGroupId, questionGroupName);
                orphanGroups++;
            }
        }
    }

    private void processQuestions(DatastoreService ds, Long groupId) {
        System.out.println("#Processing Questions");
        final Filter f = new FilterPredicate("QuestionGroup", FilterOperator.EQUAL, groupId);
        final Query qq = new Query("Question").setFilter(f);//.addSort("order",SortDirection.ASCENDING);
        final PreparedQuery qpq = ds.prepare(qq);
        List<Entity> questionsToFix = new ArrayList<Entity>(); 

        Integer newOrder = 1;
        for (Entity q : qpq.asIterable(FetchOptions.Builder.withChunkSize(500))) {
        	Integer oldOrder = (Integer) q.getProperty("order");
            System.out.printf("# %d -> %d\n", oldOrder, newOrder);
            q.setProperty("order", newOrder++);

            questionsToFix.add(q);
        }
        System.out.printf("#Fixing %d Questions\n",questionsToFix.size());
        //batchSaveEntities(ds, questionsToFix);
    }

}
