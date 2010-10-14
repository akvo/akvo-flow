package org.waterforpeople.mapping.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;

import org.waterforpeople.mapping.analytics.domain.AccessPointMetricSummary;
import org.waterforpeople.mapping.domain.AccessPointMetricMapping;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;

/**
 * persists/finds AccessPointMetricMappings
 * 
 * @author Christopher Fagiani
 * 
 */
public class AccessPointMetricMappingDao extends
		BaseDAO<AccessPointMetricMapping> {

	public AccessPointMetricMappingDao() {
		super(AccessPointMetricMapping.class);
	}

	/**
	 * finds mappings based on the non-null parameters passed in. At least 1
	 * must be non-null.
	 * 
	 * @param org
	 * @param name
	 * @param group
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<AccessPointMetricMapping> findMappings(String org,
			String group, String name) {
		if (org != null || group != null || name != null) {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			StringBuilder filterString = new StringBuilder();
			StringBuilder paramString = new StringBuilder();

			appendNonNullParam("organization", filterString, paramString,
					"String", org, paramMap);
			appendNonNullParam("metricName", filterString, paramString,
					"String", name, paramMap);
			appendNonNullParam("metricGroup", filterString, paramString,
					"String", group, paramMap);

			PersistenceManager pm = PersistenceFilter.getManager();
			javax.jdo.Query query = pm.newQuery(AccessPointMetricSummary.class);
			query.setFilter(filterString.toString());
			query.declareParameters(paramString.toString());

			return (List<AccessPointMetricMapping>) query
					.executeWithMap(paramMap);
		} else {
			return list(CURSOR_TYPE.all.toString());
		}
	}

}
