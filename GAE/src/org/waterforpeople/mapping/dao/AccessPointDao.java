package org.waterforpeople.mapping.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;

import org.waterforpeople.mapping.domain.AccessPoint;

import com.beoui.geocell.GeocellManager;
import com.beoui.geocell.model.GeocellQuery;
import com.beoui.geocell.model.Point;
import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;

/**
 * dao for manipulating access points
 * 
 * @author Christopher Fagiani
 * 
 */
public class AccessPointDao extends BaseDAO<AccessPoint> {
	private static final int MAX_RESULTS = 40;

	public AccessPointDao() {
		super(AccessPoint.class);
	}

	/**
	 * Lists all access points that are near the point identified by the lat/lon
	 * parameters in order of increasing distance. if maxDistance is 0, all
	 * points (up to MAX_RESULTS) are returned otherwise, only those points
	 * maxDistance meters away or less are returned
	 */
	public List<AccessPoint> listNearbyAccessPoints(Double lat, Double lon,
			String countryCode, double maxDistance, String cursor) {
		PersistenceManager pm = PersistenceFilter.getManager();
		if (lat != null && lon != null) {			
			Point loc = new Point(lat, lon);
			List<Object> params = new ArrayList<Object>();
			params.add(countryCode);
			GeocellQuery gq = new GeocellQuery(
					"countryCode == countryCodeParam",
					"String countryCodeParam", params);
			return GeocellManager.proximityFetch(loc, MAX_RESULTS, maxDistance,
					AccessPoint.class, gq, pm);
		} else {
			return listAccessPointByLocation(countryCode, null,null, null,cursor);
		}
	}

	/**
	 * lists all the access points for the country/community/type passed in
	 * 
	 * @param country
	 * @param community
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<AccessPoint> listAccessPointByLocation(String country,
			String community, String type, Date updatedSinceDate, String cursorString) {
		PersistenceManager pm = PersistenceFilter.getManager();
		javax.jdo.Query query = pm.newQuery(AccessPoint.class);
		Map<String, Object> paramMap = null;

		StringBuilder filterString = new StringBuilder();
		StringBuilder paramString = new StringBuilder();
		paramMap = new HashMap<String, Object>();

		appendNonNullParam("countryCode", filterString, paramString, "String",
				country, paramMap);
		appendNonNullParam("communityCode", filterString, paramString,
				"String", community, paramMap);
		appendNonNullParam("pointType", filterString, paramString, "String",
				type, paramMap);
		appendNonNullParam("lastUpdateDateTime", filterString,paramString, "Date",updatedSinceDate,paramMap,GTE_OP);
		if(updatedSinceDate != null){
		       query.declareImports("import java.util.Date");
		}
		query.setOrdering("lastUpdateDateTime desc");
		query.setFilter(filterString.toString());
		query.declareParameters(paramString.toString());

		prepareCursor(cursorString, query);

		List<AccessPoint> results = (List<AccessPoint>) query
				.executeWithMap(paramMap);

		return results;
	}

	/**
	 * searches for access points that match all of the non-null params
	 * 
	 * @param country
	 * @param community
	 * @param constDateFrom
	 * @param constDateTo
	 * @param type
	 * @param tech
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<AccessPoint> searchAccessPoints(String country,
			String community, Date collDateFrom, Date collDateTo, String type,
			String tech, String cursorString) {
		PersistenceManager pm = PersistenceFilter.getManager();
		javax.jdo.Query query = pm.newQuery(AccessPoint.class);
		StringBuilder filterString = new StringBuilder();
		StringBuilder paramString = new StringBuilder();
		Map<String, Object> paramMap = null;
		paramMap = new HashMap<String, Object>();

		appendNonNullParam("countryCode", filterString, paramString, "String",
				country, paramMap);
		appendNonNullParam("communityCode", filterString, paramString,
				"String", community, paramMap);
		appendNonNullParam("pointType", filterString, paramString, "String",
				type, paramMap);
		appendNonNullParam("typeTechnologyString", filterString, paramString,
				"String", tech, paramMap);
		appendNonNullParam("collectionDate", filterString, paramString, "Date",
				collDateFrom, paramMap, GTE_OP);
		appendNonNullParam("collectionDate", filterString, paramString, "Date",
				collDateTo, paramMap, LTE_OP);

		query.setFilter(filterString.toString());
		query.declareParameters(paramString.toString());

		if (collDateFrom != null || collDateTo != null) {
			query.declareImports("import java.util.Date");
		}

		prepareCursor(cursorString, query);
		List<AccessPoint> results = (List<AccessPoint>) query
				.executeWithMap(paramMap);

		return results;
	}

}
