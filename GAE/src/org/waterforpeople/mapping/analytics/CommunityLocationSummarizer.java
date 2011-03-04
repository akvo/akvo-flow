package org.waterforpeople.mapping.analytics;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.waterforpeople.mapping.dao.AccessPointDao;
import org.waterforpeople.mapping.dao.CommunityDao;
import org.waterforpeople.mapping.domain.AccessPoint;
import org.waterforpeople.mapping.domain.Community;
import org.waterforpeople.mapping.helper.AccessPointHelper;

import com.gallatinsystems.framework.analytics.summarization.DataSummarizer;
import com.gallatinsystems.gis.geography.domain.Country;
import com.gallatinsystems.gis.location.GeoLocationService;
import com.gallatinsystems.gis.location.GeoLocationServiceGeonamesImpl;
import com.gallatinsystems.gis.location.GeoPlace;

/**
 * This class will populate country/community lookup tables based on geo
 * information in the access point.
 * 
 * @author Christopher Fagiani
 * 
 */
public class CommunityLocationSummarizer implements DataSummarizer {
	Logger logger = Logger.getLogger(CommunityLocationSummarizer.class
			.getName());

	@Override
	public boolean performSummarization(String key, String type, String value,
			Integer offset, String cursor) {
		if (key != null) {
			AccessPointDao accessPointDao = new AccessPointDao();
			AccessPoint ap = accessPointDao.getByKey(new Long(key));
			if (ap != null) {
				CommunityDao commDao = new CommunityDao();
				Community community = commDao.findCommunityByCode(ap
						.getCommunityCode());
				if (community == null
						&& (ap.getLatitude() != null && ap.getLatitude() != 0.0
								&& ap.getLongitude() != null && ap
								.getLongitude() != 0.0)) {
					GeoLocationService gl = new GeoLocationServiceGeonamesImpl();
					GeoPlace gp = gl.findGeoPlace(ap.getLatitude().toString(),
							ap.getLongitude().toString());
					if (gp == null) {
						String countryCode = gl.getCountryCodeForPoint(ap
								.getLatitude().toString(), ap.getLongitude()
								.toString());
						if (countryCode != null) {
							gp = new GeoPlace();
							gp.setCountryCode(countryCode);
							gp.setCountryName(countryCode);
						}
					}
					if (gp != null && gp.getCountryCode() != null
							&& gp.getCountryCode().trim().length() <= 3) {
						Country ourCountry = commDao.findCountryByCode(gp
								.getCountryCode());
						if (ourCountry == null) {
							ourCountry = new Country();
							ourCountry.setIsoAlpha2Code(gp.getCountryCode());
							ourCountry.setName(gp.getCountryName());
							ourCountry.setDisplayName(gp.getCountryName());
							commDao.save(ourCountry);
						}
						community = new Community();
						community.setCommunityCode(ap.getCommunityCode());
						community.setName(gp.getName());
						community.setLat(gp.getLat());
						community.setLon(gp.getLng());
						// have to do this so we can query by it due to GAE's
						// JDO limitations
						community.setCountryCode(ourCountry.getIsoAlpha2Code());
						// this save will cascade-save the country
						commDao.save(community);
					} else {
						logger.log(Level.SEVERE,
								"Did not find a country for lat/lon "
										+ ap.getLatitude() + ","
										+ ap.getLongitude()
										+ " using the geonames service");
					}
				}
				if (ap.getCountryCode() == null && community != null) {
					ap.setCountryCode(community.getCountryCode());
				}
				
				AccessPointHelper aph = new AccessPointHelper();
				aph.setGeoDetails(ap);
				accessPointDao.saveButDonotFireAsync(ap);
			}
		}
		return true;
	}
	
	
	@Override
	public String getCursor() {
		return null;
	}
}
