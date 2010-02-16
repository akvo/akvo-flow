package org.waterforpeople.mapping.dao;

import java.util.List;

import javax.jdo.PersistenceManager;

import org.waterforpeople.mapping.db.PMF;
import org.waterforpeople.mapping.domain.AccessPoint;
import org.waterforpeople.mapping.domain.GeoRegion;

import com.google.appengine.api.datastore.Key;

public class GeoRegionDAO {
	PersistenceManager pm;
	
	public Key save(GeoRegion regions) {
		pm.makePersistent(regions);
		return regions.getKey();
	}
	
	public List<GeoRegion> listGeoRegions(){
		javax.jdo.Query query = pm.newQuery(AccessPoint.class);
		List<GeoRegion> region= (List<GeoRegion>) query.execute();
			
		return region;

	}

	public GeoRegionDAO() {
		init();
	}
	private void init() {
		pm = PMF.get().getPersistenceManager();
	}
}
