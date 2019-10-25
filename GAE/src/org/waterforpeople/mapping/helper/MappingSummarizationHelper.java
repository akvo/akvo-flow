/*
 *  Copyright (C) 2010-2012,2018 Stichting Akvo (Akvo Foundation)
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

package org.waterforpeople.mapping.helper;

import org.waterforpeople.mapping.dao.GeoIndexDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This helper performs mapping summarization as well as abstracts clients from Some of the methods
 * (addPointToPoly, for instance) are STATEFUL in that they can be used to accumulate points over
 * multiple method invocations in member variables so that they can eventually be flushed to an
 * index in a single operation
 *
 * @author Christopher Fagiani
 */
public class MappingSummarizationHelper {
    private GeoIndexDao geoIndexDao;

    private Map<String, StringBuilder> regionMap;
    private Map<String, String> firstPositionInPolys;

    public MappingSummarizationHelper() {
        geoIndexDao = new GeoIndexDao();
    }

    /**
     * adds the lat,lon point to the polygon identified by the UUID passed in. This method is
     * STATEFUL in that this class will continue to accumulate points in a member variable each time
     * this method is called. If you want to reset the internal member, call resetPoly
     *
     * @param uuid
     * @param lat
     * @param lon
     */
    public void addPointToRegion(String uuid, String lat, String lon) {
        if (regionMap == null) {
            resetPoly();
        }
        StringBuilder buf = regionMap.get(uuid);
        if (buf == null) {
            buf = new StringBuilder();
            buf.append(lat + " " + lon);
            regionMap.put(uuid, buf);
            firstPositionInPolys.put(uuid, lat + " " + lon);
        } else {
            buf.append("," + lat + " " + lon);
        }
    }

    /**
     * Persists all the accumulated points in the region map and resets the internal member
     * variables
     */
    public void saveRegions() {
        if (regionMap != null) {
            GeoIndexDao indexDao = new GeoIndexDao();
            Map<String, String> regionStrings = new HashMap<String, String>();
            for (String region : regionMap.keySet()) {
                regionStrings.put(region, "POLYGON((" + regionMap.get(region)
                        + "," + firstPositionInPolys.get(region) + "))");

            }
            indexDao.saveRegionIndex(regionStrings);
            resetPoly();
        }
    }

    /**
     * resets the internal store
     */
    public void resetPoly() {
        regionMap = new HashMap<String, StringBuilder>();
        firstPositionInPolys = new HashMap<String, String>();
    }

    /**
     * executes a summarization action for a given region. It will iterate over all access points in
     * that region and use the rule type to evalute what should be done
     *
     * @param regionUUID
     * @param type
     * @return
     */
    public String processSummarization(String regionUUID, String type) {
        return null;
    }

}
