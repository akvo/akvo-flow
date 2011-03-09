package org.waterforpeople.mapping.app.web;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.waterforpeople.mapping.app.web.dto.ExternalGISRequest;

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
			Double[] boundingBox = new Double[] { importReq.getX1(),
					importReq.getY1(), importReq.getX2(), importReq.getY2() };
			ogrFeature.setBoundingBox(boundingBox);
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
					if(geometry.getCentroidLat()!=null && geometry.getCentroidLon()!=null){
						ogrFeature.setCentroidLat(geometry.getCentroidLat());
						ogrFeature.setCentroidLon(geometry.getCentroidLon());
					}
				} catch (ParseException pe) {
					log.log(Level.SEVERE, pe.getMessage());
				}
			}
			ogrFeatureDao.save(ogrFeature);
		}
		RestResponse resp = new RestResponse();
		resp.setCode("200");
		return resp;
	}

	@SuppressWarnings("deprecation")
	private Geometry parseGeometryString(String geometryString)
			throws ParseException {
		GeometryFactory geometryFactory = new GeometryFactory();
		WKTReader reader = new WKTReader(geometryFactory);
		Geometry geo = new Geometry();
		geo.setWktText(geometryString);
		Point centroid = null;
		if (geometryString.contains("POLYGON")) {
			com.vividsolutions.jts.geom.Geometry geoHolder = null;
			if (geometryString.startsWith("POLYGON")) {
				geo.setType(GeometryType.POLYGON);
				Polygon mp = (Polygon) reader.read(geometryString);
				centroid = mp.getCentroid();
			} else if (geometryString.startsWith("MULTIPOLYGON")) {
				geo.setType(GeometryType.MULITPOLYGON);
				MultiPolygon mp = (MultiPolygon) reader.read(geometryString);
				centroid = mp.getCentroid();
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
		resp.setCode("200");

	}

}
