package com.gallatinsystems.gis.geography.dao;

import java.util.List;

import javax.jdo.PersistenceManager;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import com.gallatinsystems.gis.geography.domain.Country;

public class CountryDao extends BaseDAO<Country> {

	public CountryDao() {
		super(Country.class);
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("unchecked")
	public List<Country> list(String orderByCol, String direction,
			String cursorString) {
		PersistenceManager pm = PersistenceFilter.getManager();
		javax.jdo.Query query = pm.newQuery(Country.class);
		query.setOrdering(orderByCol + " " + direction);
		prepareCursor(cursorString, query);

		List<Country> results = (List<Country>) query.execute();

		return results;

	}

}
