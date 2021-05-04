package org.akvo.gae.remoteapi;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.repackaged.org.apache.commons.io.FileUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static org.akvo.gae.remoteapi.DataUtils.batchDelete;

public class FixOrphanedSubmissions implements Process {

    /**
     * if true, delete the orphaned instances
     * if false, only list
     */
    private boolean doIt = false;

    private final List<Key> surveyInstances = new ArrayList<>();

    @Override
    public void execute(DatastoreService ds, String[] args) throws Exception {
        //List orphaned SurveyInstances where their registration forms have been deleted
        //if doIt is true, remove them
        String instanceName = args[0];
        System.out.printf("#Instance %s\n", instanceName);
        for (String arg : args) {
            if (arg.equalsIgnoreCase("--doit")) {
                doIt = true;
                break;
            }
        }
        String pathname = "/tmp/" + instanceName + "-orphaned-instances.csv";
        final File f = new File(pathname);

        FileUtils.write(f, "FormId,DatapointIdentifier,OrphanedMonitoringInstanceId\n");
        List<Long> surveyGroupIds = getMonitoringSurveys(ds);
        System.out.printf("Found %d monitoring surveys\n", surveyGroupIds.size());
        List<Entity> dataPoints = getDataPoints(ds, surveyGroupIds);
        System.out.printf("Found %d monitoring datapoints\n", dataPoints.size());

        Set<Long> dataPointIds = dataPoints
                .stream()
                .map(entity -> entity.getKey().getId())
                .collect(Collectors.toSet());

        Map<Long, Set<Long>> mappedBySurveyId = new HashMap<>();
        for (Entity entity: dataPoints) {
            Long surveyId = (Long) entity.getProperty("creationSurveyId");
            Long dataPointId = entity.getKey().getId();
            if (mappedBySurveyId.get(surveyId) == null) {
                Set<Long> datapoints = new HashSet<>();
                datapoints.add(dataPointId);
                mappedBySurveyId.put(surveyId, datapoints);
            } else {
                mappedBySurveyId.get(surveyId).add(dataPointId);
            }
        }

        Set<Long> dataPointsFound = fetchExistingDataPointInstances(ds, mappedBySurveyId);
        dataPointIds.removeAll(dataPointsFound);

        List<Entity> instances = getSurveyInstancesToDelete(ds, new ArrayList<>(dataPointIds));

        final StringBuilder sb = new StringBuilder();
        if (instances != null) {
            for (Entity instance : instances) {
                Key key = instance.getKey();
                surveyInstances.add(key);
                sb.append(
                        String.format("%s,%s,%s", instance.getProperty("surveyId"), instance.getProperty("surveyedLocaleIdentifier"), key.getId()))
                        .append("\n");
            }
            FileUtils.write(f, sb.toString(), true);
        }
        if (surveyInstances.size() > 0) {
            System.out.printf("Found a total of %d orphaned instances\n", surveyInstances.size());
            if (doIt) {
                batchDelete(ds, surveyInstances);
            }
        } else {
            System.out.println("No orphaned SurveyInstances found");
        }
    }

    private Set<Long> fetchExistingDataPointInstances(DatastoreService ds, Map<Long, Set<Long>> mappedBySurveyId) {
        Set<Long> dataPointIds = new HashSet<>();
        Set<Entity> allDataPoints = new HashSet<>();
        for (Long surveyId : mappedBySurveyId.keySet()) {
            Query.Filter f = new Query.FilterPredicate("surveyId", Query.FilterOperator.EQUAL, surveyId);
            Query.Filter f2 = new Query.FilterPredicate("surveyedLocaleId", Query.FilterOperator.IN, mappedBySurveyId.get(surveyId));
            Query instanceQuery = new Query("SurveyInstance").setFilter(Query.CompositeFilterOperator.and(f2, f));
            List<Entity> entities = ds.prepare(instanceQuery).asList(FetchOptions.Builder.withChunkSize(1000));
            if (entities != null) {
                allDataPoints.addAll(entities);
            }
        }
        for (Entity e: allDataPoints) {
            dataPointIds.add((Long) e.getProperty("surveyedLocaleId"));
        }
        return dataPointIds;
    }

    private List<Entity> getSurveyInstances(DatastoreService ds, List<Long> surveyedLocaleIds) {
        if (!surveyedLocaleIds.isEmpty()) {
            Query.Filter f1 = new Query.FilterPredicate("surveyedLocaleId", Query.FilterOperator.IN, surveyedLocaleIds);
            Query q = new Query("SurveyInstance");
            q.setFilter(f1);
            PreparedQuery pq = ds.prepare(q);
            return pq.asList(FetchOptions.Builder.withChunkSize(1000));
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    private List<Long> getMonitoringSurveys(DatastoreService ds) {
        Query.Filter f = new Query.FilterPredicate("monitoringGroup", Query.FilterOperator.EQUAL, true);
        Query q = new Query("SurveyGroup").setFilter(f).setKeysOnly();
        PreparedQuery pq = ds.prepare(q);
        List<Entity> entities = pq.asList(FetchOptions.Builder.withChunkSize(1000));
        List<Long> surveyIds = new ArrayList<>(entities.size());
        for (Entity e : entities) {
            surveyIds.add(e.getKey().getId());
        }
        return surveyIds;
    }

    private List<Entity> getDataPoints(DatastoreService ds, List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        Query.Filter f1 = new Query.FilterPredicate("surveyGroupId", Query.FilterOperator.IN, ids);
        Query.Filter f2 = new Query.FilterPredicate("creationSurveyId", Query.FilterOperator.NOT_EQUAL, null);
        Query q = new Query("SurveyedLocale");
        q.setFilter(Query.CompositeFilterOperator.and(f1, f2));
        PreparedQuery pq = ds.prepare(q);
        return pq.asList(FetchOptions.Builder.withChunkSize(1000));
    }
}
