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
import java.util.List;
import java.util.Objects;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    /*    List<Entity> filteredDataPoints = dataPoints
                .stream()
                .filter(entity -> entity.getProperty("creationSurveyId") != null)
                .collect(Collectors.toList());
        List<Long> datapointsIds = filteredDataPoints
                .stream()
                .map(entity -> entity.getKey().getId())
                .collect(Collectors.toList());

        List<Long> dataPointIdsWithSurveyInstance = getRegistrationSurveyInstances(ds, filteredDataPoints);

        datapointsIds.removeAll(dataPointIdsWithSurveyInstance);

        List<Entity> instances = getSurveyInstances(ds, datapointsIds);*/

        List<Long> dataPointIdsToCleanup = dataPoints
                .stream()
                .filter(entity -> entity.getProperty("creationSurveyId") != null)
                .map(entity -> getDataPointIdToDelete(ds, entity))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        List<Entity> instances = getSurveyInstances(ds, dataPointIdsToCleanup);

        final StringBuilder sb = new StringBuilder();
        if (instances != null) {
            for (Entity instance : instances) {
                surveyInstances.add(instance.getKey());
                sb.append(
                        String.format("%s,%s,%s", instance.getProperty("surveyId"), instance.getProperty("surveyedLocaleIdentifier"), instance.getKey().getId()))
                        .append("\n");
            }
            FileUtils.write(f, sb.toString(), true);
        }
        if (surveyInstances.size() > 0) {
            System.out.printf("Found a total of %d orphaned instances\n", surveyInstances.size());
            if (doIt) {
                //batchDelete(ds, surveyInstances); for now we just count them
            }
        } else {
            System.out.println("No orphaned SurveyInstances found");
        }
    }

/*    private List<Long> getRegistrationSurveyInstances(DatastoreService ds, List<Entity> datapoints) {
        List<Long> dataPointsThatAreOk = new ArrayList<>();
        Query instanceQuery = new Query("SurveyInstance");
        List<Query.CompositeFilter> filters = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            Entity entity = datapoints.get(i);
            Query.Filter f2 = new Query.FilterPredicate("surveyedLocaleId", Query.FilterOperator.EQUAL, entity.getKey().getId());
            Query.Filter f = new Query.FilterPredicate("surveyId", Query.FilterOperator.EQUAL, (Long) entity.getProperty("creationSurveyId"));
            Query.CompositeFilter compositeFilter = Query.CompositeFilterOperator.and(f2, f);
           filters.add(compositeFilter);
        }
        System.out.println("Filters: " + filters.size());
        if (!filters.isEmpty()) {
            instanceQuery.setFilter(Query.CompositeFilterOperator.or(filters.toArray(new Query.CompositeFilter[0])));
            instanceQuery.setKeysOnly();
            List<Entity> entities = ds.prepare(instanceQuery).asList(FetchOptions.Builder.withChunkSize(1000));
            if (entities != null) {
                for (Entity e: entities) {
                    dataPointsThatAreOk.add(e.getKey().getId());
                }
            }
        }
        return dataPointsThatAreOk;
    }*/

    private List<Long> getRegistrationSurveyInstances(DatastoreService ds, List<Entity> datapoints) {
        List<Long> dataPointsThatAreOk = new ArrayList<>();
        Query instanceQuery = new Query("SurveyInstance");
        List<Long> dataPointIds = new ArrayList<>();
        List<Long> surveyIds = new ArrayList<>();
        for (Entity entity : datapoints) {
            dataPointIds.add(entity.getKey().getId());
            surveyIds.add((Long) entity.getProperty("creationSurveyId"));
        }

        Query.Filter f2 = new Query.FilterPredicate("surveyedLocaleId", Query.FilterOperator.IN, dataPointIds);
        Query.Filter f = new Query.FilterPredicate("surveyId", Query.FilterOperator.IN, surveyIds);
        Query.CompositeFilter compositeFilter = Query.CompositeFilterOperator.and(f2, f);
        instanceQuery.setFilter(compositeFilter);
        instanceQuery.setKeysOnly();
        List<Entity> entities = ds.prepare(instanceQuery).asList(FetchOptions.Builder.withChunkSize(1000));
        if (entities != null) {
            for (Entity e : entities) {
                dataPointsThatAreOk.add(e.getKey().getId());
            }
        }
        return dataPointsThatAreOk;
    }

    private Long getDataPointIdToDelete(DatastoreService ds, Entity entity) {
        long dataPointId = entity.getKey().getId();
        Entity registrationFormInstance = getRegistrationSurveyInstance(ds, (Long) entity.getProperty("creationSurveyId"), dataPointId);
        if (registrationFormInstance == null) {
            return dataPointId;
        }
        return null;
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

    private Entity getRegistrationSurveyInstance(DatastoreService ds, Long surveyId, Long dataPointId) {
        Query.Filter f2 = new Query.FilterPredicate("surveyedLocaleId", Query.FilterOperator.EQUAL, dataPointId);
        Query.Filter f = new Query.FilterPredicate("surveyId", Query.FilterOperator.EQUAL, surveyId);
        Query instanceQuery = new Query("SurveyInstance").setFilter(Query.CompositeFilterOperator.and(f2, f)).setKeysOnly();
        //unfortunately using asSingleInstance does not work here as sometimes more than one result is returned!
        List<Entity> entities = ds.prepare(instanceQuery).asList(FetchOptions.Builder.withLimit(1));
        return (entities == null || entities.isEmpty()) ? null : entities.get(0);
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
