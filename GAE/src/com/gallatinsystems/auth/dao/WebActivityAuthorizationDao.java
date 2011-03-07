package com.gallatinsystems.auth.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;

import com.gallatinsystems.auth.domain.WebActivityAuthorization;
import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;

/**
 * persists and finds WebActivityAuthorization objects.
 * 
 * @author Christopher Fagiani
 * 
 */
public class WebActivityAuthorizationDao extends
		BaseDAO<WebActivityAuthorization> {

	public WebActivityAuthorizationDao() {
		super(WebActivityAuthorization.class);
	}

	/**
	 * lists all webActivityAuthorizations associated with the token and,
	 * optionally, activityName passed in. If the validOnly flag is true, this
	 * list is filtered to only include "valid" items (unexpired tokens with a
	 * useCount < maxUses).
	 * 
	 * @param token
	 * @param activity
	 * @param cursorString
	 * @param validOnly
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<WebActivityAuthorization> listByToken(String token,
			String activityName, String cursorString, boolean validOnly) {
		PersistenceManager pm = PersistenceFilter.getManager();
		javax.jdo.Query query = pm.newQuery(WebActivityAuthorization.class);
		Map<String, Object> paramMap = null;

		StringBuilder filterString = new StringBuilder();
		StringBuilder paramString = new StringBuilder();
		paramMap = new HashMap<String, Object>();

		appendNonNullParam("token", filterString, paramString, "String", token,
				paramMap);
		appendNonNullParam("webActivityName", filterString, paramString, "String",
				activityName, paramMap);
		query.setFilter(filterString.toString());
		query.declareParameters(paramString.toString());
		prepareCursor(cursorString, query);

		List<WebActivityAuthorization> authList = (List<WebActivityAuthorization>) query
				.executeWithMap(paramMap);

		if (authList != null && validOnly) {
			List<WebActivityAuthorization> filteredList = new ArrayList<WebActivityAuthorization>();
			for (WebActivityAuthorization auth : authList) {
				if (auth.isValidForAuth()) {
					filteredList.add(auth);
				}
			}
			authList = filteredList;
		}
		return authList;
	}
}
