package org.waterforpeople.mapping.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;

import org.waterforpeople.mapping.domain.AccessPoint;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;

/**
 * dao for manipulating access points
 * 
 * @author Christopher Fagiani
 * 
 */
public class AccessPointDao extends BaseDAO<AccessPoint> {

	public AccessPointDao() {
		super(AccessPoint.class);
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
			String community, String type) {
		PersistenceManager pm = PersistenceFilter.getManager();
		javax.jdo.Query query = pm.newQuery(AccessPoint.class);

		StringBuilder filterString = new StringBuilder();
		StringBuilder paramString = new StringBuilder();
		Map<String, Object> paramMap = new HashMap<String, Object>();

		appendNonNullParam("countryCode", filterString, paramString, "String",
				country, paramMap);
		appendNonNullParam("communityCode", filterString, paramString,
				"String", community, paramMap);
		appendNonNullParam("pointType", filterString, paramString, "String",
				type, paramMap);
		query.setFilter(filterString.toString());
		query.declareParameters(paramString.toString());
		return (List<AccessPoint>) query.executeWithMap(paramMap);
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
			String community, Date collDateFrom, Date collDateTo,
			String type, String tech) {
		PersistenceManager pm = PersistenceFilter.getManager();
		javax.jdo.Query query = pm.newQuery(AccessPoint.class);

		StringBuilder filterString = new StringBuilder();
		StringBuilder paramString = new StringBuilder();
		Map<String, Object> paramMap = new HashMap<String, Object>();

		appendNonNullParam("countryCode", filterString, paramString, "String",
				country, paramMap);
		appendNonNullParam("communityCode", filterString, paramString,
				"String", community, paramMap);
		appendNonNullParam("pointType", filterString, paramString, "String",
				type, paramMap);
		appendNonNullParam("typeTechnologyString", filterString, paramString,
				"String", tech, paramMap);
		appendNonNullParam("collectionDate", filterString, paramString,
				"Date", collDateFrom, paramMap, GTE_OP);
		appendNonNullParam("collectionDate", filterString, paramString,
				"Date", collDateTo, paramMap, LTE_OP);

		if (collDateFrom != null || collDateTo != null) {
			query.declareImports("import java.util.Date");
		}

		query.setFilter(filterString.toString());
		query.declareParameters(paramString.toString());
		return (List<AccessPoint>) query.executeWithMap(paramMap);
	}

}
