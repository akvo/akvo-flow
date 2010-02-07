package org.waterforpeople.mapping.dao;

import java.util.List;

import javax.jdo.PersistenceManager;

import org.waterforpeople.mapping.db.PMF;
import org.waterforpeople.mapping.domain.AccessPoint;

public class AccessPointDAO {
	PersistenceManager pm;
	
	public Long save(AccessPoint ap) {
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

}
