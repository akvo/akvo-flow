package org.waterforpeople.mapping.dao;

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

		appendNonNullParam("country", filterString, paramString, "String",
				country, paramMap);
		appendNonNullParam("community", filterString, paramString, "String",
				community, paramMap);
		appendNonNullParam("type", filterString, paramString, "String", type,
				paramMap);
		query.setFilter(filterString.toString());
		query.declareParameters(paramString.toString());
		return (List<AccessPoint>) query.executeWithMap(paramMap);
	}

}
