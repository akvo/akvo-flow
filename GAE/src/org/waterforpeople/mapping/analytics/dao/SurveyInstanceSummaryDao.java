package org.waterforpeople.mapping.analytics.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;

import org.waterforpeople.mapping.analytics.domain.SurveyInstanceSummary;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.common.util.DateUtil;
import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;

/**
 * logic for saving/finding SurveyInstanceSummary objects
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyInstanceSummaryDao extends BaseDAO<SurveyInstanceSummary> {

	public SurveyInstanceSummaryDao() {
		super(SurveyInstanceSummary.class);
	}

	/**
	 * synchronized static method so that only 1 thread can be updating a
	 * summary at a time. This is inefficient but is the only way we can be sure
	 * we're keeping the count consistent since there is no "select for update"
	 * or sql dml-like construct
	 * 
	 * @param answer
	 */
	@SuppressWarnings("rawtypes")
	public static synchronized void incrementCount(String community,
			String country, Date collectionDate) {
		PersistenceManager pm = PersistenceFilter.getManager();
		javax.jdo.Query query = pm.newQuery(SurveyInstanceSummary.class);
		Date colDate = DateUtil.getDateNoTime(collectionDate);
		query
				.setFilter("countryCode == countryCodeParam && communityCode == communityCodeParam && collectionDate == collectionDateParam");
		query
				.declareParameters("String countryCodeParam, String communityCodeParam, Date collectionDateParam");
		// have to import the date class before we can use it
		query.declareImports("import java.util.Date");
		List results = (List) query.execute(community, country, colDate);
		SurveyInstanceSummary summary = null;
		if (results == null || results.size() == 0) {
			summary = new SurveyInstanceSummary();
			summary.setCount(new Long(1));
			summary.setCommunityCode(community);
			summary.setCountryCode(country);
			summary.setCollectionDate(colDate);
		} else {
			summary = (SurveyInstanceSummary) results.get(0);
			summary.setCount(summary.getCount() + 1);
		}
		SurveyInstanceSummaryDao thisDao = new SurveyInstanceSummaryDao();
		thisDao.save(summary);
	}

	/**
	 * Lists all summary objects matching the country and/or community passed
	 * in. If both are null, all results are returned
	 * 
	 * @param countryCode
	 * @param communityCode
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<SurveyInstanceSummary> listByLocation(String countryCode,
			String communityCode) {
		PersistenceManager pm = PersistenceFilter.getManager();
		javax.jdo.Query query = pm.newQuery(SurveyInstanceSummary.class);
		List<SurveyInstanceSummary> results = null;
		if (countryCode != null || communityCode != null) {
			StringBuilder filter = new StringBuilder();
			StringBuilder param = new StringBuilder();
			Map<String, Object> paramMap = new HashMap<String, Object>();
			this.appendNonNullParam("countryCode", filter, param, "String",
					countryCode, paramMap);
			this.appendNonNullParam("communityCode", filter, param, "String",
					communityCode, paramMap);
			query.setFilter(filter.toString());
			query.declareParameters(param.toString());
			results = (List<SurveyInstanceSummary>) query
					.executeWithMap(paramMap);
		} else {
			results = list(Constants.ALL_RESULTS);
		}
		return results;
	}

}
