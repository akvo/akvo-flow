package com.gallatinsystems.task.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import com.gallatinsystems.task.domain.Task;
import com.gallatinsystems.task.domain.Task.TaskStatus;

/**
 * stores and retrievs tasks from the data store.
 * 
 * @author Christopher Fagiani
 * 
 */
public class TaskDao extends BaseDAO<Task> {

	public TaskDao() {
		super(Task.class);
	}

	/**
	 * lists tasks based on the countyr code and the status passed in. If
	 * parameters are null, then all tasks are returned.
	 * 
	 * @param countryCode
	 * @param status
	 * @param cursorString
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Task> listTasksByCountry(String countryCode, TaskStatus status,
			String cursorString) {
		PersistenceManager pm = PersistenceFilter.getManager();
		javax.jdo.Query query = pm.newQuery(Task.class);
		Map<String, Object> paramMap = null;

		StringBuilder filterString = new StringBuilder();
		StringBuilder paramString = new StringBuilder();
		paramMap = new HashMap<String, Object>();

		appendNonNullParam("countryCode", filterString, paramString, "String",
				countryCode, paramMap);
		appendNonNullParam("status", filterString, paramString, "String",
				status, paramMap);
		query.setFilter(filterString.toString());
		query.declareParameters(paramString.toString());

		prepareCursor(cursorString, query);

		return (List<Task>) query.executeWithMap(paramMap);
	}

}
