/*
 *  Copyright (C) 2010-2012 Stichting Akvo (Akvo Foundation)
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

package com.gallatinsystems.auth.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;

import com.gallatinsystems.auth.domain.WebActivityAuthorization;
import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;

/**
 * persists and finds WebActivityAuthorization objects.
 * 
 * @author Christopher Fagiani
 */
public class WebActivityAuthorizationDao extends
        BaseDAO<WebActivityAuthorization> {

    public WebActivityAuthorizationDao() {
        super(WebActivityAuthorization.class);
    }

    /**
     * lists all webActivityAuthorizations associated with the token and, optionally, activityName
     * passed in. If the validOnly flag is true, this list is filtered to only include "valid" items
     * (unexpired tokens with a useCount < maxUses).
     * 
     * @param token
     * @param activity
     * @param cursorString
     * @param validOnly
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<WebActivityAuthorization> listByToken(String token,
            String activityName, String cursorString, boolean validOnly) {
        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(WebActivityAuthorization.class);
        Map<String, Object> paramMap = null;

        StringBuilder filterString = new StringBuilder();
        StringBuilder paramString = new StringBuilder();
        paramMap = new HashMap<String, Object>();

        appendNonNullParam("token", filterString, paramString, "String", token,
                paramMap);
        appendNonNullParam("webActivityName", filterString, paramString,
                "String", activityName, paramMap);
        query.setFilter(filterString.toString());
        query.declareParameters(paramString.toString());
        prepareCursor(cursorString, query);

        List<WebActivityAuthorization> authList = (List<WebActivityAuthorization>) query
                .executeWithMap(paramMap);

        if (authList != null && validOnly) {
            List<WebActivityAuthorization> filteredList = new ArrayList<WebActivityAuthorization>();
            for (WebActivityAuthorization auth : authList) {
                if (auth.isValidForAuth()) {
                    filteredList.add(auth);
                }
            }
            authList = filteredList;
        }
        return authList;
    }

    /**
     * returns all VALID authorization objects for a given user/activity combination
     * 
     * @param userId
     * @param activityName
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<WebActivityAuthorization> listByUser(Long userId,
            String activityName) {
        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(WebActivityAuthorization.class);
        Map<String, Object> paramMap = null;

        StringBuilder filterString = new StringBuilder();
        StringBuilder paramString = new StringBuilder();
        paramMap = new HashMap<String, Object>();

        appendNonNullParam("userId", filterString, paramString, "Long", userId,
                paramMap);
        appendNonNullParam("webActivityName", filterString, paramString,
                "String", activityName, paramMap);
        query.setFilter(filterString.toString());
        query.declareParameters(paramString.toString());

        List<WebActivityAuthorization> authList = (List<WebActivityAuthorization>) query
                .executeWithMap(paramMap);
        if (authList != null) {
            List<WebActivityAuthorization> filteredList = new ArrayList<WebActivityAuthorization>();
            for (WebActivityAuthorization auth : authList) {
                if (auth.isValidForAuth()) {
                    filteredList.add(auth);
                }
            }
            authList = filteredList;
        }
        return authList;
    }
}
