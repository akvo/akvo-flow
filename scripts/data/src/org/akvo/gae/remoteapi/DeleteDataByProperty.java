/*
 *  Copyright (C) 2014-2015,2017 Stichting Akvo (Akvo Foundation)
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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

/*
 * DeleteDataByProperty <instance> <user> <p12 path> <surveyId> <SurveyInstanceProperty> <PropertyValue>
 * 
 */
public class DeleteDataByProperty implements Process {

	@Override
	public void execute(DatastoreService ds, String[] args) throws Exception {

		final Long surveyId = Long.parseLong(args[0]);
		final String property=args[1];
		final String pValue=args[2];
		deleteEntities(ds, surveyId,property,pValue);
	}

	private void deleteEntities(DatastoreService ds, Long surveyId,String property, String pValue) throws EntityNotFoundException {
		List<Key> surveyInstanceKeyList=getSurveyInstanceKeyList(ds,surveyId,property,pValue);
		List<Long> instanceIds=new ArrayList<Long>();
		Iterator<Key> idsItr=surveyInstanceKeyList.iterator();
		while(idsItr.hasNext()){
			instanceIds.add(idsItr.next().getId());
		}
		deleteKinds(ds,Arrays.asList("QuestionAnswerStore","SurveyalValue"),surveyId,instanceIds);
		Entity survey = ds.get(KeyFactory.createKey("Survey", surveyId));
		Long surveyGroupId = (Long) survey.getProperty("surveyGroupId");
		if (surveyGroupId != null) {
			deleteSurveyedLocale(ds, surveyGroupId,instanceIds);
		}
		ds.delete(surveyInstanceKeyList);

	}

	private void deleteKinds(DatastoreService ds, List<String> kinds, Long surveyId,List<Long> instanceIds) {
		Filter f=new FilterPredicate("surveyId", FilterOperator.EQUAL, surveyId);
		for(String kind: kinds){
			Query q = new Query(kind).setFilter(f);
			PreparedQuery pq = ds.prepare(q);
			List<Key> keys = new ArrayList<Key>();
			for (Entity e : pq.asList(FetchOptions.Builder.withChunkSize(500))) {
				if(instanceIds.contains(e.getProperty("surveyInstanceId"))){
					keys.add(e.getKey());
				}
			}
			System.out.println(String.format("%s - deleting %s enties - surveyGroupId = %s",kind,keys.size(), surveyId));
			ds.delete(keys);
		}
	}

	private List<Key> getSurveyInstanceKeyList(DatastoreService ds,Long surveyId, String property, String pValue){
		Filter f=new FilterPredicate("surveyId", FilterOperator.EQUAL, surveyId);
		Filter nf=new FilterPredicate(property, FilterOperator.EQUAL, pValue);
		CompositeFilter cf=CompositeFilterOperator.and(f,nf);
		Query q = new Query("SurveyInstance").setFilter(cf).setKeysOnly();
		PreparedQuery pq = ds.prepare(q);
		List<Key> keys = new ArrayList<Key>();
		for (Entity e : pq.asList(FetchOptions.Builder.withChunkSize(500))) {
			keys.add(e.getKey());
		}
		return keys;
	}

	@SuppressWarnings("unchecked")
	private static void deleteSurveyedLocale(DatastoreService ds, Long surveyGroupId, List<Long> instanceIds) {
		final Filter f = new FilterPredicate("surveyGroupId", FilterOperator.EQUAL, surveyGroupId);
		final Query q = new Query("SurveyedLocale").setFilter(f);
		final PreparedQuery pq = ds.prepare(q);
		final List<Key> keys = new ArrayList<Key>();
		for (Entity e : pq.asIterable(FetchOptions.Builder.withChunkSize(500))) {
			if(instanceIds.contains(e.getProperty("lastSurveyalInstanceId"))){
				keys.add(e.getKey());
			}
		}
		System.out.println(String.format("SurveyedLocale - deleting %s enties - surveyGroupId = %s",keys.size(), surveyGroupId));
		ds.delete(keys);       
	}

}
