package org.akvo.gae.remoteapi;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.gallatinsystems.surveyal.domain.SurveyedLocale;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

/**
 * Loop through all the SurveyedLocales and 1) Generate an identifier if it does
 * not exist 2) Find and set the surveyGroupId if it does not exist
 * 
 * @author jonasenlund
 *
 */
public class SurveyedLocaleFix implements Process {

    final static Map<Long, Long> formIdToSurveyIdCache = new HashMap<>();

    @Override
    public void execute(DatastoreService ds, String[] args) throws Exception {
        Query surveyedLocaleQuery = new Query("SurveyedLocale");

        Set<Entity> updatedEntities = new HashSet<>();

        for (Entity surveyedLocale : ds.prepare(surveyedLocaleQuery)
                .asIterable(FetchOptions.Builder.withChunkSize(500))) {

            Long surveyedLocaleId = surveyedLocale.getKey().getId();
            boolean updated = false;
            Object identifier = surveyedLocale.getProperty("identifier");

            if (identifier == null) {
                String newIdentifier = SurveyedLocale.generateBase32Uuid();
                surveyedLocale.setProperty("identifier", newIdentifier);
                System.out.println("Successfully derived identifier for "
                        + surveyedLocaleId);
                updated = true;
            }

            Long surveyId = (Long) surveyedLocale.getProperty("surveyGroupId");

            if (surveyId == null) {
                surveyId = findSurveyIdByCreationFormId(ds,
                        (Long) surveyedLocale.getProperty("creationSurveyId"));
                if (surveyId == null) {
                    surveyId = findSurveyIdByLastSurveyalInstanceId(ds,
                            (Long) surveyedLocale
                                    .getProperty("lastSurveyalInstanceId"));
                    if (surveyId != null) {
                        System.out
                                .println("Successfully derived surveyId via lastSurveyalInstanceId for "
                                        + surveyedLocaleId);
                    }
                } else {
                    System.out
                            .println("Successfully derived surveyId via creationSurveyId for "
                                    + surveyedLocaleId);

                }

                if (surveyId == null) {
                    System.out.println("Could not derive surveyId for "
                            + surveyedLocaleId);
                } else {
                    surveyedLocale.setProperty("surveyGroupId", surveyId);
                    updated = true;
                }
            }

            if (updated) {
                updatedEntities.add(surveyedLocale);
            }

        }

        if (!updatedEntities.isEmpty()) {
            ds.put(updatedEntities);
            System.out.println(String.format("Updated %s entities",
                    updatedEntities.size()));
        }
    }

    public static Long findSurveyIdByCreationFormId(DatastoreService ds,
            Long creationFormId) {
        if (creationFormId == null) {
            return null;
        }
        return findSurveyIdByFormId(ds, creationFormId);
    }

    public static Long findSurveyIdByLastSurveyalInstanceId(
            DatastoreService ds, Long lastSurveyalInstanceId) {
        if (lastSurveyalInstanceId == null) {
            return null;
        }
        Filter surveyInstanceByIdFilter = new FilterPredicate(
                Entity.KEY_RESERVED_PROPERTY, FilterOperator.EQUAL,
                KeyFactory.createKey("SurveyInstance",
                        (Long) lastSurveyalInstanceId));
        Query surveyInstanceQuery = new Query("SurveyInstance")
                .setFilter(surveyInstanceByIdFilter);

        Entity surveyInstance = ds.prepare(surveyInstanceQuery)
                .asSingleEntity();

        if (surveyInstance == null) {
            return null;
        }
        return findSurveyIdByFormId(ds,
                (Long) surveyInstance.getProperty("surveyId"));
    }

    public static Long findSurveyIdByFormId(DatastoreService ds, Long formId) {
        if (formId == null) {
            return null;
        }

        if (formIdToSurveyIdCache.containsKey(formId)) {
            return formIdToSurveyIdCache.get(formId);
        }

        Filter formByIdFilter = new FilterPredicate(
                Entity.KEY_RESERVED_PROPERTY, FilterOperator.EQUAL,
                KeyFactory.createKey("Survey", (Long) formId));

        Query formQuery = new Query("Survey").setFilter(formByIdFilter);

        Entity form = ds.prepare(formQuery).asSingleEntity();

        if (form == null) {
            return null;
        }

        Long surveyId = (Long) form.getProperty("surveyGroupId");

        if (surveyId != null) {
            formIdToSurveyIdCache.put(formId, surveyId);
        }

        return surveyId;
    }
}
