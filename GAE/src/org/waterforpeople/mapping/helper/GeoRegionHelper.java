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

package org.waterforpeople.mapping.helper;

import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Logger;

import org.waterforpeople.mapping.dao.GeoRegionDAO;
import org.waterforpeople.mapping.domain.GeoRegion;

import com.google.appengine.api.datastore.Key;

public class GeoRegionHelper {
    private static final Logger log = Logger.getLogger(GeoRegionHelper.class
            .getName());

    public ArrayList<GeoRegion> processRegionsSurvey(
            ArrayList<String> regionLines) {
        ArrayList<GeoRegion> geoRegions = null;
        GeoRegionDAO grDAO = new GeoRegionDAO();
        int iCurrRegion = 1;
        int iCurrRow = 1;
        UUID currentUUID = UUID.randomUUID();
        MappingSummarizationHelper mappingHelper = new MappingSummarizationHelper();
        for (String s : regionLines) {
            GeoRegion gr = new GeoRegion();
            String[] contents = s.split(",");

            Integer iLineRegion = new Integer(contents[0]);

            if (iCurrRow == 1) {
                iCurrRegion = iLineRegion;
            } else if (iCurrRegion != iLineRegion) {
                iCurrRegion = iLineRegion;
                currentUUID = UUID.randomUUID();
            }
            mappingHelper.addPointToRegion(currentUUID.toString(), contents[3],
                    contents[4]);
            gr.setUuid(currentUUID.toString());
            gr.setOrder(new Long(contents[1]));
            gr.setName(contents[2]);
            gr.setLatitiude(new Double(contents[3]));
            gr.setLongitude(new Double(contents[4]));
            // gr.setCreateDateTime(new Date(contents[5]));
            Key key = (grDAO.save(gr)).getKey();
            iCurrRow++;
            log.info("Saved RegionRow: " + key.toString());
        }

        mappingHelper.saveRegions();

        return geoRegions;
    }

}
