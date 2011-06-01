package com.gallatinsystems.metric.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import com.gallatinsystems.metric.domain.Metric;

/**
 * Data access object for persisting Metric objects.
 * 
 * @author Christopher Fagiani
 * 
 */
public class MetricDao extends BaseDAO<Metric> {

	public MetricDao() {
		super(Metric.class);
	}

	/**
	 * lists all metrics for a given organization, optionally filtered by group.
	 * 
	 * @param organization
	 * @param group
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Metric> listMetricByOrg(String organization, String group,
			String cursorString) {
		PersistenceManager pm = PersistenceFilter.getManager();
		javax.jdo.Query query = pm.newQuery(Metric.class);
		Map<String, Object> paramMap = null;

		StringBuilder filterString = new StringBuilder();
		StringBuilder paramString = new StringBuilder();
		paramMap = new HashMap<String, Object>();

		appendNonNullParam("organization", filterString, paramString, "String",
				organization, paramMap);
		appendNonNullParam("group", filterString, paramString, "String", group,
				paramMap);
		if (filterString.toString().trim().length() > 0) {
			query.setFilter(filterString.toString());
			query.declareParameters(paramString.toString());
			prepareCursor(cursorString, query);
			return (List<Metric>) query.executeWithMap(paramMap);
		} else {
			return list(cursorString);
		}
	}

}
