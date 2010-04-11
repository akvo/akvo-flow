package org.waterforpeople.mapping.dao;

import java.util.List;

import org.waterforpeople.mapping.domain.Community;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.gis.geography.domain.Country;

/**
 * Data access methods for communities and countriesF
 * 
 * @author Christopher Fagiani
 * 
 */
public class CommunityDao extends BaseDAO<Community> {

	public CommunityDao() {
		super(Community.class);
	}

	/**
	 * finds a single country that is attached to the community identified by
	 * the code
	 * 
	 * @param communityCode
	 * @return
	 */
	public Country findCountryByCommunity(String communityCode) {
		Community comm = findCommunityByCode(communityCode);
		if (comm != null) {
			return comm.getCountry();
		} else {
			return null;
		}
	}

	/**
	 * finds a single community by its code
	 * 
	 * @param communityCode
	 * @return
	 */
	public Community findCommunityByCode(String communityCode) {
		return findByProperty("communityCode", communityCode, "String");
	}

	/**
	 * finds a country by its country code
	 * 
	 * @param code
	 * @return
	 */
	public Country findCountryByCode(String code) {
		List<Country> cList = listByProperty("isoAlpha2Code", code, "String",
				Country.class);
		if (cList != null && cList.size() > 0) {
			return cList.get(0);
		} else {
			return null;
		}
	}

	/**
	 * lists all communities within a specific country.
	 * 
	 * @param countryCode
	 * @return
	 */
	public List<Community> listCommunityByCountry(String countryCode) {
		return (List<Community>) listByProperty("countryCode", countryCode,
				"String");
	}

}
