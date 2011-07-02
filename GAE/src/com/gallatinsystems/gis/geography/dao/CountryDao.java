package com.gallatinsystems.gis.geography.dao;

import java.util.List;

import javax.jdo.PersistenceManager;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import com.gallatinsystems.gis.geography.domain.Country;

public class CountryDao extends BaseDAO<Country> {

	public CountryDao() {
		super(Country.class);
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

	public Country findByCode(String code){
		String propertyName = null;
		String propertyType = null;
		//Todo create a scanner and test for number
		
		if(code.trim().length()==2){
			propertyName = "isoAlpha2Code";
			propertyType = "String";
		}else if(code.trim().length()==3){
			propertyName = "isoAlpha3Code";
			propertyType = "String";
		}
		Country country = super.findByProperty(propertyName, code, propertyType);
		if (country!=null)
			return country;
		else
			return null;
	}
}
