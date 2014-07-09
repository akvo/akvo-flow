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

package com.gallatinsystems.gis.location;

/**
 * Service interface for location services
 * 
 * @author Christopher Fagiani
 */
public interface GeoLocationService {
    /**
     * returns the 2-letter country code for the lat/lon location passed in
     */
    public String getCountryCodeForPoint(String lat, String lon);

    /**
     * returns a geo place object that is closest to the lat/lon passed in.
     */
    public GeoPlace findGeoPlace(String lat, String lon);

    /**
     * tries to find the most detailed geoPlace possible given the lat/lon provided.
     * 
     * @param lat
     * @param lon
     * @return
     */
    public GeoPlace findDetailedGeoPlace(String lat, String lon);
}
