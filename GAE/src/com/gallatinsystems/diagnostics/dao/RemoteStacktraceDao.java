package com.gallatinsystems.diagnostics.dao;

import java.util.List;

import javax.jdo.PersistenceManager;

import com.gallatinsystems.diagnostics.domain.RemoteStacktrace;
import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;

/**
 * Persists and retrieves remoteStacktrace objects from the data store
 * 
 * @author Christopher Fagiani
 * 
 */
public class RemoteStacktraceDao extends BaseDAO<RemoteStacktrace> {

	public RemoteStacktraceDao() {
		super(RemoteStacktrace.class);
	}

	/**
	 * lists all stacktrace objects in the database. If unAckOnly is true, only
	 * unacknowledged exceptions will be returned.
	 * 
	 * @param unAckOnly
	 * @param cursorString
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<RemoteStacktrace> listStacktrace(boolean unAckOnly,
			String cursorString) {
		PersistenceManager pm = PersistenceFilter.getManager();
		List<RemoteStacktrace> results = null;
		javax.jdo.Query q = pm.newQuery(RemoteStacktrace.class);
		if (unAckOnly) {
			q.setFilter("acknowleged == ackParam");
			q.declareParameters("java.lang.Boolean ackParam");
			q.setOrdering("errorDate desc");
			prepareCursor(cursorString, q);
			results = (List<RemoteStacktrace>) q.execute(true);
		} else {
			q.setOrdering("errorDate desc");
			prepareCursor(cursorString, q);
			results = (List<RemoteStacktrace>) q.execute();
		}
		return results;
	}

}
