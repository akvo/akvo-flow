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

package com.gallatinsystems.gis.geography.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import com.gallatinsystems.gis.geography.domain.SubCountry;

/**
 * data access object to save/find SubCountry objects.
 * 
 * @author Christopher Fagiani
 */
public class SubCountryDao extends BaseDAO<SubCountry> {

    public SubCountryDao() {
        super(SubCountry.class);
    }

    /**
     * lists all sub country records with the given parent
     * 
     * @param parentId
     * @return
     */
    public List<SubCountry> listSubCountryByParent(Long parentId) {
        return listByProperty("parentKey", parentId, "Long");
    }

    /**
     * finds a single SubCountry record given the country, level and name passed in
     * 
     * @param countryCode
     * @param name
     * @param level
     * @return
     */
    public SubCountry findSubCountry(String countryCode, String name,
            Integer level) {
        List<SubCountry> results = listSubCountryByLevel(countryCode, level,
                name);
        if (results != null && results.size() > 0) {
            return results.get(0);
        } else {
            return null;
        }
    }

    /**
     * lists all sub country records for a single country that belong to the level passed in. Level
     * 0 is assumed to be the country level.
     * 
     * @param countryCode
     * @param level
     * @param name -optional name
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<SubCountry> listSubCountryByLevel(String countryCode,
            Integer level, String name) {
        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(SubCountry.class);
        Map<String, Object> paramMap = null;

        StringBuilder filterString = new StringBuilder();
        StringBuilder paramString = new StringBuilder();
        paramMap = new HashMap<String, Object>();

        appendNonNullParam("countryCode", filterString, paramString, "String",
                countryCode, paramMap);
        appendNonNullParam("level", filterString, paramString, "Integer",
                level, paramMap);
        appendNonNullParam("name", filterString, paramString, "String", name,
                paramMap);
        query.setFilter(filterString.toString());
        query.declareParameters(paramString.toString());
        return (List<SubCountry>) query.executeWithMap(paramMap);
    }

}
