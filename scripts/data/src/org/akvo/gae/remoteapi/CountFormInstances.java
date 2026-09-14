/*
 *  Copyright (C) 2017,2019 Stichting Akvo (Akvo Foundation)
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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.repackaged.org.apache.commons.io.FileUtils;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 */
public class CountFormInstances implements Process {

  private Map<Long, String> sgName = new HashMap<>();
  private Map<Long, String> sgType = new HashMap<>();
  private Map<Long, Long> sgParents = new HashMap<>();

  private Map<Long, String> surveyNames = new HashMap<>();
  // private Map<Long, String> surveyDates = new HashMap<>();
  private Map<Long, Long> surveyParents = new HashMap<>();
  private Map<Long, Long> surveyCounts = new HashMap<>();
  private Map<Long, List<Long>> surveyToGroups = new HashMap<>();

  private String iName = "";

  @Override
  public void execute(DatastoreService ds, String[] args) throws Exception {

    if (args.length != 3) {
      throw new IllegalArgumentException("Invalid arguments");
    }

    iName = args[0];
    System.out.printf("Processing %s\n", iName);

    ZoneId gmtZone = ZoneId.of("GMT");
    Date startDate = Date.from(LocalDate.parse(args[1]).atStartOfDay(gmtZone).toInstant());
    Date endDate = Date.from(LocalDate.parse(args[2]).atTime(LocalTime.MAX).atZone(gmtZone).toInstant());

    String fileName = String.format("/tmp/form-instance-counts_%s.csv", iName);
    final File file = new File(fileName);
    final StringBuffer sb = new StringBuffer();
    FileUtils.write(
        file,
        "Instance Name, Form ID, Form Name, Survey Name, Total Form Instances, Path",
        true);
    sb.append("\n");
    fetchSurveyGroups(ds);
    fetchSurveys(ds, startDate, endDate);
    drawSurveyGroupsIn(0L, "", sb);
    FileUtils.write(file, sb.toString(), true);
    System.out.printf("%s form instance counts written\n # filename: %s\n", iName, fileName);
  }

  private void drawSurveyGroupsIn(Long parent, String parentName, StringBuffer sb) {
    for (Long sg : sgParents.keySet()) {
      if (sgParents.get(sg).equals(parent)) {
        if (sgType.get(sg).equals("PROJECT")) {
          drawFormsIn(sg, sgName.get(sg), parentName, sb);
        } else {
          if (parentName != "") {
            parentName += " > ";
          }
          parentName += sgName.get(sg);
          drawSurveyGroupsIn(sg, parentName, sb);
        }
      }
    }
  }

  private void drawFormsIn(Long parent, String surveyName, String parentName, StringBuffer sb) {
    for (Long survey : surveyParents.keySet()) {
      if (surveyParents.get(survey).equals(parent)) {
        sb.append(
                String.format(
                    "%s,%d,%s,%s,%d,%s",
                    iName,
                    survey,
                    surveyName,
                    surveyNames.get(survey),
                    surveyCounts.get(survey),
                    parentName))
            .append("\n");
      }
    }
  }

  private void fetchSurveyGroups(DatastoreService ds) {

    final Query q = new Query("SurveyGroup");
    final PreparedQuery pq = ds.prepare(q);

    for (Entity g : pq.asIterable(FetchOptions.Builder.withChunkSize(500))) {

      Long surveyGroupId = g.getKey().getId();
      Long parentId = (Long) g.getProperty("parentId");
      String type = (String) g.getProperty("projectType");
      String name = (String) g.getProperty("name");
      if (parentId == null) {
      } else {
        sgParents.put(surveyGroupId, parentId);
        sgName.put(surveyGroupId, name);
        sgType.put(surveyGroupId, type);
      }
    }
  }

  private void fetchSurveys(DatastoreService ds, Date startDate, Date endDate) throws Exception {

    final Query survey_q = new Query("Survey");
    final PreparedQuery survey_pq = ds.prepare(survey_q);

    for (Entity s : survey_pq.asIterable(FetchOptions.Builder.withChunkSize(500))) {

      Long surveyId = s.getKey().getId();
      String surveyName = (String) s.getProperty("name");
      Long surveyGroup = (Long) s.getProperty("surveyGroupId");
      if (surveyGroup != null) {

        Long count = countSurveyInstance(ds, surveyId, startDate, endDate);

        if (count > 0) {
          surveyCounts.put(surveyId, count);
          surveyNames.put(surveyId, surveyName);
          surveyParents.put(surveyId, surveyGroup);
          surveyToGroups.put(surveyId, new ArrayList<Long>());
        }
      }
    }
  }

  private Long countSurveyInstance(DatastoreService ds, Long surveyId, Date startDate, Date endDate) {
    List<Filter> filters = new ArrayList<Filter>();
    filters.add(new FilterPredicate("surveyId", FilterOperator.EQUAL, surveyId));
    filters.add(new FilterPredicate("collectionDate", FilterOperator.GREATER_THAN_OR_EQUAL, startDate));
    filters.add(new FilterPredicate("collectionDate", FilterOperator.LESS_THAN_OR_EQUAL, endDate));

    Query q = new Query("SurveyInstance").setFilter(CompositeFilterOperator.and(filters)).setKeysOnly();
    Long count = 0L;
    Iterable<Entity> entities = ds.prepare(q).asIterable(FetchOptions.Builder.withChunkSize(500));
    for (@SuppressWarnings("unused") Entity e : entities) {
      count++;
    }

    return count;
  }
}
