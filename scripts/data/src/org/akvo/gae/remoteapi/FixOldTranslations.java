package org.akvo.gae.remoteapi;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FixOldTranslations implements Process {

    boolean fix = true; //set true to actually fix the translations

    @Override
    public void execute(DatastoreService ds, String[] args) throws Exception {
        List<Entity> translations = fetchBrokenTranslations(ds);
        List<Entity> updatedTranslations = translations.stream().map(it -> fixTranslation(ds, it)).filter(Objects::nonNull).collect(Collectors.toList());
        if (fix && !updatedTranslations.isEmpty()) {
            ds.put(updatedTranslations);
            System.out.println("Fixed " + updatedTranslations.size() + " broken translations");
        }
    }

    private Entity fixTranslation(DatastoreService ds, Entity t) {
        Entity fixed = null;
        String parentType = (String) t.getProperty("parentType");
        switch (parentType) {
            case "QUESTION_TEXT":
            case "QUESTION_NAME":
            case "QUESTION_DESC":
            case "QUESTION_TIP":
                fixed = fixQuestionTranslation(t, ds);
                break;
            case "QUESTION_GROUP_NAME":
            case "QUESTION_GROUP_DESC":
                fixed = fixQuestionGroupTranslation(t, ds);
                break;
            case "QUESTION_OPTION":
                fixed = fixQuestionOptionTranslation(t, ds);
                break;
            default:
                break;
        }
        return fixed;
    }

    private Entity fixQuestionGroupTranslation(Entity t, final DatastoreService ds) {
        System.out.println("Fixing QuestionGroup translation");
        Long parentId = (Long) t.getProperty("parentId");
        return fixTranslation(t, ds, parentId);
    }

    private Entity fixQuestionTranslation(Entity t, final DatastoreService ds) {
        System.out.println("Fixing Question translation");
        Long parentId = (Long) t.getProperty("parentId");
        if (parentId != null) {
            final Entity question = fetchQuestion(ds, parentId);
            if (question != null) {
                return fixTranslation(t, ds, (Long) question.getProperty("questionGroupId"));
            }
        }
        return null;
    }

    private Entity fixQuestionOptionTranslation(Entity t, final DatastoreService ds) {
        System.out.println("Fixing QuestionOption translation");
        Long parentId = (Long) t.getProperty("parentId");
        if (parentId != null) {
            final Query.Filter fs = new Query.FilterPredicate(Entity.KEY_RESERVED_PROPERTY,
                    Query.FilterOperator.EQUAL, KeyFactory.createKey("QuestionOption", parentId));
            final Query q = new Query("QuestionOption").setFilter(fs);
            Entity option = ds.prepare(q).asSingleEntity();
            if (option != null) {
                final Entity question = fetchQuestion(ds, (Long) option.getProperty("questionId"));
                if (question != null) {
                    return fixTranslation(t, ds, (Long) question.getProperty("questionGroupId"));
                }
            }
        }
        return null;
    }

    private Entity fixTranslation(Entity t, DatastoreService ds, Long groupId) {
        final Entity qGroup = fetchGroup(ds, groupId);
        if (qGroup != null) {
            t.setProperty("surveyId", qGroup.getProperty("surveyId"));
            t.setProperty("questionGroupId", groupId);
            return t;
        }
        return null;
    }

    private List<Entity> fetchBrokenTranslations(DatastoreService ds) {
        List<Entity> translations = new ArrayList<>();
        final Query q = new Query("Translation");
        final PreparedQuery pq = ds.prepare(q);
        final Query.Filter filterNullSurveyId = new Query.FilterPredicate("surveyId", Query.FilterOperator.EQUAL, null);
        final Query.Filter filterNullQuestionGroup = new Query.FilterPredicate("questionGroupId", Query.FilterOperator.EQUAL, null);
        q.setFilter(Query.CompositeFilterOperator.or(filterNullSurveyId, filterNullQuestionGroup));
        List<Entity> entities = pq.asList(FetchOptions.Builder.withChunkSize(500));
        translations.addAll(entities);
        System.out.println("Found " + translations.size() + " broken translations");
        return translations;
    }

    private Entity fetchGroup(DatastoreService ds, Long groupId) {
        if (groupId == null) {
            return null;
        }
        final Query.Filter fs = new Query.FilterPredicate(Entity.KEY_RESERVED_PROPERTY,
                Query.FilterOperator.EQUAL, KeyFactory.createKey("QuestionGroup", groupId));
        final Query q = new Query("QuestionGroup").setFilter(fs);
        return ds.prepare(q).asSingleEntity();
    }

    private Entity fetchQuestion(DatastoreService ds, Long questionId) {
        if (questionId == null) {
            return null;
        }
        final Query.Filter fs = new Query.FilterPredicate(Entity.KEY_RESERVED_PROPERTY,
                Query.FilterOperator.EQUAL, KeyFactory.createKey("Question", questionId));
        final Query q = new Query("Question").setFilter(fs);
        return ds.prepare(q).asSingleEntity();
    }
}
