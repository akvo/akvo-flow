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

import java.util.logging.Logger;

import org.waterforpeople.mapping.analytics.dao.AccessPointStatusSummaryDao;
import org.waterforpeople.mapping.domain.AccessPoint;
import org.waterforpeople.mapping.domain.AccessPoint.AccessPointType;
import org.waterforpeople.mapping.domain.AccessPoint.Status;

import com.gallatinsystems.common.util.DateUtil;
import com.gallatinsystems.framework.analytics.summarization.DataSummarizer;
import com.gallatinsystems.framework.domain.DataChangeRecord;

/**
 * handles changes to access point status values
 * 
 * @author Christopher Fagiani
 */
public class AccessPointStatusUpdater implements DataSummarizer {

    private static Logger logger = Logger.getLogger(AccessPointUpdater.class
            .getName());

    @Override
    public String getCursor() {
        // no-op
        return null;
    }

    /**
     * populates a DataChangeRecord from the input value passed in and uses it to decrement the
     * count from the AccessPointStatusSummary for the old value then increment the count for the
     * new.
     */
    @Override
    public boolean performSummarization(String key, String type, String value,
            Integer offset, String cursor) {
        DataChangeRecord change = new DataChangeRecord(value);
        AccessPoint oldPoint = hydratePoint(change.getOldVal());
        AccessPoint newPoint = hydratePoint(change.getNewVal());
        AccessPointStatusSummaryDao.incrementCount(oldPoint, null, -1);
        AccessPointStatusSummaryDao.incrementCount(newPoint, null, 1);
        return true;
    }

    /**
     * uses the packed string passed in to initialize the values of an AccessPoint object.
     * 
     * @param vals
     * @return
     */
    private AccessPoint hydratePoint(String packedString) {
        String[] vals = packedString.split("\\|");
        AccessPoint point = null;
        if (vals.length == 5) {
            point = new AccessPoint();
            point.setCountryCode(vals[0]);
            point.setCommunityCode(vals[1]);
            try {
                point.setPointType(AccessPointType.valueOf(vals[2]));
            } catch (Throwable e) {
                logger.warning("unknown type value: " + vals[2]);
            }
            try {
                point.setPointStatus(Status.valueOf(vals[3]));
            } catch (Throwable e) {
                logger.warning("unknown status value: " + vals[3]);
            }
            try {
                point.setCollectionDate(DateUtil.getYearOnlyDate(vals[4]));
            } catch (Throwable e) {
                logger.warning("bad date value: " + vals[4]);
            }
        }
        return point;
    }
}
