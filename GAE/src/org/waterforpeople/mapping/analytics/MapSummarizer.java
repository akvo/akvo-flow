package org.waterforpeople.mapping.analytics;

import java.util.Date;

import org.waterforpeople.mapping.app.web.KMLGenerator;
import org.waterforpeople.mapping.domain.AccessPoint;

import com.gallatinsystems.framework.analytics.summarization.DataSummarizer;
import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.gis.map.domain.MapFragment;
import com.gallatinsystems.gis.map.domain.MapFragment.FRAGMENTTYPE;
import com.google.appengine.api.datastore.Text;


public class MapSummarizer implements DataSummarizer {

	@Override
	public boolean performSummarization(String key, String type) {
		if (key != null) {
			BaseDAO<AccessPoint> accessPointDao = new BaseDAO<AccessPoint>(
					AccessPoint.class);
			AccessPoint ap = accessPointDao.getByKey(Long.parseLong(key));
			if (ap != null) {
				/******
				 * 1. Load Placemark VM
				 * 2. Generate placemark text
				 * 3. Save to Map Fragment Table
				 */
				KMLGenerator kmlGen  = new KMLGenerator();
				try {
					String placemark = kmlGen.bindPlacemark(ap,"PlacemarkTabs.vm");
					MapFragment mf = new MapFragment();
					mf.setFragmentValue(new Text(placemark));
					mf.setFragmentType(FRAGMENTTYPE.PLACEMARK);
					mf.setCreatedDateTime(new Date());
					mf.setCode(ap.getCountryCode());
					BaseDAO<MapFragment> mapFragmentDao = new BaseDAO<MapFragment>(MapFragment.class);
					mapFragmentDao.save(mf);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
		}
		return true;
	}

}
