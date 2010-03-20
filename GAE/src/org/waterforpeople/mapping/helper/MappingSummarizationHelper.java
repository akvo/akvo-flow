package org.waterforpeople.mapping.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.waterforpeople.mapping.dao.AccessPointDAO;
import org.waterforpeople.mapping.dao.GeoIndexDao;
import org.waterforpeople.mapping.domain.AccessPoint;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.index.strtree.STRtree;

/**
 * This helper performs mapping summarization as well as abstracts clients from
 * details of the JTS GIS package. <b>No JTS classes should leak out of this
 * layer.</b>
 * 
 * Some of the methods (addPointToPoly, for instance) are STATEFUL in that they
 * can be used to accumulate points over multiple method invocations in member
 * variables so that they can eventually be flushed to an index in a single
 * operation
 * 
 * @author Christopher Fagiani
 * 
 */
public class MappingSummarizationHelper {
	private AccessPointDAO accessPointDao;
	private GeoIndexDao geoIndexDao;

	private Map<String, StringBuilder> regionMap;
	private Map<String, String> firstPositionInPolys;

	public MappingSummarizationHelper() {
		accessPointDao = new AccessPointDAO();
		geoIndexDao = new GeoIndexDao();
	}

	/**
	 * adds the lat,lon point to the polygon identified by the UUID passed in.
	 * This method is STATEFUL in that this class will continue to accumulate
	 * points in a member variable each time this method is called. If you want
	 * to reset the internal member, call resetPoly
	 * 
	 * @param uuid
	 * @param lat
	 * @param lon
	 */
	public void addPointToRegion(String uuid, String lat, String lon) {
		if (regionMap == null) {
			resetPoly();
		}
		StringBuilder buf = regionMap.get(uuid);
		if (buf == null) {
			buf = new StringBuilder();
			buf.append(lat + " " + lon);
			regionMap.put(uuid, buf);
			firstPositionInPolys.put(uuid, lat + " " + lon);
		} else {
			buf.append("," + lat + " " + lon);
		}
	}

	/**
	 * Persists all the accumulated points in the region map and resets the
	 * internal member variables
	 */
	public void saveRegions() {
		if (regionMap != null) {
			GeoIndexDao indexDao = new GeoIndexDao();
			Map<String, String> regionStrings = new HashMap<String, String>();
			for (String region : regionMap.keySet()) {
				regionStrings.put(region, "POLYGON((" + regionMap.get(region)
						+ "," + firstPositionInPolys.get(region) + "))");

			}
			indexDao.saveRegionIndex(regionStrings);
			resetPoly();
		}
	}

	/**
	 * resets the internal store
	 */
	public void resetPoly() {
		regionMap = new HashMap<String, StringBuilder>();
		firstPositionInPolys = new HashMap<String, String>();
	}

	/**
	 * executes a summarization action for a given region. It will iterate over
	 * all access points in that region and use the rule type to evalute what
	 * should be done
	 * 
	 * @param regionUUID
	 * @param type
	 * @return
	 */
	public String processSummarization(String regionUUID, String type) {
		String result = null;
		List<AccessPoint> accessPoints = findPointsInRegion(regionUUID);

		if (accessPoints != null && accessPoints.size() > 0) {
			// now we have the list of access points in the region
			// so run the rule
			// TODO: define abstract rule handling. right now, we hard code the
			// 1 rule we have
			int functionalCount = 0;
			for (AccessPoint ap : accessPoints) {
				if ("High".equalsIgnoreCase(ap.getPointStatus())
						|| "Ok".equalsIgnoreCase(ap.getPointStatus())) {
					functionalCount++;
				}
			}
			double pctFunctional = functionalCount / accessPoints.size();
			if (pctFunctional >= 0.75) {
				result = "GREEN";
			} else if (pctFunctional >= .5) {
				result = "YELLOW";
			} else {
				result = "RED";
			}
		}
		return result;
	}

	/**
	 * returns the list of AccessPoint objects that are within the region
	 * identified by the uuid
	 * 
	 * @param regionUUID
	 * @return
	 */
	public List<AccessPoint> findPointsInRegion(String regionUUID) {
		// first, get the index for the region since, if we don't have that, we
		// can't do anything
		STRtree regionIndex = geoIndexDao.findGeoIndex(regionUUID);
		// TODO: filter access points!!! for now, we get them all
		List<AccessPoint> accessPoints = accessPointDao.listAccessPoints();
		List<AccessPoint> pointsInRegion = new ArrayList<AccessPoint>();
		if (accessPoints != null && regionIndex != null) {

			GeometryFactory geomFactory = new GeometryFactory();
			for (AccessPoint p : accessPoints) {
				Point point = geomFactory.createPoint(new Coordinate(p
						.getLatitude(), p.getLongitude()));
				if (!regionIndex.query(point.getEnvelopeInternal()).isEmpty()) {
					pointsInRegion.add(p);
				}
			}
		}
		return pointsInRegion;
	}
}
