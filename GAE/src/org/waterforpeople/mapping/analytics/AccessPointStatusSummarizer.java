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

package org.waterforpeople.mapping.analytics;

import org.waterforpeople.mapping.analytics.dao.AccessPointStatusSummaryDao;
import org.waterforpeople.mapping.dao.CommunityDao;
import org.waterforpeople.mapping.domain.AccessPoint;

import com.gallatinsystems.framework.analytics.summarization.DataSummarizer;
import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.gis.geography.domain.Country;

/**
 * This class will populate country/community lookup tables based on geo information in the access
 * point.
 * 
 * @author Christopher Fagiani
 */
public class AccessPointStatusSummarizer implements DataSummarizer {

    /**
     * summarizes AccessPoints by incrementing counts of AP by country
     */
    @Override
    public boolean performSummarization(String key, String type, String value,
            Integer offset, String cursor) {
        if (key != null) {
            BaseDAO<AccessPoint> accessPointDao = new BaseDAO<AccessPoint>(
                    AccessPoint.class);
            AccessPoint ap = accessPointDao.getByKey(Long.parseLong(key));
            if (ap != null) {
                CommunityDao commDao = new CommunityDao();
                Country c = commDao.findCountryByCommunity(ap
                        .getCommunityCode());
                if (c == null) {
                    // if we didn't find a country, use a placeholder for now
                    c = new Country();
                    c.setIsoAlpha2Code("??");
                    c.setName("Unknown");
                }
                AccessPointStatusSummaryDao.incrementCount(ap, c, 1);
            }
        }
        return true;
    }

    @Override
    public String getCursor() {
        return null;
    }

}
