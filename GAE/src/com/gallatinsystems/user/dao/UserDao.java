package com.gallatinsystems.user.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import com.gallatinsystems.user.domain.User;

/**
 * Dao for User objects
 * 
 * @author Christopher Fagiani
 * 
 */
public class UserDao extends BaseDAO<User> {

	public UserDao() {
		super(User.class);
	}

	/**
	 * finds a single user by email address.
	 * 
	 * @param email
	 * @return
	 */
	public User findUserByEmail(String email) {
		return findByProperty("emailAddress", email, STRING_TYPE);
	}

	/**
	 * searches for users that match the non-null params
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<User> searchUser(String username, String emailAddress,
			String orderByField, String orderByDir, String cursorString) {
		PersistenceManager pm = PersistenceFilter.getManager();
		javax.jdo.Query query = pm.newQuery(User.class);
		StringBuilder filterString = new StringBuilder();
		StringBuilder paramString = new StringBuilder();
		Map<String, Object> paramMap = null;
		paramMap = new HashMap<String, Object>();

		appendNonNullParam("userName", filterString, paramString, "String",
				username, paramMap);
		appendNonNullParam("emailAddress", filterString, paramString, "String",
				emailAddress, paramMap);

		if (orderByField != null) {
			String ordering = orderByDir;
			if (ordering == null) {
				ordering = "asc";
			}
			query.setOrdering(orderByField + " " + ordering);
		}
		if (filterString.length() > 0) {
			query.setFilter(filterString.toString());
			query.declareParameters(paramString.toString());
		}

		prepareCursor(cursorString, query);
		List<User> results = (List<User>) query.executeWithMap(paramMap);
		return results;
	}
	
}
