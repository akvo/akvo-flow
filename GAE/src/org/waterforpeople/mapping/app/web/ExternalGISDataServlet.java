package org.waterforpeople.mapping.app.web;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.geotools.geometry.jts.JTSFactoryFinder;
import org.waterforpeople.mapping.app.web.dto.ExternalGISRequest;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;
import com.gallatinsystems.gis.map.domain.Geometry;
import com.gallatinsystems.gis.map.domain.OGRFeature;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
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
			BaseDAO<OGRFeature> ogrFeatureDao = new BaseDAO<OGRFeature>(
					OGRFeature.class);
			OGRFeature ogrFeature = new OGRFeature();
			ogrFeature.setCountryCode(importReq.getCountryCode());
			ogrFeature.setName(importReq.getName());
			ogrFeature.setProjectCoordinateSystemIdentifier(importReq
					.getGeoCoordinateSystemIdentifier());
			ogrFeature.setDatumIdentifier(importReq.getDatumIdentifier());
			ogrFeature.setReciprocalOfFlattening(importReq
					.getReciprocalOfFlattening());
			ogrFeature.setSpheroid(importReq.getSpheroid());
			Double[] boundingBox = new Double[] { importReq.getX1(),
					importReq.getY1(), importReq.getX2(), importReq.getY2() };
			ogrFeature.setBoundingBox(boundingBox);
			if (importReq.getGeometryString() != null) {
				try {
					Geometry geometry = parseGeometryString(importReq
							.getGeometryString());
					ogrFeature.setGeometry(geometry);
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

	private Geometry parseGeometryString(String geometryString)
			throws ParseException {
		GeometryFactory geometryFactory = JTSFactoryFinder
				.getGeometryFactory(null);
		WKTReader reader = new WKTReader(geometryFactory);
		Polygon polygon = (Polygon) reader.read(geometryString);
		Geometry geo = new Geometry();
		geo.setType("POLYGON");
		for (Coordinate coord : polygon.getCoordinates()) {
			geo.addCoordinate(coord.x, coord.y);
		}
		return geo;
	}

	@Override
	protected void writeOkResponse(RestResponse resp) throws Exception {
		resp.setCode("200");

	}

}
