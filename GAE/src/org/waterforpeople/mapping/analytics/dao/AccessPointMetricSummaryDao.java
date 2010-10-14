package org.waterforpeople.mapping.analytics.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;

import org.waterforpeople.mapping.analytics.domain.AccessPointMetricSummary;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;

/**
 * Dao for manipulating access point summary domain objects
 * 
 * @author Christopher Fagiani
 * 
 */
public class AccessPointMetricSummaryDao extends
		BaseDAO<AccessPointMetricSummary> {

	public AccessPointMetricSummaryDao() {
		super(AccessPointMetricSummary.class);
	}

	/**
	 * lists metrics that match the prototype passed in. The object passed in
	 * must have at least 1 field populated (besides count). In practice,
	 * callers should populate as many fields as possible to narrow results
	 * 
	 * @param prototype
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<AccessPointMetricSummary> listMetrics(
			AccessPointMetricSummary prototype) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder filterString = new StringBuilder();
		StringBuilder paramString = new StringBuilder();

		appendNonNullParam("organization", filterString, paramString, "String",
				prototype.getOrganization(), paramMap);
		appendNonNullParam("country", filterString, paramString, "String",
				prototype.getCountry(), paramMap);
		appendNonNullParam("district", filterString, paramString, "String",
				prototype.getDistrict(), paramMap);		
		appendNonNullParam("subgroup1", filterString, paramString, "String",
				prototype.getSubgroup1(), paramMap);
		appendNonNullParam("subgroup2", filterString, paramString, "String",
				prototype.getSubgroup2(), paramMap);
		appendNonNullParam("subgroup3", filterString, paramString, "String",
				prototype.getSubgroup3(), paramMap);
		appendNonNullParam("metricName", filterString, paramString, "String",
				prototype.getMetricName(), paramMap);
		appendNonNullParam("metricGroup", filterString, paramString, "String",
				prototype.getMetricGroup(), paramMap);
		appendNonNullParam("metricValue", filterString, paramString, "String",
				prototype.getMetricValue(), paramMap);

		PersistenceManager pm = PersistenceFilter.getManager();
		javax.jdo.Query query = pm.newQuery(AccessPointMetricSummary.class);
		query.setFilter(filterString.toString());
		query.declareParameters(paramString.toString());

		return (List<AccessPointMetricSummary>) query.executeWithMap(paramMap);

	}

	/**
	 * synchronized static method so that only 1 thread can be updating a
	 * summary at a time. This is inefficient but is the only way we can be sure
	 * we're keeping the count consistent since there is no "select for update"
	 * or sql dml-like construct. When this method is called, the metric object
	 * passed in must have its value populated as well as at LEAST the
	 * organization and country.
	 * 
	 * @param answer
	 */
	public static synchronized void incrementCount(
			AccessPointMetricSummary metric, int unit) {
		AccessPointMetricSummaryDao dao = new AccessPointMetricSummaryDao();
		List<AccessPointMetricSummary> results = dao.listMetrics(metric);
		AccessPointMetricSummary summary = null;
		if ((results == null || results.size() == 0) && unit > 0) {
			metric.setCount(new Long(unit));
			summary = metric;
		} else if (results != null && results.size() > 0) {
			summary = (AccessPointMetricSummary) results.get(0);
			summary.setCount(summary.getCount() + unit);
		}
		if (summary != null) {
			AccessPointMetricSummaryDao summaryDao = new AccessPointMetricSummaryDao();
			if (summary.getCount() > 0) {
				summaryDao.save(summary);
			} else if (summary.getKey() != null) {
				// if count has been decremented to 0 and the object is
				// already persisted, delete it
				summaryDao.delete(summary);
			}
		}
	}
}
