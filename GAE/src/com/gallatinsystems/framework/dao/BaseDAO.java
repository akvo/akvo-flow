package com.gallatinsystems.framework.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;

import com.gallatinsystems.framework.domain.BaseDomain;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class BaseDAO<T extends BaseDomain> {
	protected static final String STRING_TYPE = "String";
	private Class<T> concreteClass;
	protected Logger log;

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
		if (obj.getLastUpdateDateTime() == null) {
			obj.setLastUpdateDateTime(new Date());
		}

		obj = pm.makePersistent(obj);

		return obj;
	}

	public T saveOrUpdate(T object) {
		return save(object);
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
	 * lists all of the concreteClass instances in the datastore. TODO: if we
	 * think we'll use this on large tables, we should use Extents
	 * 
	 * @return
	 */
	public List<T> list() {
		return list(concreteClass);
	}

	/**
	 * lists all of the type passed in.
	 * 
	 * TODO: if we think we'll use this on large tables, we should use Extents
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <E extends BaseDomain> List<E> list(Class<E> c) {
		PersistenceManager pm = PersistenceFilter.getManager();
		List<E> results = null;
		javax.jdo.Query query = pm.newQuery(c);
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

		javax.jdo.Query query = pm.newQuery(clazz);
		query.setFilter(propertyName + " == " + propertyName + "Param");
		query.declareParameters(propertyType + " " + propertyName + "Param");
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
	 */
	protected void appendNonNullParam(String paramName, StringBuilder filter,
			StringBuilder param, String type, Object value,
			Map<String, Object> paramMap) {
		if (value != null) {
			if (paramMap.keySet().size() > 0) {
				filter.append(" &&");
				param.append(", ");
			}

			filter.append(paramName).append(" == ").append(paramName).append(
					"Param");
			param.append(type).append(" ").append(paramName).append("Param");
			paramMap.put(paramName + "Param", value);
		}
	}
}
