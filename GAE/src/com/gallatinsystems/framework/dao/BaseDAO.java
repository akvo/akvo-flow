package com.gallatinsystems.framework.dao;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;

import org.datanucleus.store.appengine.query.JDOCursorHelper;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.framework.domain.BaseDomain;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class BaseDAO<T extends BaseDomain> {
	public static final int DEFAULT_RESULT_COUNT = 20;
	protected static final String STRING_TYPE = "String";
	protected static final String EQ_OP = " == ";
	protected static final String GTE_OP = " >= ";
	protected static final String LTE_OP = " <= ";
	private Class<T> concreteClass;
	protected Logger log;

	public enum CURSOR_TYPE {
		all
	};

	public BaseDAO(Class<T> e) {
		setDomainClass(e);
		log = Logger.getLogger(this.getClass().getName());
	}

	/**
	 * Injected version of the actual Class to pass for the persistentClass in
	 * the query creation. This must be set before using this implementation
	 * class or any derived class.
	 * 
	 * @param e
	 *            an instance of the type of object to use for this instance of
	 *            the DAO implementation.
	 */
	public void setDomainClass(Class<T> e) {
		this.concreteClass = e;
	}

	/**
	 * saves an object to the data store AND closes the persistence manager
	 * instance to force a flush
	 * 
	 * @param <E>
	 * @param obj
	 * @return
	 */
	public <E extends BaseDomain> E save(E obj) {

		PersistenceManager pm = PersistenceFilter.getManager();
		if (obj.getCreatedDateTime() == null) {
			obj.setCreatedDateTime(new Date());
		}
		obj.setLastUpdateDateTime(new Date());
		obj = pm.makePersistent(obj);

		return obj;
	}

	public <E extends BaseDomain> E saveAndFlush(E obj) {
		PersistenceManager pm = PersistenceFilter.getManager();
		if (obj.getCreatedDateTime() == null) {
			obj.setCreatedDateTime(new Date());
		}
		obj.setLastUpdateDateTime(new Date());
		obj = pm.makePersistent(obj);
		pm.flush();

		return obj;
	}

	public T saveOrUpdate(T object) {
		return save(object);
	}

	public <E extends BaseDomain> Collection<E> save(Collection<E> objList) {
		if (objList != null) {
			for (E item : objList) {

				if (item.getCreatedDateTime() == null) {
					item.setCreatedDateTime(new Date());
				}

				item.setLastUpdateDateTime(new Date());
			}
			PersistenceManager pm = PersistenceFilter.getManager();
			objList = pm.makePersistentAll(objList);

		}
		return objList;

	}

	/**
	 * gets the core persistent object for the dao concrete class using the
	 * string key (obtained from KeyFactory.stringFromKey())
	 * 
	 * @param keyString
	 * @return
	 */
	public T getByKey(String keyString) {
		return getByKey(keyString, concreteClass);
	}

	public T getByKey(Key key) {
		return getByKey(key, concreteClass);
	}

	/**
	 * convenience method to allow loading of other persistent objects by key
	 * from this dao
	 * 
	 * @param keyString
	 * @return
	 */
	public <E extends BaseDomain> E getByKey(String keyString, Class<E> clazz) {
		PersistenceManager pm = PersistenceFilter.getManager();
		E result = null;
		Key k = KeyFactory.stringToKey(keyString);
		try {
			result = (E) pm.getObjectById(clazz, k);
		} catch (JDOObjectNotFoundException nfe) {
			log.warning("No " + clazz.getCanonicalName() + " found with key: "
					+ k);
		}
		return result;
	}

	public <E extends BaseDomain> E getByKey(Key key, Class<E> clazz) {
		PersistenceManager pm = PersistenceFilter.getManager();
		E result = null;

		try {
			result = (E) pm.getObjectById(clazz, key);
		} catch (JDOObjectNotFoundException nfe) {
			log.warning("No " + clazz.getCanonicalName() + " found with key: "
					+ key);
		}
		return result;
	}

	public T getByKey(Long id) {
		return getByKey(id, concreteClass);
	}

	public <E extends BaseDomain> E getByKey(Long id, Class<E> clazz) {
		PersistenceManager pm = PersistenceFilter.getManager();
		String itemKey = KeyFactory.createKeyString(clazz.getSimpleName(), id);
		E result = null;
		try {
			result = pm.getObjectById(clazz, itemKey);
		} catch (JDOObjectNotFoundException nfe) {
			log.warning("No " + clazz.getCanonicalName() + " found with id: "
					+ id);
		}
		return result;
	}

	/**
	 * lists all of the concreteClass instances in the datastore. 
	 * if we think we'll use this on large tables, we should use Extents
	 * 
	 * @return
	 */
	public List<T> list(String cursorString) {
		return list(concreteClass, cursorString);
	}

	/**
	 * lists all of the type passed in.
	 * 
	 *  if we think we'll use this on large tables, we should use Extents
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <E extends BaseDomain> List<E> list(Class<E> c, String cursorString) {
		PersistenceManager pm = PersistenceFilter.getManager();
		javax.jdo.Query query = pm.newQuery(c);

		if (cursorString != null
				&& !cursorString.trim().toLowerCase().equals(
						Constants.ALL_RESULTS)) {
			Cursor cursor = Cursor.fromWebSafeString(cursorString);
			Map<String, Object> extensionMap = new HashMap<String, Object>();
			extensionMap.put(JDOCursorHelper.CURSOR_EXTENSION, cursor);
			query.setExtensions(extensionMap);
		}
		List<E> results = null;
		this.prepareCursor(cursorString, query);
		results = (List<E>) query.execute();

		return results;
	}

	/**
	 * returns a single object based on the property value
	 * 
	 * @param propertyName
	 * @param propertyValue
	 * @param propertyType
	 * @return
	 */
	protected T findByProperty(String propertyName, Object propertyValue,
			String propertyType) {
		T result = null;
		List<T> results = listByProperty(propertyName, propertyValue,
				propertyType);
		if (results.size() > 0) {
			result = results.get(0);
		}
		return result;
	}

	/**
	 * lists all the objects of the same type as the concreteClass with property
	 * equal to the value passed in
	 * 
	 * since using this requires the caller know the persistence data type of
	 * the field and the field name, this method is protected so that it can
	 * only be used by subclass DAOs. We don't want those details to leak into
	 * higher layers of the code.
	 * 
	 * @param propertyName
	 * @param propertyValue
	 * @param propertyType
	 * @return
	 */
	protected List<T> listByProperty(String propertyName, Object propertyValue,
			String propertyType) {
		return listByProperty(propertyName, propertyValue, propertyType,
				concreteClass);
	}
	
	protected List<T> listByProperty(String propertyName, Object propertyValue,
			String propertyType, String orderByCol, String orderByDir) {
		return listByProperty(propertyName, propertyValue, propertyType, orderByCol, orderByDir,
				concreteClass);
	}

	protected List<T> listByProperty(String propertyName, Object propertyValue,
			String propertyType, String orderByCol) {
		return listByProperty(propertyName, propertyValue, propertyType,
				orderByCol, concreteClass);
	}

	/**
	 * convenience method to list all instances of the type passed in that match
	 * the property
	 * 
	 * since using this requires the caller know the persistence data type of
	 * the field and the field name, this method is protected so that it can
	 * only be used by subclass DAOs. We don't want those details to leak into
	 * higher layers of the code.
	 * 
	 * @param propertyName
	 * @param propertyValue
	 * @param propertyType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected <E extends BaseDomain> List<E> listByProperty(
			String propertyName, Object propertyValue, String propertyType,
			Class<E> clazz) {
		PersistenceManager pm = PersistenceFilter.getManager();
		List<E> results = null;

		String paramName = propertyName + "Param";
		if (paramName.contains(".")) {
			paramName = paramName.substring(paramName.indexOf(".") + 1);
		}
		javax.jdo.Query query = pm.newQuery(clazz);
		query.setFilter(propertyName + " == " + paramName);
		query.declareParameters(propertyType + " " + paramName);
		results = (List<E>) query.execute(propertyValue);

		return results;
	}
	protected <E extends BaseDomain> List<E> listByProperty(
			String propertyName, Object propertyValue, String propertyType, String orderByField, String orderByDir,
			Class<E> clazz) {
		PersistenceManager pm = PersistenceFilter.getManager();
		List<E> results = null;

		String paramName = propertyName + "Param";
		if (paramName.contains(".")) {
			paramName = paramName.substring(paramName.indexOf(".") + 1);
		}
		javax.jdo.Query query = pm.newQuery(clazz);
		query.setFilter(propertyName + " == " + paramName);
		
		query.setOrdering(orderByField + " " + orderByDir);
		query.declareParameters(propertyType + " " + paramName);
		results = (List<E>) query.execute(propertyValue);

		return results;
	}
	/**
	 * convenience method to list all instances of the type passed in that match
	 * the property
	 * 
	 * since using this requires the caller know the persistence data type of
	 * the field and the field name, this method is protected so that it can
	 * only be used by subclass DAOs. We don't want those details to leak into
	 * higher layers of the code.
	 * 
	 * @param propertyName
	 * @param propertyValue
	 * @param propertyType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected <E extends BaseDomain> List<E> listByProperty(
			String propertyName, Object propertyValue, String propertyType,
			String orderByCol, Class<E> clazz) {
		PersistenceManager pm = PersistenceFilter.getManager();
		List<E> results = null;

		String paramName = propertyName + "Param";
		if (paramName.contains(".")) {
			paramName = paramName.substring(paramName.indexOf(".") + 1);
		}
		javax.jdo.Query query = pm.newQuery(clazz);
		query.setFilter(propertyName + " == " + paramName);
		query.declareParameters(propertyType + " " + paramName);
		query.setOrdering(orderByCol);
		results = (List<E>) query.execute(propertyValue);

		return results;
	}

	/**
	 * deletes an object from the db
	 * 
	 * @param <E>
	 * @param obj
	 */
	public <E extends BaseDomain> void delete(E obj) {
		PersistenceManager pm = PersistenceFilter.getManager();
		pm.deletePersistent(obj);
	}

	/**
	 * 
	 * deletes a list of objects in a single datastore interaction
	 */
	public <E extends BaseDomain> void delete(Collection<E> obj) {
		PersistenceManager pm = PersistenceFilter.getManager();
		pm.deletePersistentAll(obj);
	}

	/**
	 * utility method to form a hash map of query parameters using an equality
	 * operator
	 * 
	 * @param paramName
	 *            - name of object property
	 * @param filter
	 *            - in/out stringBuilder of query filters
	 * @param param
	 *            -in/out stringBuilder of param names
	 * @param type
	 *            - data type of field
	 * @param value
	 *            - value to bind to param
	 * @param paramMap
	 *            - in/out parameter map
	 */
	protected void appendNonNullParam(String paramName, StringBuilder filter,
			StringBuilder param, String type, Object value,
			Map<String, Object> paramMap) {
		appendNonNullParam(paramName, filter, param, type, value, paramMap,
				EQ_OP);
	}

	/**
	 * utility method to form a hash map of query parameters
	 * 
	 * @param paramName
	 *            - name of object property
	 * @param filter
	 *            - in/out stringBuilder of query filters
	 * @param param
	 *            -in/out stringBuilder of param names
	 * @param type
	 *            - data type of field
	 * @param value
	 *            - value to bind to param
	 * @param paramMap
	 *            - in/out parameter map
	 * @param operator
	 *            - operator to use
	 */
	protected void appendNonNullParam(String paramName, StringBuilder filter,
			StringBuilder param, String type, Object value,
			Map<String, Object> paramMap, String operator) {
		if (value != null) {
			if (paramMap.keySet().size() > 0) {
				filter.append(" && ");
				param.append(", ");
			}
			String paramValName = paramName + "Param"
					+ paramMap.keySet().size();
			filter.append(paramName).append(" ").append(operator).append(" ")
					.append(paramValName);
			param.append(type).append(" ").append(paramValName);
			paramMap.put(paramValName, value);
		}
	}

	@SuppressWarnings("unchecked")
	public static String getCursor(List results) {
		if (results != null && results.size() > 0) {
			Cursor cursor = JDOCursorHelper.getCursor(results);
			if (cursor != null) {
				return cursor.toWebSafeString();
			} else {
				return null;
			}
		}
		return null;

	}

	protected void prepareCursor(String cursorString, javax.jdo.Query query) {
		if (cursorString != null
				&& !cursorString.trim().toLowerCase().equals(
						Constants.ALL_RESULTS)) {
			Cursor cursor = Cursor.fromWebSafeString(cursorString);
			Map<String, Object> extensionMap = new HashMap<String, Object>();
			extensionMap.put(JDOCursorHelper.CURSOR_EXTENSION, cursor);
			query.setExtensions(extensionMap);
		}
		if (cursorString == null || !cursorString.equals(Constants.ALL_RESULTS))
			query.setRange(0, DEFAULT_RESULT_COUNT);
	}
}
