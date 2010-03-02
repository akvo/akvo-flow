package com.gallatinsystems.framework.dao;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;

import org.waterforpeople.mapping.db.PMF;

import com.gallatinsystems.framework.domain.BaseDomain;

public class BaseDAO {
	PersistenceManager pm;
	private static final Logger log = Logger.getLogger(BaseDAO.class.getName());

	private void init() {
		pm = PMF.get().getPersistenceManager();
	}

	public BaseDAO() {
		init();
	}

	public PersistenceManager getPersistenceManager() {
		return pm;
	}

	public <T extends BaseDomain> T save(T object) {
		if (object.getCreatedDateTime() == null) {
			object.setCreatedDateTime(new Date());
		}
		if (object.getLastUpdateDateTime() == null) {
			object.setLastUpdateDateTime(new Date());
		}
		return pm.makePersistent(object);
	}

	public <T extends BaseDomain> T saveOrUpdate(T object) {
		return save(object);
	}

	public <T extends BaseDomain> T getByKey(Long keyId) {
		T object = null;
		javax.jdo.Query query = pm.newQuery(object.getClass());
		query.setFilter("key == KeyIdParam");
		query.declareParameters("Long KeyIdParam");
		List<T> results = (List<T>) query.execute(keyId);
		if (results.size() > 0) {
			object = results.get(0);
		}
		return object;
	}

	public <T extends BaseDomain> List<T> list() {
		T object = null;
		javax.jdo.Query query = pm.newQuery(object.getClass());
		List<T> results = (List<T>) query.execute();
		return results;

	}

}
