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

import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.List;

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

/*
 * - Checks that all questions are in the questiongroup of their survey
 */
public class CheckSurveyStructure implements Process {

    //    private static String ERR_MSG = "Unable to hide SurveyedLocale [%s], reason: %s";

    @Override
    public void execute(DatastoreService ds, String[] args) throws Exception {

	int e1 = 0, e2 = 0, e3 = 0, e4 = 0, q = 0;

        System.out.println("Processing Question Groups");

	Map<Long,Long>qgToSurvey = new HashMap<>();
	
        final Query group_q = new Query("QuestionGroup");
        final PreparedQuery group_pq = ds.prepare(group_q);

        for (Entity g : group_pq.asIterable(FetchOptions.Builder.withChunkSize(500))) {

            Long questionGroupId = g.getKey().getId();
            Long questionGroupSurvey = (Long) g.getProperty("surveyId");
            String questionGroupName = (String) g.getProperty("name");
	    if (questionGroupId == null) {
		System.out.printf("ERR group %d '%s'not in a survey!\n",questionGroupId,questionGroupName);
		e1++;
	    } else {
		qgToSurvey.put(questionGroupId, questionGroupSurvey);
		//System.out.printf(" group %d -> survey %d\n",questionGroupId, questionGroupSurvey);
	    }
        }

        System.out.println("Processing Questions");

        final Query qq = new Query("Question");
        final PreparedQuery qpq = ds.prepare(qq);

        for (Entity sl : qpq.asIterable(FetchOptions.Builder.withChunkSize(500))) {

            Long questionId = sl.getKey().getId();
            Long questionSurvey = (Long) sl.getProperty("surveyId");
            Long questionGroup = (Long) sl.getProperty("questionGroupId");
            String questionText = (String) sl.getProperty("text");

	    if (questionGroup == null || questionSurvey == null) { //check for no qg or survey
		if (questionGroup == null) { //check for no qg
		    System.out.printf("ERR: Question %d '%s',survey %d, group %d!\n", questionId, questionText, questionSurvey, questionGroup);
		}
		e2++;
	    } else { //TODO: check for wrong survey/qg
		Long questionGroupSurvey = (Long) qgToSurvey.get(questionGroup);
		if (! questionSurvey.equals(questionGroupSurvey)) {
		    System.out.printf("ERR: Question %d '%s' not in same survey as group!\n", questionId, questionText);
		    e3++;
		} else {
		    q++;
		}
	    } 
	}
        System.out.printf("Found %d good, %d surveyless Question Groups\n",qgToSurvey.size(),e1);
        System.out.printf("Found %d good, %d survey/groupless, %d kidnapped Questions\n",q,e2,e3);


    }
}
