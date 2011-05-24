package com.gallatinsystems.messaging.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import com.gallatinsystems.messaging.domain.Message;

/**
 * Data access object for manipulating Message objects
 * 
 * @author Christopher Fagiani
 * 
 */
public class MessageDao extends BaseDAO<Message> {

	public MessageDao() {
		super(Message.class);
	}

	/**
	 * lists all messages
	 * 
	 * @param about
	 *            - optional subject
	 * @param id
	 *            - optional ID
	 * @param cursor
	 *            - cursor string
	 * 
	 * @return - all messages matching criteria
	 */
	@SuppressWarnings("unchecked")
	public List<Message> listBySubject(String about, Long id, String cursor) {
		PersistenceManager pm = PersistenceFilter.getManager();
		javax.jdo.Query query = pm.newQuery(Message.class);
		StringBuilder filterString = new StringBuilder();
		StringBuilder paramString = new StringBuilder();
		Map<String, Object> paramMap = null;
		paramMap = new HashMap<String, Object>();
		appendNonNullParam("actionAbout", filterString, paramString, "String",
				about, paramMap);
		appendNonNullParam("objectId", filterString, paramString, "Long", id,
				paramMap);

		if(filterString.toString().trim().length()>0){
			query.setFilter(filterString.toString());
			query.declareParameters(paramString.toString());
		}
		query.setOrdering("lastUpdateDateTime desc");
		prepareCursor(cursor, query);
		List<Message> results = (List<Message>) query.executeWithMap(paramMap);
		return results;
	}

}
