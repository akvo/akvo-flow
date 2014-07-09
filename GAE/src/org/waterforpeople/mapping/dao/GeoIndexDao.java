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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import services.S3Driver;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.index.strtree.STRtree;
import com.vividsolutions.jts.io.WKTReader;

/**
 * This data access object will persist and retrieve Geo information from the GIS index. This
 * implementation uses JTS (see http://www.vividsolutions.com/jts/jtshome.htm).
 * 
 * @author Christopher Fagiani
 */
public class GeoIndexDao {
    private static final Logger log = Logger.getLogger(GeoIndexDao.class
            .getName());

    private static final String INDEX_BASE_URL = "http://dru-test.s3.amazonaws.com/gis/index/";

    /**
     * this will create (or replace) a geo index for each of the regions passed in.
     * 
     * @param regions - map with regions uuid as key and POLYGON WellKnownFormat strings as values
     */
    public void saveRegionIndex(Map<String, String> regions) {
        GeometryFactory factory;
        STRtree index = new STRtree();
        try {
            S3Driver s3Driver = new S3Driver();
            for (String region : regions.keySet()) {
                factory = new GeometryFactory();
                WKTReader reader = new WKTReader(factory);
                Polygon regionPolygon = (Polygon) reader.read(regions
                        .get(region));

                index
                        .insert(regionPolygon.getEnvelopeInternal(),
                                regionPolygon);

                index.build();

                ByteArrayOutputStream byteArr = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(byteArr);
                oos.writeObject(index);

                // NOTE: this will give an exception in the Dev environment due
                // to a bug with the GAE local implementation. It'll work on the
                // server.
                s3Driver.uploadFile("dru-test", "gis/index/" + region, byteArr
                        .toByteArray());

                oos.close();
            }

        } catch (Exception e) {
            log.log(Level.WARNING, "Could not upload index", e);
        }
    }

    /**
     * fetches a pre-generated index
     * 
     * @param regionUUID
     * @return
     */
    public STRtree findGeoIndex(String regionUUID) {
        STRtree index = null;
        ObjectInputStream ois = null;
        try {
            URL url = new URL(INDEX_BASE_URL + regionUUID);
            ois = new ObjectInputStream(url.openStream());
            index = (STRtree) ois.readObject();
        } catch (Exception e) {
            log.log(Level.WARNING, "Could not download index", e);
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    // no-op
                }
            }
        }
        return index;
    }
}
