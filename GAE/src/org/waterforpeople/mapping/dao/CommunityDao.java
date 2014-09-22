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

package org.waterforpeople.mapping.dao;

import java.util.List;

import org.waterforpeople.mapping.domain.Community;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.gis.geography.domain.Country;

/**
 * Data access methods for communities and countries
 * 
 * @author Christopher Fagiani
 */
public class CommunityDao extends BaseDAO<Community> {

    public enum MAP_TYPE {
        PUBLIC, KMZ
    };

    public CommunityDao() {
        super(Community.class);
    }

    /**
     * looks up a community using the name
     * 
     * @param name
     * @return
     */
    public Community findCommunityByName(String name) {
        return findByProperty("name", name, "String");
    }

    /**
     * finds a single country that is attached to the community identified by the code
     * 
     * @param communityCode
     * @return
     */
    public Country findCountryByCommunity(String communityCode) {
        Community comm = findCommunityByCode(communityCode);
        if (comm != null) {
            return findCountryByCode(comm.getCountryCode());
        } else {
            return null;
        }
    }

    /**
     * finds a single community by its code
     * 
     * @param communityCode
     * @return
     */
    public Community findCommunityByCode(String communityCode) {
        return findByProperty("communityCode", communityCode, "String");
    }

    /**
     * finds a country by its country code
     * 
     * @param code
     * @return
     */
    public Country findCountryByCode(String code) {
        if (code != null) {
            List<Country> cList = listByProperty("isoAlpha2Code", code,
                    "String", Country.class);
            if (cList != null && cList.size() > 0) {
                return cList.get(0);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public List<Country> listMapCountries(MAP_TYPE type) {
        if (type == MAP_TYPE.PUBLIC) {
            return listByProperty("includeInExternal", true, "Boolean",
                    Country.class);
        } else {
            return listByProperty("includeInKMZ", true, "Boolean",
                    Country.class);
        }
    }

    /**
     * lists all communities within a specific country.
     * 
     * @param countryCode
     * @return
     */
    public List<Community> listCommunityByCountry(String countryCode) {
        return (List<Community>) listByProperty("countryCode", countryCode,
                "String");
    }

}
