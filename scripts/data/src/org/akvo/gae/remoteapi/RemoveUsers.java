/*
 *  Copyright (C) 2015 Stichting Akvo (Akvo Foundation)
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
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import static org.akvo.gae.remoteapi.DataUtils.USER_EMAIL_FIELD;
import static org.akvo.gae.remoteapi.DataUtils.USER_KIND;

public class RemoveUsers implements Process {

    @Override
    public void execute(DatastoreService ds, String[] args) throws Exception {

        String userEmail = args[0];
        Filter emailAddressFilter = new Query.FilterPredicate(USER_EMAIL_FIELD, FilterOperator.EQUAL, userEmail);

        Query userQuery = new Query(USER_KIND).setFilter(emailAddressFilter).setKeysOnly();
        Entity user = ds.prepare(userQuery).asSingleEntity();
        if (user != null) {
            ds.delete(user.getKey());
            System.out.println("User " + userEmail + " deleted");
        } else {
            System.out.println("User " + userEmail + " not found");
        }
    }
}
