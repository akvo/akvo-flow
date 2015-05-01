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
import java.util.Date;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

public class InstanceStats implements Process {

	@SuppressWarnings("unused")
	@Override
	public void execute(DatastoreService ds, String[] args) throws Exception {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
		Date jan1 = df.parse("2014-01-01T23:59:59+00:00");
		Date dec31 = df.parse("2014-12-31T23:59:59+00:00");
		String instance = args[0];
		int chunkSize = 10000;

		Filter asOfJan = new FilterPredicate("createdDateTime", FilterOperator.LESS_THAN_OR_EQUAL, jan1);
		Filter asOfDec = new FilterPredicate("createdDateTime", FilterOperator.LESS_THAN_OR_EQUAL, dec31);
		
		Query janFI = new Query("SurveyInstance").setFilter(asOfJan).setKeysOnly();
		Query decFI = new Query("SurveyInstance").setFilter(asOfDec).setKeysOnly();

		long janFICount = 0;
		long decFICount = 0;

		for (Entity fi : ds.prepare(janFI).asIterable(FetchOptions.Builder.withChunkSize(chunkSize))) {
			janFICount++;
		}

		for (Entity fi : ds.prepare(decFI).asIterable(FetchOptions.Builder.withChunkSize(chunkSize))) {
			decFICount++;
		}

		Query janDP = new Query("SurveyedLocale").setFilter(asOfJan).setKeysOnly();
		Query decDP = new Query("SurveyedLocale").setFilter(asOfDec).setKeysOnly();

		long janDPCount = 0;
		long decDPCount = 0;

		for (Entity dp : ds.prepare(janDP).asIterable(FetchOptions.Builder.withChunkSize(chunkSize))) {
			janDPCount++;
		}
		
		for (Entity dp : ds.prepare(decDP).asIterable(FetchOptions.Builder.withChunkSize(chunkSize))) {
			decDPCount++;
		}

		String out = String.format("%s,%s,%s,%s,%s", instance, janFICount, decFICount, janDPCount, decDPCount);

		System.out.println(out);
	}
}
