package org.waterforpeople.mapping.analytics;

import org.waterforpeople.mapping.analytics.dao.AccessPointStatusSummaryDao;
import org.waterforpeople.mapping.dao.CommunityDao;
import org.waterforpeople.mapping.domain.AccessPoint;

import com.gallatinsystems.framework.analytics.summarization.DataSummarizer;
import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.gis.geography.domain.Country;

/**
 * This class will populate country/community lookup tables based on geo
 * information in the access point.
 * 
 * @author Christopher Fagiani
 * 
 */
public class AccessPointStatusSummarizer implements DataSummarizer {

	/**
	 * summarizes AccessPoints by incrementing counts of AP by country
	 */
	@Override
	public boolean performSummarization(String key, String type, String value,
			Integer offset, String cursor) {
		if (key != null) {
			BaseDAO<AccessPoint> accessPointDao = new BaseDAO<AccessPoint>(
					AccessPoint.class);
			AccessPoint ap = accessPointDao.getByKey(Long.parseLong(key));
			if (ap != null) {
				CommunityDao commDao = new CommunityDao();
				Country c = commDao.findCountryByCommunity(ap
						.getCommunityCode());
				if (c == null) {
					// if we didn't find a country, use a placeholder for now
					c = new Country();
					c.setIsoAlpha2Code("??");
					c.setName("Unknown");
				}
				AccessPointStatusSummaryDao.incrementCount(ap, c, 1);
			}
		}
		return true;
	}

	@Override
	public String getCursor() {
		return null;
	}

}
