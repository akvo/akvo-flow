package org.waterforpeople.mapping.analytics;

import java.util.Date;

import java.util.logging.Logger;
import org.waterforpeople.mapping.app.web.KMLGenerator;
import org.waterforpeople.mapping.domain.AccessPoint;

import com.gallatinsystems.framework.analytics.summarization.DataSummarizer;
import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.gis.map.domain.MapFragment;
import com.gallatinsystems.gis.map.domain.MapFragment.FRAGMENTTYPE;
import com.google.appengine.api.datastore.Text;

public class MapSummarizer implements DataSummarizer {
	private static Logger log = Logger.getLogger(AbstractRestApiServlet.class
			.getName());

	@Override
	public boolean performSummarization(String key, String type, String value,
			Integer offset, String cursor) {
		if (key != null) {
			BaseDAO<AccessPoint> accessPointDao = new BaseDAO<AccessPoint>(
					AccessPoint.class);
			AccessPoint ap = accessPointDao.getByKey(Long.parseLong(key));
			if (ap != null) {
				/******
				 * 1. Load Placemark VM 2. Generate placemark text 3. Save to
				 * Map Fragment Table
				 */
				KMLGenerator kmlGen = new KMLGenerator();
				try {
					String placemark = kmlGen.bindPlacemark(ap,
							"PlacemarkTabs.vm");
					if (placemark != null) {
						MapFragment mf = new MapFragment();
						mf.setFragmentValue(new Text(placemark));
						mf
								.setFragmentType(FRAGMENTTYPE.COUNTRY_INDIVIDUAL_PLACEMARK);
						mf.setCreatedDateTime(new Date());
						mf.setCountryCode(ap.getCountryCode());
						mf.setTechnologyType(ap.getTypeTechnologyString());
						mf.setPointType(ap.getPointType());
						BaseDAO<MapFragment> mapFragmentDao = new BaseDAO<MapFragment>(
								MapFragment.class);
						mapFragmentDao.save(mf);
					} else {
						log.info("Could not save MapFragment for placemark: "
								+ ap.toString());
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return true;
	}

	@Override
	public String getCursor() {
		return null;
	}

}
