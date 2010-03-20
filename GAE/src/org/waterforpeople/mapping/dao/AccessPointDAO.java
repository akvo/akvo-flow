package org.waterforpeople.mapping.dao;

import java.util.List;

import javax.jdo.PersistenceManager;

import org.waterforpeople.mapping.db.PMF;
import org.waterforpeople.mapping.domain.AccessPoint;

public class AccessPointDAO {
	PersistenceManager pm;

	public Long save(AccessPoint ap) {
		if (ap.getCollectionDate() == null) {

		}
		Boolean savedSuccessFlag = false;
		pm.makePersistent(ap);
		return ap.getId();
	}

	public AccessPointDAO() {
		init();
	}

	public AccessPoint get(Long id) {
		AccessPoint si = null;

		javax.jdo.Query query = pm.newQuery(AccessPoint.class);
		query.setFilter("id == idParam");
		query.declareParameters("Long idParam");
		List<AccessPoint> results = (List<AccessPoint>) query.execute(id);
		if (results.size() > 0) {
			si = results.get(0);
		}
		return si;
	}

	private void init() {
		pm = PMF.get().getPersistenceManager();
	}

	public List<AccessPoint> listAccessPoints() {
		javax.jdo.Query query = pm.newQuery(AccessPoint.class);
		List<AccessPoint> points = (List<AccessPoint>) query.execute();

		return points;
	}

	/**
	 * lists all access points with lat/long within the bounding box passed in
	 * 
	 * @param lat1
	 * @param lon1
	 * @param lat2
	 * @param lon2
	 * @return
	 */
	public List<AccessPoint> listAccessPointsWithinRegion(Double lat1,
			Double lon1, Double lat2, Double lon2) {
		javax.jdo.Query query = pm.newQuery(AccessPoint.class);
		query
				.setFilter("latitude >= lat1param && latitude <= lat2param");
		query
				.declareParameters("Double lat1param, Double lat2param");
		List<AccessPoint> list = (List<AccessPoint>) query.executeWithArray(lat1, lat2);
		if(list !=null){
			query = pm.newQuery(AccessPoint.class);
			query
			.setFilter("id in idlistparam && longitude >= lon1param && longitude <= lon2param");
	query
			.declareParameters("Double lon1param, Double lon2param");
	list = (List<AccessPoint>) query.executeWithArray(lon1, lon2);
		}
		return list;

	}
}
