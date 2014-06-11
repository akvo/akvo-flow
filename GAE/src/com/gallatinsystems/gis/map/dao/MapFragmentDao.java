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

package com.gallatinsystems.gis.map.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import com.gallatinsystems.gis.map.domain.MapFragment;
import com.gallatinsystems.gis.map.domain.MapFragment.FRAGMENTTYPE;

public class MapFragmentDao extends BaseDAO<MapFragment> {

    public MapFragmentDao() {
        super(MapFragment.class);
    }

    /**
     * searches for access points that match all of the non-null params
     * 
     * @param country
     * @param community
     * @param constDateFrom
     * @param constDateTo
     * @param type
     * @param tech
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<MapFragment> searchMapFragments(String country,
            String community, String techType,
            FRAGMENTTYPE fragmentType, String cursorString, String orderByCol,
            String orderByDirection) {
        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(MapFragment.class);
        StringBuilder filterString = new StringBuilder();
        StringBuilder paramString = new StringBuilder();
        Map<String, Object> paramMap = null;
        paramMap = new HashMap<String, Object>();

        appendNonNullParam("countryCode", filterString, paramString, "String",
                country, paramMap);
        appendNonNullParam("technologyType", filterString, paramString,
                "String", techType, paramMap);
        appendNonNullParam("fragmentType", filterString, paramString, "String",
                fragmentType, paramMap);

        query.setFilter(filterString.toString());
        query.declareParameters(paramString.toString());

        if (orderByCol != null && orderByDirection != null)
            query.setOrdering(orderByCol + " " + orderByDirection);

        prepareCursor(cursorString, query);
        List<MapFragment> results = (List<MapFragment>) query
                .executeWithMap(paramMap);

        return results;
    }

    public List<MapFragment> listFragmentsByCountryAndTechType(
            String countryCode, String techType) {
        return searchMapFragments(countryCode, null, techType,
                FRAGMENTTYPE.COUNTRY_INDIVIDUAL_PLACEMARK, "all", "countryCode", "asc");
    }

    public List<MapFragment> listAllFragments() {
        return searchMapFragments(null, null, null,
                FRAGMENTTYPE.COUNTRY_ALL_PLACEMARKS, "all", null, null);
    }
}
