package org.waterforpeople.mapping.analytics.dao;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;

import org.waterforpeople.mapping.analytics.domain.AccessPointStatusSummary;
import org.waterforpeople.mapping.domain.AccessPoint;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import com.gallatinsystems.gis.geography.domain.Country;

/**
 * updates access point status summary objects
 * 
 * @author Christopher Fagiani
 * 
 */
public class AccessPointStatusSummaryDao extends
		BaseDAO<AccessPointStatusSummary> {

	public AccessPointStatusSummaryDao() {
		super(AccessPointStatusSummary.class);
	}

	/**
	 * synchronized static method so that only 1 thread can be updating a
	 * summary at a time. This is inefficient but is the only way we can be sure
	 * we're keeping the count consistent since there is no "select for update"
	 * or sql dml-like construct
	 * 
	 * @param answer
	 */
	@SuppressWarnings("unchecked")
	public static synchronized void incrementCount(AccessPoint ap, Country c) {
		PersistenceManager pm = PersistenceFilter.getManager();
		javax.jdo.Query query = pm.newQuery(AccessPointStatusSummary.class);
		query
				.setFilter("year == yearParam && status == statusParam && community == communityParam && type == typeParam");
		query
				.declareParameters("String yearParam, String statusParam, String communityParam, String typeParam");
		String yearString = null;
		if (ap.getCollectionDate() != null) {
			GregorianCalendar cal = new GregorianCalendar();
			cal.setTime(ap.getCollectionDate());
			yearString = cal.get(Calendar.YEAR) + "";
		}
		List results = (List) query.executeWithArray(yearString, ap
				.getPointStatus(), ap.getCommunityCode(), ap.getPointType()
				.toString());
		AccessPointStatusSummary summary = null;
		if (results == null || results.size() == 0) {
			summary = new AccessPointStatusSummary();
			summary.setCount(new Long(1));
			summary.setYear(yearString);
			summary.setStatus(ap.getPointStatus());
			summary.setCountry(c.getIsoAlpha2Code());
			summary.setCommunity(ap.getCommunityCode());
			summary.setType(ap.getPointType().toString());
			summary.setCostPerUnit(ap.getCostPer());
			try {
				summary.setHouseholdsServed(ap
						.getNumberOfHouseholdsUsingPoint() != null ? new Long(
						ap.getNumberOfHouseholdsUsingPoint().trim()) : 0);
			} catch (NumberFormatException e) {
				Logger
						.getLogger(AccessPointStatusSummaryDao.class.getName())
						.log(Level.SEVERE,
								"Access point has non-integer value for numberOfHouseholdsUsingPoint");
			}
		} else {
			summary = (AccessPointStatusSummary) results.get(0);
			summary.setCount(summary.getCount() + 1);
		}
		AccessPointStatusSummaryDao thisDao = new AccessPointStatusSummaryDao();
		thisDao.save(summary);
	}

	/**
	 * lists access point summary objects that match the criteria passed in, any
	 * of which are nullable.
	 * 
	 * @param country
	 * @param community
	 * @param year
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<AccessPointStatusSummary> listByLocationAndYear(String country,
			String community, String year, String type, String status) {
		PersistenceManager pm = PersistenceFilter.getManager();
		javax.jdo.Query query = pm.newQuery(AccessPointStatusSummary.class);

		StringBuilder filterString = new StringBuilder();
		StringBuilder paramString = new StringBuilder();
		Map<String, Object> paramMap = new HashMap<String, Object>();

		appendNonNullParam("country", filterString, paramString, "String",
				country, paramMap);
		appendNonNullParam("community", filterString, paramString, "String",
				community, paramMap);
		appendNonNullParam("year", filterString, paramString, "String", year,
				paramMap);
		appendNonNullParam("type", filterString, paramString, "String", type,
				paramMap);
		appendNonNullParam("status", filterString, paramString, "String",
				type, paramMap);
		return (List<AccessPointStatusSummary>) query.executeWithMap(paramMap);
	}

}
