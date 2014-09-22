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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.gallatinsystems.gis.map.dao.OGRFeatureDao;
import com.gallatinsystems.gis.map.domain.Geometry;
import com.gallatinsystems.gis.map.domain.Geometry.GeometryType;
import com.gallatinsystems.gis.map.domain.OGRFeature;
import com.gallatinsystems.gis.map.domain.OGRFeature.FeatureType;
import com.gallatinsystems.survey.xml.SurveyXMLAdapter;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

/**
 * This service utilizes the geonames web services (ws.geonames.org) to look up geographic
 * information given latitude/longitude coordinates.
 * 
 * @author Christopher Fagiani
 */

public class GeoLocationServiceGeonamesImpl implements GeoLocationService {
    private static final Logger log = Logger.getLogger(SurveyXMLAdapter.class
            .getName());
    // private static final String COUNTRY_SERVICE_URL = "http://ws.geonames.org/countryCode?";
    private static final String PLACE_SERVICE_URL = "http://api.geonames.org/findNearbyPlaceName?username=akvo&";
    private static final String LAT_PARAM = "lat";
    private static final String LON_PARAM = "lng";

    /**
     * bounding box map of countries we care about order of coordinates: top left, bottom right
     */
    private Map<String, Double[]> COUNTRY_MBR = new HashMap<String, Double[]>() {
        private static final long serialVersionUID = 9163559500580094769L;
        {
            put("GT", new Double[] {
                    17.8152, -92.2414, 13.7373, -88.2232
            });
            put("HN", new Double[] {
                    17.4505, -89.3508, 12.9824, -82.4995
            });
            put("DO", new Double[] {
                    19.9298, -72.0035, 17.4693, -68.3200
            });
            put("NI", new Double[] {
                    15.0259, -87.6903, 10.7075, -82.5921
            });
            // TODO: make sure we check ecuador before peru since they overlap
            put("EC", new Double[] {
                    1.6504, -92.0005, -4.9988, -75.1846,
            });
            put("BO", new Double[] {
                    -9.6806, -69.6408, -22.8961, -57.4581
            });
            put("PE", new Double[] {
                    -0.0130, -81.3267, -18.3497, -68.6780,
            });
            put("RW", new Double[] {
                    -1.064, 28.871, -2.867, 30.97
            });
            put("MW", new Double[] {
                    -9.3675, 32.6740, -17.1250, 35.9168
            });
            put("UG", new Double[] {
                    4.2144, 29.5732, -1.4840, 35.0360
            });
            put("IN", new Double[] {
                    36.2617, 68.0323, 6.7471, 97.4030
            });
            put("LR",
                    // new Double[] { 4.353060, -11.492080, 8.551790, -7.365110 });
                    new Double[] {
                            3.0, -14.7, 8.551790, -7.365110
                    });
        }
    };

    private Map<String, String> COUNTRY_NAME = new HashMap<String, String>() {
        private static final long serialVersionUID = -6506773226209066480L;

        {
            put("GT", "Guatemala");
            put("HN", "Honduras");
            put("NI", "Nicaragua");
            put("DO", "Dominican Republic");
            put("EC", "Ecuador");
            put("BO", "Bolivia");
            put("PE", "Peru");
            put("RW", "Rwanda");
            put("MW", "Malawi");
            put("UG", "Uganda");
            put("IN", "India");
        }
    };

    /**
     * returns the 2-letter country code for the lat/lon location passed in
     */
    public String getCountryCodeForPoint(String lat, String lon) {
        OGRFeatureDao ogrFeatureDao = new OGRFeatureDao();
        List<OGRFeature> ogrList = ogrFeatureDao.listByExtentAndType(
                Double.parseDouble(lon), Double.parseDouble(lat),
                FeatureType.COUNTRY, "x1", "asc", "all");
        String countryCode = null;
        for (OGRFeature item : ogrList) {
            Geometry geo = item.getGeometry();
            GeometryFactory geometryFactory = new GeometryFactory();
            WKTReader reader = new WKTReader(geometryFactory);
            com.vividsolutions.jts.geom.Geometry shape = null;
            if (geo != null && geo.getType() != null) {
                try {
                    if (geo.getType().equals(GeometryType.POLYGON)) {
                        shape = (Polygon) reader.read(geo.getWktText());
                    } else if (geo.getType().equals(GeometryType.MULITPOLYGON)) {
                        shape = (MultiPolygon) reader.read(geo.getWktText());
                    }
                } catch (ParseException e) {
                    log.log(Level.SEVERE, e.getMessage());
                }
                Coordinate coord = new Coordinate(Double.parseDouble(lon),
                        Double.parseDouble(lat));
                Point point = geometryFactory.createPoint(coord);
                if (shape != null && shape.contains(point)) {
                    countryCode = item.getCountryCode();
                    break;
                }
            } else {
                log.log(Level.INFO, item.getCountryCode()
                        + " has a null geometry");
            }
        }
        return countryCode;

    }

    // /**
    // * returns the 2-letter country code for the lat/lon location passed in
    // */
    // public String getCountryCodeForPoint(String lat, String lon) {
    // String countryCode = null;
    // countryCode = callApi(COUNTRY_SERVICE_URL, lat, lon, true);
    // if (countryCode != null) {
    // countryCode = countryCode.trim();
    // } else {
    // GeoPlace p = manualLookup(lat, lon);
    // if (p != null) {
    // countryCode = p.getCountryCode();
    // }
    // }
    // return countryCode;
    // }

    /**
     * returns a geo place object that is closest to the lat/lon passed in.
     */
    public GeoPlace findGeoPlace(String lat, String lon) {
        GeoPlaces places = parseXml(callApi(PLACE_SERVICE_URL, lat, lon, false));
        if (places != null && places.getGeoname() != null) {
            return places.getGeoname().get(0);
        } else {
            return manualLookup(lat, lon);
        }
    }

    /**
     * Forms a service url and calls the api, returning the entire response body as a string
     */
    private String callApi(String base, String lat, String lon, boolean retry) {
        String result = null;
        try {
            result = invokeApi(base, lat, lon);
        } catch (IOException ie) {
            // retry because of timeout
            log.log(Level.WARNING, "Timeout for " + base, ie);
            if (retry) {
                try {
                    result = invokeApi(base, lat, lon);
                } catch (Exception e) {
                    log.log(Level.WARNING,
                            "Could not invoke geonames api via url " + base, e);
                }
            }
        } catch (Exception e) {
            log.log(Level.WARNING, "Could not invoke geonames api via url "
                    + base, e);
        }
        return result;
    }

    private String invokeApi(String base, String lat, String lon)
            throws IOException, Exception {
        URL url = new URL(base + LAT_PARAM + "=" + lat + "&" + LON_PARAM + "="
                + lon);
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                url.openStream()));
        String line = null;
        StringBuilder builder = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        reader.close();
        return builder.toString();
    }

    private GeoPlaces parseXml(String xmlString) {
        GeoPlaces places = null;
        if (xmlString != null) {
            try {
                JAXBContext jc = JAXBContext.newInstance(GeoPlaces.class,
                        GeoPlace.class);
                Unmarshaller unmarshaller;
                unmarshaller = jc.createUnmarshaller();
                StringReader sr = new StringReader(xmlString);
                places = (GeoPlaces) unmarshaller.unmarshal(sr);

            } catch (JAXBException e) {
                log.log(Level.SEVERE, "Could not parse api response", e);
            }
        } else {
            log.log(Level.SEVERE, "Geonames response xml was null");
        }
        return places;
    }

    /**
     * absolute fall-back lookup for geo places that uses a statically intialized bounding box
     * 
     * @param latStr
     * @param lonStr
     * @return
     */
    private GeoPlace primitiveLookup(String latStr, String lonStr) {
        GeoPlace place = null;
        try {
            if (latStr != null && lonStr != null) {
                double lat = Double.parseDouble(latStr);
                double lon = Double.parseDouble(lonStr);
                for (Entry<String, Double[]> entry : COUNTRY_MBR.entrySet()) {
                    if (lat <= entry.getValue()[0]
                            && lat >= entry.getValue()[2]
                            && lon >= entry.getValue()[1]
                            && lon <= entry.getValue()[3]) {
                        place = new GeoPlace();
                        place.setCountryCode(entry.getKey());
                        place.setCountryName(COUNTRY_NAME.get(entry.getKey()));
                        break;
                    }
                }
            } else {
                log.log(Level.SEVERE, "Lat or lon is null");
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "Lat/Lon are non numeric: ", e);
        }
        return place;
    }

    public GeoPlace manualLookup(String latStr, String lonStr) {
        return manualLookup(latStr, lonStr, FeatureType.COUNTRY);
    }

    public GeoPlace manualLookup(String latStr, String lonStr,
            OGRFeature.FeatureType type) {
        log.log(Level.INFO, "Inside Manaual Lookup for " + latStr + ":"
                + lonStr + ":" + type.toString());
        GeoPlace place = null;
        OGRFeatureDao ogrFeatureDao = new OGRFeatureDao();

        // create a list of candidates, based on a bounding box search
        List<OGRFeature> ogrList = ogrFeatureDao.listByExtentAndType(
                Double.parseDouble(lonStr), Double.parseDouble(latStr), type,
                "x1", "asc", "all");
        String countryCode = null;
        // now we have to check if one of the candidates is the one by looking at the real GIS data
        for (OGRFeature item : ogrList) {
            Geometry geo = item.getGeometry();
            GeometryFactory geometryFactory = new GeometryFactory();
            WKTReader reader = new WKTReader(geometryFactory);
            com.vividsolutions.jts.geom.Geometry shape = null;
            if (geo != null && geo.getType() != null) {
                try {
                    if (geo.getType().equals(GeometryType.POLYGON)) {
                        shape = (Polygon) reader.read(geo.getWktText());
                    } else if (geo.getType().equals(GeometryType.MULITPOLYGON)) {
                        shape = (MultiPolygon) reader.read(geo.getWktText());
                    }
                } catch (ParseException e) {
                    log.log(Level.SEVERE, e.getMessage());
                }
                Coordinate coord = new Coordinate(Double.parseDouble(lonStr),
                        Double.parseDouble(latStr));
                Point point = geometryFactory.createPoint(coord);
                Boolean containsFlag = false;
                if (shape != null) {

                    // we iterate over the number of geometries in the shape,
                    // as their might be disjoined items such as islands
                    for (int i = 0; i < shape.getNumGeometries(); i++) {
                        // this is where we check if the point actually lies in the geometry
                        if (shape.getGeometryN(i).contains(point)) {
                            containsFlag = true;
                        }
                    }
                    if (containsFlag) {
                        place = new GeoPlace();
                        countryCode = item.getCountryCode();
                        if (countryCode != null) {
                            place.setCountryCode(countryCode);
                            place.setCountryName(item.getName());
                        }
                        place.setSub1(item.getSub1());
                        place.setSub2(item.getSub2());
                        place.setSub3(item.getSub3());
                        place.setSub4(item.getSub4());
                        place.setSub5(item.getSub5());
                        place.setSub6(item.getSub6());
                        log.log(Level.INFO,
                                "Found point inside " + item.getCountryCode()
                                        + " " + item.toString());
                    }
                }
            } else {
                log.log(Level.INFO, item.getCountryCode()
                        + " has a null geometry");
            }
        }
        // if we are trying to find the subcountry, and can't find it
        // try the country
        if (place == null && type.equals(OGRFeature.FeatureType.SUB_COUNTRY_OTHER)) {
            place = manualLookup(latStr, lonStr, OGRFeature.FeatureType.COUNTRY);
        }

        // if we can't find the country, search on bounding box
        if (place == null) {
            place = primitiveLookup(latStr, lonStr);
        }
        return place;
    }

    public GeoPlace resolveSubCountry(String latStr, String lonStr,
            String countryCode) {
        GeoPlace place = null;
        if (countryCode != null && latStr != null && lonStr != null) {

            OGRFeatureDao ogrFeatureDao = new OGRFeatureDao();
            List<OGRFeature> ogrList = ogrFeatureDao.listByExtentTypeCountry(
                    Double.parseDouble(lonStr), Double.parseDouble(latStr),
                    countryCode, "x1", "asc", "all");
            for (OGRFeature item : ogrList) {
                Geometry geo = item.getGeometry();
                GeometryFactory geometryFactory = new GeometryFactory();
                WKTReader reader = new WKTReader(geometryFactory);
                com.vividsolutions.jts.geom.Geometry shape = null;
                if (geo != null && geo.getType() != null) {
                    try {
                        if (geo.getType().equals(GeometryType.POLYGON)) {
                            shape = (Polygon) reader.read(geo.getWktText());
                        } else if (geo.getType().equals(
                                GeometryType.MULITPOLYGON)) {
                            shape = (MultiPolygon) reader
                                    .read(geo.getWktText());
                        }
                    } catch (ParseException e) {
                        log.log(Level.SEVERE, e.getMessage());
                    }
                    Coordinate coord = new Coordinate(
                            Double.parseDouble(lonStr),
                            Double.parseDouble(latStr));
                    Point point = geometryFactory.createPoint(coord);
                    if (shape != null && shape.contains(point)) {
                        place = new GeoPlace();
                        countryCode = item.getCountryCode();
                        place.setCountryCode(countryCode);
                        place.setCountryName(item.getName());
                        place.setSub1(item.getSub1());
                        place.setSub2(item.getSub2());
                        place.setSub3(item.getSub3());
                        place.setSub4(item.getSub4());
                        place.setSub5(item.getSub5());
                        place.setSub6(item.getSub6());
                        log.log(Level.INFO,
                                "Found point inside " + item.getCountryCode()
                                        + " " + item.toString());
                    }
                } else {
                    log.log(Level.INFO, item.getCountryCode()
                            + " has a null geometry");
                }
            }
        }
        return place;
    }

    /**
     * tries 4 different things to resolve the lat/lon to a geo place (as soon as a method returns a
     * non-null place, execution short-circuits:
     * <ul>
     * <li>Look in the shapefile for a sub_country feature</li>
     * <li>try to resolve the country using the statically configured bounding boxes</li>
     * <li>Look in the shapefiles for a country feature</li>
     * <li>invoke the geonames service to find a place</li>
     * </ul>
     */
    @Override
    public GeoPlace findDetailedGeoPlace(String lat, String lon) {
        // first, check the shape files
        GeoPlace gp = manualLookup(lat, lon,
                OGRFeature.FeatureType.SUB_COUNTRY_OTHER);
        // if we don't find the sub_country, try the country
        // This is also already tried in the manualLookup method
        if (gp == null) {
            gp = manualLookup(lat, lon);
        }
        // if it's still null, then try calling the geonames service
        if (gp == null) {
            gp = findGeoPlace(lat, lon);
        }
        return gp;
    }

}
