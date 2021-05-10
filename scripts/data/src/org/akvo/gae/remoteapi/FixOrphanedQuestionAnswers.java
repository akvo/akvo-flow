package org.akvo.gae.remoteapi;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.repackaged.org.apache.commons.io.FileUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import static org.akvo.gae.remoteapi.DataUtils.batchDelete;

public class FixOrphanedQuestionAnswers implements Process {

    @Override
    public void execute(DatastoreService ds, String[] args) throws Exception {
        String formId = args[0];
        System.out.printf("Will do cleanup for form %s\n", formId);

        long formId1 = Long.parseLong(formId);
        ds.delete(KeyFactory.createKey("QuestionAnswerStore", 119950001));
        List<Entity> questionAnswers = fetchQuestionAnswers(ds, formId1);
        Map<Long, List<Key>> mappedQAByInstanceId = mapQuestionAnswers(questionAnswers);
        System.out.printf("Found %d questionAnswersStore in total\n", questionAnswers.size());
        List<Key> questionAnswersToDelete = getQuestionAnswersToDelete(ds, mappedQAByInstanceId, formId1);
        System.out.printf("Found %d question answers to delete\n", questionAnswersToDelete.size());

        if (questionAnswersToDelete.size() > 0) {
            System.out.printf("Found a total of %d orphaned QuestionAnswerStore that need cleanup\n", questionAnswersToDelete.size());
            boolean doIt = false;
            for (String arg : args) {
                if (arg.equalsIgnoreCase("--doit")) {
                    doIt = true;
                    break;
                }
            }

            if (doIt) {
                batchDelete(ds, questionAnswersToDelete);
            }
        } else {
            System.out.println("No orphaned QuestionAnswerStore found");
        }
    }

    private Map<Long, List<Key>> mapQuestionAnswers(List<Entity> questionAnswers) {
        Map<Long, List<Key>> mappedQAByInstanceId = new HashMap<>();
        for (Entity e: questionAnswers) {
            Long surveyInstanceId = (Long) e.getProperty("surveyInstanceId");
            Key qaKey = e.getKey();
            if (mappedQAByInstanceId.get(surveyInstanceId) == null) {
                List<Key> keys = new ArrayList<>();
                keys.add(qaKey);
                mappedQAByInstanceId.put(surveyInstanceId, keys);
            } else {
                mappedQAByInstanceId.get(surveyInstanceId).add(qaKey);
            }
        }
        return mappedQAByInstanceId;
    }

    private List<Entity> fetchQuestionAnswers(DatastoreService ds, long formId) {
        Query.Filter f = new Query.FilterPredicate("surveyId", Query.FilterOperator.EQUAL, formId);
        Query q = new Query("QuestionAnswerStore").setFilter(f);
        PreparedQuery pq = ds.prepare(q);
        return pq.asList(FetchOptions.Builder.withChunkSize(1000));
    }

    private List<Key> getQuestionAnswersToDelete(DatastoreService ds, Map<Long, List<Key>> questionAnswers, long formId1) {
        List<Key> questionAnswersToDelete = new ArrayList<>();
        if (questionAnswers == null || questionAnswers.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        Set<Long> surveyInstanceIds = questionAnswers.keySet();
        System.out.printf("Have %d survey instances\n", surveyInstanceIds.size());
        Set<Long> surveyInstancesFound = fetchSurveyInstances(ds, formId1);
        surveyInstanceIds.removeAll(surveyInstancesFound);
        System.out.printf("Have %d survey instances that have been deleted \n", surveyInstanceIds.size());
        for (Long instanceId: surveyInstanceIds) {
            System.out.printf("SurveyInstance %d has been deleted\n", instanceId);
            questionAnswersToDelete.addAll(questionAnswers.get(instanceId));
        }
        return questionAnswersToDelete;
    }

    private Set<Long> fetchSurveyInstances(DatastoreService ds, long surveyId) {
        Query.Filter f1 = new Query.FilterPredicate("surveyId", Query.FilterOperator.EQUAL, surveyId);
        Query q = new Query("SurveyInstance");
        q.setFilter(f1).setKeysOnly();
        PreparedQuery pq = ds.prepare(q);
        List<Entity> entities = pq.asList(FetchOptions.Builder.withChunkSize(1000));
        System.out.printf("Found %d instances\n", entities.size());
        Set<Long> existingSurveyInstances = new HashSet<>();
        for (Entity e : entities) {
            existingSurveyInstances.add(e.getKey().getId());
        }
        return existingSurveyInstances;
    }
}
