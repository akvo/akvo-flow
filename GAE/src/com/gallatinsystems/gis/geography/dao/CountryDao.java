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

import java.util.List;

import javax.jdo.PersistenceManager;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import com.gallatinsystems.gis.geography.domain.Country;

/**
 * dao for saving/finding countries
 * 
 * @author Christopher Fagiani
 */
public class CountryDao extends BaseDAO<Country> {

    public CountryDao() {
        super(Country.class);
    }

    /**
     * returns a sorted list of countries
     * 
     * @param orderByCol
     * @param direction
     * @param cursorString
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Country> list(String orderByCol, String direction,
            String cursorString) {
        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(Country.class);
        query.setOrdering(orderByCol + " " + direction);
        prepareCursor(cursorString, query);

        List<Country> results = (List<Country>) query.execute();

        return results;

    }

    /**
     * finds a single country by its code (either 2 letter or 3 letter)
     * 
     * @param code
     * @return
     */
    public Country findByCode(String code) {
        String propertyName = null;
        String propertyType = null;
        // Todo create a scanner and test for number

        if (code.trim().length() == 2) {
            propertyName = "isoAlpha2Code";
            propertyType = "String";
        } else if (code.trim().length() == 3) {
            propertyName = "isoAlpha3Code";
            propertyType = "String";
        }
        Country country = super
                .findByProperty(propertyName, code, propertyType);
        if (country != null)
            return country;
        else
            return null;
    }
}
