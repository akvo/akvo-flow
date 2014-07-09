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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.waterforpeople.mapping.dao.AccessPointDao;
import org.waterforpeople.mapping.dao.CommunityDao;
import org.waterforpeople.mapping.domain.AccessPoint;
import org.waterforpeople.mapping.domain.Community;
import org.waterforpeople.mapping.helper.AccessPointHelper;

import com.gallatinsystems.framework.analytics.summarization.DataSummarizer;
import com.gallatinsystems.gis.geography.domain.Country;
import com.gallatinsystems.gis.location.GeoLocationService;
import com.gallatinsystems.gis.location.GeoLocationServiceGeonamesImpl;
import com.gallatinsystems.gis.location.GeoPlace;

/**
 * This class will populate country/community lookup tables based on geo information in the access
 * point.
 * 
 * @author Christopher Fagiani
 */
public class CommunityLocationSummarizer implements DataSummarizer {
    Logger logger = Logger.getLogger(CommunityLocationSummarizer.class
            .getName());

    /**
     * This will respond to changes in Access Point lat/lon values by looking up the
     * community/country names by location and, if they've changed, updating the access point with
     * the new values.
     */
    @Override
    public boolean performSummarization(String key, String type, String value,
            Integer offset, String cursor) {
        if (key != null) {
            AccessPointDao accessPointDao = new AccessPointDao();
            AccessPoint ap = accessPointDao.getByKey(new Long(key));
            if (ap != null) {
                CommunityDao commDao = new CommunityDao();
                Community community = commDao.findCommunityByCode(ap
                        .getCommunityCode());
                if (community == null
                        && (ap.getLatitude() != null && ap.getLatitude() != 0.0
                                && ap.getLongitude() != null && ap
                                .getLongitude() != 0.0)) {
                    GeoLocationService gl = new GeoLocationServiceGeonamesImpl();
                    GeoPlace gp = gl.findGeoPlace(ap.getLatitude().toString(),
                            ap.getLongitude().toString());
                    if (gp == null) {
                        String countryCode = gl.getCountryCodeForPoint(ap
                                .getLatitude().toString(), ap.getLongitude()
                                .toString());
                        if (countryCode != null) {
                            gp = new GeoPlace();
                            gp.setCountryCode(countryCode);
                            gp.setCountryName(countryCode);
                        }
                    }
                    if (gp != null && gp.getCountryCode() != null
                            && gp.getCountryCode().trim().length() <= 3) {
                        Country ourCountry = commDao.findCountryByCode(gp
                                .getCountryCode());
                        if (ourCountry == null) {
                            ourCountry = new Country();
                            ourCountry.setIsoAlpha2Code(gp.getCountryCode());
                            ourCountry.setName(gp.getCountryName());
                            ourCountry.setDisplayName(gp.getCountryName());
                            commDao.save(ourCountry);
                        }
                        community = new Community();
                        community.setCommunityCode(ap.getCommunityCode());
                        community.setName(gp.getName());
                        community.setLat(gp.getLat());
                        community.setLon(gp.getLng());
                        // have to do this so we can query by it due to GAE's
                        // JDO limitations
                        community.setCountryCode(ourCountry.getIsoAlpha2Code());
                        // this save will cascade-save the country
                        commDao.save(community);
                    } else {
                        logger.log(
                                Level.SEVERE,
                                "Did not find a country for lat/lon "
                                        + ap.getLatitude() + ","
                                        + ap.getLongitude()
                                        + " using the geonames service");
                    }
                }
                if (community != null && community.getName() == null) {
                    community.setName(ap.getCommunityName());
                }
                if (ap.getCountryCode() == null && community != null) {
                    ap.setCountryCode(community.getCountryCode());
                }

                AccessPointHelper aph = new AccessPointHelper();
                aph.setGeoDetails(ap);
                accessPointDao.save(ap);
            }
        }
        return true;
    }

    @Override
    public String getCursor() {
        return null;
    }
}
