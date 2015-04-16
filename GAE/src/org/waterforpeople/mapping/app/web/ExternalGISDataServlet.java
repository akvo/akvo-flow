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

package org.waterforpeople.mapping.app.web;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.waterforpeople.mapping.app.util.DtoMarshaller;
import org.waterforpeople.mapping.app.web.dto.ExternalGISRequest;
import org.waterforpeople.mapping.app.web.dto.OGRFeatureDto;
import org.waterforpeople.mapping.app.web.dto.OGRFeatureRestResponse;

import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;
import com.gallatinsystems.gis.map.dao.OGRFeatureDao;
import com.gallatinsystems.gis.map.domain.Geometry;
import com.gallatinsystems.gis.map.domain.Geometry.GeometryType;
import com.gallatinsystems.gis.map.domain.OGRFeature;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

public class ExternalGISDataServlet extends AbstractRestApiServlet {
    private static final Logger log = Logger
            .getLogger(ExternalGISDataServlet.class.getName());
    /**
	 * 
	 */
    private static final long serialVersionUID = -521412331243490340L;

    @Override
    protected RestRequest convertRequest() throws Exception {
        HttpServletRequest req = getRequest();
        RestRequest restRequest = new ExternalGISRequest();
        restRequest.populateFromHttpRequest(req);
        return restRequest;

    }

    @Override
    protected RestResponse handleRequest(RestRequest req) throws Exception {
        ExternalGISRequest importReq = (ExternalGISRequest) convertRequest();
        RestResponse resp = new RestResponse();
        if (req.getAction().equals(ExternalGISRequest.IMPORT_ACTION)) {
            OGRFeatureDao ogrFeatureDao = new OGRFeatureDao();
            OGRFeature ogrFeature = new OGRFeature();
            ogrFeature.setCountryCode(importReq.getCountryCode());
            ogrFeature.setName(importReq.getName());
            ogrFeature.setProjectCoordinateSystemIdentifier(importReq
                    .getGeoCoordinateSystemIdentifier());
            ogrFeature.setDatumIdentifier(importReq.getDatumIdentifier());
            ogrFeature.setReciprocalOfFlattening(importReq
                    .getReciprocalOfFlattening());
            ogrFeature.setSpheroid(importReq.getSpheroid());
            ogrFeature.setUnCode(importReq.getUnCode());
            ogrFeature.setPop2005(importReq.getPop2005());
            ogrFeature.setCentroidLat(importReq.getCentroidLat());
            ogrFeature.setCentroidLon(importReq.getCentroidLon());
            // Double[] boundingBox = new Double[] { importReq.getX1(),
            // importReq.getY1(), importReq.getX2(), importReq.getY2() };
            // ogrFeature.setBoundingBox(boundingBox);
            ogrFeature.setFeatureType(importReq.getOgrFeatureType());
            ogrFeature.setSub1(importReq.getSub1());
            ogrFeature.setSub2(importReq.getSub2());
            ogrFeature.setSub3(importReq.getSub3());
            ogrFeature.setSub4(importReq.getSub4());
            ogrFeature.setSub5(importReq.getSub5());
            ogrFeature.setSub6(importReq.getSub6());
            ogrFeature.setTotalPopulation(importReq.getTotalPopulation());
            ogrFeature.setDensity(importReq.getDensity());
            ogrFeature.setFemalePopulation(importReq.getFemalePopulation());
            ogrFeature.setMalePopulation(importReq.getMalePopulation());
            ogrFeature.setNumberHouseholds(importReq.getNumberHouseholds());

            if (importReq.getGeometryString() != null) {
                try {
                    Geometry geometry = parseGeometryString(importReq
                            .getGeometryString());
                    ogrFeature.setGeometry(geometry);
                    if (geometry.getCentroidLat() != null
                            && geometry.getCentroidLon() != null) {
                        ogrFeature.setCentroidLat(geometry.getCentroidLat());
                        ogrFeature.setCentroidLon(geometry.getCentroidLon());
                    }
                    Double x1;
                    Double y1;
                    Double x2;
                    Double y2;
                    // int length = geometry.getBoundingBox().length;
                    x1 = geometry.getBoundingBox()[1].x;
                    y1 = geometry.getBoundingBox()[1].y;
                    x2 = geometry.getBoundingBox()[3].x;
                    y2 = geometry.getBoundingBox()[3].y;
                    Double[] boundingBox = new Double[] {
                            x1, y1, x2, y2
                    };
                    ogrFeature.setBoundingBox(boundingBox);
                } catch (ParseException pe) {
                    log.log(Level.SEVERE, pe.getMessage());
                }
            }

            ogrFeatureDao.save(ogrFeature);
            resp = convertToResponse(null, null, null);
        } else if (req.getAction().equals(
                ExternalGISRequest.LIST_MATCHING_OGRFEATURE_ACTION)) {
            OGRFeatureDao ogrFeatDao = new OGRFeatureDao();
            String subLevelValue = null;
            Integer level = null;
            if (importReq.getSub1() != null) {
                subLevelValue = importReq.getSub1();
                level = 1;
            } else if (importReq.getSub2() != null) {
                subLevelValue = importReq.getSub2();
                level = 2;
            } else if (importReq.getSub3() != null) {
                subLevelValue = importReq.getSub3();
                level = 3;
            } else if (importReq.getSub4() != null) {
                subLevelValue = importReq.getSub4();
                level = 4;
            } else if (importReq.getSub5() != null) {
                subLevelValue = importReq.getSub5();
                level = 5;
            } else if (importReq.getSub6() != null) {
                subLevelValue = importReq.getSub6();
                level = 6;
            }
            List<OGRFeature> ogrFeatureList = ogrFeatDao
                    .listBySubLevelCountryName(importReq.getCountryCode(),
                            level, subLevelValue, null, null);
            resp = convertToResponse(ogrFeatureList, OGRFeatureDao
                    .getCursor(ogrFeatureList), importReq.getCursor());

        }
        return resp;
    }

    private RestResponse convertToResponse(List<OGRFeature> ogrList,
            String cursor, String oldCursor) {
        OGRFeatureRestResponse resp = new OGRFeatureRestResponse();
        if (ogrList != null) {
            List<OGRFeatureDto> dtoList = new ArrayList<OGRFeatureDto>();
            for (OGRFeature item : ogrList) {
                dtoList.add(marshallDomainToDto(item));
            }
            resp.setOgrFeatures(dtoList);
        }
        return resp;
    }

    private OGRFeatureDto marshallDomainToDto(OGRFeature item) {
        OGRFeatureDto dto = new OGRFeatureDto();
        DtoMarshaller.copyToDto(item, dto);
        return dto;
    }

    private Geometry parseGeometryString(String geometryString)
            throws ParseException {
        GeometryFactory geometryFactory = new GeometryFactory();
        WKTReader reader = new WKTReader(geometryFactory);
        Geometry geo = new Geometry();
        geo.setWktText(geometryString);
        Point centroid = null;
        if (geometryString.contains("POLYGON")) {
            if (geometryString.startsWith("POLYGON")) {
                geo.setType(GeometryType.POLYGON);
                Polygon mp = (Polygon) reader.read(geometryString);
                centroid = mp.getCentroid();
            } else if (geometryString.startsWith("MULTIPOLYGON")) {
                geo.setType(GeometryType.MULITPOLYGON);
                MultiPolygon mp = (MultiPolygon) reader.read(geometryString);
                centroid = mp.getCentroid();
                com.vividsolutions.jts.geom.Geometry e = mp.getEnvelope();
                Coordinate[] boundingBox = e.getCoordinates();
                geo.setBoundingBox(boundingBox);
            }
            if (centroid != null) {
                geo.setCentroidLat(centroid.getY());
                geo.setCentroidLon(centroid.getX());
            }
        } else if (geometryString.startsWith("POINT")) {
            Point point = (Point) reader.read(geometryString);
            geo.setType(GeometryType.POINT);
            Coordinate coord = point.getCoordinate();
            geo.addCoordinate(coord.x, coord.y);
        }
        return geo;
    }

    @Override
    protected void writeOkResponse(RestResponse resp) throws Exception {
        getResponse().setStatus(200);
        OGRFeatureRestResponse piResp = (OGRFeatureRestResponse) resp;
        JSONObject result = new JSONObject(piResp);
        getResponse().getWriter().println(result.toString());

    }

}
