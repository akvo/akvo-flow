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

package com.gallatinsystems.gis.coordinate.utilities;

/**
 * Utility for manipulation of geo coordinates
 */
public class CoordinateUtilities {

    static final Double polarAxis = 6356752.314;
    static final Integer equRad = 6378137;
    static final Double ecc = 0.081819191;
    static final Double e2 = 0.006739497;
    static final Double k0 = 0.9996;
    static final Double pi = 3.14159265358979323846264338327950288;

    public static void main(String[] args) {
        Integer x = Integer.parseInt(args[0]);
        Integer y = Integer.parseInt(args[1]);
        Integer zone = Integer.parseInt(args[2]);
        System.out.println("Converted Coordiantes: "
                + convertUTMtoLatLon(x, y, NSLatitude.SOUTH, zone));
    }

    public static final double DEGREES_TO_RADIANS = (Math.PI / 180.0);

    // Mean radius in KM
    public static final double EARTH_RADIUS = 6371.0;

    /**
     * computes the distance between 2 points
     * 
     * @param startLat
     * @param startLon
     * @param endLat
     * @param endLon
     * @return
     */
    public static Double computeDistance(Double startLat, Double startLon, Double endLat,
            Double endLon) {
        Double distance = null;

        double p1 = Math.cos(startLat)
                * Math.cos(startLon) * Math.cos(endLat)
                * Math.cos(endLat);
        double p2 = Math.cos(startLat)
                * Math.sin(startLon) * Math.cos(endLat)
                * Math.sin(endLon);
        double p3 = Math.sin(startLat) * Math.sin(endLat);

        distance = (Math.acos(p1 + p2 + p3) * EARTH_RADIUS);
        // Return distance in meters
        distance = distance * 1000;
        return distance;

    }

    public Coordinate computePointAlongBearingDistance(
            Coordinate startingPoint, Double distance, Double bearing) {
        Double lat1 = startingPoint.getLatitude() * DEGREES_TO_RADIANS;
        Double lon1 = startingPoint.getLongitude() * DEGREES_TO_RADIANS;
        bearing = bearing * DEGREES_TO_RADIANS;

        Double lat2 = Math.asin(Math.sin(lat1)
                * Math.cos(distance / EARTH_RADIUS) + Math.cos(lat1)
                * Math.sin(distance / EARTH_RADIUS) * Math.cos(bearing));
        Double lon2 = lon1
                + Math.atan2(
                        Math.sin(bearing) * Math.sin(distance / EARTH_RADIUS)
                                * Math.cos(lat1),
                        Math.cos(distance / EARTH_RADIUS) - Math.sin(lat1)
                                * Math.sin(lat2));
        lon2 = (lon2 + 3 * Math.PI) % (2 * Math.PI) - Math.PI;

        lat2 = lat2 / DEGREES_TO_RADIANS;
        lon2 = lon2 / DEGREES_TO_RADIANS;

        Coordinate newPoint = new Coordinate(lat2, lon2);
        return newPoint;
    }

    /**
     * computes the APPROXIMATE distance between 2 points (lat/lon in DEGREES, not radians) forumula
     * described here: http://www.meridianworlddata.com/Distance-Calculation.asp
     * 
     * @param lat1
     * @param lon1
     * @param lat2
     * @param lon2
     * @return
     */
    public static Double computeDistanceInMiles(Double lat1, Double lon1,
            Double lat2, Double lon2) {
        double x = 69.1 * (lat2 - lat1);
        double y = 53.0 * (lon2 - lon1) * Math.cos(lat1 / 57.3);
        return Math.sqrt(x * x + y * y);
    }

    public String convertDecimalToDegrees(Double lat, Double lon) {
        String degrees = null;
        Long latDecimal = 0L;

        Long lonDecimal = 0L;

        latDecimal = lat.longValue();
        Double degreesLat = (lat - latDecimal) * 60;

        lonDecimal = lon.longValue();
        Double degreesLon = (lon - lonDecimal) * 60;

        degrees = "lat: " + latDecimal + " degrees " + degreesLat;
        degrees += "lon: " + lonDecimal + " degrees " + degreesLon;

        return degrees;
    }

    public static String convertUTMtoLatLon(Integer eastingCoor,
            Integer northingCoor, NSLatitude lat, Integer zone) {
        Integer zoneCentralLongitude = computeZoneCentralLongitude(zone);

        Double arcLength = (10000000 - northingCoor) / k0;
        // =arc/(a*(1-ec^2/4-3*ec^4/64-5*ec^6/256))

        Double a1 = Math.pow(ecc, 2) / 4;
        Double a2 = 3 * (Math.pow(ecc, 4) / 64);
        Double a3 = 5 * (Math.pow(ecc, 6) / 256);

        Double a = 1 - a1 - a2 - a3;

        Double mu = arcLength / (equRad * a);
        // =(1-(1-ec*ec)^(1/2))/(1+(1-ec*ec)^(1/2))
        Double a4 = Math.sqrt((1 - Math.pow(ecc, 2)));

        Double e1 = (1 - a4) / (1 + a4);
        // =3*ei/2-27*ei^3/32
        Double c1 = ((3 * e1) / 2) - (27 * Math.pow(e1, 3) / 32);
        // =21*ei^2/16-55*ei^4/32
        Double c2 = (21 * Math.pow(e1, 2) / 16) - (55 * Math.pow(e1, 4) / 32);
        Double c3 = (151 * Math.pow(e1, 3) / 96);
        Double c4 = (1097 * Math.pow(e1, 4) / 512);
        // =mu+ca*SIN(2*mu)+cb*SIN(4*mu)+ccc*SIN(6*mu)+cd*SIN(8*mu)
        Double footprintLat = mu + (c1 * Math.sin(2 * mu))
                + (c2 * Math.sin(4 * mu)) + (c3 * Math.sin(6 * mu))
                + (c4 * Math.sin(8 * mu));

        // Lat =180*(_phi1-fact1*(fact2+fact3+fact4))/PI()
        // Lon =F3-E22

        Double C1 = e2 * Math.pow(Math.cos(footprintLat), 2);
        Double T1 = Math.pow(Math.tan(footprintLat), 2);
        // =a/(1-(ec*SIN(_phi1))^2)^(1/2)
        Double N1 = equRad
                / Math.sqrt((1 - (Math.pow(ecc * Math.sin(footprintLat), 2))));
        // =a*(1-ec*ec)/(1-(ec*SIN(_phi1))^2)^(3/2)
        Double R1 = (equRad * (1 - Math.pow(ecc, 2)))
                / Math.pow((1 - Math.pow(ecc * Math.sin(footprintLat), 2)),
                        3 / 2);
        // =H2/(n0*k0)
        Double D = (500000 - eastingCoor) / (N1 * k0);

        // =n0*TAN(_phi1)/r0
        Double fact1 = (N1 * Math.tan(footprintLat)) / R1;
        // =dd0*dd0/2
        Double fact2 = Math.pow(D, 2) / 2;
        // =(5+3*t0+10*Q0-4*Q0*Q0-9*eisq)*dd0^4/24
        Double fact3 = (5 + 3 * T1 + 10 * C1 - 4 * Math.pow(C1, 2) - 9 * e2)
                * Math.pow(D, 4) / 24;
        // =(61+90*t0+298*Q0+45*t0*t0-252*eisq-3*Q0*Q0)*dd0^6/720
        Double fact4 = (61 + 90 * T1 + 298 * e2 + 45 * Math.pow(T1, 2) - 252
                * e2 - 3 * Math.pow(C1, 2))
                * Math.pow(D, 6) / 720;

        // Double latitude = 180*(footprintLat - fact1*(fact2+fact3+fact4))/pi;
        Double latitude;

        latitude = (180 * (footprintLat - fact1 * (fact2 + fact3 + fact4)))
                / pi;
        latitude = latitude * -1;

        // long = long0 + (Q5 - Q6 + Q7)/cos(fp), where:
        //
        // Q5 = D
        // Q6 = (1 + 2T1 + C1)D3/6
        // Q7 = (5 - 2C1 + 28T1 - 3C12 + 8e'2 + 24T12)D5/120

        Double Q5 = D;
        Double Q6 = (1 + 2 * T1 + C1) * Math.pow(D, 3) / 6;
        Double Q7 = (5 - 2 * C1 + 28 * T1 - 3 * Math.pow(C1, 2) + 8 * e2 + 24 * Math
                .pow(T1, 2)) * Math.pow(D, 5) / 120;
        // =(_lof1-_lof2+_lof3)/COS(_phi1)
        Double H20 = Q5 - Q6 + Q7 / Math.cos(footprintLat);
        Double E22 = H20 * 180 / pi;
        Double longitude = zoneCentralLongitude - E22;

        return latitude + ", " + longitude;
    }

    private static Integer computeZoneCentralLongitude(Integer zone) {
        Integer zcl = 0;
        // =IF(E19>0,6*E19-183,3)
        if (zone > 0) {
            zcl = 6 * zone - 183;
        }
        return zcl;
    }

    public static enum NSLatitude {
        NORTH, SOUTH
    };

}
