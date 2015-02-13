package org.akvo.gae.remoteapi;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
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
		
		Query surveyQuery = new Query("SurveyGroup");
		
		for (Entity survey : ds.prepare(surveyQuery).asIterable()) {
			Object projectType = survey.getProperty("projectType");
			if ("PROJECT".equals(projectType)) {
				long id = survey.getKey().getId();
				Filter filter = new FilterPredicate("surveyGroupId", FilterOperator.EQUAL, id);
				Query formQuery = new Query("Survey").setFilter(filter);
				boolean containsHouseholdData = false;
				for (Entity form : ds.prepare(formQuery).asIterable()) {
					Object pointType = form.getProperty("pointType");
					if ("Household".equals(pointType)) {
						containsHouseholdData = true;
					}
				}
				Object privacyLevel = survey.getProperty("privacyLevel");
				if (containsHouseholdData && !"PRIVATE".equals(privacyLevel)) {
					survey.setProperty("privacyType", "PRIVATE");
					ds.put(survey);
					System.out.println(String.format("Survey #%s (%s) privacyLevel changed to PRIVATE", 
							id, survey.getProperty("name")));
				}
			}
		}
	}
}
