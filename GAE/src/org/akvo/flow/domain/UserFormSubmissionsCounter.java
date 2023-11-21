package org.akvo.flow.domain;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.gallatinsystems.user.domain.User;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

public class UserFormSubmissionsCounter {
    private DatastoreService datastore;

    public UserFormSubmissionsCounter(DatastoreService datastore) {
        this.datastore = datastore;
    }

    public Integer countFor(User user) {
        List<Long> surveyIds = getSurveyIds(user);
        if (CollectionUtils.isEmpty(surveyIds)) {
            return 0;
        }

        Query q = new Query("SurveyInstance").setFilter(new FilterPredicate("surveyId", FilterOperator.IN, surveyIds));
        PreparedQuery pq = datastore.prepare(q);

        return pq.countEntities(FetchOptions.Builder.withChunkSize(500));
    }

    private List<Long> getSurveyIds(User user) {
        List<Long> ids = new ArrayList<Long>();
        Query q = new Query("Survey").setFilter(new FilterPredicate("createUserId", FilterOperator.EQUAL, user.getKey().getId()));
        PreparedQuery pq = datastore.prepare(q);

        for (Entity s : pq.asIterable(FetchOptions.Builder.withChunkSize(500))) {
            ids.add(s.getKey().getId());
        }

        return ids;
    }
}
