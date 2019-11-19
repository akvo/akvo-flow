package org.akvo.gae.remoteapi;

import com.google.appengine.api.datastore.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UnAssignUnPublishedForms implements Process {

    /**
     * if true, make the changes
     * if false, only list
     */
    private boolean doIt = false;

    private List<Long> forms = new ArrayList<>();
    private List<Entity> surveyAssignments = new ArrayList<>();
    private List<Entity> surveyAssignmentsToSave = new ArrayList<>();
    private List<Entity> jobQueues = new ArrayList<>();
    private List<Key> jobQueuesToDelete = new ArrayList<>();

    @Override
    public void execute(DatastoreService ds, String[] args) throws Exception {
        //List all unpublished forms with version 1
        //update both the DA and DSJQ

        for (int i = 0; i < args.length; i++) {
            System.out.printf("#Argument %d: %s\n", i, args[i]);
            if (args[i].equalsIgnoreCase("--doit")) {
                doIt = true;
            }
        }

        retrieveV1UnPublishedForms(ds);
        if (forms.size() > 0) {
            retrieveSurveyAssignments(ds);
            retrieveJobQueuesAssignments(ds);
            if (doIt) {
                fixSurveyAssignments(ds);
                fixJobQueues(ds);
            }
        } else {
            System.out.println("No unpublished forms found, nothing to do");
        }
    }

    private void fixJobQueues(DatastoreService ds) {
        if (jobQueues.size() > 0) {
            for (Entity e : jobQueues) {
                Long surveyId = (Long) e.getProperty("surveyID");
                if (forms.contains(surveyId)) {
                    jobQueuesToDelete.add(e.getKey());
                }
            }
            System.out.println("Found " + jobQueuesToDelete.size() + " DeviceSurveyJobQueue to delete");
            if (jobQueuesToDelete.size() > 0) {
                ds.delete(jobQueuesToDelete);
                System.out.println("DeviceSurveyJobQueue deleted");
            }
        } else {
            System.out.println("No DeviceSurveyJobQueue found to fix");
        }
    }

    @SuppressWarnings("unchecked")
    private void fixSurveyAssignments(DatastoreService ds) {
        if (surveyAssignments.size() > 0) {
            for (Entity e : surveyAssignments) {
                ArrayList<Long> surveyIds = ((ArrayList<Long>) e.getProperty("formIds"));
                ArrayList<Long> fixedFormIds = new ArrayList<>();
                for (Long id : surveyIds) {
                    if (!forms.contains(id)) {
                        fixedFormIds.add(id);
                    }
                }
                if (fixedFormIds.size() != surveyIds.size()) {
                    e.setProperty("formIds", fixedFormIds);
                    surveyAssignmentsToSave.add(e);
                }
            }
            System.out.println("Found " + surveyAssignmentsToSave.size() + " SurveyAssignment to modify");
            if (surveyAssignmentsToSave.size() > 0) {
                ds.put(surveyAssignmentsToSave);
                System.out.println("Fixed SurveyAssignment");
            }
        } else {
            System.out.println("No SurveyAssignment found to fix");
        }
    }

    private void retrieveJobQueuesAssignments(DatastoreService ds) {
        final Query q = new Query("DeviceSurveyJobQueue");
        //Exclude old assignments
        final Query.Filter upToDateFilter = new Query.FilterPredicate("effectiveEndDate",
                Query.FilterOperator.GREATER_THAN_OR_EQUAL, new Date());
        q.setFilter(upToDateFilter);
        final PreparedQuery pq = ds.prepare(q);

        List<Entity> entities = pq.asList(FetchOptions.Builder.withChunkSize(500));
        jobQueues.addAll(entities);
        System.out.println("Found " + jobQueues.size() + " up to date assignments");
    }

    private void retrieveSurveyAssignments(DatastoreService ds) {
        final Query q = new Query("SurveyAssignment");
        //Exclude old assignments
        final Query.Filter upToDateFilter = new Query.FilterPredicate("endDate",
                Query.FilterOperator.GREATER_THAN_OR_EQUAL, new Date());
        q.setFilter(upToDateFilter);
        final PreparedQuery pq = ds.prepare(q);

        List<Entity> entities = pq.asList(FetchOptions.Builder.withChunkSize(500));
        surveyAssignments.addAll(entities);
        System.out.println("Found " + surveyAssignments.size() + " up to date assignments");
    }

    private void retrieveV1UnPublishedForms(final DatastoreService ds) {
        final Query q = new Query("Survey");
        final Query.Filter unPublishedFilter = new Query.FilterPredicate("status",
                Query.FilterOperator.EQUAL, "NOT_PUBLISHED");
        final Query.Filter version1Filter = new Query.FilterPredicate("version",
                Query.FilterOperator.EQUAL, 1.0);
        q.setFilter(Query.CompositeFilterOperator.and(unPublishedFilter, version1Filter)).setKeysOnly();
        final PreparedQuery pq = ds.prepare(q);

        //Loop over forms
        Iterable<Entity> entities = pq.asIterable(FetchOptions.Builder.withChunkSize(500));
        for (Entity form : entities) {
            Long id = form.getKey().getId();
            forms.add(id);
        }
        System.out.println("Found " + forms.size() + " unpublished v1 forms");
    }
}
