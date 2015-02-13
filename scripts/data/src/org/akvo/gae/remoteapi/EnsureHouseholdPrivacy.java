package org.akvo.gae.remoteapi;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;;

public class EnsureHouseholdPrivacy implements Process {

	@Override
	public void execute(DatastoreService ds, String[] args) throws Exception {
		
	    if (args.length != 0) {
            System.err.println("Usage: " + RemoteAPI.class.getName()
                    + "EnsureHouseholdPrivacy <appid> <username> <password>");
            System.exit(1);
        }

	    Filter projectFilter = new FilterPredicate("projectType", FilterOperator.EQUAL, "PROJECT");
	    Filter publicFilter = new FilterPredicate("privacyLevel", FilterOperator.EQUAL, "PUBLIC");
	    Query surveyQuery = new Query("SurveyGroup").setFilter(CompositeFilterOperator.and(projectFilter, publicFilter));

        for (Entity survey : ds.prepare(surveyQuery).asIterable()) {
            long id = survey.getKey().getId();
            Filter surveyIdFilter = new FilterPredicate("surveyGroupId", FilterOperator.EQUAL, id);
            Filter householdFilter = new FilterPredicate("pointType", FilterOperator.EQUAL, "Household");
            Query formQuery = new Query("Survey").setFilter(CompositeFilterOperator.and(surveyIdFilter, householdFilter));
            boolean containsHouseholdData = !ds.prepare(formQuery).asList(FetchOptions.Builder.withLimit(1)).isEmpty();
            if (containsHouseholdData) {
                survey.setProperty("privacyLevel", "PRIVATE");
                ds.put(survey);
                System.out.println(String.format("Survey #%s (%s) privacyLevel changed to PRIVATE",
                        id, survey.getProperty("name")));
                
            }
        }
    }
}
