package com.gallatinsystems.surveyal.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import com.gallatinsystems.surveyal.domain.SurveyedLocale;

/**
 * Data access object for manipulating SurveyedLocales
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyedLocaleDao extends BaseDAO<SurveyedLocale> {

	public SurveyedLocaleDao() {
		super(SurveyedLocale.class);
	}

	/**
	 * lists the set of SurveyedLocales that are within tolerance of the lat/lon
	 * coordinates passed in.
	 * 
	 * @param lat
	 * @param lon
	 * @param tolerance
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<SurveyedLocale> listLocalesByCoordinates(double lat,
			double lon, double tolerance) {
		PersistenceManager pm = PersistenceFilter.getManager();
		javax.jdo.Query query = pm.newQuery(SurveyedLocale.class);
		Map<String, Object> paramMap = null;

		StringBuilder filterString = new StringBuilder();
		StringBuilder paramString = new StringBuilder();
		paramMap = new HashMap<String, Object>();

		appendNonNullParam("latitude", filterString, paramString, "Double", lat
				- tolerance, paramMap, GTE_OP);
		appendNonNullParam("latitude", filterString, paramString, "Double", lat
				+ tolerance, paramMap, LTE_OP);

		query.setFilter(filterString.toString());
		query.declareParameters(paramString.toString());

		List<SurveyedLocale> candidates = (List<SurveyedLocale>) query
				.executeWithMap(paramMap);
		// since the datastore only supports an inequality check on a single
		// parameter at a time, only look at LAT in the query. Filter on Lon
		// afterwards.
		List<SurveyedLocale> results = new ArrayList<SurveyedLocale>();
		if (candidates != null) {
			for (SurveyedLocale l : candidates) {
				if (l.getLongitude() > (lon - tolerance)
						&& l.getLongitude() < (lon + tolerance)) {
					results.add(l);
				}
			}
		}
		return results;
	}

	/**
	 * lists all locales that match the geo constraints passed in
	 * 
	 * @param countryCode
	 * @param level
	 * @param subValue
	 * @param type
	 * @param org
	 * @param cursor
	 * @param desiredResults
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<SurveyedLocale> listBySubLevel(String countryCode,
			Integer level, String subValue, String type, String org,
			String cursor, Integer desiredResults) {
		PersistenceManager pm = PersistenceFilter.getManager();
		javax.jdo.Query query = pm.newQuery(SurveyedLocale.class);
		StringBuilder filterString = new StringBuilder();
		StringBuilder paramString = new StringBuilder();
		Map<String, Object> paramMap = null;
		paramMap = new HashMap<String, Object>();

		appendNonNullParam("localeType", filterString, paramString, "String",
				type, paramMap);
		appendNonNullParam("countryCode", filterString, paramString, "String",
				countryCode, paramMap);
		appendNonNullParam("organization", filterString, paramString, "String",
				org, paramMap);
		if (level != null && level > 0 && level <= 6) {
			appendNonNullParam("sublevel" + level, filterString, paramString,
					"String", subValue, paramMap);
		}
		query.setFilter(filterString.toString());
		query.declareParameters(paramString.toString());
		prepareCursor(cursor, desiredResults, query);
		List<SurveyedLocale> results = (List<SurveyedLocale>) query
				.executeWithMap(paramMap);
		return results;
	}

}
