package org.waterforpeople.mapping.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.waterforpeople.mapping.dao.AccessPointDAO;
import org.waterforpeople.mapping.dao.GeoIndexDao;
import org.waterforpeople.mapping.domain.AccessPoint;

/**
 * This helper performs mapping summarization as well as abstracts clients from
 * details of the JTS GIS package.
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
	 * persistes all the accumulated points in the region map and resets the
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
	 * executes a summarization action. It will look up the region index for the
	 * region specified and, if found, will retrieve all access points that are
	 * within that region
	 * 
	 * @param regionUUID
	 * @param type
	 * @return
	 */
	public List<AccessPoint> processSummarization(String regionUUID, String type) {
		//TODO: get index, then iterate over each access point and check if it's in the region
		//then run the rule identified by "type" on those access points.
		
		/*return accessPointDao.listAccessPointsWithinRegion(lat1, lon1, lat2,
				lon2);*/
		return null;
	}
}
