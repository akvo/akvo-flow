package com.gallatinsystems.diagnostics.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	 * @param phoneNumber
	 * @param deviceId
	 * @param unAckOnly
	 * @param cursorString
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<RemoteStacktrace> listStacktrace(String phoneNumber,
			String deviceId, boolean unAckOnly, String cursorString) {
		PersistenceManager pm = PersistenceFilter.getManager();
		List<RemoteStacktrace> results = null;
		javax.jdo.Query q = pm.newQuery(RemoteStacktrace.class);
		StringBuilder filterString = new StringBuilder();
		StringBuilder paramString = new StringBuilder();
		Map<String, Object> paramMap = new HashMap<String, Object>();

		appendNonNullParam("phoneNumber", filterString, paramString, "String",
				phoneNumber, paramMap);
		appendNonNullParam("deviceIdentifier", filterString, paramString,
				"String", deviceId, paramMap);
		if (unAckOnly) {
			appendNonNullParam("acknowleged", filterString, paramString,
					"java.lang.Boolean", false, paramMap);
		}
		q.setOrdering("errorDate desc");
		if (unAckOnly || phoneNumber != null || deviceId != null) {
			q.setFilter(filterString.toString());
			q.declareParameters(paramString.toString());
			prepareCursor(cursorString, q);
			results = (List<RemoteStacktrace>) q.executeWithMap(paramMap);
		} else {
			prepareCursor(cursorString, q);
			results = (List<RemoteStacktrace>) q.execute();
		}
		return results;
	}

}
