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

package org.waterforpeople.mapping.domain;

public class GeoCoordinates {
    private Double latitude;
    private Double longitude;
    private Double altitude;
    private String code;

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getAltitude() {
        return altitude;
    }

    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public static GeoCoordinates extractGeoCoordinate(String line) {
        GeoCoordinates gc = null;
        if (line != null && line.trim().length() > 0
                && !line.trim().equals("||") && !line.startsWith("||")) {
            gc = new GeoCoordinates();
            String[] coordinates = line.split("\\|", -1);
            if (coordinates.length > 1) {
                try {
                    gc.setLatitude(Double.parseDouble(coordinates[0]));
                    gc.setLongitude(Double.parseDouble(coordinates[1]));
                } catch (NumberFormatException nfe) {
                    // if we can't parse the lat/lon, the whole operation should fail
                    return null;
                }
            } else {
                return null;
            }
            if (coordinates.length > 2) {
                if (coordinates[2] != null
                        && coordinates[2].trim().length() > 0) {
                    try {
                        gc.setAltitude(Double.parseDouble(coordinates[2]));
                    } catch (NumberFormatException nfe) {
                        // the altitude cannot be parsed as double, so set it to null
                        gc.setAltitude(null);
                    }
                }
            }
            if (coordinates.length > 3) {
                gc.setCode(coordinates[3]);
            }
        }
        return gc;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("GeoCoordinates:");
        sb.append("\n--Latitude: " + this.latitude);
        sb.append("\n--Longitude: " + this.longitude);
        sb.append("\n--Altitude: " + this.altitude);
        return sb.toString();
    }

}
