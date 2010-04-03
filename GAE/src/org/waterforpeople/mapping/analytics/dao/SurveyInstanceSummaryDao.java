package org.waterforpeople.mapping.analytics.dao;

import java.util.Date;
import java.util.List;

import javax.jdo.PersistenceManager;

import org.waterforpeople.mapping.analytics.domain.SurveyInstanceSummary;

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
	@SuppressWarnings("unchecked")
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

}
